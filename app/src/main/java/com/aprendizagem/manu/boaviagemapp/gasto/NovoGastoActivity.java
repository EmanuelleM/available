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
import android.net.Uri;
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

import java.util.Calendar;

public class NovoGastoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private DatabaseHelper mHelper;
    private final String idViagem = String.valueOf(Constantes.getIdViagemSelecionada());
    private final String idUsuario = String.valueOf(Constantes.getIdDoUsuario());
    private Uri mCurrentGastoUri;
    private EditText mEditTextDescricaoGasto;
    private EditText mEditTextValorGasto;
    private EditText mEditTextMetodoPagamento;
    private Button mButtonDataChegada;
    private String mDataGasto;
    private final Calendar mCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.novo_gasto);

        Intent intent = getIntent();
        mCurrentGastoUri = intent.getData();
        setTitle(getString(R.string.novo_gasto));
        invalidateOptionsMenu();

        mHelper = new DatabaseHelper(this);

        mEditTextDescricaoGasto = findViewById(R.id.edit_text_descricao_gasto);
        mEditTextValorGasto = findViewById(R.id.edit_text_valor_gasto);
        mEditTextMetodoPagamento = findViewById(R.id.edit_text_metodo_pagamento);

        mButtonDataChegada = findViewById(R.id.button_pega_data_gasto);
        mButtonDataChegada.setOnClickListener(this);

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
                if (mEditTextDescricaoGasto.getText().toString().isEmpty() ||
                        mEditTextValorGasto.getText().toString().isEmpty()) {
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

        SQLiteDatabase db = mHelper.getReadableDatabase();

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
        String value = mEditTextValorGasto.getText().toString().replace(",", ".");

        if (!value.isEmpty())
            try {
                valorAdicionadoUsuario = Double.parseDouble(value);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        double novoValorGastoTotal = valorAdicionadoUsuario + antigoGastoTotal;

        SQLiteDatabase db = mHelper.getReadableDatabase();

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

        final String descricaoGasto = mEditTextDescricaoGasto.getText().toString();
        final String valorGasto = mEditTextValorGasto.getText().toString();
        final String metodoPagamento = mEditTextMetodoPagamento.getText().toString();

        if (mCurrentGastoUri == null && TextUtils.isEmpty(descricaoGasto) && TextUtils.isEmpty(valorGasto)) {
            return;
        }

        new TaskSalvaGasto(this, idViagem, descricaoGasto, valorGasto, mDataGasto, metodoPagamento,
                idUsuario)
                .execute();
    }

    private Dialog pegaDataGasto() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                mDataGasto = dayOfMonth + "/" + monthOfYear + "/" + year;
                mButtonDataChegada.setText(mDataGasto);
            }
        };
        return new DatePickerDialog(this,
                dateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)) {
        };
    }
}