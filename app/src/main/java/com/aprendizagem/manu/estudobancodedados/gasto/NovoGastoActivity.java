package com.aprendizagem.manu.estudobancodedados.gasto;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract;
import com.aprendizagem.manu.estudobancodedados.database.Contract.GastoEntry;
import com.aprendizagem.manu.estudobancodedados.database.DatabaseHelper;
import com.aprendizagem.manu.estudobancodedados.model.Viagem;
import com.aprendizagem.manu.estudobancodedados.viagem.ListaViagemActivity;

import java.util.Locale;

public class NovoGastoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_GASTO_LOADER = 0;

    private Uri mCurrentGastoUri;

    private EditText textDescricaoGasto;
    private EditText textValorGasto;
    private EditText textMetodoPagamento;
    private EditText textDataGasto;

    double valorTotalGasto;

    private Button salvarGasto;

    private boolean mGastomodificado = false;

    String idViagem = String.valueOf(Constantes.getIdViagemSelecionada());

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
        DatabaseHelper helper = new DatabaseHelper(this);
        final SQLiteDatabase db = helper.getReadableDatabase();

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
                novoGastoTotal(db, idViagem);
                salvarGasto();
            }
        });

    }

    private double getGastoTotal(SQLiteDatabase db, String id) {
        Cursor cursor = db.rawQuery(
                "SELECT gasto_total FROM viagens WHERE _id = ?",
                new String[]{id}
        );
        cursor.moveToFirst();
        double gastoTotal = cursor.getDouble(0);
        cursor.close();
        return gastoTotal;
    }

    private double novoGastoTotal(SQLiteDatabase db, String id) {

        double antigoGastoTotal = getGastoTotal(db, idViagem);

        double valorAdicionadoUsuario = 0;
        String value = textValorGasto.getText().toString().trim();
        if (!value.isEmpty())
            try {
                valorAdicionadoUsuario = Double.parseDouble(value);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        double novoValorGastoTotal = valorAdicionadoUsuario + antigoGastoTotal;
        Cursor cursor = db.rawQuery("Update viagens SET gasto_total= ? where _id = ? ",
                new String[]{String.valueOf(novoValorGastoTotal), id});

        cursor.moveToFirst();
        cursor.close();

        return novoValorGastoTotal;
    }

    private void salvarGasto() {

        Intent intent;
        String getIdViagem = String.valueOf(Constantes.getIdViagemSelecionada());
        String descricaoGasto = textDescricaoGasto.getText().toString().trim();


        String valorGasto = textValorGasto.getText().toString().trim();
        String valorGastoConvertido = valorGasto;
        valorGastoConvertido.replaceAll(",", ".");

        String metodoPagamento = textMetodoPagamento.getText().toString().trim();
        String dataGasto = textDataGasto.getText().toString().trim();

        if (mCurrentGastoUri == null && TextUtils.isEmpty(descricaoGasto) && TextUtils.isEmpty(valorGasto)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(GastoEntry.COLUMN_VIAGEM_ID, getIdViagem);
        values.put(GastoEntry.COLUMN_DESCRICAO_GASTO, descricaoGasto);
        values.put(GastoEntry.COLUMN_VALOR_GASTO, valorGastoConvertido);
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

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        textDescricaoGasto.setText("");
        textValorGasto.setText("");
        textMetodoPagamento.setText("");
        textDataGasto.setText("");
    }
}
