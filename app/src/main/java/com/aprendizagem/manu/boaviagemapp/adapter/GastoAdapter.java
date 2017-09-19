package com.aprendizagem.manu.boaviagemapp.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.database.Contract.GastoEntry;

public class GastoAdapter extends
        RecyclerView.Adapter<ViewHolderGasto> {

    private Cursor mCursor;
    private final ItemClickListenerAdapter mListener;

    public GastoAdapter(ItemClickListenerAdapter aoClicarNoItem) {
        mListener = aoClicarNoItem;
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
                mCursor.moveToPosition(position);
                if (mListener != null) mListener.itemFoiClicado(mCursor);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolderGasto holder, final int position) {
        mCursor.moveToPosition(position);

        int descricaoGastoColumnIndex = mCursor.getColumnIndex(GastoEntry.COLUMN_DESCRICAO_GASTO);
        int valorGastoColumnIndex = mCursor.getColumnIndex(GastoEntry.COLUMN_VALOR_GASTO);
        int dataGastoColumnIndex = mCursor.getColumnIndex(GastoEntry.COLUMN_DATA_GASTO);

        final String descricaoGasto = mCursor.getString(descricaoGastoColumnIndex);
        String valorGasto = mCursor.getString(valorGastoColumnIndex);
        String dataGasto = mCursor.getString(dataGastoColumnIndex);

        holder.txtDescricaoGasto.setText(descricaoGasto);
        holder.txtValorGasto.setText(valorGasto);
        holder.txtDataGasto.setText(dataGasto);
    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        long valueReturn = 0;
        if (mCursor != null && mCursor.moveToPosition(position)) {
            int idx_id = mCursor.getColumnIndex(GastoEntry._ID);
            valueReturn = mCursor.getLong(idx_id);
        }
        return valueReturn;
    }

    public Cursor getmCursor() {
        return mCursor;
    }

    public void setmCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
