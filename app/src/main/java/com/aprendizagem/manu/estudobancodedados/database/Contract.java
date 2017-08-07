package com.aprendizagem.manu.estudobancodedados.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {

    private Contract() {
    }

    public static final String CONTENT_AUTHORITY = "com.aprendizagem.manu.estudobancodedados";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Nessa classe defini quais tabelas eu irei criar, onde irei criar e com quais campos irei criar
     * <p>
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_VIAGENS = "viagens";
    public static final String PATH_GASTOS = "gastos";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ViagemEntry implements BaseColumns {

        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VIAGENS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIAGENS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIAGENS;

        /**
         * Name of database table for travels
         */
        public final static String TABLE_NAME = "viagens";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_DESTINO = "destino";
        public final static String COLUMN_RAZAO = "razao";
        public final static String COLUMN_LOCAL_ACOMODACAO = "local_acomodacao";
        public final static String COLUMN_DATA_CHEGADA = "data_chegada";
        public final static String COLUMN_DATA_PARTIDA = "data_saida";

        /**
         * Possible values for the gender of the pet.
         */
        public static final int RAZAO_DESCONHECIDA= 0;
        public static final int RAZAO_TRABALHO = 1;
        public static final int RAZAO_LAZER = 0;

        public static boolean getRazaoDaViagem(int razao) {
            if (razao == RAZAO_DESCONHECIDA ||razao == RAZAO_TRABALHO || razao == RAZAO_LAZER) {
                return true;
            }
            return false;
        }
    }

    public static final class GastoEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GASTOS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GASTOS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GASTOS;

        public final static String TABLE_NAME = "gastos";

        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_VIAGEM_ID = "viagem_id";
        public final static String COLUMN_DESCRICAO_GASTO = "descricao_gasto";
        public final static String COLUMN_VALOR_GASTO = "valor_gasto";
        public final static String COLUMN_DATA_GASTO = "data_gasto";
    }
}
