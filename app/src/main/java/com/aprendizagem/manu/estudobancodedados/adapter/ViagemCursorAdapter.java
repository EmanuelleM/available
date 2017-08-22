package com.aprendizagem.manu.estudobancodedados.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;

public class ViagemCursorAdapter extends
        RecyclerView.Adapter<ViagemCursorAdapter.ViewHolder> {

    private Cursor cursor;
    private ItemClickListenerAdapter mListener;
    Context mContext;

    public ViagemCursorAdapter(ItemClickListenerAdapter aoClicarNoItem, Context applicationContext) {
        mListener = aoClicarNoItem;
        mContext = applicationContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_viagem, parent, false);

        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
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

        if (razaoViagem == 1) {
            holder.campoRazaoViagem.setText(R.string.razao_lazer);
        } else if (razaoViagem == 2) {
            holder.campoRazaoViagem.setText(R.string.razao_negocios);
        } else if (razaoViagem == 0) {
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

    public interface ItemClickListenerAdapter {
        void itemFoiClicado(Cursor cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView campoDestinoViagem;
        TextView campoValorTotalGastoViagem;
        TextView campoRazaoViagem;
        TextView campoDataChegada;
        TextView campoDataPartida;
        ImageView acionarManu;

        public ViewHolder(View itemView) {
            super(itemView);
            campoDestinoViagem = itemView.findViewById(R.id.text_view_destino_viagem);
            campoValorTotalGastoViagem = itemView.findViewById(R.id.text_view_total_viagem);
            campoRazaoViagem = itemView.findViewById(R.id.text_view_razao_viagem);
            campoDataChegada = itemView.findViewById(R.id.text_view_data_chegada);
            campoDataPartida = itemView.findViewById(R.id.text_view_data_partida);
            acionarManu = itemView.findViewById(R.id.image_view_arrow);
        }
    }
}

