package com.aprendizagem.manu.boaviagemapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.database.Contract;

import java.util.Locale;

public class ViagemAdapter extends
        RecyclerView.Adapter<ViewHolderViagem> {

    private Context mContext;
    private Cursor cursor;
    private ViagemAdapter.ItemClickListenerAdapter mListener;

    int index_id;

    public ViagemAdapter(ViagemAdapter.ItemClickListenerAdapter aoClicarNoItem, Context applicationContext) {
        mListener = aoClicarNoItem;
        mContext = applicationContext;
    }

    @Override
    public ViewHolderViagem onCreateViewHolder(ViewGroup parent, final int viewType) {


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_viagem, parent, false);

        final ViewHolderViagem vh = new ViewHolderViagem(v);

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
    public void onBindViewHolder(ViewHolderViagem holder, final int position) {
        cursor.moveToPosition(position);

        int destinoColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DESTINO);
        int razaoViagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_RAZAO);
        int gastoViagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_GASTO_TOTAL);
        int dataChegadaViagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DATA_CHEGADA);
        int dataPartidaViagemColumnIndex = cursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DATA_PARTIDA);

        final String destino = cursor.getString(destinoColumnIndex);
        double gastoViagem = cursor.getDouble(gastoViagemColumnIndex);
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
        String valorFormatado = String.format(Locale.getDefault(), "%.2f", gastoViagem);

        holder.campoDestinoViagem.setText(destino);
        holder.campoValorTotalGastoViagem.setText(valorFormatado.replace(".", ","));
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
                index_id = cursor.getColumnIndex(Contract.ViagemEntry._ID);
                return cursor.getLong(index_id);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public void setCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }

    public interface ItemClickListenerAdapter {
        void itemFoiClicado(Cursor cursor);
    }

//    public static class ViewHolderViagem extends RecyclerView.ViewHolderViagem {
//        TextView campoDestinoViagem;
//        TextView campoValorTotalGastoViagem;
//        TextView campoRazaoViagem;
//        TextView campoDataChegada;
//        TextView campoDataPartida;
//        ImageView acionarManu;
//
//        public ViewHolderViagem(View itemView) {
//            super(itemView);
//            campoDestinoViagem = itemView.findViewById(R.id.text_view_destino_viagem);
//            campoValorTotalGastoViagem = itemView.findViewById(R.id.text_view_total_viagem);
//            campoRazaoViagem = itemView.findViewById(R.id.text_view_razao_viagem);
//            campoDataChegada = itemView.findViewById(R.id.text_view_data_chegada);
//            campoDataPartida = itemView.findViewById(R.id.text_view_data_partida);
//            acionarManu = itemView.findViewById(R.id.image_view_arrow);
//        }
//    }
}

