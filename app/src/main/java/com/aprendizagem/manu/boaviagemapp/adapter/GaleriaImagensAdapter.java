package com.aprendizagem.manu.boaviagemapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aprendizagem.manu.boaviagemapp.R;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ImagemGaleriaEntry;
import com.squareup.picasso.Picasso;

public class GaleriaImagensAdapter extends RecyclerView.Adapter<ViewHolderImagem> {

    private Context mContext;
    private Cursor mCursor;
    private ItemClickListenerAdapter mListener;

    public GaleriaImagensAdapter(ItemClickListenerAdapter aoClicarNoItem, Context applicationContext) {
        mListener = aoClicarNoItem;
        mContext = applicationContext;
    }

    @Override
    public ViewHolderImagem onCreateViewHolder(ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_imagem_galeria, parent, false);

        final ViewHolderImagem vh = new ViewHolderImagem(v);

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
    public void onBindViewHolder(final ViewHolderImagem holder, final int position) {
        mCursor.moveToPosition(position);

        int caminhoImagem = mCursor.getColumnIndex(ImagemGaleriaEntry.COLUMN_CAMINHO_IMAGEM);

        String stringCaminhoImagem = mCursor.getString(caminhoImagem);

        Picasso.with(mContext).
                load(stringCaminhoImagem)
                .into(holder.imageViewImagem);

    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        long valueReturn = 0;
        if (mCursor != null && mCursor.moveToPosition(position)) {
                int idx_id = mCursor.getColumnIndex(ImagemGaleriaEntry._ID);
                valueReturn = mCursor.getLong(idx_id);
            }
            return valueReturn;
    }

    public Cursor getmCursor() {
        return mCursor;
    }

    public void setmCursor(Cursor newCursor) {
        notifyDataSetChanged();
        mCursor = newCursor;
    }
}