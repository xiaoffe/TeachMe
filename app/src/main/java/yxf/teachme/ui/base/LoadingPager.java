package yxf.teachme.ui.base;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
//import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import yxf.teachme.R;
import yxf.teachme.factory.ThreadFactory;
import yxf.teachme.manager.ThreadPoolProxy;
import yxf.teachme.util.UIUtils;

/**
 * Created by Administrator on 2017/7/17.
 */

public abstract class LoadingPager extends FrameLayout {

    public static final int STATE_NONE = -1;            // 默认状态
    public static final int STATE_LOADING = 0;            // 加载中
    public static final int STATE_ERROR = 1;            // 错误
    public static final int STATE_EMPTY = 2;            // 空
    public static final int STATE_SUCCESS = 3;            // 成功

    //定义四种页面
    private View mLoadingView;
    private View mEmptyView;
    private View mErrorView;
    private View mSuccessView;


    //默认的状态
    public int mCurState = STATE_NONE;
    private Button mReLoad;
    private TextView mNullContent;


    /**
     * 任何应用其实就只有4种页面类型-->常规的页面
     * ① 加载页面
     * ② 错误页面  -->常规的页面
     * ③ 空页面
     * <p/>
     * ④ 成功页面
     * ①②③三种页面一个应用基本是固定的
     * 每一个fragment/activity对应的页面④就不一样
     * 进入应用的时候显示①,②③④需要加载数据之后才知道显示哪个
     * <p/>
     * <p/>
     * 构造方法，在这里进入====>
     */
    public LoadingPager(Context context) {
        super(context);

        //初始化创建时的状态页面
        initCommonView();
    }


    /**
     * @des 初始化常规视图① 加载中页面② 错误页面③ 空页面
     * @call LoadingPager初始化的时候被调用
     */
    private void initCommonView() {

        //后期修改各中样式，这里的操作是把要返回的界面设置成（加载中页面，错误页面，空页面）

        //① 加载页面
        mLoadingView = View.inflate(getContext(), R.layout.pager_loading, null);
        this.addView(mLoadingView);

        // ② 错误页面
        mErrorView = View.inflate(getContext(), R.layout.pager_error, null);
        mReLoad = (Button) mErrorView.findViewById(R.id.bt_view_error_rerequire);
        mReLoad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggleLoadData();
            }
        });
        this.addView(mErrorView);
        // ③ 空页面
        mEmptyView = View.inflate(getContext(), R.layout.pager_empty, null);
        mNullContent = (TextView) mEmptyView.findViewById(R.id.tv_pager_empty);
        mNullContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggleLoadData();
            }
        });
        this.addView(mEmptyView);


        //刷新界面
        refreshUIByState();
    }


    /**
     * @des 根据当前的状态显示不同的视图
     * @call 1.LoadingPager初始化的时候被调用
     * @call 2.正在开始加载前, 重置当前状态, 会刷新ui
     * @call 3.数据加载完成之后被调用
     * <p/>
     * 根据状态来设置四个页面的显示和隐藏（每次状态变了，都要调用这个方法去刷新页面）
     */
    private void refreshUIByState() {
        //控制加载页面的显示/隐藏
        mLoadingView.setVisibility((mCurState == STATE_LOADING) || (mCurState == STATE_NONE) ? View.VISIBLE : View.GONE);

        //控制错误页面的显示/隐藏
        mErrorView.setVisibility(mCurState == STATE_ERROR ? View.VISIBLE : View.GONE);

        //控制空白页面的显示/隐藏
        mEmptyView.setVisibility(mCurState == STATE_EMPTY ? View.VISIBLE : View.GONE);

        //如果数据为空，当前状态为加载成功。则设置页面为空页面？
        if (mSuccessView == null && mCurState == STATE_SUCCESS) {
            mSuccessView = initSuccessView();
            this.addView(mSuccessView);
        }

        //如果数据不为空，控制数据页面显示/隐藏
        if (mSuccessView != null) {
            mSuccessView.setVisibility(mCurState == STATE_SUCCESS ? View.VISIBLE : View.GONE);
        }

    }


    // 数据加载的流程

    /**
     * ① 触发加载  	进入页面开始加载/点击某一个按钮的时候加载
     * ② 异步加载数据  -->显示加载视图，子类不需要再创建子线程
     * ③ 处理加载结果
     * ① 成功-->显示成功视图
     * ② 失败
     * ① 数据为空-->显示空视图
     * ② 数据加载失败-->显示加载失败的视图
     */

    public void triggleLoadData() {

        /**
         * 如果页面是正在加载中，或者已经加载成功了，那么我们就不需要去加载数据
         */
        if (mCurState == STATE_LOADING || mCurState == STATE_SUCCESS) {
            return;
        }

        //加载开始前,重置状态为加载中
        mCurState = STATE_LOADING;

        //显示加载 页面
        upDateUISafe();

        //通过代理来获取到线程池
        ThreadPoolProxy threadPool = ThreadFactory.getNormalPool();

        //创建任务
        LodingDataTask task = new LodingDataTask();

        //把任务扔到线程池里头去执行
        threadPool.execute(task);


    }

    private class LodingDataTask implements Runnable {
        @Override
        public void run() {
            //获取到加载数据的状态（成功，失败，出错，或者空数据）
            LoadedResult result = initData();
            //改变当前的状态
            mCurState = result.getState();
            //状态变了，当然是去更新界面
            upDateUISafe();
        }
    }

    /**
     * 安全更新UI界面
     */
    public void upDateUISafe() {
//        UIUtils.postTaskSafely(new Runnable() {
//            @Override
//            public void run() {
//                refreshUIByState();
//            }
//        });
        Observable.timer(0, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        refreshUIByState();
                    }
                });
    }


    /**
     * @des 真正子子线程中加载数据, 必须实现, 但是不知道具体实现, 交给子类去实现
     * @call triggerLoadData方法被调用的时候调用
     */
    protected abstract LoadedResult initData();

    /**
     * @des 数据加载成功的时候返回具体的成功视图, 必须实现, 但是不知道具体实现, 交给子类实现
     * @call 数据加载成功的时候
     */
    protected abstract View initSuccessView();


    //加载数据返回状态。
    public enum LoadedResult {

        SUCCESS(STATE_SUCCESS), EMPTY(STATE_EMPTY), ERROR(STATE_ERROR);

        private int mState;

        public int getState() {
            return mState;
        }

        private LoadedResult(int state) {
            mState = state;
        }
    }


}
