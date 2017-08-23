package com.aprendizagem.manu.estudobancodedados.gasto;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_gasto);

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        ListView gastoListView = (ListView) findViewById(R.id.list_view_gasto);

        View emptyView = findViewById(R.id.include_lista_gasto_vazia);
        gastoListView.setEmptyView(emptyView);

        mCursorAdapter = new GastoCursorAdapter(this, null);
        gastoListView.setAdapter(mCursorAdapter);

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
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
