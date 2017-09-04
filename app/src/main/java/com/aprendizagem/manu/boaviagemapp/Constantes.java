package com.aprendizagem.manu.boaviagemapp;

public class Constantes {

    private static String ID_DO_USUARIO;
    private static int ID_VIAGEM_SELECIONADA;
    private static String NOME_DESTINO_VIAGEM;

    public static String getIdDoUsuario() {
        return ID_DO_USUARIO;
    }

    public static void setIdDoUsuario(String idDoUsuario) {
        ID_DO_USUARIO = idDoUsuario;
    }

    public static int getIdViagemSelecionada() {
        return ID_VIAGEM_SELECIONADA;
    }

    public static void setIdViagemSelecionada(int idViagemSelecionada) {
        ID_VIAGEM_SELECIONADA = idViagemSelecionada;
    }

    public static String getNomeDestinoViagem() {
        return NOME_DESTINO_VIAGEM;
    }

    public static void setNomeDestinoViagem(String nomeDestinoViagem) {
        NOME_DESTINO_VIAGEM = nomeDestinoViagem;
    }
}
