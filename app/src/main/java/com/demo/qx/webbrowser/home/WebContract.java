package com.demo.qx.webbrowser.home;

import com.demo.qx.webbrowser.BasePresenter;
import com.demo.qx.webbrowser.BaseView;
import com.demo.qx.webbrowser.data.Download;
import com.demo.qx.webbrowser.data.WebPage;
import com.demo.qx.webbrowser.download.downloadUnity.DownloadManager;

/**
 * Created by qx on 16/10/5.
 */

public interface WebContract {
    interface View extends BaseView<Presenter> {
        void setTitle(String title);

        void setAddress(String url);

        void changeProgress(int progress);
    }

    interface Presenter extends BasePresenter {

        void setTitle(String title);

        void setAddress(String url);

        void changeProgress(int progress);

        void addBookmarks(WebPage webPage);

        void addHistory(WebPage webPage);

        void addDownload(Download download, DownloadManager downloadManager);
    }
}
