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

    private Context privateContext;
    private Cursor privateCursor;
    private ItemClickListenerAdapter privateListener;

    int index_id;

    public ViagemAdapter(ItemClickListenerAdapter aoClicarNoItem, Context applicationContext) {
        privateListener = aoClicarNoItem;
        privateContext = applicationContext;
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
                privateCursor.moveToPosition(position);
                if (privateListener != null) privateListener.itemFoiClicado(privateCursor);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolderViagem holder, final int position) {
        privateCursor.moveToPosition(position);

        int destinoColumnIndex = privateCursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DESTINO);
        int razaoViagemColumnIndex = privateCursor.getColumnIndex(Contract.ViagemEntry.COLUMN_RAZAO);
        int gastoViagemColumnIndex = privateCursor.getColumnIndex(Contract.ViagemEntry.COLUMN_GASTO_TOTAL);
        int dataChegadaViagemColumnIndex = privateCursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DATA_CHEGADA);
        int dataPartidaViagemColumnIndex = privateCursor.getColumnIndex(Contract.ViagemEntry.COLUMN_DATA_PARTIDA);

        final String destino = privateCursor.getString(destinoColumnIndex);
        double gastoViagem = privateCursor.getDouble(gastoViagemColumnIndex);
        String dataChegada = privateCursor.getString(dataChegadaViagemColumnIndex);
        String dataPartida = privateCursor.getString(dataPartidaViagemColumnIndex);

        int razaoViagem = privateCursor.getInt(razaoViagemColumnIndex);

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
        return (privateCursor != null) ? privateCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (privateCursor != null) {
            if (privateCursor.moveToPosition(position)) {
                index_id = privateCursor.getColumnIndex(Contract.ViagemEntry._ID);
                return privateCursor.getLong(index_id);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public void setPrivateCursor(Cursor newCursor) {
        privateCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getPrivateCursor() {
        return privateCursor;
    }

}

