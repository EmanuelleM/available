package com.aprendizagem.manu.estudobancodedados.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract;

import java.util.Locale;

public class GastoCursorAdapter extends
        RecyclerView.Adapter<GastoCursorAdapter.ViewHolder> {

    private Context mContext;
    private Cursor cursor;
    private ItemClickListenerAdapter mListener;

    public GastoCursorAdapter(ItemClickListenerAdapter aoClicarNoItem, Context applicationContext) {
        mListener = aoClicarNoItem;
        mContext = applicationContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_gasto, parent, false);

        final ViewHolder vh = new ViewHolder(v);

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        cursor.moveToPosition(position);

        int descricaoGastoColumnIndex = cursor.getColumnIndex(Contract.GastoEntry.COLUMN_DESCRICAO_GASTO);
        int valorGastoColumnIndex = cursor.getColumnIndex(Contract.GastoEntry.COLUMN_VALOR_GASTO);
        int dataGastoColumnIndex = cursor.getColumnIndex(Contract.GastoEntry.COLUMN_DATA_GASTO);
        int metodoPagamentoColumnIndex = cursor.getColumnIndex(Contract.GastoEntry.COLUMN_METODO_PAGAMENTO);

        final String descricaoGasto = cursor.getString(descricaoGastoColumnIndex);
        double valorGasto = cursor.getDouble(valorGastoColumnIndex);
        String dataGasto = cursor.getString(dataGastoColumnIndex);
        String metodoPagemento = cursor.getString(metodoPagamentoColumnIndex);

        String valorFormatado = String.format(Locale.getDefault(), "%.2f", valorGasto);

        holder.txtDescricaoGasto.setText(descricaoGasto);
        holder.txtValorGasto.setText(valorFormatado.replace(".", ","));
        holder.txtDataGasto.setText(dataGasto);
//        holder.metodoPagamentoGasto.setText(metodoPagemento);
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                int idx_id = cursor.getColumnIndex(Contract.GastoEntry._ID);
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
        TextView txtDescricaoGasto;  
        TextView txtValorGasto;
        TextView txtDataGasto; 
        TextView metodoPagamentoGasto; 
        
        public ViewHolder(View itemView) {
            super(itemView);
            txtDescricaoGasto = itemView.findViewById(R.id.text_view_descricao_gasto);
            txtValorGasto = itemView.findViewById(R.id.text_view_valor_gasto);
            txtDataGasto = itemView.findViewById(R.id.text_view_data_gasto);
            metodoPagamentoGasto = itemView.findViewById(R.id.edit_text_metodo_pagamento);
            
        }
    }
}
