package com.aprendizagem.manu.boaviagemapp.viagem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
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
import com.aprendizagem.manu.boaviagemapp.modelo.Viagem;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.Calendar;

public class NovaViagemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    private static final int EXISTING_VIAGEM_LOADER = 0;

    private Uri mCurrentViagemUri;

    private EditText mEditTextDestino;
    private EditText mEditLocalHospedagem;
    private Button mButtonDataPartida;
    private Button mButtonDataChegada;
    private RadioButton mOptionRazaoLazer;
    private RadioButton mOptionRazaoNegocios;
    private String mDataChegada;
    private String mDataPartida;
    private String mDestino;
    private String mLocalHospedagem;
    private Calendar mCalendar = Calendar.getInstance();
    private GoogleApiClient mClient;
    private int mRazao = ViagemEntry.RAZAO_DESCONHECIDA;
    private static final int PLACE_PICKER_REQUEST = 1000;

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

        mEditTextDestino = findViewById(R.id.edit_text_destino);
        mEditLocalHospedagem = findViewById(R.id.edit_text_local_hospedagem);

        mButtonDataChegada = findViewById(R.id.button_pega_data_chegada);
        mButtonDataPartida = findViewById(R.id.button_pega_data_saida);

        mButtonDataChegada.setOnClickListener(this);
        mButtonDataPartida.setOnClickListener(this);

        mOptionRazaoLazer = findViewById(R.id.radio_lazer);
        mOptionRazaoNegocios = findViewById(R.id.radio_negocios);

        ImageButton imageButtonPegaLocalizacao = findViewById(R.id.image_button_pega_localizacao);
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
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
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
                mEditLocalHospedagem.setText(place.getName());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private Dialog pegaDataChegada() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                mDataChegada = dayOfMonth + "/" + monthOfYear + "/" + year;
                mButtonDataChegada.setText(mDataChegada);
            }
        };
        return new DatePickerDialog(this,
                dateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)) {
        };
    }

    private Dialog pegaDataPartida() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                mDataPartida = dayOfMonth + "/" + monthOfYear + "/" + year;
                mButtonDataPartida.setText(mDataPartida);
            }
        };
        return new DatePickerDialog(this,
                dateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)) {
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_salvar_geral, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.salvar:

                if (mEditTextDestino.getText().toString().trim().isEmpty()) {
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

        mDestino = mEditTextDestino.getText().toString().trim();
        mLocalHospedagem = mEditLocalHospedagem.getText().toString().trim();
        String idDousuario = Constantes.getIdDoUsuario();
        int razaoViagem = mRazao;

        Viagem viagem = new Viagem();

        viagem.setDestino(mDestino);
        viagem.setLocalHospedagem(mLocalHospedagem);
        viagem.setRazaoViagem(mRazao);
        viagem.setDataChegada(mDataChegada);
        viagem.setDataPartida(mDataPartida);
        viagem.setIdDoUsuario(Constantes.getIdDoUsuario());



        if (mCurrentViagemUri == null) {

            new TaskSalvaViagem(this, mDestino, mLocalHospedagem,
                    razaoViagem,
                    mDataChegada, mDataPartida,
                    idDousuario).execute();
        } else {

            ContentValues values = new ContentValues();
            values.put(ViagemEntry.COLUMN_DESTINO, mDestino);
            values.put(ViagemEntry.COLUMN_LOCAL_ACOMODACAO, mLocalHospedagem);
            values.put(ViagemEntry.COLUMN_RAZAO, razaoViagem);
            values.put(ViagemEntry.COLUMN_DATA_CHEGADA, mDataChegada);
            values.put(ViagemEntry.COLUMN_DATA_PARTIDA, mDataPartida);
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

            mDestino = cursor.getString(destinoColumnIndex);
            mLocalHospedagem = cursor.getString(localHospedagemColumnIndex);
            mDataChegada = cursor.getString(dataChegadaViagemColumnIndex);
            mDataPartida = cursor.getString(dataPartidaViagemColumnIndex);
            int razaoViagem = cursor.getInt(razaoViagemColumnIndex);

            if (razaoViagem == 1) {

                mOptionRazaoLazer.setChecked(true);

            } else if (razaoViagem == 2) {

                mOptionRazaoNegocios.setChecked(true);
            }

            mEditTextDestino.setText(mDestino);
            mEditLocalHospedagem.setText(mLocalHospedagem);

            if (mDataChegada == null) {
                mButtonDataChegada.setText(R.string.data_da_chegada);
            } else {
                mButtonDataChegada.setText(mDataChegada);
            }

            if (mDataPartida == null) {
                mButtonDataPartida.setText(R.string.data_da_partida);
            } else {
                mButtonDataPartida.setText(mDataPartida);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}