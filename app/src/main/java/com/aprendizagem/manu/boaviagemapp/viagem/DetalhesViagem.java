package com.aprendizagem.manu.boaviagemapp.viagem;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aprendizagem.manu.boaviagemapp.Constantes;
import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.adapter.GaleriaImagensAdapter;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ImagemGaleriaEntry;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ViagemEntry;
import com.aprendizagem.manu.boaviagemapp.database.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.util.List;

import javax.annotation.Nullable;

public class DetalhesViagem extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    TextView txtDestino;
    TextView txtDataChegada;
    TextView txtDataPartida;
    TextView txtLocalHospedagem;
    TextView txtValorGasto;

    ImageButton adiconarImagem;

    DatabaseHelper helper = new DatabaseHelper(this);

    List<Uri> caminhoDaImagem;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;

    private static final int EXISTING_VIAGEM_LOADER = 0;
    private Uri privateCurrentUri;
    private static final int REQUEST_CODE_CHOOSE = 23;

    public static final int REQUEST_PERMISSIONS_CODE = 128;

    GaleriaImagensAdapter galeriaImagemAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhes_viagem);

        if (!verificaPermissaoLeitura()) {
            solicitaPermissaoLeitura();
        }

        Intent intent = getIntent();
        privateCurrentUri = intent.getData();

        txtDestino = findViewById(R.id.text_view_destino);
        txtDataChegada = findViewById(R.id.text_view_data_chegada);
        txtDataPartida = findViewById(R.id.text_view_data_partida);
        txtLocalHospedagem = findViewById(R.id.text_view_hospedagem);
        txtValorGasto = findViewById(R.id.text_view_valor_gasto);

        adiconarImagem = findViewById(R.id.image_button_adiciona_viagem);

        adiconarImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionaImagem();
            }
        });

//        exibeImagemTeste();

        galeriaImagemAdapter = new GaleriaImagensAdapter(new GaleriaImagensAdapter.ItemClickListenerAdapter() {
            @Override
            public void itemFoiClicado(Cursor cursor) {
                cursor.getInt(cursor.getColumnIndex(ImagemGaleriaEntry._ID));

            }
        }, this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_galeria);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(galeriaImagemAdapter);

        getLoaderManager().initLoader(EXISTING_VIAGEM_LOADER, null, this);
    }

    public boolean verificaPermissaoLeitura() {
        //verifica se as permissoes foram concedida

        boolean acessoConcedido = false;

        int checaPermissaoDeLeitura = ContextCompat.checkSelfPermission(DetalhesViagem.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int checaPermissaoDeEscrita = ContextCompat.checkSelfPermission(DetalhesViagem.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (checaPermissaoDeLeitura == PackageManager.PERMISSION_GRANTED &&
                checaPermissaoDeEscrita == PackageManager.PERMISSION_GRANTED) {
            acessoConcedido = true;
        }

        return acessoConcedido;
    }

    public void solicitaPermissaoLeitura() {

        ActivityCompat.requestPermissions(DetalhesViagem.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

    }

    public void solicitaPermissaoEscrita() {
        ActivityCompat.requestPermissions(DetalhesViagem.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                for (int i = 0; i < permissions.length; i++) {


                    if (permissions[i].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        adicionaImagem();
                    }
//                    else if( permissions[i].equalsIgnoreCase( Manifest.permission.READ_EXTERNAL_STORAGE )
//                            && grantResults[i] == PackageManager.PERMISSION_GRANTED ){
//
//                        readFile(Environment.getExternalStorageDirectory().toString() + "/myFolder");
//                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    public void exibeImagemTeste() {
//
//        ImageView imageView = findViewById(R.id.my_image_view);
//
//        Glide.with(this).load(getCaminhoImagem()).into(imageView);
//    }

//    private String getCaminhoImagem() {
//
//        SQLiteDatabase db = helper.getReadableDatabase();
//
//        String[] projection = {
//                ImagemGaleriaEntry.COLUMN_CAMINHO_IMAGEM
//        };
//
//        String selection = ImagemGaleriaEntry.COLUMN_VIAGEM_ID + " = '" + Constantes
//                .getIdViagemSelecionada() + "'";
//
//        Cursor cursor = db.query(
//                ImagemGaleriaEntry.TABLE_NAME,
//                projection,
//                selection,
//                null,
//                null,
//                null,
//                null
//        );
//        String caminhoImagem = "";
//
//        if (cursor.moveToFirst()) {
//            caminhoImagem = cursor.getString(0);
//            cursor.close();
//        }
//        return caminhoImagem;
//    }

    public void adicionaImagem() {

        Matisse.from(DetalhesViagem.this)
                .choose(MimeType.allOf())
                .theme(R.style.Matisse_Dracula)
                .countable(true)
                .maxSelectable(1)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            caminhoDaImagem = Matisse.obtainResult(data);
            Log.d("Matisse", "caminhoDaImagem: " + caminhoDaImagem);
            Toast.makeText(this, "caminhoDaImagem: " + caminhoDaImagem, Toast.LENGTH_SHORT)
                    .show();

            ContentValues values = new ContentValues();
            values.put(ImagemGaleriaEntry.COLUMN_VIAGEM_ID, Constantes.getIdViagemSelecionada());
            values.put(ImagemGaleriaEntry.COLUMN_CAMINHO_IMAGEM, String.valueOf(caminhoDaImagem.get(0)));

            getContentResolver().insert(ImagemGaleriaEntry.CONTENT_URI, values);

        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
     /*   String[] projection = {
                ViagemEntry._ID,
                ViagemEntry.COLUMN_DESTINO,
                ViagemEntry.COLUMN_LOCAL_ACOMODACAO,
                ViagemEntry.COLUMN_RAZAO,
                ViagemEntry.COLUMN_DATA_CHEGADA,
                ViagemEntry.COLUMN_DATA_PARTIDA,
                ViagemEntry.COLUMN_GASTO_TOTAL
        };

        return new CursorLoader(this,
                privateCurrentUri,
                projection,
                null,
                null,
                null);*/

        String[] projection = {
                ImagemGaleriaEntry._ID,
                ImagemGaleriaEntry.COLUMN_VIAGEM_ID,
                ImagemGaleriaEntry.COLUMN_CAMINHO_IMAGEM,

        };

        return new CursorLoader(this,
                ImagemGaleriaEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        cursor.moveToFirst();

        galeriaImagemAdapter.setCursor(cursor);

      /*  int destinoColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DESTINO);
        int localHospedagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_LOCAL_ACOMODACAO);
        int dataChegadaViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DATA_CHEGADA);
        int dataPartidaViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DATA_PARTIDA);
        int valorGastoViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_GASTO_TOTAL);

        txtDestino.setText(String.format("%s %s", getString(R.string.voce_esta_viajando_para), cursor.getString(destinoColumnIndex)));
        txtLocalHospedagem.setText(String.format("%s %s", getString(R.string.voce_esta_hospedado), cursor.getString(localHospedagemColumnIndex)));
        txtDataChegada.setText(String.format("%s %s", getString(R.string.sua_viagem_comecou), cursor.getString(dataChegadaViagemColumnIndex)));
        txtDataPartida.setText(String.format("%s %s", getString(R.string.sua_viagem_termina), cursor.getString(dataPartidaViagemColumnIndex)));

        txtValorGasto.setText(String.format("%s %s", getString(R.string.gasto_atual_da_viagem), cursor.getDouble(valorGastoViagemColumnIndex)));
*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
