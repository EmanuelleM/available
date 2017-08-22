package com.aprendizagem.manu.estudobancodedados.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract.GastoEntry;

public class GastoCursorAdapter extends CursorAdapter {

    public GastoCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_lista_gasto, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView txtDescricaoGasto = view.findViewById(R.id.text_view_descricao_gasto);
        TextView txtValorGasto = view.findViewById(R.id.text_view_valor_gasto);
        TextView txtDataGasto = view.findViewById(R.id.text_view_data_gasto);

        int destinoColumnIndex = cursor.getColumnIndex(GastoEntry.COLUMN_DESCRICAO_GASTO);
        int idValorGastoColumIndex = cursor.getColumnIndex(GastoEntry.COLUMN_VALOR_GASTO);
        int dataGastoColumIndex = cursor.getColumnIndex(GastoEntry.COLUMN_DATA_GASTO);

        String descricaoGasto = cursor.getString(destinoColumnIndex);
        String valorGastoViagem = cursor.getString(idValorGastoColumIndex);
        String dataGastoViagem = cursor.getString(dataGastoColumIndex);

        txtDescricaoGasto.setText(descricaoGasto);
        txtValorGasto.setText(valorGastoViagem.replace(".", ","));
        txtDataGasto.setText(dataGastoViagem);
    }
}
