package com.aprendizagem.manu.estudobancodedados.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract;
import com.aprendizagem.manu.estudobancodedados.database.DatabaseHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WidgetService extends IntentService {

    public WidgetService() {
        super("WidgetService");
    }

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    String idUsuarioVindoDoFirebase;

    @Override
    protected void onHandleIntent(Intent intent) {
        ComponentName cn = new ComponentName(this, WidgetAplicativo.class);
        RemoteViews atualizarFrame = new RemoteViews(getPackageName(), R.layout.widget);
        DatabaseHelper gerenciador = new DatabaseHelper(this);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        idUsuarioVindoDoFirebase = mFirebaseUser.getUid();

        try {
            Cursor c = gerenciador.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM viagens WHERE id_usuario = '" + idUsuarioVindoDoFirebase + "'", null);
            c.moveToFirst();
            int contador = c.getInt(0);
            c.close();

            if (contador > 0) {
                int deslocamento = (int) (contador * Math.random());
                String args[] = {String.valueOf(deslocamento)};
                c = gerenciador.getReadableDatabase().rawQuery("SELECT destino, gasto_total, data_chegada, data_saida FROM viagens LIMIT 1 OFFSET ?", args);
                c.moveToFirst();

                final int destinoColumnIndex = c.getColumnIndex(Contract.ViagemEntry.COLUMN_DESTINO);
                final String destino = c.getString(destinoColumnIndex);
                atualizarFrame.setTextViewText(R.id.text_view_nome_viagem, destino);

                final int valorGastoColumnIndex = c.getColumnIndex(Contract.ViagemEntry.COLUMN_GASTO_TOTAL);
                final String gastoTotal = c.getString(valorGastoColumnIndex);
                atualizarFrame.setTextViewText(R.id.text_view_gasto_viagem, gastoTotal);

                c.close();
            } else {
                atualizarFrame.setTextViewText(R.id.text_view_nome_viagem, getString(R.string.sem_viagens_na_lista));
            }
        } finally {
            gerenciador.close();
        }

        appWidgetManager.updateAppWidget(cn, atualizarFrame);

    }

}
