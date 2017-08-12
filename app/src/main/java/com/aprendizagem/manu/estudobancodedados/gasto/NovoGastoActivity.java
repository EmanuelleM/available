package com.aprendizagem.manu.estudobancodedados.gasto;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract.GastoEntry;
import com.aprendizagem.manu.estudobancodedados.viagem.ListaViagemActivity;

public class NovoGastoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_GASTO_LOADER = 0;

    private Uri mCurrentGastoUri;

    private EditText textDescricaoGasto;
    private EditText textValorGasto;
    private EditText textMetodoPagamento;
    private EditText textDataGasto;

    private Button salvarGasto;

    private boolean mGastomodificado = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mGastomodificado = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.novo_gasto);

        Intent intent = getIntent();
        mCurrentGastoUri = intent.getData();

        if (mCurrentGastoUri == null) {
            setTitle(getString(R.string.novo_gasto));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editando_gasto));

            getLoaderManager().initLoader(EXISTING_GASTO_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        textDescricaoGasto = (EditText) findViewById(R.id.edit_text_descricao_gasto);
        textValorGasto = (EditText) findViewById(R.id.edit_text_valor_gasto);
        textMetodoPagamento = (EditText) findViewById(R.id.edit_text_metodo_pagamento);
        textDataGasto = (EditText) findViewById(R.id.edit_text_data_gasto);

        salvarGasto = (Button) findViewById(R.id.button_salvar_gasto);
        salvarGasto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                salvarGasto();
            }
        });

        textDescricaoGasto.setOnTouchListener(mTouchListener);
        textValorGasto.setOnTouchListener(mTouchListener);
        textMetodoPagamento.setOnTouchListener(mTouchListener);
        textDataGasto.setOnTouchListener(mTouchListener);

    }

    private void salvarGasto() {
        Intent intent = getIntent();
        Bundle idViagem = intent.getExtras();
        String getIdViagem = String.valueOf(idViagem.get("id_viagem"));
        String descricaoGasto = textDescricaoGasto.getText().toString().trim();
        String valorGasto = textValorGasto.getText().toString().trim();
        String metodoPagamento = textMetodoPagamento.getText().toString().trim();
        String dataGasto = textDataGasto.getText().toString().trim();
        if ( mCurrentGastoUri== null && TextUtils.isEmpty(descricaoGasto) && TextUtils.isEmpty(valorGasto)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(GastoEntry.COLUMN_VIAGEM_ID, getIdViagem);
        values.put(GastoEntry.COLUMN_DESCRICAO_GASTO, descricaoGasto);
        values.put(GastoEntry.COLUMN_VALOR_GASTO, valorGasto);
        values.put(GastoEntry.COLUMN_DATA_GASTO, dataGasto);
        values.put(GastoEntry.COLUMN_METODO_PAGAMENTO, metodoPagamento);

        if (mCurrentGastoUri == null) {

            Uri newUri = getContentResolver().insert(GastoEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.falha_insercao),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.sucesso_insercao),
                        Toast.LENGTH_SHORT).show();

                intent = new Intent(NovoGastoActivity.this, ListaViagemActivity.class);
                startActivity(intent);
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentGastoUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.falha_update),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_sucesso),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                GastoEntry._ID,
                GastoEntry.COLUMN_DESCRICAO_GASTO,
                GastoEntry.COLUMN_VALOR_GASTO,
                GastoEntry.COLUMN_METODO_PAGAMENTO,
                GastoEntry.COLUMN_DATA_GASTO,
                GastoEntry.COLUMN_VIAGEM_ID
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentGastoUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of viagem attributes that we're interested in
            int descricaoGastoColumnIndex = cursor.getColumnIndex(GastoEntry.COLUMN_DESCRICAO_GASTO);
            int valorGastoColumnIndex = cursor.getColumnIndex(GastoEntry.COLUMN_VALOR_GASTO);
            int metodoPagamentoColumnIndex = cursor.getColumnIndex(GastoEntry.COLUMN_METODO_PAGAMENTO);
            int dataGastoColumnIndex = cursor.getColumnIndex(GastoEntry.COLUMN_DATA_GASTO);

            // Extract out the value from the Cursor for the given column index
            String descricaoGasto = cursor.getString(descricaoGastoColumnIndex);
            String valorGasto = cursor.getString(valorGastoColumnIndex);
            String metodoPagemento = cursor.getString(metodoPagamentoColumnIndex);
            String dataPagamento = cursor.getString(dataGastoColumnIndex);

            // Update the views on the screen with the values from the database
            textDescricaoGasto.setText(descricaoGasto);
            textValorGasto.setText(valorGasto);
            textMetodoPagamento.setText(metodoPagemento);
            textDataGasto.setText(dataPagamento);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        textDescricaoGasto.setText("");
        textValorGasto.setText("");
        textMetodoPagamento.setText("");
        textDataGasto.setText("");
    }
}
