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
    private Cursor cursor;
    private GaleriaImagensAdapter.ItemClickListenerAdapter mListener;

    public GaleriaImagensAdapter(GaleriaImagensAdapter.ItemClickListenerAdapter aoClicarNoItem, Context applicationContext) {
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
                cursor.moveToPosition(position);
                if (mListener != null) mListener.itemFoiClicado(cursor);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolderImagem holder, final int position) {
        cursor.moveToPosition(position);

        int caminhoImagem = cursor.getColumnIndex(ImagemGaleriaEntry.COLUMN_CAMINHO_IMAGEM);

        String stringCaminhoImagem = cursor.getString(caminhoImagem);

        Picasso.with(mContext).
                load(stringCaminhoImagem)
                .into(holder.imageViewImagem);

    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                int idx_id = cursor.getColumnIndex(ImagemGaleriaEntry._ID);
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
        notifyDataSetChanged();
        cursor = newCursor;
    }

    public interface ItemClickListenerAdapter {
        void itemFoiClicado(Cursor cursor);
    }
}