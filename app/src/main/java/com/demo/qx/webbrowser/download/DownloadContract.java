package com.demo.qx.webbrowser.download;

import com.demo.qx.webbrowser.BasePresenter;
import com.demo.qx.webbrowser.BaseView;
import com.demo.qx.webbrowser.data.Download;

import java.util.List;

/**
 * Created by qx on 16/10/26.
 */

public interface DownloadContract {
    interface View extends BaseView<Presenter> {
        void showDownload(List<Download> downloads);

        void showNoDownload();
    }

    interface Presenter extends BasePresenter {


        void loadDownload(boolean forceUpdate);

        void pause(Download download);

        void resume(Download download);

        void removeDownloadAndFile(Download download);

        void removeDownload(Download download);

        void startAll();

        void pauseAll();
    }
}
