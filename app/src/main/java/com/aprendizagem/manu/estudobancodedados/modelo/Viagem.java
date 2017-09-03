package com.aprendizagem.manu.estudobancodedados.modelo;

import java.util.Date;

public class Viagem{
    private String id;
    private String destino;
    private String razaoViagem;
    private String dataChegada;
    private String dataSaida;
    private String valorTotal;

    public Viagem(String id, String destino, String razaoViagem, String dataChegada, String dataSaida, String valorTotal) {
        this.id = id;
        this.destino = destino;
        this.razaoViagem = razaoViagem;
        this.dataChegada = dataChegada;
        this.dataSaida = dataSaida;
        this.valorTotal = valorTotal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getRazaoViagem() {
        return razaoViagem;
    }

    public void setRazaoViagem(String razaoViagem) {
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

    public void setDataSaida(String dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }
}
