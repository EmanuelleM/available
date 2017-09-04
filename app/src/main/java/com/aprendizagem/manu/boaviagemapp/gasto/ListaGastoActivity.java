package com.aprendizagem.manu.boaviagemapp.gasto;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.aprendizagem.manu.boaviagemapp.Constantes;
import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.adapter.GastoAdapter;
import com.aprendizagem.manu.boaviagemapp.database.Contract.GastoEntry;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ViagemEntry;
import com.aprendizagem.manu.boaviagemapp.database.DatabaseHelper;

public class ListaGastoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GASTO_LOADER = 0;

    GastoAdapter mCursorAdapter;
    RecyclerView recyclerViewGasto;
    DatabaseHelper helper = new DatabaseHelper(this);
    SQLiteDatabase db;
    String getIdViagem = String.valueOf(Constantes.getIdViagemSelecionada());
    String getIdUsuario = String.valueOf(Constantes.getIdDoUsuario());

    Toolbar listaGastoToolbar;
    TextView tituloPaginaToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_gasto);

        listaGastoToolbar = (Toolbar) findViewById(R.id.toolbar_lista_gasto);
        setSupportActionBar(listaGastoToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tituloPaginaToolbar = (TextView) findViewById(R.id.titulo_pagina);
        tituloPaginaToolbar.setText(Constantes.getNomeDestinoViagem());

        recyclerViewGasto = (RecyclerView) findViewById(R.id.recycler_view_gasto);
        recyclerViewGasto.setHasFixedSize(true);

        mCursorAdapter = new GastoAdapter(new GastoAdapter.ItemClickListenerAdapter() {
            @Override
            public void itemFoiClicado(Cursor cursor) {
                int id = cursor.getInt(cursor.getColumnIndex(GastoEntry._ID));
                opcoesParaCliqueDoItemGasto(id);
            }
        }, this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerViewGasto.setLayoutManager(mLayoutManager);
        mCursorAdapter.setHasStableIds(true);
        recyclerViewGasto.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(GASTO_LOADER, null, this);
    }

    private void opcoesParaCliqueDoItemGasto(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ListaGastoActivity.this);
        builder.setItems(R.array.opcoes_item_gasto, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        deletarGasto(position);
                        break;
                }
            }
        });
        AlertDialog dialog =
                builder.create();
        dialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                GastoEntry._ID,
                GastoEntry.COLUMN_DESCRICAO_GASTO,
                GastoEntry.COLUMN_VIAGEM_ID,
                GastoEntry.COLUMN_VALOR_GASTO,
                GastoEntry.COLUMN_DATA_GASTO,
                GastoEntry.COLUMN_METODO_PAGAMENTO,
                GastoEntry.COLUMN_ID_USUARIO};

        String selection = GastoEntry.COLUMN_VIAGEM_ID + " = " + getIdViagem + " AND "
                + GastoEntry.COLUMN_ID_USUARIO + " = '" + getIdUsuario + "'";

        return new CursorLoader(this,
                GastoEntry.CONTENT_URI,
                projection,
                selection,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void deletarGasto(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.confirmar_exclusao)
                .setTitle(R.string.excluir_gasto);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                novoGastoTotal();

                final int x = 1;
                Cursor cursor = mCursorAdapter.getCursor();
                cursor.moveToPosition(x);
                getContentResolver().delete(
                        Uri.withAppendedPath(GastoEntry.CONTENT_URI, String.valueOf(position)),
                        null, null);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private double getGastoTotal() {

        db = helper.getReadableDatabase();

        String[] projection = {
                ViagemEntry.COLUMN_GASTO_TOTAL
        };

        String selection = ViagemEntry._ID + " = '" + getIdViagem + "' AND " +
                ViagemEntry.COLUMN_ID_USUARIO + "= '" + getIdUsuario + "'";

        Cursor cursor = db.query(
                ViagemEntry.TABLE_NAME,
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
        Cursor cursor = mCursorAdapter.getCursor();

        String value = cursor.getString(cursor.getColumnIndex(GastoEntry.COLUMN_VALOR_GASTO)).replace(",", ".");

        double valorConvertido = Double.parseDouble(value);

        double antigoGastoTotal = getGastoTotal();

        cursor.moveToFirst();

        double novoValorGastoTotal = antigoGastoTotal - valorConvertido;

        db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put(ViagemEntry.COLUMN_GASTO_TOTAL, novoValorGastoTotal);

        String selection = ViagemEntry._ID + " = '" + getIdViagem + "'";

        db.update(
                ViagemEntry.TABLE_NAME,
                values,
                selection, null);

        db.close();

    }
}
