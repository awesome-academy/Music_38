package com.framgia.vhlee.musicplus.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.data.source.local.contract.FavoriteReaderContract;

import java.util.ArrayList;
import java.util.Collections;

public class FavoriteReaderDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    private static final String CAN_NOT_ADD = "Can not add track to favorite";
    private static final String NO_TRACK = "No track in favorities";
    private String CAN_NOT_DELETE = "Can not delete track from favorite";

    private static final String LIKE_ARG = " LIKE ?";
    private static final String QUESTION_ARG = " = ?";
    private static final String OPEN_PARENTHESIS = " (";
    private static final String CLOSE_PARENTHESIS = ")";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String TEXT_TYPE = " TEXT";
    public static final String COMMA = ",";
    public static final String DROP_IF_EXIST = "DROP TABLE IF EXISTS ";

    private static final String SQL_CREATE_FAVORITE_ENTRY =
            "CREATE TABLE " + FavoriteReaderContract.FavoriteEntry.TABLE_NAME + OPEN_PARENTHESIS +
                    FavoriteReaderContract.FavoriteEntry._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA +
                    FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_TRACK_ID + TEXT_TYPE + COMMA +
                    FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_IS_OFFLINE + TEXT_TYPE +
                    CLOSE_PARENTHESIS;

    private static final String SQL_DELETE_FAVORITE_ENTRY =
            DROP_IF_EXIST + FavoriteReaderContract.FavoriteEntry.TABLE_NAME;


    public FavoriteReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FAVORITE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_FAVORITE_ENTRY);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void putTrack(Track track, TrackDataSource.DataCallback<String> callback) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_TRACK_ID, track.getId());
        if (track.isOffline()) {
            values.put(FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_IS_OFFLINE,
                    FavoriteReaderContract.TrackType.OFFLINE);
        } else {
            values.put(FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_IS_OFFLINE,
                    FavoriteReaderContract.TrackType.ONLINE);
        }
        long id = db.insert(FavoriteReaderContract.FavoriteEntry.TABLE_NAME,
                null, values);
        if (id > 0) {
            callback.onSuccess(Collections.singletonList(String.valueOf(id)));
            return;
        }
        callback.onFail(CAN_NOT_ADD);
    }

    public void getTracks(TrackDataSource.DataCallback<Long> callback) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] projection = {
                FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_TRACK_ID,
        };
        Cursor cursor = sqLiteDatabase.query(
                FavoriteReaderContract.FavoriteEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        ArrayList<Long> trackIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_TRACK_ID));
            trackIds.add(Long.valueOf(itemId));
        }
        sqLiteDatabase.close();
        if (trackIds.size() > 0) {
            callback.onSuccess(trackIds);
            return;
        }
        callback.onFail(NO_TRACK);
    }

    public void deleteTrack(Track track, TrackDataSource.DataCallback<String> callback) {
        long trackId = track.getId();
        SQLiteDatabase db = getReadableDatabase();
        String selection = FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_TRACK_ID + LIKE_ARG;
        String[] selectionArgs = { String.valueOf(trackId) };
        int deletedRows = db.delete(
                FavoriteReaderContract.FavoriteEntry.TABLE_NAME,
                selection,
                selectionArgs);
        if (deletedRows > 0) {
            callback.onSuccess(Collections.singletonList(String.valueOf(deletedRows)));
            return;
        }
        callback.onFail(CAN_NOT_DELETE);
    }

    public boolean canAddTrack(Track track) {
        long trackId = track.getId();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] projection = {
                FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_TRACK_ID,
        };
        String selection = FavoriteReaderContract.FavoriteEntry.COLUMN_NAME_TRACK_ID + QUESTION_ARG;
        String[] selectionArgs = { String.valueOf(trackId) };

        Cursor cursor = sqLiteDatabase.query(
                FavoriteReaderContract.FavoriteEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);
        return (cursor.getCount() <= 0);
    }
}
