package yxf.teachme.ui.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import yxf.teachme.R;

/**
 * Created by Administrator on 2017/7/18.
 */

public class RefreshItemView {

    public static final int LOADMORE_LOADING = 0;    // 希望视图显示-->正在加载中的视图
    public static final int LOADMORE_ERROR = 1;    // 希望视图显示-->正在重试的视图
    public static final int LOADMORE_NONE = 2;    // 希望视图显示-->什么也不显示
    private final Activity mActivity;
    private final View mRootView;
    private final LayoutInflater mLayoutInflater;
    private final ViewGroup mParent;
    private LinearLayout mLoadMore;
    private LinearLayout mError;


    public RefreshItemView(Activity activity, ViewGroup parent) {
        this.mActivity = activity;
        this.mLayoutInflater = LayoutInflater.from(activity);
        this.mParent = parent;
        this.mRootView = initView();
    }


    public View getRootView() {
        return mRootView;
    }

    protected View initView() {
        View rootView = mLayoutInflater.inflate(R.layout.item_loadmore, mParent, false);
        //找到两个控件
        mLoadMore = (LinearLayout) rootView.findViewById(R.id.item_loadmore_container_loading);
        mError = (LinearLayout) rootView.findViewById(R.id.item_loadmore_container_retry);

        return rootView;
    }

    public void reflashUI(Integer datas) {

        // 隐藏两个视图
        mLoadMore.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);

        switch (datas) {

            case LOADMORE_LOADING:
                mLoadMore.setVisibility(View.VISIBLE);
                break;

            case LOADMORE_ERROR:
                mError.setVisibility(View.VISIBLE);
                break;
            // 默认状态 两个都要隐藏
            case LOADMORE_NONE:
                break;

        }

    }
}