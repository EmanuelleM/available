package com.aprendizagem.manu.boaviagemapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aprendizagem.manu.boaviagemapp.database.Contract.GastoEntry;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ViagemEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "boaviagem.db";

    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_VIAGEM_TABLE = "CREATE TABLE " + ViagemEntry.TABLE_NAME + " ("
                + ViagemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ViagemEntry.COLUMN_DESTINO + " TEXT NOT NULL, "
                + ViagemEntry.COLUMN_RAZAO + " INTEGER, "
                + ViagemEntry.COLUMN_LOCAL_ACOMODACAO + " TEXT, "
                + ViagemEntry.COLUMN_DATA_CHEGADA + " TEXT, "
                + ViagemEntry.COLUMN_DATA_PARTIDA + " TEXT, "
                + ViagemEntry.COLUMN_GASTO_TOTAL + " TEXT, "
                + ViagemEntry.COLUMN_ID_USUARIO + " TEXT);";

        String SQL_CREATE_GASTO_TABLE = "CREATE TABLE " + GastoEntry.TABLE_NAME + " ("
                + GastoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GastoEntry.COLUMN_DESCRICAO_GASTO + " TEXT NOT NULL, "
                + GastoEntry.COLUMN_VIAGEM_ID + " INTEGER, "
                + GastoEntry.COLUMN_METODO_PAGAMENTO + " INTEGER, "
                + GastoEntry.COLUMN_VALOR_GASTO + " TEXT, "
                + GastoEntry.COLUMN_DATA_GASTO + " TEXT, "
                + GastoEntry.COLUMN_ID_USUARIO + " TEXT);";

        db.execSQL(SQL_CREATE_VIAGEM_TABLE);
        db.execSQL(SQL_CREATE_GASTO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ViagemEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GastoEntry.TABLE_NAME);
        onCreate(db);

    }
}
