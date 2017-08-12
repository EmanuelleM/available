package com.aprendizagem.manu.estudobancodedados.gasto;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.adapter.GastoCursorAdapter;
import com.aprendizagem.manu.estudobancodedados.database.Contract.GastoEntry;

public class ListaGastoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GASTO_LOADER = 0;

    GastoCursorAdapter mCursorAdapter;
    Intent intent = getIntent();
//    Bundle idViagem = intent.getExtras();
//    String getIdViagem = String.valueOf(idViagem.get("id_viagem"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_gasto);

        ListView petListView = (ListView) findViewById(R.id.list_view_gasto);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
//        View emptyView = findViewById(R.id.empty_view);
//        petListView.setEmptyView(emptyView);

        mCursorAdapter = new GastoCursorAdapter(this, null);
        petListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(GASTO_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Intent intent = getIntent();
    Bundle idViagem = intent.getExtras();
    String getIdViagem = String.valueOf(idViagem.get("id_viagem"));
//    String getIdViagem = "3";

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                GastoEntry._ID,
                GastoEntry.COLUMN_DESCRICAO_GASTO,
                GastoEntry.COLUMN_VIAGEM_ID};

        String sortOrder =
                GastoEntry.COLUMN_VIAGEM_ID +
                        " DESC";

        String selection = GastoEntry.COLUMN_VIAGEM_ID +
                " = " + getIdViagem;

    return new CursorLoader(this,
            GastoEntry.CONTENT_URI, //tabela
            projection, //campos retornados
            selection, //clausula where e condicao
            null, //se eu tivesse mais de uma condicao poderia colocar aqui?
            null); //ordem de exibicao da lista
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
