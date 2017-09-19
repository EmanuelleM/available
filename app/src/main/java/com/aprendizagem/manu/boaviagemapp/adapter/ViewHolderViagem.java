package com.aprendizagem.manu.boaviagemapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aprendizagem.manu.boaviagemapp.R;

class ViewHolderViagem extends RecyclerView.ViewHolder {

    final TextView campoDestinoViagem;
    final  TextView campoValorTotalGastoViagem;
    final TextView campoRazaoViagem;
    final TextView campoDataChegada;
    final TextView campoDataPartida;

    ViewHolderViagem(View itemView) {
        super(itemView);
        campoDestinoViagem = itemView.findViewById(R.id.text_view_destino_viagem);
        campoValorTotalGastoViagem = itemView.findViewById(R.id.text_view_total_viagem);
        campoRazaoViagem = itemView.findViewById(R.id.text_view_razao_viagem);
        campoDataChegada = itemView.findViewById(R.id.text_view_data_chegada);
        campoDataPartida = itemView.findViewById(R.id.text_view_data_partida);
    }
}