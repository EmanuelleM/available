package com.aprendizagem.manu.boaviagemapp.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.database.Contract;
import com.aprendizagem.manu.boaviagemapp.database.DatabaseHelper;

import java.util.Locale;

public class WidgetService extends IntentService {

    public WidgetService() {
        super("WidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ComponentName cn = new ComponentName(this, WidgetAplicativo.class);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget);
        DatabaseHelper gerenciador = new DatabaseHelper(this);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        try {
            Cursor c = gerenciador.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM viagens", null);
            c.moveToFirst();
            int contador = c.getInt(0);
            c.close();

            if (contador > 0) {
                int deslocamento = (int) (contador * Math.random());
                String args[] = {String.valueOf(1)};
                c = gerenciador.getReadableDatabase().rawQuery("SELECT destino, gasto_total, data_chegada, data_saida FROM viagens LIMIT 1 OFFSET ?", args);
                c.moveToFirst();

                final int destinoColumnIndex = c.getColumnIndex(Contract.ViagemEntry.COLUMN_DESTINO);
                final String destino = c.getString(destinoColumnIndex);
                remoteViews.setTextViewText(R.id.text_view_nome_viagem, destino);

                final int dataChegadaColumnIndex = c.getColumnIndex(Contract.ViagemEntry.COLUMN_DATA_CHEGADA);
                final String dataChegada= c.getString(dataChegadaColumnIndex);
                remoteViews.setTextViewText(R.id.text_view_data_chegada, dataChegada);

                final int dataSaidaColumnIndex = c.getColumnIndex(Contract.ViagemEntry.COLUMN_DATA_PARTIDA);
                final String dataSaida= c.getString(dataSaidaColumnIndex);
                remoteViews.setTextViewText(R.id.text_view_data_saida, dataSaida);

                final int valorGastoColumnIndex = c.getColumnIndex(Contract.ViagemEntry.COLUMN_GASTO_TOTAL);
                final double gastoTotal = c.getDouble(valorGastoColumnIndex);
                String valorFormatado = String.format(Locale.getDefault(), "%.2f", gastoTotal);
                remoteViews.setTextViewText(R.id.text_view_gasto_viagem, "R$" + valorFormatado.replace(".", ","));

                c.close();
            } else {
                remoteViews.setTextViewText(R.id.text_view_nome_viagem, getString(R.string.sem_viagens_na_lista));
            }
        } finally {
            gerenciador.close();
        }

        Intent i = new Intent(this, WidgetService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        remoteViews.setOnClickPendingIntent(R.id.proxima_viagem, pi);
        appWidgetManager.updateAppWidget(cn, remoteViews);

    }

}
