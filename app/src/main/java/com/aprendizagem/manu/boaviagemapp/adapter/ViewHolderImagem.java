package com.aprendizagem.manu.boaviagemapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.aprendizagem.manu.boaviagemapp.R;

class ViewHolderImagem extends RecyclerView.ViewHolder {

    final ImageView imageViewImagem;

    ViewHolderImagem(View itemView) {
        super(itemView);
        imageViewImagem = itemView.findViewById(R.id.image_view_imagem_galeria);

    }
}