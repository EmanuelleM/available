package com.aprendizagem.manu.estudobancodedados;

public class Constantes {

    private static String ID_DO_USUARIO;
    private static int ID_VIAGEM_SELECIONADA;
    public static final String APP_NAME = "Boa viagem";
    public static final	String	AUTH_TOKEN_TYPE	= "oauth2:https://www.googleapis.com/auth/calendar";
    public static final	String	API_KEY	= "AIzaSyD7kbECpHuK6IYW1I4wICDoEnmZXkXjHIo";

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

}
