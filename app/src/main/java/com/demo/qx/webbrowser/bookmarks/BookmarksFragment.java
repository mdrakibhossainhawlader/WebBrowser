package com.demo.qx.webbrowser.bookmarks;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.qx.webbrowser.R;
import com.demo.qx.webbrowser.custom.ItemLongClickedPopWindow;
import com.demo.qx.webbrowser.data.WebPage;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by qx on 16/10/26.
 */

public class BookmarksFragment extends Fragment implements BookmarksContract.View{
    private BookmarksContract.Presenter mPresenter;
    private BookmarksAdapter mListAdapter;
    private ListView mListView;
    private TextView mTextView;
    ItemLongClickedPopWindow mPopupWindow;

    public static BookmarksFragment newInstance() {
        return new BookmarksFragment();
    }
    @Override
    public void setPresenter(BookmarksContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new BookmarksAdapter(new ArrayList<WebPage>(0), mItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        mListView = (ListView) root.findViewById(R.id.bookmarks_list);
        mListView.setAdapter(mListAdapter);
        mTextView= (TextView) root.findViewById(R.id.no_bookmarks);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void showBookmarks(List<WebPage> webPages) {
        mListAdapter.replaceData(webPages);
        mListView.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.GONE);
    }

    @Override
    public void showNoBookmarks() {
        mListView.setVisibility(View.GONE);
        mTextView.setVisibility(View.VISIBLE);
    }

    private class ItemClickedListener implements View.OnClickListener {
        private String title;
        private String address;

        public ItemClickedListener(View item){
            title = ((TextView) item.findViewById(R.id.title)).getText().toString();
            address = ((TextView) item.findViewById(R.id.address)).getText().toString();
        }

        @Override
        public void onClick(View view) {
            mPopupWindow.dismiss();
            if(view.getId()==R.id.item_long_click_modifyBookmarks){
                LayoutInflater modifyBookmarksInflater = LayoutInflater.from(getActivity());
                View modifyBookmarksView =modifyBookmarksInflater.inflate(R.layout.dialog_bookmarks,null);
                final TextView title_input = (TextView)modifyBookmarksView.findViewById(R.id.title_input);
                final TextView address_input = (TextView)modifyBookmarksView.findViewById(R.id.address_input);
                title_input.setText(title);
                address_input.setText(address);
                new AlertDialog.Builder(getActivity())
                        .setTitle("编辑书签")
                        .setView(modifyBookmarksView)
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(address_input.getText()))return;
                                mPresenter.addBookmarks(new WebPage(address_input.getText().toString(),title_input.getText().toString()));
                                mPresenter.removeBookmarks(address);
                            }
                        }).setNegativeButton("取消",null)
                        .create()
                        .show();
            }else if(view.getId()==R.id.item_long_click_deleteBookmarks){
                new AlertDialog.Builder(getActivity())
                        .setTitle("删除书签")
                        .setMessage("是否要删除\""+title+"\"这个书签？")
                        .setPositiveButton("删除",new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.removeBookmarks(address);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create()
                        .show();
            }
        }
    }

    private class BookmarksAdapter extends BaseAdapter {
        private List<WebPage> mWebPage;
        private ItemListener mItemListener;

        public BookmarksAdapter(List<WebPage> webPages, ItemListener itemListener) {
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
                rowView = inflater.inflate(R.layout.item_webpage, viewGroup, false);
            }
            final WebPage webPage = getItem(i);
            TextView titleTV = (TextView) rowView.findViewById(R.id.title);
            TextView addressTV = (TextView) rowView.findViewById(R.id.address);
            titleTV.setText(webPage.getTitle());
            addressTV.setText(webPage.getUrl());
            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mPopupWindow = new ItemLongClickedPopWindow(getActivity(), ItemLongClickedPopWindow.BOOKMARKS_POPUPWINDOW,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    mPopupWindow.showAsDropDown(v, v.getWidth()/2, -v.getHeight()/2);
                    ItemClickedListener itemClickedListener = new ItemClickedListener(v);
                    mPopupWindow.getView(R.id.item_long_click_modifyBookmarks).setOnClickListener(itemClickedListener);
                    mPopupWindow.getView(R.id.item_long_click_deleteBookmarks).setOnClickListener(itemClickedListener);
                    return true;
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
            Intent intent = getActivity().getIntent();
            intent.putExtra("URL", clickedWebPage.getUrl());
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        }
    };

        public interface ItemListener {
        void onClick(WebPage clickedWebPage);
    }
}
