package com.aprendizagem.manu.boaviagemapp.gasto;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aprendizagem.manu.boaviagemapp.Constantes;
import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.adapter.GastoAdapter;
import com.aprendizagem.manu.boaviagemapp.adapter.ItemClickListenerAdapter;
import com.aprendizagem.manu.boaviagemapp.database.Contract.GastoEntry;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ViagemEntry;
import com.aprendizagem.manu.boaviagemapp.database.DatabaseHelper;
import com.aprendizagem.manu.boaviagemapp.viagem.ListaViagemActivity;
import com.aprendizagem.manu.boaviagemapp.viagem.NovaViagemActivity;

public class ListaGastoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GASTO_LOADER = 0;

    private GastoAdapter mCursorAdapter;

    private RecyclerView mRecyclerViewGasto;
    private TextView mListaGastoVazia;

    private DatabaseHelper mHelper;
    private SQLiteDatabase mDb;

    private String getIdViagem;
    private String getIdUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_gasto);

        getIdViagem = String.valueOf(Constantes.getIdViagemSelecionada());
        getIdUsuario = String.valueOf(Constantes.getIdDoUsuario());
        mHelper = new DatabaseHelper(this);
        mListaGastoVazia = findViewById(R.id.text_view_lista_gasto_vazia);

        Toolbar listaGastoToolbar = findViewById(R.id.toolbar_lista_gasto);
        setSupportActionBar(listaGastoToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView tituloPaginaToolbar = findViewById(R.id.titulo_pagina);
        tituloPaginaToolbar.setText(Constantes.getNomeDestinoViagem());

        mRecyclerViewGasto = findViewById(R.id.recycler_view_gasto);
        mRecyclerViewGasto.setHasFixedSize(true);

        mCursorAdapter = new GastoAdapter(new ItemClickListenerAdapter() {
            @Override
            public void itemFoiClicado(Cursor cursor) {
                int id = cursor.getInt(cursor.getColumnIndex(GastoEntry._ID));
                opcoesParaCliqueDoItemGasto(id);
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerViewGasto.setLayoutManager(mLayoutManager);
        mCursorAdapter.setHasStableIds(true);
        mRecyclerViewGasto.setAdapter(mCursorAdapter);

        exibeFloatActionButton();

        getLoaderManager().initLoader(GASTO_LOADER, null, this);
    }

    private void exibeFloatActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab_novo_gasto);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaGastoActivity.this, NovoGastoActivity.class);
                Constantes.setIdDoUsuario(getIdUsuario);
                startActivity(intent);
            }
        });
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount()==0){
            mRecyclerViewGasto.setVisibility(View.GONE);
            mListaGastoVazia.setVisibility(View.VISIBLE);
        }else{
            mListaGastoVazia.setVisibility(View.GONE);
            mRecyclerViewGasto.setVisibility(View.VISIBLE);
            mCursorAdapter.setmCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void deletarGasto(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.confirmar_exclusao_gasto);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                novoGastoTotal();

                final int x = 1;
                Cursor cursor = mCursorAdapter.getmCursor();
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

        mDb = mHelper.getReadableDatabase();

        String[] projection = {
                ViagemEntry.COLUMN_GASTO_TOTAL
        };

        String selection = ViagemEntry._ID + " = '" + getIdViagem + "' AND " +
                ViagemEntry.COLUMN_ID_USUARIO + "= '" + getIdUsuario + "'";

        Cursor cursor = mDb.query(
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
        Cursor cursor = mCursorAdapter.getmCursor();

        String value = cursor.getString(cursor.getColumnIndex(GastoEntry.COLUMN_VALOR_GASTO)).replace(",", ".");

        double valorConvertido = Double.parseDouble(value);

        double antigoGastoTotal = getGastoTotal();

        cursor.moveToFirst();

        double novoValorGastoTotal = antigoGastoTotal - valorConvertido;

        mDb = mHelper.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put(ViagemEntry.COLUMN_GASTO_TOTAL, novoValorGastoTotal);

        String selection = ViagemEntry._ID + " = '" + getIdViagem + "'";

        mDb.update(
                ViagemEntry.TABLE_NAME,
                values,
                selection, null);

        mDb.close();

    }
}
