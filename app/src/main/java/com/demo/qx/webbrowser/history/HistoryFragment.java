package com.demo.qx.webbrowser.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.qx.webbrowser.R;
import com.demo.qx.webbrowser.data.WebPage;

import java.util.List;

/**
 * Created by qx on 16/10/26.
 */

public class HistoryFragment extends Fragment implements HistoryContract.View{
    private HistoryContract.Presenter mPresenter;
    private HistoryAdapter mListAdapter;
    private ListView mListView;
    private TextView mTextView;
    private List<WebPage> mWebPages;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }
    @Override
    public void setPresenter(HistoryContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebPages=mPresenter.getHistory();
        mListAdapter = new HistoryAdapter(mWebPages, mItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        mListView = (ListView) root.findViewById(R.id.history_list);
        mListView.setAdapter(mListAdapter);
        mTextView= (TextView) root.findViewById(R.id.no_history);

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mPresenter.clearCompletedTasks();
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mPresenter.loadTasks(true);
                break;
        }
        return true;
    }*/

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
    }*/

    @Override
    public void showHistory(List<WebPage> webPages) {
        mListAdapter.replaceData(webPages);

        mListView.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.GONE);
    }

    @Override
    public void showNoHistory() {
        mListView.setVisibility(View.GONE);
        mTextView.setVisibility(View.VISIBLE);
    }

    private static class HistoryAdapter extends BaseAdapter {

        private List<WebPage> mWebPage;
        private ItemListener mItemListener;

        public HistoryAdapter(List<WebPage> webPages, ItemListener itemListener) {
            setList(webPages);
            mItemListener = itemListener;
        }

        public void replaceData(List<WebPage> webPages) {
            setList(webPages);
            notifyDataSetChanged();
        }

        private void setList(List<WebPage> webPages) {
            mWebPage = webPages;
        }

        @Override
        public int getCount() {
            return mWebPage.size();
        }

        @Override
        public WebPage getItem(int i) {
            return mWebPage.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.bookmarks_item, viewGroup, false);
            }

            final WebPage webPage = getItem(i);

            TextView titleTV = (TextView) rowView.findViewById(R.id.title);
            TextView addressTV = (TextView) rowView.findViewById(R.id.address);
            titleTV.setText(webPage.getTitle());
            addressTV.setText(webPage.getUrl());
            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //// TODO: 16/10/27 menu
                    return false;
                }
            });
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onClick(webPage);
                }
            });

            return rowView;
        }
    }

    ItemListener mItemListener = new ItemListener() {
        @Override
        public void onClick(WebPage clickedWebPage) {
            ((HistoryActivity)getActivity()).openURL(clickedWebPage.getUrl());
        }
    };

    public interface ItemListener {

        void onClick(WebPage clickedWebPage);

    }
}
