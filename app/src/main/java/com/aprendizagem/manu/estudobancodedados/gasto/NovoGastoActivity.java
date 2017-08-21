package com.aprendizagem.manu.estudobancodedados.gasto;

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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract;
import com.aprendizagem.manu.estudobancodedados.database.Contract.GastoEntry;
import com.aprendizagem.manu.estudobancodedados.database.DatabaseHelper;
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

    DatabaseHelper helper = new DatabaseHelper(this);

    String idViagem = String.valueOf(Constantes.getIdViagemSelecionada());

    Toolbar novoGastoToolbar;
    TextView nomeLocalViagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.novo_gasto);

        DatabaseHelper helper = new DatabaseHelper(this);
        final SQLiteDatabase db = helper.getReadableDatabase();

        novoGastoToolbar = (Toolbar) findViewById(R.id.toolbar_novo_gasto);
        nomeLocalViagem = (TextView) findViewById(R.id.text_view_viagem_gasto);
        setSupportActionBar(novoGastoToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        nomeLocalViagem.setText(getDestino(db, idViagem));

        Intent intent = getIntent();
        mCurrentGastoUri = intent.getData();

        if (mCurrentGastoUri == null) {
            setTitle(getString(R.string.novo_gasto));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editando_gasto));

            getLoaderManager().initLoader(EXISTING_GASTO_LOADER, null, this);
        }
        textDescricaoGasto = (EditText) findViewById(R.id.edit_text_descricao_gasto);
        textValorGasto = (EditText) findViewById(R.id.edit_text_valor_gasto);
        textMetodoPagamento = (EditText) findViewById(R.id.edit_text_metodo_pagamento);
        textDataGasto = (EditText) findViewById(R.id.edit_text_data_gasto);

        salvarGasto = (Button) findViewById(R.id.button_salvar_gasto);
        salvarGasto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                novoValorGastoTotal(db, idViagem);
                salvarGasto();
            }
        });
    }

    private String getDestino(SQLiteDatabase db, String id) {
        Cursor cursor = db.rawQuery(
                "SELECT destino FROM viagens WHERE _id = ?",
                new String[]{id}
        );
        cursor.moveToFirst();
        String descricaoDesctino = cursor.getString(0);
        cursor.close();
        return descricaoDesctino;
    }
    private double getGastoTotal(SQLiteDatabase db, String id) {

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

    private void novoValorGastoTotal(SQLiteDatabase db, String id) {

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

    private void salvarGasto() {

        Intent intent;
        String getIdViagem = String.valueOf(Constantes.getIdViagemSelecionada());
        String descricaoGasto = textDescricaoGasto.getText().toString().trim();

        String valorGasto = textValorGasto.getText().toString().trim();

        String metodoPagamento = textMetodoPagamento.getText().toString().trim();
        String dataGasto = textDataGasto.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(GastoEntry.COLUMN_VIAGEM_ID, getIdViagem);
        values.put(GastoEntry.COLUMN_DESCRICAO_GASTO, descricaoGasto);
        values.put(GastoEntry.COLUMN_VALOR_GASTO, valorGasto);
        values.put(GastoEntry.COLUMN_DATA_GASTO, dataGasto);
        values.put(GastoEntry.COLUMN_METODO_PAGAMENTO, metodoPagamento);


        Uri newUri = getContentResolver().insert(GastoEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, getString(R.string.erro_salvar_gasto),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.gasto_salvo),
                    Toast.LENGTH_SHORT).show();

            intent = new Intent(NovoGastoActivity.this, ListaViagemActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

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
        textDescricaoGasto.setText("");
        textValorGasto.setText("");
        textMetodoPagamento.setText("");
        textDataGasto.setText("");
    }
}
