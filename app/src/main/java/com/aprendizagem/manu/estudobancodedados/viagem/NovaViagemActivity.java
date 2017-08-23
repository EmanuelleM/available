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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;

public class NovaViagemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    private static final int EXISTING_VIAGEM_LOADER = 0;

    private Uri mCurrentViagemUri;

    private EditText editDestino;
    private EditText editLocalHospedagem;
    private Button salvarViagem;

    private Button buttonDataPartida;
    private Button buttonDataChegada;
    private String dataChegada;
    private String dataPartida;

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

        salvarViagem = (Button) findViewById(R.id.button_salvar_viagem);
        salvarViagem.setOnClickListener(this);

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_salvar_viagem:
                salvarViagem();
                break;
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

        if (mCurrentViagemUri == null && TextUtils.isEmpty(localHospedagem) && TextUtils.isEmpty(destino)
                && mRazao == ViagemEntry.RAZAO_DESCONHECIDA) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ViagemEntry.COLUMN_DESTINO, destino);
        values.put(ViagemEntry.COLUMN_LOCAL_ACOMODACAO, localHospedagem);
        values.put(ViagemEntry.COLUMN_RAZAO, razaoViagem);
        values.put(ViagemEntry.COLUMN_DATA_CHEGADA, dataChegada);
        values.put(ViagemEntry.COLUMN_DATA_PARTIDA, dataPartida);
        values.put(ViagemEntry.COLUMN_ID_USUARIO, idDousuario);


        Uri newUri = getContentResolver().insert(ViagemEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, getString(R.string.erro_salvar_viagem),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.viagem_salva),
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NovaViagemActivity.this, ListaViagemActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        editDestino.setText("");
        editLocalHospedagem.setText("");
    }
}