package com.aprendizagem.manu.estudobancodedados.widget;


import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract;
import com.aprendizagem.manu.estudobancodedados.database.DatabaseHelper;
import com.aprendizagem.manu.estudobancodedados.gasto.ListaGastoActivity;

public class WidgetIntentService extends IntentService {
    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ComponentName cn = new ComponentName(this, NewAppWidgetViagem.class);
        RemoteViews atualizarFrame = new RemoteViews("com.aprendizagem.manu.estudobancodedados", R.layout.oficial_app_widget);
        DatabaseHelper gerenciador = new DatabaseHelper(this);
        AppWidgetManager mgr = AppWidgetManager.getInstance(this);

        try {
            Cursor c = gerenciador.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM viagens", null);
            c.moveToFirst();
            int contador = c.getInt(0);
            c.close();

            if (contador > 0) {
                int deslocamento = (int) (contador * Math.random());
                String args[] = {String.valueOf(deslocamento)};
                c = gerenciador.getReadableDatabase().rawQuery("SELECT _id, destino FROM viagens LIMIT 1 OFFSET ?", args);
                c.moveToFirst();
                atualizarFrame.setTextViewText(R.id.destino, c.getString(0));

                Intent i = new Intent(this, ListaGastoActivity.class);
                i.putExtra(Contract.ViagemEntry._ID, c.getString(0));
                i.putExtra(Contract.GastoEntry.COLUMN_ID_USUARIO, c.getString(1));
                PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                atualizarFrame.setOnClickPendingIntent(R.id.destino, pi);

                c.close();
            } else {
                atualizarFrame.setTextViewText(R.id.destino, getString(android.R.string.untitled));
            }
        } finally {
            gerenciador.close();
        }

        Intent i = new Intent(this, WidgetIntentService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        atualizarFrame.setOnClickPendingIntent(R.id.proximo, pi);
        mgr.updateAppWidget(cn, atualizarFrame);
    }
}
