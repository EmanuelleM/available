package com.aprendizagem.manu.boaviagemapp.gasto;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.aprendizagem.manu.boaviagemapp.Constantes;
import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.database.Contract;
import com.aprendizagem.manu.boaviagemapp.database.Contract.GastoEntry;
import com.aprendizagem.manu.boaviagemapp.database.DatabaseHelper;
import com.aprendizagem.manu.boaviagemapp.viagem.ListaViagemActivity;

public class NovoGastoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    DatabaseHelper helper = new DatabaseHelper(this);
    String idViagem = String.valueOf(Constantes.getIdViagemSelecionada());
    String idUsuario = String.valueOf(Constantes.getIdDoUsuario());

    private Uri mCurrentGastoUri;

    private EditText editTextDescricaoGasto;
    private EditText editTextValorGasto;
    private EditText editTextMetodoPagamento;

    private Button buttonDataChegada;

    private String dataGasto;

    private Calendar calendar = Calendar.getInstance();

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.novo_gasto);

        Intent intent = getIntent();
        mCurrentGastoUri = intent.getData();
        setTitle(getString(R.string.novo_gasto));
        invalidateOptionsMenu();

        editTextDescricaoGasto = (EditText) findViewById(R.id.edit_text_descricao_gasto);
        editTextValorGasto = (EditText) findViewById(R.id.edit_text_valor_gasto);
        editTextMetodoPagamento = (EditText) findViewById(R.id.edit_text_metodo_pagamento);

        buttonDataChegada = (Button) findViewById(R.id.button_pega_data_gasto);
        buttonDataChegada.setOnClickListener(this);

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
                if (editTextDescricaoGasto.getText().toString().trim().isEmpty() ||
                        editTextValorGasto.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "É necessário informar a descrição e o valor do gasto",
                            Toast
                                    .LENGTH_SHORT)
                            .show();
                } else {
                    salvarGasto();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private double getGastoTotal(String idUsuario) {

        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                Contract.ViagemEntry.COLUMN_GASTO_TOTAL
        };

        String selection = Contract.ViagemEntry._ID + " = '" + idViagem + "' AND " +
                Contract.ViagemEntry.COLUMN_ID_USUARIO + "= '" + idUsuario + "'";

        Cursor cursor = db.query(
                Contract.ViagemEntry.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null
        );
        double gastoTotal = 0;

        if (cursor.moveToFirst()) {
            gastoTotal = cursor.getDouble(0);
            cursor.close();
        }
        return gastoTotal;
    }

    private void novoGastoTotal() {

        double antigoGastoTotal = getGastoTotal(idUsuario);
        double valorAdicionadoUsuario = 0;
        String value = editTextValorGasto.getText().toString().trim().replace(",", ".");

        if (!value.isEmpty())
            try {
                valorAdicionadoUsuario = Double.parseDouble(value);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        double novoValorGastoTotal = valorAdicionadoUsuario + antigoGastoTotal;

        SQLiteDatabase db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(Contract.ViagemEntry.COLUMN_GASTO_TOTAL, novoValorGastoTotal);

        String selection = Contract.ViagemEntry._ID + " = '" + idViagem + "' AND " +
                Contract.ViagemEntry.COLUMN_ID_USUARIO + "= '" + idUsuario + "'";

        db.update(
                Contract.ViagemEntry.TABLE_NAME,
                values,
                selection, null);

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
                GastoEntry.COLUMN_VIAGEM_ID,
                GastoEntry.COLUMN_ID_USUARIO
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

    private void salvarGasto() {
        novoGastoTotal();

        final String descricaoGasto = editTextDescricaoGasto.getText().toString().trim();
        final String valorGasto = editTextValorGasto.getText().toString().trim();
        final String metodoPagamento = editTextMetodoPagamento.getText().toString().trim();

        if (mCurrentGastoUri == null && TextUtils.isEmpty(descricaoGasto) && TextUtils.isEmpty(valorGasto)) {
            return;
        }

        new TaskSalvaGastos(descricaoGasto, valorGasto, metodoPagamento).execute();
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

    private class TaskSalvaGastos extends AsyncTask<Void, Void, Uri> {

        private final String descricaoGasto;
        private final String valorGasto;
        private final String metodoPagamento;

        TaskSalvaGastos(String descricaoGasto, String valorGasto, String metodoPagamento) {
            this.descricaoGasto = descricaoGasto;
            this.valorGasto = valorGasto;
            this.metodoPagamento = metodoPagamento;
        }

        @Override
        protected Uri doInBackground(Void... params) {

            ContentValues values = new ContentValues();

            values.put(GastoEntry.COLUMN_VIAGEM_ID, idViagem);
            values.put(GastoEntry.COLUMN_DESCRICAO_GASTO, descricaoGasto);
            values.put(GastoEntry.COLUMN_VALOR_GASTO, valorGasto);
            values.put(GastoEntry.COLUMN_DATA_GASTO, dataGasto);
            values.put(GastoEntry.COLUMN_METODO_PAGAMENTO, metodoPagamento);
            values.put(GastoEntry.COLUMN_ID_USUARIO, idUsuario);

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
    }
}