package com.aprendizagem.manu.boaviagemapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aprendizagem.manu.boaviagemapp.R;

class ViewHolderGasto extends RecyclerView.ViewHolder {

    final TextView txtDescricaoGasto;
    final TextView txtValorGasto;
    final TextView txtDataGasto;

    ViewHolderGasto(View itemView) {
        super(itemView);
        txtDescricaoGasto = itemView.findViewById(R.id.text_view_descricao_gasto);
        txtValorGasto = itemView.findViewById(R.id.text_view_valor_gasto);
        txtDataGasto = itemView.findViewById(R.id.text_view_data_gasto);

    }
}