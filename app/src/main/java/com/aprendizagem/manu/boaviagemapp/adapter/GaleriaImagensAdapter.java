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

    private Context privateContext;
    private Cursor privateCursor;
    private ItemClickListenerAdapter privateListener;

    public GaleriaImagensAdapter(ItemClickListenerAdapter aoClicarNoItem, Context applicationContext) {
        privateListener = aoClicarNoItem;
        privateContext = applicationContext;
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
                privateCursor.moveToPosition(position);
                if (privateListener != null) privateListener.itemFoiClicado(privateCursor);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolderImagem holder, final int position) {
        privateCursor.moveToPosition(position);

        int caminhoImagem = privateCursor.getColumnIndex(ImagemGaleriaEntry.COLUMN_CAMINHO_IMAGEM);

        String stringCaminhoImagem = privateCursor.getString(caminhoImagem);

        Picasso.with(privateContext).
                load(stringCaminhoImagem)
                .into(holder.imageViewImagem);

    }

    @Override
    public int getItemCount() {
        return (privateCursor != null) ? privateCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (privateCursor != null) {
            if (privateCursor.moveToPosition(position)) {
                int idx_id = privateCursor.getColumnIndex(ImagemGaleriaEntry._ID);
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
        notifyDataSetChanged();
        privateCursor = newCursor;
    }
}