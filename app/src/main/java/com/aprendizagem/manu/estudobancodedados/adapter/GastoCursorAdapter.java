package com.aprendizagem.manu.estudobancodedados.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract.GastoEntry;

/**
 * Created by emanu on 06/08/2017.
 */

public class GastoCursorAdapter extends CursorAdapter {

    public GastoCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.item_lista_gasto, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView indiceGasto = (TextView) view.findViewById(R.id.id_gasto);
        TextView idViagemGasto = (TextView) view.findViewById(R.id.id_viagem_gasto);
        TextView txtDescricaoGasto = (TextView) view.findViewById(R.id.descricao_gasto);


        // Find the columns of pet attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(GastoEntry._ID);
        int destinoColumnIndex = cursor.getColumnIndex(GastoEntry.COLUMN_DESCRICAO_GASTO);
        int idViagemGastoColumIndex = cursor.getColumnIndex(GastoEntry.COLUMN_VIAGEM_ID);

        // Read the pet attributes from the Cursor for the current pet
        String idGasto = cursor.getString(idColumnIndex);
        String descricaoGasto = cursor.getString(destinoColumnIndex);
        String viagemGasto = cursor.getString(idViagemGastoColumIndex);

        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(descricaoGasto)) {
            descricaoGasto = context.getString(R.string.unknown_breed);
        }

        // Update the TextViews with the attributes for the current pet
        txtDescricaoGasto.setText(descricaoGasto);
        indiceGasto.setText(idGasto);
        idViagemGasto.setText(viagemGasto);
    }
}
