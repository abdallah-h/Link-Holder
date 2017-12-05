package com.example.abdallah.linksholder.data;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.abdallah.linksholder.EditorActivity;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by abdallah on 2017-12-02.
 */

public class Provider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = Provider.class.getSimpleName();

    /** URI matcher code for the content URI for the table */
    private static final int LINK = 100;

    /** URI matcher code for the content URI for a single table */
    private static final int LINK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH, LINK);

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH + "/#", LINK_ID);
    }

    /** Database helper object */
    private DbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case LINK:
                cursor = database.query(Contract.DataEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LINK_ID:
                selection = Contract.DataEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(Contract.DataEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LINK:
                return insertLink(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */


    private Uri insertLink(Uri uri, ContentValues values) {

        String name = values.getAsString(Contract.DataEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("requires valid name");
        }


        // If the to is provided, check that it's greater than or equal to 0 kg
        String url = values.getAsString(Contract.DataEntry.COLUMN_URL);
        if (url == null ) {
            throw new IllegalArgumentException("requires valid url");
        }

        // No need to check the breed, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(Contract.DataEntry.TABLE_NAME, null, values);
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
            case LINK:
                return updateLink(uri, contentValues, selection, selectionArgs);

            case LINK_ID:
                selection = Contract.DataEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateLink(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updateLink(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(Contract.DataEntry.COLUMN_NAME)) {
            String name = values.getAsString(Contract.DataEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("requires valid name");
            }
        }

        if (values.containsKey(Contract.DataEntry.COLUMN_URL)) {
            String url = values.getAsString(Contract.DataEntry.COLUMN_URL);
            if (url == null ) {
                throw new IllegalArgumentException("requires valid url");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(Contract.DataEntry.TABLE_NAME, values, selection, selectionArgs);

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
            case LINK:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(Contract.DataEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LINK_ID:
                // Delete a single row given by the ID in the URI
                selection = Contract.DataEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(Contract.DataEntry.TABLE_NAME, selection, selectionArgs);
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
            case LINK:
                return Contract.DataEntry.CONTENT_LIST_TYPE;
            case LINK_ID:
                return Contract.DataEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
