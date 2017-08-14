package com.aprendizagem.manu.estudobancodedados.gasto;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.adapter.GastoCursorAdapter;
import com.aprendizagem.manu.estudobancodedados.database.Contract.GastoEntry;
import com.aprendizagem.manu.estudobancodedados.database.DatabaseHelper;

public class ListaGastoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GASTO_LOADER = 0;

    GastoCursorAdapter mCursorAdapter;
    Toolbar listaGastoToolbar;
    TextView destinoToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_gasto);

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        ListView gastoListView = (ListView) findViewById(R.id.list_view_gasto);

        View emptyView = findViewById(R.id.include_lista_gasto_vazia);
        gastoListView.setEmptyView(emptyView);

        listaGastoToolbar = (Toolbar) findViewById(R.id.toolbar_lista_gasto);
        destinoToolbar = (TextView) findViewById(R.id.text_view_destino);
        setSupportActionBar(listaGastoToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        String idViagem = String.valueOf(Constantes.getIdViagemSelecionada());
        destinoToolbar.setText(getDestino(db,idViagem));

        mCursorAdapter = new GastoCursorAdapter(this, null);
        gastoListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(GASTO_LOADER, null, this);
    }

    private	String getDestino(SQLiteDatabase db, String	id) {
        Cursor	cursor	= db.rawQuery(
                "SELECT destino FROM viagens WHERE _id = ?",
                new	String[]{id}
        );
        cursor.moveToFirst();
        String descricaoDesctino = cursor.getString(0);
        cursor.close();
        return	descricaoDesctino;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String getIdViagem = String.valueOf(Constantes.getIdViagemSelecionada());

        String[] projection = {
                GastoEntry._ID,
                GastoEntry.COLUMN_DESCRICAO_GASTO,
                GastoEntry.COLUMN_VIAGEM_ID};

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
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
