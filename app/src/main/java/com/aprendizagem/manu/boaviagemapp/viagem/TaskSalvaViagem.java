package com.aprendizagem.manu.boaviagemapp.viagem;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.database.Contract;

class TaskSalvaViagem extends AsyncTask<Void, Void, Uri> {

    private final Context privateContext;
    private final String destino;
    private final String localHospedagem;
    private final int razaoViagem;
    private final String dataChegada;
    private final String dataPartida;
    private final String idDousuario;

    TaskSalvaViagem(Context contextViagem, String destino, String
            localHospedagem,
                    int razaoViagem,
                    String dataChegada, String dataPartida,
                    String idDousuario) {

        this.privateContext = contextViagem;
        this.destino = destino;
        this.localHospedagem = localHospedagem;
        this.razaoViagem = razaoViagem;
        this.dataChegada = dataChegada;
        this.dataPartida = dataPartida;
        this.idDousuario = idDousuario;
    }

    @Override
    protected Uri doInBackground(Void... params) {

        ContentValues values = new ContentValues();
        values.put(Contract.ViagemEntry.COLUMN_DESTINO, destino);
        values.put(Contract.ViagemEntry.COLUMN_LOCAL_ACOMODACAO, localHospedagem);
        values.put(Contract.ViagemEntry.COLUMN_RAZAO, razaoViagem);
        values.put(Contract.ViagemEntry.COLUMN_DATA_CHEGADA, dataChegada);
        values.put(Contract.ViagemEntry.COLUMN_DATA_PARTIDA, dataPartida);
        values.put(Contract.ViagemEntry.COLUMN_ID_USUARIO, idDousuario);

        return privateContext.getContentResolver().insert(Contract.ViagemEntry.CONTENT_URI, values);
    }

    @Override
    protected void onPostExecute(Uri uri) {
        super.onPostExecute(uri);

        if (uri != null) {

            Toast.makeText(privateContext, R.string.viagem_salva, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(privateContext, ListaViagemActivity
                    .class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            privateContext.startActivity(intent);

        } else {
            Toast.makeText(privateContext, R.string
                            .erro_salvar_viagem,
                    Toast.LENGTH_SHORT).show();

        }
    }
}