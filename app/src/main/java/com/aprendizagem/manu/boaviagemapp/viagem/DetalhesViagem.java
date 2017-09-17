package com.aprendizagem.manu.boaviagemapp.viagem;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
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
import com.aprendizagem.manu.boaviagemapp.database.Contract;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ImagemGaleriaEntry;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ViagemEntry;
import com.aprendizagem.manu.boaviagemapp.database.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

public class DetalhesViagem extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    private static final int EXISTING_VIAGEM_LOADER = 0;

    TextView txtDestino;
    TextView txtDataChegada;
    TextView txtDataPartida;
    TextView txtLocalHospedagem;
    TextView txtValorGasto;

    ImageButton adiconarImagem;

    private Uri mCurrentViagemUri;
    private int PICK_IMAGE_REQUEST = 1;
    UriAdapter uriAdapter;

    DatabaseHelper helper = new DatabaseHelper(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhes_viagem);

        Intent intent = getIntent();
        mCurrentViagemUri = intent.getData();

        txtDestino = (TextView) findViewById(R.id.text_view_destino);
        txtDataChegada = (TextView) findViewById(R.id.text_view_data_chegada);
        txtDataPartida = (TextView) findViewById(R.id.text_view_data_partida);
        txtLocalHospedagem = (TextView) findViewById(R.id.text_view_hospedagem);
        txtValorGasto = (TextView) findViewById(R.id.text_view_valor_gasto);

        adiconarImagem = (ImageButton) findViewById(R.id.image_button_adiciona_viagem);

        adiconarImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionaImagem(view);
            }
        });

//        GridView gridview = (GridView) findViewById(R.id.grid_view_imagem);
//        gridview.setAdapter(new GaleriaImagensAdapter(this));
//
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//
//            }
//        });

      exibeImagemTeste();


        getLoaderManager().initLoader(EXISTING_VIAGEM_LOADER, null, this);
    }

    public void exibeImagemTeste(){

        ImageView imageView = (ImageView) findViewById(R.id.my_image_view);

        Glide.with(this).load(getCaminhoImagem()).into(imageView);
    }

    private String getCaminhoImagem() {

        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                ImagemGaleriaEntry.COLUMN_CAMINHO_IMAGEM
        };

        String selection = ImagemGaleriaEntry.COLUMN_VIAGEM_ID + " = '" + Constantes
                .getIdViagemSelecionada() + "'";

        Cursor cursor = db.query(
                ImagemGaleriaEntry.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null
        );
        String caminhoImagem = "";

        if (cursor.moveToFirst()) {
            caminhoImagem = cursor.getString(0);
            cursor.close();
        }
        return caminhoImagem;
    }

    private static final int REQUEST_CODE_CHOOSE = 23;


    public static Set<MimeType> ofImage() {
        return EnumSet.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF, MimeType.GIF);
    }

    public void adicionaImagem(final View view) {

        Matisse.from(DetalhesViagem.this)
                .choose(ofImage())
                .theme(R.style.Matisse_Dracula)
                .countable(true)
                .maxSelectable(1)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    List<Uri> caminhoDaImagem;

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
        String[] projection = {
                ViagemEntry._ID,
                ViagemEntry.COLUMN_DESTINO,
                ViagemEntry.COLUMN_LOCAL_ACOMODACAO,
                ViagemEntry.COLUMN_RAZAO,
                ViagemEntry.COLUMN_DATA_CHEGADA,
                ViagemEntry.COLUMN_DATA_PARTIDA,
                ViagemEntry.COLUMN_GASTO_TOTAL
        };

        return new CursorLoader(this,
                mCurrentViagemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();

        int destinoColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DESTINO);
        int localHospedagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_LOCAL_ACOMODACAO);
        int dataChegadaViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DATA_CHEGADA);
        int dataPartidaViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DATA_PARTIDA);
        int valorGastoViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_GASTO_TOTAL);

        txtDestino.setText(getString(R.string.voce_esta_viajando_para) + " " + cursor.getString(destinoColumnIndex));
        txtLocalHospedagem.setText(getString(R.string.voce_esta_hospedado) + " " + cursor.getString(localHospedagemColumnIndex));
        txtDataChegada.setText(getString(R.string.sua_viagem_comecou) + " " + cursor.getString(dataChegadaViagemColumnIndex));
        txtDataPartida.setText(getString(R.string.sua_viagem_termina) + " " + cursor.getString(dataPartidaViagemColumnIndex));

        txtValorGasto.setText(getString(R.string.gasto_atual_da_viagem) + " " + cursor.getDouble(valorGastoViagemColumnIndex));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private static class UriAdapter extends RecyclerView.Adapter<UriAdapter.UriViewHolder> {

        private List<Uri> mUris;
        private List<String> mPaths;

        void setData(List<Uri> uris, List<String> paths) {
            mUris = uris;
            mPaths = paths;
            notifyDataSetChanged();
        }

        @Override
        public UriViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UriViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.uri_item, parent, false));
        }

        @Override
        public void onBindViewHolder(UriViewHolder holder, int position) {
            holder.mUri.setText(mUris.get(position).toString());
            holder.mPath.setText(mPaths.get(position));

            holder.mUri.setAlpha(position % 2 == 0 ? 1.0f : 0.54f);
            holder.mPath.setAlpha(position % 2 == 0 ? 1.0f : 0.54f);
        }

        @Override
        public int getItemCount() {
            return mUris == null ? 0 : mUris.size();
        }

        static class UriViewHolder extends RecyclerView.ViewHolder {

            private TextView mUri;
            private TextView mPath;

            UriViewHolder(View contentView) {
                super(contentView);
                mUri = (TextView) contentView.findViewById(R.id.uri);
                mPath = (TextView) contentView.findViewById(R.id.path);
            }
        }
    }
}
