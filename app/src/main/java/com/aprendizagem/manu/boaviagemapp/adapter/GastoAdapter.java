package com.aprendizagem.manu.boaviagemapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.database.Contract.GastoEntry;

public class GastoAdapter extends
        RecyclerView.Adapter<ViewHolderGasto> {

    private Context privateContext;
    private Cursor privateCursor;
    private ItemClickListenerAdapter privateListener;

    public GastoAdapter(ItemClickListenerAdapter aoClicarNoItem, Context applicationContext) {
        privateListener = aoClicarNoItem;
        privateContext = applicationContext;
    }

    @Override
    public ViewHolderGasto onCreateViewHolder(ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_gasto, parent, false);

        final ViewHolderGasto vh = new ViewHolderGasto(v);

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
    public void onBindViewHolder(final ViewHolderGasto holder, final int position) {
        privateCursor.moveToPosition(position);

        int descricaoGastoColumnIndex = privateCursor.getColumnIndex(GastoEntry.COLUMN_DESCRICAO_GASTO);
        int valorGastoColumnIndex = privateCursor.getColumnIndex(GastoEntry.COLUMN_VALOR_GASTO);
        int dataGastoColumnIndex = privateCursor.getColumnIndex(GastoEntry.COLUMN_DATA_GASTO);

        final String descricaoGasto = privateCursor.getString(descricaoGastoColumnIndex);
        String valorGasto = privateCursor.getString(valorGastoColumnIndex);
        String dataGasto = privateCursor.getString(dataGastoColumnIndex);

        holder.txtDescricaoGasto.setText(descricaoGasto);
        holder.txtValorGasto.setText(valorGasto);
        holder.txtDataGasto.setText(dataGasto);
    }

    @Override
    public int getItemCount() {
        return (privateCursor != null) ? privateCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (privateCursor != null) {
            if (privateCursor.moveToPosition(position)) {
                int idx_id = privateCursor.getColumnIndex(GastoEntry._ID);
                return privateCursor.getLong(idx_id);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Cursor getPrivateCursor() {
        return privateCursor;
    }

    public void setPrivateCursor(Cursor newCursor) {
        privateCursor = newCursor;
        notifyDataSetChanged();
    }
}
