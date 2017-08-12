package com.aprendizagem.manu.estudobancodedados.viagem;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.adapter.ViagemCursorAdapter;
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;
import com.aprendizagem.manu.estudobancodedados.gasto.ListaGastoActivity;
import com.aprendizagem.manu.estudobancodedados.gasto.NovoGastoActivity;
//import com.facebook.stetho.Stetho;

public class ListaViagemActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int VIAGEM_LOADER = 0;

    ViagemCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_viagem);
//        Stetho.initializeWithDefaults(this);
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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, final long id) {
                final int posicaoViagem = position +1;
                AlertDialog.Builder builder = new AlertDialog.Builder(ListaViagemActivity.this);

                builder.setItems(R.array.opcoes_item_viagem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Intent intent;
                        switch (item) {
                            case 0:
                                intent = new Intent(ListaViagemActivity.this, ListaGastoActivity.class);
//                                intent.setData(posicaoViagem);
                                intent.putExtra("id_viagem", posicaoViagem);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(ListaViagemActivity.this, NovoGastoActivity.class);
                                intent.putExtra("id_viagem", posicaoViagem);
//                                intent.setData(currentViagemUri);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                AlertDialog dialog =
                        builder.create();
                dialog.show();
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
