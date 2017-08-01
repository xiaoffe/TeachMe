package yxf.teachme.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Created by Administrator on 2017/7/17.
 */

public abstract class BaseFragment extends Fragment {
    private LoadingPager mLoadingPager;

    //onCreateView返回了一个FrameLayout
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //对页面进行判断是否为空，空则创建，防止加载数据的时候空指针异常
        if (mLoadingPager == null) {

            //获取到UI加载器
            mLoadingPager = new LoadingPager(getContext()) {

                @Override
                protected LoadingPager.LoadedResult initData() {
                    //子类准备数据
                    return BaseFragment.this.initData();
                }

                @Override
                protected View initSuccessView() {
                    //子类返回界面
                    return BaseFragment.this.initSuccessView();
                }
            };
            baseLoadingDatas();
        } else {
            ViewParent parent = mLoadingPager.getParent();//获取到父亲
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mLoadingPager);
            }
        }

        return mLoadingPager;
    }

    /**
     任何应用其实就只有4种页面类型-->常规的页面
     ① 加载页面
     ② 错误页面
     ③ 空页面

     ④ 成功页面
     ①②③三种页面一个应用基本是固定的
     每一个fragment/activity对应的页面④就不一样
     进入应用的时候显示①,②③④需要加载数据之后才知道显示哪个
     */

    /**
     * @des 真正子子线程中加载数据, 必须实现, 但是不知道具体实现, 交给子类去实现
     * @des 它是和LoadingPager中的initData同名方法
     * @call triggerLoadData方法被调用的时候调用
     */
    protected abstract LoadingPager.LoadedResult initData();


    /**
     * @des 数据加载成功的时候返回具体的成功视图, 必须实现, 但是不知道具体实现, 交给子类实现
     * @des 它是和LoadingPager中的initSuccessView同名方法
     * @call 数据加载成功的时候
     */
    protected abstract View initSuccessView();

    /**
     * 这个方法用于加载数据。由子类调用
     */
    // return BaseFragment.this.initData();最终还是靠这个public方法暴露出去的。
    public void baseLoadingDatas() {
        if (mLoadingPager != null) {
            mLoadingPager.triggleLoadData();
            Log.d("basefragment", "data loaded..");
        }else{
            Log.d("basefragment", "data unloaded..");
        }
    }
}
