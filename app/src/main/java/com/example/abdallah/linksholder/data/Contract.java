package com.example.abdallah.linksholder.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by abdallah on 2017-12-02.
 */

public final class Contract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private Contract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.abdallah.linksholder";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH = "link";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */

    public static abstract class DataEntry implements BaseColumns {

        public static final String TABLE_NAME = "links";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_URL = "url";

        /**
         * The content URI to access the pet data in the provider
         */
        /*
         inside each of the Entry classes in the contract,
         we create a full URI for the class as a constant called CONTENT_URI.
         The Uri.withAppendedPath() method appends the BASE_CONTENT_URI
         (which contains the scheme and the content authority) to the path segment.
        */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);

        /**
         * The MIME type
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        /**
         * The MIME type
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
    }
}
