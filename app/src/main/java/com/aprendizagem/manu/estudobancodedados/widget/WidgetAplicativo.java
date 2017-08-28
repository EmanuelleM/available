package com.aprendizagem.manu.estudobancodedados.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class WidgetAplicativo extends AppWidgetProvider {

//    @Override
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
//                         int[] appWidgetIds) {
//        ComponentName cn = new ComponentName(context, .class);
//        RemoteViews atualizarFrame = new RemoteViews("com.aprendizagem.manu.estudobancodedados", R.layout.widget);
//        DatabaseHelper databaseHelper = new DatabaseHelper(context);
//
//        try {
//            Cursor c = databaseHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM viagens", null);
//            c.moveToFirst();
//            int count = c.getInt(0);
//            c.close();
//
//            if (count > 0) {
//                int offset = (int) (count * Math.random());
//                String args[] = {String.valueOf(offset)};
//                c = databaseHelper.getReadableDatabase().rawQuery("SELECT destino FROM viagens LIMIT 1 OFFSET ?", args);
//                c.moveToFirst();
//                Log.d("Destino", " "+c.getString(0));
//                atualizarFrame.setTextViewText(R.id.text_view_nome_viagem, "dlksfmnkaldsfmkç");
//            } else {
//                atualizarFrame.setTextViewText(R.id.text_view_nome_viagem, context.getString(R.string.sem_viagens_na_lista));
//            }
//        } finally {
//            databaseHelper.close();
//        }
//
//        appWidgetManager.updateAppWidget(cn, atualizarFrame);
//    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        context.startService(new Intent(context, WidgetService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, WidgetService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        context.startService(new Intent(context, WidgetService.class));
    }
}
