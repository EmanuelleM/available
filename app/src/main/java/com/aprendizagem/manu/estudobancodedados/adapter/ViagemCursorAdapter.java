package com.aprendizagem.manu.estudobancodedados.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;

public class ViagemCursorAdapter extends
        RecyclerView.Adapter<ViagemCursorAdapter.VH> {

   /* public ViagemCursorAdapter(Context context, Cursor c) {

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
            campoValorTotalGastoViagem.setText(gastoViagem.replace(".", ","));
        }else{
            campoValorTotalGastoViagem.setText(" 0.00");
        }

        campoDataChegada.setText(dataChegada);
        campoDataPartida.setText(dataPartida);
    }
}

*/

    private Cursor cursor;
    private AoClicarNoItem mListener;

    public ViagemCursorAdapter(AoClicarNoItem listener) {
        mListener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_viagem, parent, false);

        final VH vh = new VH(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = vh.getAdapterPosition();
                cursor.moveToPosition(position);
                if (mListener != null) mListener.itemFoiClicado(cursor);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        cursor.moveToPosition(position);

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
            holder.campoRazaoViagem.setText(R.string.razao_lazer);
        }else if (razaoViagem == 2){
            holder.campoRazaoViagem.setText(R.string.razao_negocios);
        }else if (razaoViagem == 0){
            holder.campoRazaoViagem.setText("");
        }

        holder.campoDestinoViagem.setText(destino);
        holder.campoValorTotalGastoViagem.setText(gastoViagem);
        holder.campoDataChegada.setText(dataChegada);
        holder.campoDataPartida.setText(dataPartida);
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                int idx_id = cursor.getColumnIndex(ViagemEntry._ID);
                return cursor.getLong(idx_id);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public interface AoClicarNoItem {
        void itemFoiClicado(Cursor cursor);
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView campoDestinoViagem;
        TextView campoValorTotalGastoViagem;
        TextView campoRazaoViagem;
        TextView campoDataChegada;
        TextView campoDataPartida;


        public VH(View view) {
            super(view);
            campoDestinoViagem = view.findViewById(R.id.text_view_destino_viagem);
            campoValorTotalGastoViagem = view.findViewById(R.id.text_view_total_viagem);
            campoRazaoViagem = view.findViewById(R.id.text_view_razao_viagem);
            campoDataChegada = view.findViewById(R.id.text_view_data_chegada);
            campoDataPartida = view.findViewById(R.id.text_view_data_partida);
        }
    }
}

