package com.aprendizagem.manu.estudobancodedados.widget;


import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract;
import com.aprendizagem.manu.estudobancodedados.gasto.ListaGastoActivity;
import com.google.firebase.auth.FirebaseUser;

public class TodayWidgetIntentService extends IntentService {
    private String idUsuarioVindoDoFirebase;
    private FirebaseUser mFirebaseUser;

    private static final String[] VIAGEM_COMUMNS = {
            Contract.ViagemEntry._ID,
            Contract.ViagemEntry.COLUMN_DESTINO,
            Contract.ViagemEntry.COLUMN_RAZAO,
            Contract.ViagemEntry.COLUMN_DATA_CHEGADA,
            Contract.ViagemEntry.COLUMN_DATA_PARTIDA,
            Contract.ViagemEntry.COLUMN_LOCAL_ACOMODACAO,
            Contract.ViagemEntry.COLUMN_GASTO_TOTAL
    };
    // these indices must match the projection
    private static final int VIAGEM = 100;
    private static final int VIAGEM_ID = 101;


    public TodayWidgetIntentService() {
        super("TodayWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        idUsuarioVindoDoFirebase = mFirebaseUser.getUid();

        String selection = Contract.ViagemEntry.COLUMN_ID_USUARIO +
                " = " + idUsuarioVindoDoFirebase;

        Uri weatherForLocationUri = Contract.ViagemEntry.CONTENT_URI;


        Cursor cursor = getContentResolver().query(
                weatherForLocationUri, VIAGEM_COMUMNS,
                selection,
                null,
                Contract.ViagemEntry._ID + " DESC");

        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TodayWidgetProvider.class));

        // Extract the weather data from the Cursor

        int destinoColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DESTINO);
        int razaoViagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_RAZAO);
        int gastoViagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_GASTO_TOTAL);
        int dataChegadaViagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DATA_CHEGADA);
        int dataPartidaViagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DATA_PARTIDA);

        String destino = cursor.getString(destinoColumnIndex);
        String gastoViagem = cursor.getString(gastoViagemColumnIndex);
        String dataChegada = cursor.getString(dataChegadaViagemColumnIndex);
        String dataPartida = cursor.getString(dataPartidaViagemColumnIndex);

        int razaoViagem = cursor.getInt(razaoViagemColumnIndex);

        cursor.close();

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            // Find the correct layout based on the widget's width

            // Add the data to the RemoteViews
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.new_app_widget);
            views.setTextViewText(R.id.widget_description, destino);
            views.setTextViewText(R.id.widget_high_temperature, gastoViagem);
            views.setTextViewText(R.id.widget_low_temperature, dataChegada);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, ListaGastoActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
