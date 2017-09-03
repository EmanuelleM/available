package com.aprendizagem.manu.estudobancodedados.viagem;

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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract;
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;
import com.aprendizagem.manu.estudobancodedados.gasto.NovoGastoActivity;
import com.aprendizagem.manu.estudobancodedados.modelo.Viagem;

import java.util.Locale;

public class NovaViagemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    private static final int EXISTING_VIAGEM_LOADER = 0;

    Menu menu;

    private Uri mCurrentViagemUri;

    private EditText editDestino;
    private EditText editLocalHospedagem;
    private Button buttonDataPartida;
    private Button buttonDataChegada;
    private String dataChegada;
    private String dataPartida;

    private Viagem viagem;

    private Calendar calendar = Calendar.getInstance();

    private int mRazao = ViagemEntry.RAZAO_DESCONHECIDA;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    };

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

        editDestino = (EditText) findViewById(R.id.edit_text_destino);
        editLocalHospedagem = (EditText) findViewById(R.id.edit_text_local_hospedagem);

        editDestino.setOnTouchListener(mTouchListener);
        editLocalHospedagem.setOnTouchListener(mTouchListener);

        buttonDataChegada = (Button) findViewById(R.id.button_pega_data_chegada);
        buttonDataPartida = (Button) findViewById(R.id.button_pega_data_saida);

        buttonDataChegada.setOnClickListener(this);
        buttonDataPartida.setOnClickListener(this);
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
                salvarViagem();
                startActivity(new Intent(this, ListaViagemActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_pega_data_chegada:
                pegaDataChegada().show();
                break;
            case R.id.button_pega_data_saida:
                pegaDataPartida().show();

        }
    }

    private void salvarViagem() {

        String destino = editDestino.getText().toString().trim();
        String localHospedagem = editLocalHospedagem.getText().toString().trim();
        String idDousuario = Constantes.getIdDoUsuario();
        int razaoViagem = mRazao;

        new TaskSalvaViagem(destino, localHospedagem, razaoViagem, dataChegada, dataPartida, idDousuario).execute();
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

            final int destinoColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DESTINO);
            int razaoViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_RAZAO);
            int gastoViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_GASTO_TOTAL);
            int dataChegadaViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DATA_CHEGADA);
            int dataPartidaViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DATA_PARTIDA);

            final String destino = cursor.getString(destinoColumnIndex);
            double gastoViagem = cursor.getDouble(gastoViagemColumnIndex);
            String dataChegada = cursor.getString(dataChegadaViagemColumnIndex);
            String dataPartida = cursor.getString(dataPartidaViagemColumnIndex);
            String valorFormatado = String.format(Locale.getDefault(), "%.2f", gastoViagem);

            editDestino.setText(destino);
            editLocalHospedagem.setText(valorFormatado.replace(".", ","));
            buttonDataChegada.setText(dataChegada);
            buttonDataPartida.setText(dataPartida);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        editDestino.setText("");
        editLocalHospedagem.setText("");
    }

    private class TaskSalvaViagem extends AsyncTask<Void, Void, Uri> {

        private final String destino;
        private final String localHospedagem;
        private final int razaoViagem;
        private final String dataChegada;
        private final String datapartida;
        private final String idDousuario;

        TaskSalvaViagem(String destino, String localHospedagem, int razaoViagem, String dataChegada, String datapartida, String idDousuario) {
            this.destino = destino;
            this.localHospedagem = localHospedagem;
            this.razaoViagem = razaoViagem;
            this.dataChegada = dataChegada;
            this.datapartida = datapartida;
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

            return getContentResolver().insert(Contract.ViagemEntry.CONTENT_URI, values);

        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            if (uri != null) {

                Toast.makeText(NovaViagemActivity.this, getString(R.string
                                .gasto_salvo),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(NovaViagemActivity.this, ListaViagemActivity
                        .class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            } else {
                Toast.makeText(NovaViagemActivity.this, getString(R.string
                                .erro_salvar_gasto),
                        Toast.LENGTH_SHORT).show();

            }
        }
    }
}