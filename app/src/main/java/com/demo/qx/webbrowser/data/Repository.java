package com.demo.qx.webbrowser.data;

import android.support.annotation.NonNull;

import com.demo.qx.webbrowser.data.Local.LocalDataSource;
import com.demo.qx.webbrowser.data.Remote.RemoteDataSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qx on 16/10/25.
 */

public class Repository implements DataSource {
    private static Repository INSTANCE = null;
    boolean mBookmarksCacheIsDirty = false;
    boolean mHistoryCacheIsDirty = false;
    private final DataSource mRemoteDataSource;
    private final DataSource mLocalDataSource;
    Map<String, WebPage> mCachedBookmarks;
    Map<String, WebPage> mCachedHistory;


    private Repository(@NonNull DataSource remoteDataSource,
                       @NonNull DataSource localDataSource) {
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
    }

    public static Repository getInstance(RemoteDataSource remoteDataSource, LocalDataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new Repository(remoteDataSource, localDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void getBookmarks(@NonNull  final LoadCallback callback) {
        if (mCachedBookmarks != null && !mBookmarksCacheIsDirty) {
            callback.onLoaded(new ArrayList<>(mCachedBookmarks.values()));
            return;
        }

            mLocalDataSource.getBookmarks(new LoadCallback() {
                @Override
                public void onLoaded(List<WebPage> webPages) {
                    refreshCache(webPages);
                    callback.onLoaded(new ArrayList<>(mCachedBookmarks.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }

            });
        }


    @Override
    public void refreshBookmarks() {
        mBookmarksCacheIsDirty = true;
    }

    @Override
    public void addBookmarks(WebPage webPage) {
        mLocalDataSource.addBookmarks(webPage);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedBookmarks == null) {
            mCachedBookmarks = new LinkedHashMap<>();
        }
        mCachedBookmarks.put(webPage.getUrl(), webPage);
    }

    @Override
    public void deleteBookmarks(@NonNull String url) {
        mLocalDataSource.deleteBookmarks(url);

        mCachedBookmarks.remove(url);
    }

    @Override
    public void addHistory(WebPage webPage) {

    }

    @Override
    public void deleteAllHistory() {
        mLocalDataSource.deleteAllHistory();

        if (mCachedHistory == null) {
            mCachedHistory = new LinkedHashMap<>();
        }
        mCachedHistory.clear();
    }
    private void refreshCache(List<WebPage> webPages) {
        if (mCachedBookmarks == null) {
            mCachedBookmarks = new LinkedHashMap<>();
        }
        mCachedBookmarks.clear();
        for (WebPage webPage : webPages) {
            mCachedBookmarks.put(webPage.getUrl(), webPage);
        }
        mBookmarksCacheIsDirty = false;
    }

}
