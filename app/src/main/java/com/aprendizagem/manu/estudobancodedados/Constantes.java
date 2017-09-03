package com.aprendizagem.manu.estudobancodedados;

public class Constantes {

    private static String ID_DO_USUARIO;
    private static int ID_VIAGEM_SELECIONADA;
    private static int EDITAR_ITEM;

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

    public static int getEditarItem() {
        return EDITAR_ITEM;
    }

    public static void setEditarItem(int editarItem) {
        EDITAR_ITEM = editarItem;
    }
}
