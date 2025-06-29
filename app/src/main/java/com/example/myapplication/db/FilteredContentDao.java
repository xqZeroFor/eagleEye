package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.myapplication.model.FilteredContent;
import java.util.ArrayList;
import java.util.List;

public class FilteredContentDao {
    private FilteredContentDbHelper dbHelper;
    private SQLiteDatabase database;

    public FilteredContentDao(Context context) {
        dbHelper = new FilteredContentDbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertFilteredContent(String title, String source, String filterType) {
        ContentValues values = new ContentValues();
        values.put(FilteredContentDbHelper.COLUMN_TITLE, title);
        values.put(FilteredContentDbHelper.COLUMN_SOURCE, source);
        values.put(FilteredContentDbHelper.COLUMN_FILTER_TYPE, filterType);

        return database.insert(FilteredContentDbHelper.TABLE_NAME, null, values);
    }

    public List<FilteredContent> getAllFilteredContent() {
        List<FilteredContent> contents = new ArrayList<>();
        Cursor cursor = database.query(
                FilteredContentDbHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FilteredContentDbHelper.COLUMN_TIMESTAMP + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                FilteredContent content = new FilteredContent(
                        cursor.getLong(cursor.getColumnIndexOrThrow(FilteredContentDbHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FilteredContentDbHelper.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FilteredContentDbHelper.COLUMN_SOURCE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FilteredContentDbHelper.COLUMN_FILTER_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FilteredContentDbHelper.COLUMN_TIMESTAMP))
                );
                contents.add(content);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contents;
    }

    public void deleteAllContent() {
        database.delete(FilteredContentDbHelper.TABLE_NAME, null, null);
    }
} 