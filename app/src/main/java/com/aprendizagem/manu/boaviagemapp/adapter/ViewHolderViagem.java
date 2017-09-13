package com.aprendizagem.manu.boaviagemapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aprendizagem.manu.boaviagemapp.R;

class ViewHolderViagem extends RecyclerView.ViewHolder {
    TextView campoDestinoViagem;
    TextView campoValorTotalGastoViagem;
    TextView campoRazaoViagem;
    TextView campoDataChegada;
    TextView campoDataPartida;
    ImageView acionarManu;

    ViewHolderViagem(View itemView) {
        super(itemView);
        campoDestinoViagem = itemView.findViewById(R.id.text_view_destino_viagem);
        campoValorTotalGastoViagem = itemView.findViewById(R.id.text_view_total_viagem);
        campoRazaoViagem = itemView.findViewById(R.id.text_view_razao_viagem);
        campoDataChegada = itemView.findViewById(R.id.text_view_data_chegada);
        campoDataPartida = itemView.findViewById(R.id.text_view_data_partida);
        acionarManu = itemView.findViewById(R.id.image_view_arrow);
    }
}