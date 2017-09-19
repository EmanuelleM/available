package com.aprendizagem.manu.boaviagemapp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aprendizagem.manu.boaviagemapp.database.Contract;
import com.aprendizagem.manu.boaviagemapp.database.Contract.GastoEntry;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ImagemGaleriaEntry;
import com.aprendizagem.manu.boaviagemapp.database.Contract.ViagemEntry;
import com.aprendizagem.manu.boaviagemapp.database.DatabaseHelper;

public class Provider extends ContentProvider {

    private static final String LOG_TAG = Provider.class.getSimpleName();

    private static final int VIAGEM = 100;
    private static final int VIAGEM_ID = 101;

    private static final int GASTO = 102;
    private static final int GASTO_ID = 103;
    private static final int GASTOS_VIAGEM_ID = 104;

    private static final int IMAGEM = 105;
    private static final int IMAGEM_ID = 106;

    private String mFalhaInsercao = "Falha ao inserir linha";
    private String mUriDesconhecida = "URI desconhecida ";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_VIAGENS, VIAGEM);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_VIAGENS + "/#", VIAGEM_ID);

        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_GASTOS, GASTO);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_GASTOS + "/#", GASTO_ID);

        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_IMAGENS, IMAGEM);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_IMAGENS + "/#", IMAGEM_ID);
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

            case IMAGEM:
                cursor = database.query(
                        ImagemGaleriaEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case IMAGEM_ID:
                selection = ImagemGaleriaEntry.COLUMN_VIAGEM_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(
                        ImagemGaleriaEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new IllegalArgumentException(mUriDesconhecida + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VIAGEM:
                return insertViagem(uri, contentValues);
            case GASTO:
                return insertGasto(uri, contentValues);
            case IMAGEM:
                return insertImagem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Inserção não suportada" + uri);
        }
    }

    private Uri insertImagem(Uri uri, ContentValues values) {
        int idViagem = values.getAsInteger(ImagemGaleriaEntry.COLUMN_VIAGEM_ID);
        if (idViagem == 0) {
            throw new IllegalArgumentException("Necessário informar o id da viagem");

        }

        String caminho = values.getAsString(ImagemGaleriaEntry.COLUMN_CAMINHO_IMAGEM);
        if (caminho == null) {
            throw new IllegalArgumentException("Necessário informar um caminho");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ImagemGaleriaEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, mFalhaInsercao + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertGasto(Uri uri, ContentValues values) {
        String name = values.getAsString(GastoEntry.COLUMN_DESCRICAO_GASTO);
        if (name == null) {
            throw new IllegalArgumentException("Informe uma descricao para o gasto");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(GastoEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, mFalhaInsercao + uri);
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
            Log.e(LOG_TAG, mFalhaInsercao + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        if (match == VIAGEM_ID) {
            selection = ViagemEntry._ID + "=?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        } else if (match != VIAGEM) {

            throw new IllegalArgumentException("Atualização não é permitida para o item " +
                    uri);
        }
        return updateViagem(uri, contentValues, selection, selectionArgs);


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

            case GASTO:
                rowsDeleted = database.delete(GastoEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case GASTO_ID:
                selection = GastoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(GastoEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case IMAGEM:
                rowsDeleted = database.delete(ImagemGaleriaEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case IMAGEM_ID:
                selection = ImagemGaleriaEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ImagemGaleriaEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Não é possivel deletar esse item " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        String stringMatcher;
        switch (match) {
            case VIAGEM:
                stringMatcher = ViagemEntry.CONTENT_LIST_TYPE;
                break;
            case VIAGEM_ID:
                stringMatcher = ViagemEntry.CONTENT_ITEM_TYPE;
                break;
            case GASTO:
                stringMatcher = GastoEntry.CONTENT_LIST_TYPE;
                break;
            case GASTO_ID:
                stringMatcher = GastoEntry.CONTENT_ITEM_TYPE;
                break;
            case GASTOS_VIAGEM_ID:
                stringMatcher = GastoEntry.CONTENT_ITEM_TYPE;
                break;
            case IMAGEM:
                stringMatcher = ImagemGaleriaEntry.CONTENT_ITEM_TYPE;
                break;
            case IMAGEM_ID:
                stringMatcher = ImagemGaleriaEntry.CONTENT_ITEM_TYPE;
                break;
            default:
                throw new IllegalStateException(mUriDesconhecida + uri + " para o item " +
                        match);
        }
        return stringMatcher;
    }
}
