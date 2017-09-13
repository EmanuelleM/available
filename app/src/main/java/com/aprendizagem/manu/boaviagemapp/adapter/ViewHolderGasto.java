package com.aprendizagem.manu.boaviagemapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aprendizagem.manu.boaviagemapp.R;

class ViewHolderGasto extends RecyclerView.ViewHolder {
    TextView txtDescricaoGasto;
    TextView txtValorGasto;
    TextView txtDataGasto;
    TextView metodoPagamentoGasto;

    ViewHolderGasto(View itemView) {
        super(itemView);
        txtDescricaoGasto = itemView.findViewById(R.id.text_view_descricao_gasto);
        txtValorGasto = itemView.findViewById(R.id.text_view_valor_gasto);
        txtDataGasto = itemView.findViewById(R.id.text_view_data_gasto);
        metodoPagamentoGasto = itemView.findViewById(R.id.edit_text_metodo_pagamento);

    }
}