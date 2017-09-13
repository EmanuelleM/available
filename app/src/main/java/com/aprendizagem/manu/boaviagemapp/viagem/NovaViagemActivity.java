package com.aprendizagem.manu.boaviagemapp.viagem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.aprendizagem.manu.boaviagemapp.Constantes;
import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.database.Contract;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ViagemEntry;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class NovaViagemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    private static final int EXISTING_VIAGEM_LOADER = 0;

    Menu menu;

    private Uri mCurrentViagemUri;

    private EditText editTextDestino;
    private EditText editLocalHospedagem;

    private Button buttonDataPartida;
    private Button buttonDataChegada;

    private RadioButton optionRazaoLazer;
    private RadioButton optionRazaoNegocios;

    private ImageButton imageButtonPegaLocalizacao;

    private String dataChegada;
    private String dataPartida;
    private String destino;
    private String localHospedagem;
    private String idDousuario;
    private int razaoViagem;


    private Calendar calendar = Calendar.getInstance();

    private int mRazao = ViagemEntry.RAZAO_DESCONHECIDA;

    private GoogleApiClient mGoogleApiClient;

    private static final int PLACE_PICKER_REQUEST = 1000;
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nova_viagem);

        Intent intent = getIntent();
        mCurrentViagemUri = intent.getData();

        if (mCurrentViagemUri == null) {
            setTitle(getString(R.string.nova_viagem));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editando_viagem));
            getLoaderManager().initLoader(EXISTING_VIAGEM_LOADER, null, this);
        }

        editTextDestino = (EditText) findViewById(R.id.edit_text_destino);
        editLocalHospedagem = (EditText) findViewById(R.id.edit_text_local_hospedagem);

        buttonDataChegada = (Button) findViewById(R.id.button_pega_data_chegada);
        buttonDataPartida = (Button) findViewById(R.id.button_pega_data_saida);

        buttonDataChegada.setOnClickListener(this);
        buttonDataPartida.setOnClickListener(this);

        optionRazaoLazer = (RadioButton) findViewById(R.id.radio_lazer);
        optionRazaoNegocios = (RadioButton) findViewById(R.id.radio_negocios);

        imageButtonPegaLocalizacao = (ImageButton) findViewById(R.id.image_button_pega_localizacao);
        imageButtonPegaLocalizacao.setOnClickListener(this);

        mClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_button_pega_localizacao:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(NovaViagemActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_pega_data_chegada:
                pegaDataChegada().show();
                break;
            case R.id.button_pega_data_saida:
                pegaDataPartida().show();

        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_lazer:
                if (checked)
                    mRazao = 1;
                break;
            case R.id.radio_negocios:
                if (checked)
                    mRazao = 2;
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getApplicationContext(), data);
                editLocalHospedagem.setText(place.getName());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private Dialog pegaDataChegada() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                dataChegada = dayOfMonth + "/" + monthOfYear + "/" + year;
                buttonDataChegada.setText(dataChegada);
            }
        };
        return new DatePickerDialog(this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)) {
        };
    }

    private Dialog pegaDataPartida() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                dataPartida = dayOfMonth + "/" + monthOfYear + "/" + year;
                buttonDataPartida.setText(dataPartida);
            }
        };
        return new DatePickerDialog(this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)) {
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_salvar_geral, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.salvar:

                if (editTextDestino.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.informe_o_destino,
                            Toast.LENGTH_SHORT)
                            .show();
                } else {

                    salvarViagem();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void salvarViagem() {

        destino = editTextDestino.getText().toString().trim();
        localHospedagem = editLocalHospedagem.getText().toString().trim();
        idDousuario = Constantes.getIdDoUsuario();
        razaoViagem = mRazao;

        if (mCurrentViagemUri == null) {

            new TaskSalvaViagem(destino, localHospedagem, razaoViagem,
                    dataChegada, dataPartida,
                    idDousuario).execute();
        } else {

            ContentValues values = new ContentValues();
            values.put(ViagemEntry.COLUMN_DESTINO, destino);
            values.put(ViagemEntry.COLUMN_LOCAL_ACOMODACAO, localHospedagem);
            values.put(ViagemEntry.COLUMN_RAZAO, razaoViagem);
            values.put(ViagemEntry.COLUMN_DATA_CHEGADA, dataChegada);
            values.put(ViagemEntry.COLUMN_DATA_PARTIDA, dataPartida);
            values.put(ViagemEntry.COLUMN_ID_USUARIO, idDousuario);

            String selection =
                    Contract.ViagemEntry.COLUMN_ID_USUARIO + "= '" + idDousuario + "'";

            getContentResolver().update(mCurrentViagemUri, values, selection, null);

            startActivity(new Intent(this, ListaViagemActivity.class));
            finish();

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ViagemEntry._ID,
                ViagemEntry.COLUMN_DESTINO,
                ViagemEntry.COLUMN_LOCAL_ACOMODACAO,
                ViagemEntry.COLUMN_RAZAO,
                ViagemEntry.COLUMN_DATA_CHEGADA,
                ViagemEntry.COLUMN_DATA_PARTIDA
        };

        return new CursorLoader(this,
                mCurrentViagemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {

            int destinoColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DESTINO);
            int localHospedagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_LOCAL_ACOMODACAO);
            int razaoViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_RAZAO);
            int dataChegadaViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DATA_CHEGADA);
            int dataPartidaViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DATA_PARTIDA);

            String destino = cursor.getString(destinoColumnIndex);
            String localHospedagem = cursor.getString(localHospedagemColumnIndex);
            String dataChegada = cursor.getString(dataChegadaViagemColumnIndex);
            String dataPartida = cursor.getString(dataPartidaViagemColumnIndex);
            int razaoViagem = cursor.getInt(razaoViagemColumnIndex);

            if (razaoViagem == 1) {

                optionRazaoLazer.setChecked(true);

            } else if (razaoViagem == 2) {

                optionRazaoNegocios.setChecked(true);
            }

            editTextDestino.setText(destino);
            editLocalHospedagem.setText(localHospedagem);

            if (dataChegada == null) {
                buttonDataChegada.setText(R.string.data_da_chegada);
            } else {
                buttonDataChegada.setText(dataChegada);
            }

            if (dataPartida == null) {
                buttonDataPartida.setText(R.string.data_da_partida);
            } else {
                buttonDataPartida.setText(dataPartida);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class TaskSalvaViagem extends AsyncTask<Void, Void, Uri> {

        private final String destino;
        private final String localHospedagem;
        private final int razaoViagem;
        private final String dataChegada;
        private final String dataPartida;
        private final String idDousuario;

        TaskSalvaViagem(String destino, String localHospedagem, int razaoViagem, String dataChegada, String dataPartida,
                        String idDousuario) {
            this.destino = destino;
            this.localHospedagem = localHospedagem;
            this.razaoViagem = razaoViagem;
            this.dataChegada = dataChegada;
            this.dataPartida = dataPartida;
            this.idDousuario = idDousuario;
        }

        @Override
        protected Uri doInBackground(Void... params) {

            ContentValues values = new ContentValues();
            values.put(ViagemEntry.COLUMN_DESTINO, destino);
            values.put(ViagemEntry.COLUMN_LOCAL_ACOMODACAO, localHospedagem);
            values.put(ViagemEntry.COLUMN_RAZAO, razaoViagem);
            values.put(ViagemEntry.COLUMN_DATA_CHEGADA, dataChegada);
            values.put(ViagemEntry.COLUMN_DATA_PARTIDA, dataPartida);
            values.put(ViagemEntry.COLUMN_ID_USUARIO, idDousuario);

            Uri result = null;

            if (mCurrentViagemUri == null) {
                result = getContentResolver().insert(ViagemEntry.CONTENT_URI, values);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            if (uri != null) {

                Toast.makeText(NovaViagemActivity.this, getString(R.string
                                .viagem_salva),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(NovaViagemActivity.this, ListaViagemActivity
                        .class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            } else {
                Toast.makeText(NovaViagemActivity.this, getString(R.string
                                .erro_salvar_viagem),
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

}