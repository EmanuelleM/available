package com.aprendizagem.manu.estudobancodedados.model;

import java.util.Date;

public class Viagem {
    private String id;
    private String destino;
    private Integer tipoViagem;
    private String localHospedagem;
    private Date dataChegada;
    private Date dataSaida;
    private String orcamento;
    private String id_usuario;

    public Viagem() {
    }

    public Viagem(String id, String destino, Integer tipoViagem, String localHospedagem, Date dataChegada, Date dataSaida, String orcamento, String id_usuario) {
        this.id = id;
        this.destino = destino;
        this.tipoViagem = tipoViagem;
        this.localHospedagem = localHospedagem;
        this.dataChegada = dataChegada;
        this.dataSaida = dataSaida;
        this.orcamento = orcamento;
        this.id_usuario = id_usuario;
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

    public Integer getTipoViagem() {
        return tipoViagem;
    }

    public void setTipoViagem(Integer tipoViagem) {
        this.tipoViagem = tipoViagem;
    }

    public String getLocalHospedagem() {
        return localHospedagem;
    }

    public void setLocalHospedagem(String localHospedagem) {
        this.localHospedagem = localHospedagem;
    }

    public Date getDataChegada() {
        return dataChegada;
    }

    public void setDataChegada(Date dataChegada) {
        this.dataChegada = dataChegada;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(String orcamento) {
        this.orcamento = orcamento;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }
}
