package com.aprendizagem.manu.boaviagemapp.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ViagemEntry;

import java.util.Locale;

public class ViagemAdapter extends
        RecyclerView.Adapter<ViewHolderViagem> {

    private Cursor mCursor;
    private final ItemClickListenerAdapter mListener;

    public ViagemAdapter(ItemClickListenerAdapter aoClicarNoItem) {
        mListener = aoClicarNoItem;
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
                mCursor.moveToPosition(position);
                if (mListener != null) mListener.itemFoiClicado(mCursor);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolderViagem holder, final int position) {
        mCursor.moveToPosition(position);

        int destinoColumnIndex = mCursor.getColumnIndex(ViagemEntry.COLUMN_DESTINO);
        int razaoViagemColumnIndex = mCursor.getColumnIndex(ViagemEntry.COLUMN_RAZAO);
        int gastoViagemColumnIndex = mCursor.getColumnIndex(ViagemEntry.COLUMN_GASTO_TOTAL);
        int dataChegadaViagemColumnIndex = mCursor.getColumnIndex(ViagemEntry.COLUMN_DATA_CHEGADA);
        int dataPartidaViagemColumnIndex = mCursor.getColumnIndex(ViagemEntry.COLUMN_DATA_PARTIDA);

        final String destino = mCursor.getString(destinoColumnIndex);
        double gastoViagem = mCursor.getDouble(gastoViagemColumnIndex);
        String dataChegada = mCursor.getString(dataChegadaViagemColumnIndex);
        String dataPartida = mCursor.getString(dataPartidaViagemColumnIndex);

        int razaoViagem = mCursor.getInt(razaoViagemColumnIndex);

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
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        long valueReturn = 0;
        if (mCursor != null && mCursor.moveToPosition(position)) {
            int idx_id = mCursor.getColumnIndex(ViagemEntry._ID);
            valueReturn = mCursor.getLong(idx_id);
        }
        return valueReturn;
    }

    public void setmCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getmCursor() {
        return mCursor;
    }

}

