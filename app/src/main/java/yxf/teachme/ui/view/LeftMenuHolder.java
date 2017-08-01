package yxf.teachme.ui.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import yxf.teachme.MainApplication;
import yxf.teachme.R;
import yxf.teachme.ui.base.BaseHolder;

/**
 * Created by Administrator on 2017/7/20.
 */

public class LeftMenuHolder extends BaseHolder<String>{
    private Activity mContext;
    public LeftMenuHolder(Activity context) {
        mContext = context;
    }

    @Override
    protected View initView() {
        View view = View.inflate(MainApplication.getAppContext(), R.layout.holder_left_menu, null);
        return view;
    }

    @Override
    protected void reflashUI(String datas) {
    }
}
