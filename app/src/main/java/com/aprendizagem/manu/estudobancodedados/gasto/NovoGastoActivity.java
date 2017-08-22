package com.aprendizagem.manu.estudobancodedados.gasto;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract;
import com.aprendizagem.manu.estudobancodedados.database.Contract.GastoEntry;
import com.aprendizagem.manu.estudobancodedados.database.DatabaseHelper;
import com.aprendizagem.manu.estudobancodedados.viagem.ListaViagemActivity;

public class NovoGastoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    DatabaseHelper helper = new DatabaseHelper(this);
    String idViagem = String.valueOf(Constantes.getIdViagemSelecionada());

    private Uri mCurrentGastoUri;

    private EditText textDescricaoGasto;
    private EditText textValorGasto;
    private EditText textMetodoPagamento;

    private Button buttonDataChegada;
    private Button salvarGasto;

    private String dataGasto;


    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.novo_gasto);
        DatabaseHelper helper = new DatabaseHelper(this);
        final SQLiteDatabase db = helper.getReadableDatabase();

        Intent intent = getIntent();
        mCurrentGastoUri = intent.getData();
        setTitle(getString(R.string.novo_gasto));
        invalidateOptionsMenu();

        // Find all relevant views that we will need to read user input from
        textDescricaoGasto = (EditText) findViewById(R.id.edit_text_descricao_gasto);
        textValorGasto = (EditText) findViewById(R.id.edit_text_valor_gasto);
        textMetodoPagamento = (EditText) findViewById(R.id.edit_text_metodo_pagamento);

        buttonDataChegada = (Button) findViewById(R.id.button_pega_data_gasto);
        buttonDataChegada.setOnClickListener(this);

        salvarGasto = (Button) findViewById(R.id.button_salvar_gasto);

        salvarGasto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                novoGastoTotal(db, idViagem);

                final String getIdViagem = String.valueOf(Constantes.getIdViagemSelecionada());
                final String descricaoGasto = textDescricaoGasto.getText().toString().trim();

                final String valorGasto = textValorGasto.getText().toString().trim();

                final String metodoPagamento = textMetodoPagamento.getText().toString().trim();

                if (mCurrentGastoUri == null && TextUtils.isEmpty(descricaoGasto) && TextUtils.isEmpty(valorGasto)) {
                    return;
                }

                new AsyncTask<Void, Void, Uri>() {

                    @Override
                    protected Uri doInBackground(Void... params) {

                        ContentValues values = new ContentValues();

                        values.put(GastoEntry.COLUMN_VIAGEM_ID, getIdViagem);
                        values.put(GastoEntry.COLUMN_DESCRICAO_GASTO, descricaoGasto);
                        values.put(GastoEntry.COLUMN_VALOR_GASTO, valorGasto);
                        values.put(GastoEntry.COLUMN_DATA_GASTO, dataGasto);
                        values.put(GastoEntry.COLUMN_METODO_PAGAMENTO, metodoPagamento);

                        return getContentResolver().insert(GastoEntry.CONTENT_URI, values);

                    }

                    @Override
                    protected void onPostExecute(Uri uri) {
                        super.onPostExecute(uri);

                        if (uri != null) {

                            Toast.makeText(NovoGastoActivity.this, getString(R.string
                                            .gasto_salvo),
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(NovoGastoActivity.this, ListaViagemActivity
                                    .class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        } else {
                            Toast.makeText(NovoGastoActivity.this, getString(R.string
                                            .erro_salvar_gasto),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                }.execute();
            }
        });
    }

    private Dialog pegaDataGasto() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                dataGasto = dayOfMonth + "/" + monthOfYear + "/" + year;
                buttonDataChegada.setText(dataGasto);
            }
        };
        return new DatePickerDialog(this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)) {
        };
    }

    private double getGastoTotal(SQLiteDatabase db, String id) {

        db = helper.getReadableDatabase();

        String[] projection = {
                Contract.ViagemEntry.COLUMN_GASTO_TOTAL
        };

        String selection = Contract.ViagemEntry._ID + " = ?";
        String[] selectionArgs = {id};

        Cursor cursor = db.query(
                Contract.ViagemEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        double gastoTotal = cursor.getDouble(0);
        cursor.close();
        return gastoTotal;

    }

    private void novoGastoTotal(SQLiteDatabase db, String id) {

        double antigoGastoTotal = getGastoTotal(db, idViagem);
        double valorAdicionadoUsuario = 0;
        String value = textValorGasto.getText().toString().trim().replace(",", ".");

        if (!value.isEmpty())
            try {
                valorAdicionadoUsuario = Double.parseDouble(value);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        double novoValorGastoTotal = valorAdicionadoUsuario + antigoGastoTotal;

        db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(Contract.ViagemEntry.COLUMN_GASTO_TOTAL, novoValorGastoTotal);

        String selection = Contract.ViagemEntry._ID + " LIKE ?";
        String[] selectionArgs = {id};

        db.update(
                Contract.ViagemEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        db.close();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                GastoEntry._ID,
                GastoEntry.COLUMN_DESCRICAO_GASTO,
                GastoEntry.COLUMN_VALOR_GASTO,
                GastoEntry.COLUMN_METODO_PAGAMENTO,
                GastoEntry.COLUMN_DATA_GASTO,
                GastoEntry.COLUMN_VIAGEM_ID
        };

        return new CursorLoader(this,
                mCurrentGastoUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_pega_data_gasto:
                pegaDataGasto().show();
                break;
        }
    }
}