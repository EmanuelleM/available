package com.aprendizagem.manu.boaviagemapp.gasto;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.database.Contract;
import com.aprendizagem.manu.boaviagemapp.viagem.ListaViagemActivity;

class TaskSalvaGasto extends AsyncTask<Void, Void, Uri> {

    private final Context mContext;
    private final String mDescricaoGasto;
    private final String mValorGasto;
    private final String mMetodoPagamento;
    private final String mDataGasto;
    private final String mIdViagem;
    private final String mIdUsuario;

    TaskSalvaGasto(Context contextViagem,
                   String idViagem,
                   String descricaoGasto,
                   String valorGasto,
                   String dataGasto,
                   String metodoPagamento,
                   String idUsuario) {
        this.mContext = contextViagem;
        this.mDescricaoGasto = descricaoGasto;
        this.mValorGasto = valorGasto;
        this.mMetodoPagamento = metodoPagamento;
        this.mDataGasto = dataGasto;
        this.mIdViagem = idViagem;
        this.mIdUsuario = idUsuario;
    }

    @Override
    protected Uri doInBackground(Void... params) {

        ContentValues values = new ContentValues();

        values.put(Contract.GastoEntry.COLUMN_VIAGEM_ID, mIdViagem);
        values.put(Contract.GastoEntry.COLUMN_DESCRICAO_GASTO, mDescricaoGasto);
        values.put(Contract.GastoEntry.COLUMN_VALOR_GASTO, mValorGasto);
        values.put(Contract.GastoEntry.COLUMN_DATA_GASTO, mDataGasto);
        values.put(Contract.GastoEntry.COLUMN_METODO_PAGAMENTO, mMetodoPagamento);
        values.put(Contract.GastoEntry.COLUMN_ID_USUARIO, mIdUsuario);

        return mContext.getContentResolver().insert(Contract.GastoEntry.CONTENT_URI, values);

    }

    @Override
    protected void onPostExecute(Uri uri) {
        super.onPostExecute(uri);

        if (uri != null) {

            Toast.makeText(mContext, R.string
                            .gasto_salvo,
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(mContext, ListaViagemActivity
                    .class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);

        } else {
            Toast.makeText(mContext, R.string
                            .erro_salvar_gasto,
                    Toast.LENGTH_SHORT).show();

        }
    }
}