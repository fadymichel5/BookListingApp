package com.example.shata.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Shata on 07/07/2017.
 */

public class BookLoader extends AsyncTaskLoader<ArrayList<Book>> {

    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Book> loadInBackground() {
        if (mUrl == null)
            return null;

        ArrayList<Book> earthquake = Utilis.fetchBookData(mUrl);
        Log.i("earthquake", "we hnaaaaaaaaaaaaaaaaaaa hhehehehheheh");
        return earthquake;
    }
}
