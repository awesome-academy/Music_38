package com.framgia.vhlee.musicplus.data.source.remote;

import android.os.AsyncTask;

import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public abstract class BaseAsyncTask<T> extends AsyncTask<String, T, List<T>> {

    private static final String REQUEST_METHOD = "GET";
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;
    protected TrackDataSource.DataCallback<T> mCallback;
    protected Exception mException;
    private HttpURLConnection mUrlConnection;

    public BaseAsyncTask(TrackDataSource.DataCallback<T> callback) {
        mCallback = callback;
    }

    @Override
    protected List<T> doInBackground(String... strings) {
        String respond = "";
        try {
            URL url = new URL(strings[0]);
            mUrlConnection = (HttpURLConnection) url.openConnection();
            mUrlConnection.setRequestMethod(REQUEST_METHOD);
            mUrlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            mUrlConnection.setReadTimeout(READ_TIMEOUT);
            mUrlConnection.connect();
            InputStream inputStream = mUrlConnection.getInputStream();
            respond = readResponse(inputStream);
        } catch (IOException e) {
            mException = e;
        }
        mUrlConnection.disconnect();
        return convertJson(respond);
    }

    @Override
    protected void onPostExecute(List<T> datas) {
        super.onPostExecute(datas);
        if (mException == null) {
            mCallback.onSuccess(datas);
        } else {
            mCallback.onFail(mException.getMessage());
        }
    }

    private String readResponse(InputStream inputStream) throws IOException {
        if (inputStream == null) return null;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    public abstract List<T> convertJson(String jsonString);
}
