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

    /**
     * URI matcher code for the content URI for the pets table
     */
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
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
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

        // Return the cursor
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
// Check that the name is not null
        String name = values.getAsString(GastoEntry.COLUMN_DESCRICAO_GASTO);
        if (name == null) {
            throw new IllegalArgumentException("Informe uma descricao para o gasto");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert the new pet with the given values
        long id = database.insert(GastoEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertViagem(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ViagemEntry.COLUMN_DESTINO);
        if (name == null) {
            throw new IllegalArgumentException("Informe um destino");
        }
        // Check that the gender is valid
        Integer razao = values.getAsInteger(ViagemEntry.COLUMN_RAZAO);
        if (razao == null || !ViagemEntry.getRazaoDaViagem(razao)) {
            throw new IllegalArgumentException("Requer um razao valida");
        }
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert the new pet with the given values
        long id = database.insert(ViagemEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
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
                // For the VIAGEM_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ViagemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateViagem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateViagem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ViagemEntry.COLUMN_DESTINO)) {
            String name = values.getAsString(ViagemEntry.COLUMN_DESTINO);
            if (name == null) {
                throw new IllegalArgumentException("Viagem requer um destino");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(ViagemEntry.COLUMN_RAZAO)) {
            Integer razao = values.getAsInteger(ViagemEntry.COLUMN_RAZAO);
            if (razao == null || !ViagemEntry.getRazaoDaViagem(razao)) {
                throw new IllegalArgumentException("Razao requerida");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ViagemEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VIAGEM:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ViagemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIAGEM_ID:
                // Delete a single row given by the ID in the URI
                selection = ViagemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ViagemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
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
