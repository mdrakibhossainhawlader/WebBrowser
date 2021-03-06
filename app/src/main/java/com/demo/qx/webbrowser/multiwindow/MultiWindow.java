package com.demo.qx.webbrowser.multiwindow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.demo.qx.webbrowser.MyApp;
import com.demo.qx.webbrowser.R;
import com.demo.qx.webbrowser.home.WebFragment;

import java.util.Iterator;

import static com.demo.qx.webbrowser.MyApp.RESULT_NO_BACK;
import static com.demo.qx.webbrowser.MyApp.RESULT_NO_FRAGMENT_REMAIN;

/**
 * Created by qx on 16/10/24.
 */

public class MultiWindow extends AppCompatActivity implements MultiFragment.RemoveListener, MultiFragment.OpenListener, View.OnClickListener {
    ViewPager mViewPager;
    MyAdapter mMyAdapter;
    ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    void initView() {
        setContentView(R.layout.multiwindow);
        mImageView = (ImageView) findViewById(R.id.add_page);
        mImageView.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        MyApp.sMultiFragments.clear();
        for (int i = 0; i < MyApp.sWebFragmentList.size(); i++) {
            MultiFragment tmp = MultiFragment.getInstance(MyApp.sWebFragmentList.get(i).getAId(), MyApp.sWebFragmentList.get(i).mCurrentTitle, MyApp.sWebFragmentList.get(i).mBitmap, this, this);
            MyApp.sMultiFragments.add(tmp);
        }
        mMyAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMyAdapter);
        mViewPager.setPageMargin(40);
        mViewPager.setOffscreenPageLimit(3);
    }

    void noFragmentRemain() {
        Intent intent = getIntent();
        setResult(RESULT_NO_FRAGMENT_REMAIN, intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void remove(long hCode) {
        if (MyApp.sWebFragmentList.size() == 0||MyApp.sMultiFragments.size() == 0) return;
        Iterator<MultiFragment> iterator = MyApp.sMultiFragments.iterator();
        while (iterator.hasNext()) {
            MultiFragment temp = iterator.next();
            if (temp.getHCode() == hCode) {
                iterator.remove();
            }
        }
        mMyAdapter.notifyDataSetChanged();
        Iterator<WebFragment> webIterator = MyApp.sWebFragmentList.iterator();
        while (webIterator.hasNext()) {
            WebFragment temp = webIterator.next();
            if (temp.getAId() == hCode) {
                webIterator.remove();
            }
        }
        if (MyApp.sMultiFragments.size() == 0) noFragmentRemain();
    }

    @Override
    public void open(long hCode) {
        int index = 0;
        for (int i = 0; i < MyApp.sWebFragmentList.size(); i++) {
            if (MyApp.sWebFragmentList.get(i).getAId() == hCode) {
                index = i;
                break;
            }
        }
        Intent intent = getIntent();
        intent.putExtra("ID", index);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getIntent();
        setResult(RESULT_NO_BACK, intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_page:
                noFragmentRemain();
                break;
        }
    }
}