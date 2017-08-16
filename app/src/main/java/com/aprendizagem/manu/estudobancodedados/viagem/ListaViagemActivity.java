package com.aprendizagem.manu.estudobancodedados.viagem;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.adapter.ViagemCursorAdapter;
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;
import com.aprendizagem.manu.estudobancodedados.gasto.ListaGastoActivity;
import com.aprendizagem.manu.estudobancodedados.gasto.NovoGastoActivity;
import com.aprendizagem.manu.estudobancodedados.login.Login;
import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ListaViagemActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.OnConnectionFailedListener {

    private static final int VIAGEM_LOADER = 0;

    ViagemCursorAdapter mCursorAdapter;

    public static final String ANONYMOUS = "anonymous";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String nomeUsuarioVindoDoFirebase;
    private String idUsuarioVindoDoFirebase;

    Toolbar listaGastoToolbar;
    TextView nomeUsuarioToolbar;
    ListView viagemListView;
    View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initializeWithDefaults(this);

        nomeUsuarioVindoDoFirebase = ANONYMOUS;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        } else {
            setContentView(R.layout.lista_viagem);

            nomeUsuarioVindoDoFirebase = mFirebaseUser.getDisplayName();
            idUsuarioVindoDoFirebase = mFirebaseUser.getUid();

            listaGastoToolbar = (Toolbar) findViewById(R.id.toolbar_lista_viagem);

            nomeUsuarioToolbar = (TextView) findViewById(R.id.text_view_nome_usuario);
            setSupportActionBar(listaGastoToolbar);
            nomeUsuarioToolbar.setText(" " + nomeUsuarioVindoDoFirebase);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_nova_viagem);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ListaViagemActivity.this, NovaViagemActivity.class);
                    Constantes.setIdDoUsuario(idUsuarioVindoDoFirebase);
                    startActivity(intent);
                }
            });

            viagemListView = (ListView) findViewById(R.id.list_view_viagem);

            emptyView = findViewById(R.id.include_lista_viagem_vazia);
            viagemListView.setEmptyView(emptyView);

            mCursorAdapter = new ViagemCursorAdapter(this, null);
            viagemListView.setAdapter(mCursorAdapter);

            viagemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, final long id) {
                    final int posicaoViagem = position + 1;
                    opcoesParaCliqueDaViagem(posicaoViagem);
                }
            });

            getLoaderManager().initLoader(VIAGEM_LOADER, null, this);

            NativeExpressAdView adView = (NativeExpressAdView)findViewById(R.id.native_ad_view);

            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    private void opcoesParaCliqueDaViagem(final int posicaoViagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListaViagemActivity.this);

        builder.setItems(R.array.opcoes_item_viagem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent intent;
                switch (item) {
                    case 0:
                        intent = new Intent(ListaViagemActivity.this, ListaGastoActivity.class);
                        Constantes.setIdViagemSelecionada(posicaoViagem);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(ListaViagemActivity.this, NovoGastoActivity.class);
                        Constantes.setIdViagemSelecionada(posicaoViagem);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lista_viagem_menu, menu);
        return true;
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
