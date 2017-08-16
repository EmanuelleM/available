package com.aprendizagem.manu.estudobancodedados.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;
import com.aprendizagem.manu.estudobancodedados.database.Contract.GastoEntry;

public class Provider extends ContentProvider {

    public static final String LOG_TAG = Provider.class.getSimpleName();

    private static final int VIAGEM = 100;
    private static final int VIAGEM_ID = 101;

    private static final int GASTO = 102;
    private static final int GASTO_ID = 103;
    private static final int GASTOS_VIAGEM_ID = 104;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_VIAGENS, VIAGEM);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_VIAGENS + "/#", VIAGEM_ID);

        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_GASTOS, GASTO);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_GASTOS + "/#", GASTO_ID);
    }

    private DatabaseHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case VIAGEM:
                cursor = database.query(
                        ViagemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case VIAGEM_ID:
                selection = ViagemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ViagemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case GASTO:
                cursor = database.query(
                        GastoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case GASTO_ID:
                selection = GastoEntry.COLUMN_VIAGEM_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(
                        GastoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VIAGEM:
                return insertViagem(uri, contentValues);
            case GASTO:
                return insertGasto(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertGasto(Uri uri, ContentValues values) {
        String name = values.getAsString(GastoEntry.COLUMN_DESCRICAO_GASTO);
        if (name == null) {
            throw new IllegalArgumentException("Informe uma descricao para o gasto");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(GastoEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertViagem(Uri uri, ContentValues values) {
        String name = values.getAsString(ViagemEntry.COLUMN_DESTINO);
        if (name == null) {
            throw new IllegalArgumentException("Informe um destino");
        }
        Integer razao = values.getAsInteger(ViagemEntry.COLUMN_RAZAO);
        if (razao == null || !ViagemEntry.getRazaoDaViagem(razao)) {
            throw new IllegalArgumentException("Requer um razao valida");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ViagemEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VIAGEM:
                return updateViagem(uri, contentValues, selection, selectionArgs);
            case VIAGEM_ID:
                selection = ViagemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateViagem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateViagem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ViagemEntry.COLUMN_DESTINO)) {
            String name = values.getAsString(ViagemEntry.COLUMN_DESTINO);
            if (name == null) {
                throw new IllegalArgumentException("Viagem requer um destino");
            }
        }

        if (values.containsKey(ViagemEntry.COLUMN_RAZAO)) {
            Integer razao = values.getAsInteger(ViagemEntry.COLUMN_RAZAO);
            if (razao == null || !ViagemEntry.getRazaoDaViagem(razao)) {
                throw new IllegalArgumentException("Razao requerida");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ViagemEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VIAGEM:
                rowsDeleted = database.delete(ViagemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIAGEM_ID:
                selection = ViagemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ViagemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VIAGEM:
                return ViagemEntry.CONTENT_LIST_TYPE;

            case VIAGEM_ID:
                return ViagemEntry.CONTENT_ITEM_TYPE;

            case GASTO:
                return GastoEntry.CONTENT_LIST_TYPE;

            case GASTO_ID:
                return GastoEntry.CONTENT_ITEM_TYPE;

            case GASTOS_VIAGEM_ID:
                return GastoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
