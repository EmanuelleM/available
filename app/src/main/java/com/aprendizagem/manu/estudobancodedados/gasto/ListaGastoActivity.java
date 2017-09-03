package com.aprendizagem.manu.estudobancodedados.gasto;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.adapter.GastoCursorAdapter;
import com.aprendizagem.manu.estudobancodedados.database.Contract.GastoEntry;
import com.aprendizagem.manu.estudobancodedados.database.DatabaseHelper;

public class ListaGastoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GASTO_LOADER = 0;

    GastoCursorAdapter mCursorAdapter;
    RecyclerView recyclerViewGasto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_gasto);

        DatabaseHelper helper = new DatabaseHelper(this);

        recyclerViewGasto = (RecyclerView) findViewById(R.id.recycler_view_gasto);
        recyclerViewGasto.setHasFixedSize(true);

        mCursorAdapter = new GastoCursorAdapter(new GastoCursorAdapter.ItemClickListenerAdapter() {
            @Override
            public void itemFoiClicado(Cursor cursor) {
//                long id = cursor.getLong(cursor.getColumnIndex(Contract.ViagemEntry._ID));
//                opcoesParaCliqueDaViagem((int) id);
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String getIdViagem = String.valueOf(Constantes.getIdViagemSelecionada());
        String getIdUsuario = String.valueOf(Constantes.getIdDoUsuario());

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

        Log.d("query", "" + selection);

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

}
