package com.aprendizagem.manu.boaviagemapp.viagem;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.database.Contract;

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

        getLoaderManager().initLoader(EXISTING_VIAGEM_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                Contract.ViagemEntry._ID,
                Contract.ViagemEntry.COLUMN_DESTINO,
                Contract.ViagemEntry.COLUMN_LOCAL_ACOMODACAO,
                Contract.ViagemEntry.COLUMN_RAZAO,
                Contract.ViagemEntry.COLUMN_DATA_CHEGADA,
                Contract.ViagemEntry.COLUMN_DATA_PARTIDA,
                Contract.ViagemEntry.COLUMN_GASTO_TOTAL
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

        int destinoColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DESTINO);
        int localHospedagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_LOCAL_ACOMODACAO);
        int dataChegadaViagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DATA_CHEGADA);
        int dataPartidaViagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DATA_PARTIDA);
        int valorGastoViagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_GASTO_TOTAL);

        txtDestino.setText(getString(R.string.voce_esta_viajando_para) + " " + cursor.getString(destinoColumnIndex));
        txtLocalHospedagem.setText(getString(R.string.voce_esta_hospedado) + " " +cursor.getString(localHospedagemColumnIndex));
        txtDataChegada.setText(getString(R.string.sua_viagem_comecou) + " " +cursor.getString(dataChegadaViagemColumnIndex));
        txtDataPartida.setText(getString(R.string.sua_viagem_termina) + " " +cursor.getString(dataPartidaViagemColumnIndex));
        txtValorGasto.setText(getString(R.string.gasto_atual_da_viagem) + " " +cursor.getDouble(valorGastoViagemColumnIndex));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View view) {

    }
}
