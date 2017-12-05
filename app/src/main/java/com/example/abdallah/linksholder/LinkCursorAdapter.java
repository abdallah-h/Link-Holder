package com.example.abdallah.linksholder;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.abdallah.linksholder.data.Contract;

/**
 * Created by abdallah on 2017-12-02.
 */

public class LinkCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link LinkCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public LinkCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView urlTextView = (TextView) view.findViewById(R.id.url);

        int nameColumnIndex = cursor.getColumnIndex(Contract.DataEntry.COLUMN_NAME);
        int urlColumnIndex = cursor.getColumnIndex(Contract.DataEntry.COLUMN_URL);

        String name = cursor.getString(nameColumnIndex);
        String url = cursor.getString(urlColumnIndex);

        nameTextView.setText(name);
        urlTextView.setText(url);

    }
}
