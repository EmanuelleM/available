package com.aprendizagem.manu.boaviagemapp.viagem;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aprendizagem.manu.boaviagemapp.Constantes;
import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.adapter.ViagemAdapter;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ViagemEntry;
import com.aprendizagem.manu.boaviagemapp.gasto.ListaGastoActivity;
import com.aprendizagem.manu.boaviagemapp.gasto.NovoGastoActivity;
import com.aprendizagem.manu.boaviagemapp.login.LoginActivity;
//import com.facebook.stetho.Stetho;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ListaViagemActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.OnConnectionFailedListener {

    private static final int VIAGEM_LOADER = 0;

    ViagemAdapter viagemAdapter;
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

//        if (BuildConfig.DEBUG){
//
//        Stetho.initializeWithDefaults(this);}

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
            startActivity(new Intent(this, LoginActivity.class));
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

        viagemAdapter = new ViagemAdapter(new ViagemAdapter.ItemClickListenerAdapter() {
            @Override
            public void itemFoiClicado(Cursor cursor) {
                opcoesParaCliqueDaViagem(cursor.getInt(cursor.getColumnIndex(ViagemEntry._ID)));
                Constantes.setNomeDestinoViagem(cursor.getString(cursor.getColumnIndex(ViagemEntry.COLUMN_DESTINO)));
            }
        }, this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        viagemAdapter.notifyDataSetChanged();
        recyclerViewViagem.setLayoutManager(mLayoutManager);
        viagemAdapter.setHasStableIds(true);
        recyclerViewViagem.setAdapter(viagemAdapter);
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
                startActivity(new Intent(this, LoginActivity.class));
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

    private void opcoesParaCliqueDaViagem(final int idViagem) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ListaViagemActivity.this);
        builder.setItems(R.array.opcoes_item_viagem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent intent;
                Uri currentUri = ContentUris.withAppendedId(ViagemEntry.CONTENT_URI, idViagem);
                switch (item) {
                    case 0:
                        intent = new Intent(ListaViagemActivity.this, ListaGastoActivity.class);
                        Constantes.setIdViagemSelecionada(idViagem);
                        Constantes.setIdDoUsuario(idUsuarioVindoDoFirebase);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(ListaViagemActivity.this, NovoGastoActivity.class);
                        Constantes.setIdViagemSelecionada(idViagem);
                        Constantes.setIdDoUsuario(idUsuarioVindoDoFirebase);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(ListaViagemActivity.this, NovaViagemActivity.class);
                        intent.setData(currentUri);
                        Constantes.setIdDoUsuario(idUsuarioVindoDoFirebase);
                        startActivity(intent);
                        break;
                    case 3:
                        deletarViagem(idViagem);
                        break;
                    case 4:
                        intent = new Intent(ListaViagemActivity.this, GaleriaImagemViagem.class);
                        Constantes.setIdViagemSelecionada(idViagem);
                        startActivity(intent);
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
        viagemAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        viagemAdapter.setCursor(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, getResources().getString(R.string.falha_de_conexao), Toast.LENGTH_SHORT).show();

    }

    private void deletarViagem(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.confirmar_exclusao_viagem);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                final int x = 1;
                Cursor cursor = viagemAdapter.getCursor();
                cursor.moveToPosition(x);
                getContentResolver().delete(
                        Uri.withAppendedPath(ViagemEntry.CONTENT_URI, String.valueOf(position)),
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
}

