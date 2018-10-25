package com.framgia.vhlee.musicplus.data.source.local;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class MusicStorage extends AsyncTask<String, Void, List<Track>> {
    private static final String SQL_SELECTION = "=?";
    private Context mContext;
    private TrackDataSource.DataCallback<Track> mCallback;

    public MusicStorage(Context context, TrackDataSource.DataCallback<Track> callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected List<Track> doInBackground(String... paths) {
        return loadTracks();
    }

    @Override
    protected void onPostExecute(List<Track> tracks) {
        super.onPostExecute(tracks);
        if (tracks != null) {
            mCallback.onSuccess(tracks);
        } else {
            mCallback.onFail("");
        }
    }

    private List<Track> loadTracks() {
        List<Track> tracks = new ArrayList<>();
        String[] projections = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID};
        Cursor cursor = mContext.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projections, null, null, MediaStore.Audio.Media.DISPLAY_NAME);
        if (cursor == null) return null;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            Track track = new Track(id, title, artist);
            track.setDownloadUrl(path)
                    .setArtworkUrl(getAlbumArt(albumId))
                    .setOffline(true);
            tracks.add(track);
        }
        cursor.close();
        return tracks;
    }

    private String getAlbumArt(int albumId) {
        String[] projections = {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART};
        String selection = StringUtil.append(MediaStore.Audio.Albums._ID, SQL_SELECTION);
        String[] selectionArgs = {String.valueOf(albumId)};
        Cursor cursorAlbum = mContext.getContentResolver()
                .query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        projections, selection,
                        selectionArgs,
                        null);
        if (cursorAlbum == null) return null;
        String artwork = null;
        if (cursorAlbum.moveToFirst()) {
            artwork = cursorAlbum.getString(
                    cursorAlbum.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
        }
        cursorAlbum.close();
        return artwork;
    }
}
