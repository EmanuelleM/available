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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.adapter.ViagemCursorAdapter;
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;
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

    public static final String ANONYMOUS = "anonymous";
    private static final int VIAGEM_LOADER = 0;

    ViagemCursorAdapter mCursorAdapter;

    RecyclerView recyclerViewViagem;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    String nomeUsuarioVindoDoFirebase;
    String idUsuarioVindoDoFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initializeWithDefaults(this);

        nomeUsuarioVindoDoFirebase = ANONYMOUS;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null) {
            setContentView(R.layout.lista_viagem);

            nomeUsuarioVindoDoFirebase = mFirebaseUser.getDisplayName();
            idUsuarioVindoDoFirebase = mFirebaseUser.getUid();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_nova_viagem);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ListaViagemActivity.this, NovaViagemActivity.class);
                    Constantes.setIdDoUsuario(idUsuarioVindoDoFirebase);
                    startActivity(intent);
                }
            });

            recyclerViewViagem = (RecyclerView) findViewById(R.id.list_view_viagem);
            recyclerViewViagem.setHasFixedSize(true);

            mCursorAdapter = new ViagemCursorAdapter(new ViagemCursorAdapter.ItemClickListenerAdapter() {
                @Override
                public void itemFoiClicado(Cursor cursor) {
                    long id = cursor.getLong(cursor.getColumnIndex(ViagemEntry._ID));
                    opcoesParaCliqueDaViagem((int) id);
                    Toast.makeText(ListaViagemActivity.this, "" + id, Toast.LENGTH_SHORT).show();
                }
            }, this);

            recyclerViewViagem.setLayoutManager(new LinearLayoutManager(ListaViagemActivity.this));
            mCursorAdapter.setHasStableIds(true);
            recyclerViewViagem.setAdapter(mCursorAdapter);

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
}

