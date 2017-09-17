package com.aprendizagem.manu.boaviagemapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aprendizagem.manu.boaviagemapp.R;

class ViewHolderImagem extends RecyclerView.ViewHolder {
    TextView textViewIdImagem;
    TextView textViewCaminhoImagem;


    ViewHolderImagem(View itemView) {
        super(itemView);
        textViewIdImagem = itemView.findViewById(R.id.text_view_id_imagem);
        textViewCaminhoImagem = itemView.findViewById(R.id.text_view_caminho_imagem);

    }
}