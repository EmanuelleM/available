package com.aprendizagem.manu.estudobancodedados.viagem;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.adapter.ViagemCursorAdapter;
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;
import com.aprendizagem.manu.estudobancodedados.database.DatabaseHelper;
import com.aprendizagem.manu.estudobancodedados.gasto.ListaGastoActivity;
import com.aprendizagem.manu.estudobancodedados.gasto.NovoGastoActivity;
import com.aprendizagem.manu.estudobancodedados.login.Login;
import com.facebook.stetho.Stetho;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ListaViagemActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.OnConnectionFailedListener {

    private static final int VIAGEM_LOADER = 0;

    ViagemCursorAdapter mCursorAdapter;
    Toolbar listaGastoToolbar;

    RecyclerView recyclerViewViagem;
    String idUsuarioVindoDoFirebase;
    private Menu menu;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initializeWithDefaults(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null) {

            setContentView(R.layout.lista_viagem);

            listaGastoToolbar = (Toolbar) findViewById(R.id.toolbar_lista_viagem);
            setSupportActionBar(listaGastoToolbar);

            listaGastoToolbar.collapseActionView();

            idUsuarioVindoDoFirebase = mFirebaseUser.getUid();

            exibeFloatActionButton();
            exibeMenu();

            exibeListaDeViagens();

            getLoaderManager().initLoader(VIAGEM_LOADER, null, this);

        } else {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    private void exibeListaDeViagens() {
        recyclerViewViagem = (RecyclerView) findViewById(R.id.recycler_view_viagem);
        recyclerViewViagem.setHasFixedSize(true);

        mCursorAdapter = new ViagemCursorAdapter(new ViagemCursorAdapter.ItemClickListenerAdapter() {
            @Override
            public void itemFoiClicado(Cursor cursor) {
                long id = cursor.getLong(cursor.getColumnIndex(ViagemEntry._ID));
                opcoesParaCliqueDaViagem((int) id);
            }
        }, this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mCursorAdapter.notifyDataSetChanged();
        recyclerViewViagem.setLayoutManager(mLayoutManager);
        mCursorAdapter.setHasStableIds(true);
        recyclerViewViagem.setAdapter(mCursorAdapter);
    }

    private void exibeFloatActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_nova_viagem);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaViagemActivity.this, NovaViagemActivity.class);
                Constantes.setIdDoUsuario(idUsuarioVindoDoFirebase);
                startActivity(intent);
            }
        });
    }

    private void exibeMenu() {
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_lista_viagem);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption(R.id.sair);
                } else if (isShow) {
                    isShow = false;
                    hideOption(R.id.sair);
                    exibeFloatActionButton();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.lista_viagem_menu, menu);
        hideOption(R.id.sair);
        return true;
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sair:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                startActivity(new Intent(this, Login.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ViagemEntry._ID,
                ViagemEntry.COLUMN_DESTINO,
                ViagemEntry.COLUMN_RAZAO,
                ViagemEntry.COLUMN_DATA_CHEGADA,
                ViagemEntry.COLUMN_DATA_PARTIDA,
                ViagemEntry.COLUMN_LOCAL_ACOMODACAO,
                ViagemEntry.COLUMN_GASTO_TOTAL,
                ViagemEntry.COLUMN_ID_USUARIO
        };

        String selection = ViagemEntry.COLUMN_ID_USUARIO +
                " = '" + idUsuarioVindoDoFirebase + "'";

        return new CursorLoader(this,
                ViagemEntry.CONTENT_URI,
                projection,
                selection,
                null,
                null);
    }

    private void opcoesParaCliqueDaViagem(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ListaViagemActivity.this);
        builder.setItems(R.array.opcoes_item_viagem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent intent;
                switch (item) {
                    case 0:
                        intent = new Intent(ListaViagemActivity.this, ListaGastoActivity.class);
                        Constantes.setIdViagemSelecionada(position);
                        Constantes.setIdDoUsuario(idUsuarioVindoDoFirebase);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(ListaViagemActivity.this, NovoGastoActivity.class);
                        Constantes.setIdViagemSelecionada(position);
                        Constantes.setIdDoUsuario(idUsuarioVindoDoFirebase);
                        startActivity(intent);
                        break;
                    case 2:
                        deletarViagem(position);
                        break;
                }
            }
        });
        AlertDialog dialog =
                builder.create();
        dialog.show();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.setCursor(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, getResources().getString(R.string.falha_de_conexao), Toast.LENGTH_SHORT).show();

    }

    DatabaseHelper helper = new DatabaseHelper(this);

    private void deletarViagem(final int position) {
        Log.d("entrou no  ", " deletarviagem");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.confirmar_exclusao)
                .setTitle(R.string.excluir_viagem);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SQLiteDatabase db = helper.getReadableDatabase();

                String selection = "_id = ?";
                String[] selectionArgs = {"" + position};

                db.delete(ViagemEntry.TABLE_NAME, selection, selectionArgs);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

