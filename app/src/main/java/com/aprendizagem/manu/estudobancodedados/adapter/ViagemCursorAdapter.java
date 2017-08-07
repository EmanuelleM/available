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
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;

/**
 * Created by emanu on 06/08/2017.
 */

public class ViagemCursorAdapter extends CursorAdapter {

    public ViagemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.item_lista_viagem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView idViagem = (TextView) view.findViewById(R.id.id_viagem);
        TextView destinoViagem = (TextView) view.findViewById(R.id.destino_viagem);

        // Find the columns of pet attributes that we're interested in
        int destinoColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DESTINO);
        int idColumnIndex = cursor.getColumnIndex(ViagemEntry._ID);

        // Read the pet attributes from the Cursor for the current pet
        String destino = cursor.getString(destinoColumnIndex);
        String idviagem = cursor.getString(idColumnIndex);

        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(idviagem)) {
            idviagem = context.getString(R.string.unknown_breed);
        }

        // Update the TextViews with the attributes for the current pet
        idViagem.setText(destino);
        destinoViagem.setText(idviagem);
    }
}
