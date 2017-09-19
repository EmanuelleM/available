package com.aprendizagem.manu.boaviagemapp.modelo;

public class Viagem{

    private String id;
    private String localHospedagem;
    private String destino;
    private int razaoViagem;
    private String dataChegada;
    private String dataSaida;
    private String valorTotal;
    private String idDoUsuario;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalHospedagem() {
        return localHospedagem;
    }

    public void setLocalHospedagem(String localHospedagem) {
        this.localHospedagem = localHospedagem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getRazaoViagem() {
        return razaoViagem;
    }

    public void setRazaoViagem(int razaoViagem) {
        this.razaoViagem = razaoViagem;
    }

    public String getDataChegada() {
        return dataChegada;
    }

    public void setDataChegada(String dataChegada) {
        this.dataChegada = dataChegada;
    }

    public String getDataSaida() {
        return dataSaida;
    }

    public void setDataPartida(String dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getIdDoUsuario() {
        return idDoUsuario;
    }

    public void setIdDoUsuario(String idDoUsuario) {
        this.idDoUsuario = idDoUsuario;
    }

    public static void salvaViagem() {

    }

//        mDestino = mEditTextDestino.getText().toString().trim();
//        localHospedagem = mEditLocalHospedagem.getText().toString().trim();
//        String idDousuario = Constantes.getIdDoUsuario();
//        int razaoViagem = mRazao;
//
//        if (mCurrentViagemUri == null) {
//
//            new TaskSalvaViagem(this, mDestino, localHospedagem,
//                    razaoViagem,
//                    mDataChegada, mDataPartida,
//                    idDousuario).execute();
//        } else {
//
//            ContentValues values = new ContentValues();
//            values.put(Contract.ViagemEntry.COLUMN_DESTINO, mDestino);
//            values.put(Contract.ViagemEntry.COLUMN_LOCAL_ACOMODACAO, localHospedagem);
//            values.put(Contract.ViagemEntry.COLUMN_RAZAO, razaoViagem);
//            values.put(Contract.ViagemEntry.COLUMN_DATA_CHEGADA, mDataChegada);
//            values.put(Contract.ViagemEntry.COLUMN_DATA_PARTIDA, mDataPartida);
//            values.put(Contract.ViagemEntry.COLUMN_ID_USUARIO, idDousuario);
//
//            String selection =
//                    Contract.ViagemEntry.COLUMN_ID_USUARIO + "= '" + idDousuario + "'";
//
//            getContentResolver().update(mCurrentViagemUri, values, selection, null);
//
//            startActivity(new Intent(this, ListaViagemActivity.class));
//            finish();
//
//        }
//    }
}
