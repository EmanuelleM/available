package com.aprendizagem.manu.estudobancodedados.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;

public class ViagemCursorAdapter extends CursorAdapter {

    public ViagemCursorAdapter(Context context, Cursor c) {

        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_lista_viagem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView campoDestinoViagem = view.findViewById(R.id.text_view_destino_viagem);
        TextView campoValorTotalGastoViagem = view.findViewById(R.id.text_view_total_viagem);
        TextView campoRazaoViagem = view.findViewById(R.id.text_view_razao_viagem);
        TextView campoDataChegada = view.findViewById(R.id.text_view_data_chegada);
        TextView campoDataPartida = view.findViewById(R.id.text_view_data_partida);

        int destinoColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DESTINO);
        int razaoViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_RAZAO);
        int gastoViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_GASTO_TOTAL);
        int dataChegadaViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DATA_CHEGADA);
        int dataPartidaViagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DATA_PARTIDA);

        String destino = cursor.getString(destinoColumnIndex);
        String gastoViagem = cursor.getString(gastoViagemColumnIndex);
        String dataChegada = cursor.getString(dataChegadaViagemColumnIndex);
        String dataPartida = cursor.getString(dataPartidaViagemColumnIndex);

        int razaoViagem = cursor.getInt(razaoViagemColumnIndex);

        if (razaoViagem == 1){
            campoRazaoViagem.setText(R.string.razao_lazer);
        }else if (razaoViagem == 2){
            campoRazaoViagem.setText(R.string.razao_negocios);
        }else if (razaoViagem == 0){
            campoRazaoViagem.setText("");
        }

        campoDestinoViagem.setText(destino);
        if(gastoViagem != " "){
            campoValorTotalGastoViagem.setText(gastoViagem);
        }else{
            campoValorTotalGastoViagem.setText(" 0.00");
        }

        campoDataChegada.setText(dataChegada);
        campoDataPartida.setText(dataPartida);
    }
}
