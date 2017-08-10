package com.aprendizagem.manu.estudobancodedados.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;
import com.aprendizagem.manu.estudobancodedados.database.Contract.GastoEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "boaviagem.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link DatabaseHelper}.
     *
     * @param context of the app
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_VIAGEM_TABLE = "CREATE TABLE " + ViagemEntry.TABLE_NAME + " ("
                + ViagemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ViagemEntry.COLUMN_DESTINO + " TEXT NOT NULL, "
                + ViagemEntry.COLUMN_RAZAO + " INTEGER, "
                + ViagemEntry.COLUMN_LOCAL_ACOMODACAO + " TEXT, "
                + ViagemEntry.COLUMN_DATA_CHEGADA + " TEXT, "
                + ViagemEntry.COLUMN_DATA_PARTIDA + " TEXT);";

        String SQL_CREATE_GASTO_TABLE = "CREATE TABLE " + GastoEntry.TABLE_NAME + " ("
                + GastoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GastoEntry.COLUMN_DESCRICAO_GASTO + " TEXT NOT NULL, "
                + GastoEntry.COLUMN_VIAGEM_ID + " INTEGER, "
                + GastoEntry.COLUMN_METODO_PAGAMENTO + " INTEGER, "
                + GastoEntry.COLUMN_VALOR_GASTO + " TEXT, "
                + GastoEntry.COLUMN_DATA_GASTO + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_VIAGEM_TABLE);
        db.execSQL(SQL_CREATE_GASTO_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.

    }
}
