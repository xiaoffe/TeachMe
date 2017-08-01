package yxf.teachme.ui.base;

import android.view.View;

/**
 * Created by Administrator on 2017/7/20.
 */

public abstract class BaseHolder<T> {

    private View mRootView;
    protected T mDatas;

    public BaseHolder() {
        this.mRootView = initView();
    }

    public View getRootView() {
        return mRootView;
    }

    public void setDatas(T datas) {
        this.mDatas = datas;

        reflashUI(mDatas);
    }

    protected abstract View initView();

    protected abstract void reflashUI(T datas);

}