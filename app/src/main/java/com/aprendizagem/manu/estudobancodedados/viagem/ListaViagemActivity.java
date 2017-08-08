package com.aprendizagem.manu.estudobancodedados.viagem;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.adapter.ViagemCursorAdapter;
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;
import com.aprendizagem.manu.estudobancodedados.gasto.ListaGastoActivity;
import com.aprendizagem.manu.estudobancodedados.gasto.NovoGastoActivity;
import com.facebook.stetho.Stetho;

public class ListaViagemActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int VIAGEM_LOADER = 0;

    ViagemCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_viagem);
        Stetho.initializeWithDefaults(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_nova_viagem);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaViagemActivity.this, NovaViagemActivity.class);
                startActivity(intent);
            }
        });

        ListView viagemListView = (ListView) findViewById(R.id.list_view_viagem);

        mCursorAdapter = new ViagemCursorAdapter(this, null);
        viagemListView.setAdapter(mCursorAdapter);

        viagemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(ListaViagemActivity.this, NovoGastoActivity.class);

                Uri currentViagemUri = ContentUris.withAppendedId(ViagemEntry.CONTENT_URI, id);

                intent.setData(currentViagemUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(VIAGEM_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ViagemEntry._ID,
                ViagemEntry.COLUMN_DESTINO,
                ViagemEntry.COLUMN_RAZAO};

        return new CursorLoader(this,   // Parent activity context
                ViagemEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
