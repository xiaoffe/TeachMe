package yxf.teachme.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import rx.Observable;
//import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import yxf.teachme.R;
import yxf.teachme.activity.SyncpadActivity;
import yxf.teachme.listener.LoadHeaderImagesListener;
import yxf.teachme.ui.base.BaseFragment;
import yxf.teachme.ui.base.LoadingPager;
import yxf.teachme.ui.layout.CoordinatorTabLayout;
import yxf.teachme.ui.view.RefreshItemView;

/**
 * Created by Administrator on 2017/7/17.
 */

public class FragmentOne extends BaseFragment {
    private static final String TAG = "FragmentOne";
    private View rootView;
    CoordinatorTabLayout mCoordinatorTabLayout;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private AskListAdapter askListAdapter;
    private int[] mColorArray;
    private final String[] mTitles = {"Android"};

    private static final int ITEM_TYPE_NORMAL = 1;//普通的条目
    private static final int ITEM_TYPE_BLANK = 2;//最后一条空白的条目
    private static final int ITEM_TYPE_MORE = 3;//这是加载更多的界面
    private List<String> mDataList = new ArrayList<>();
    private RefreshItemView refreshItemView;
    @Override
    protected LoadingPager.LoadedResult initData() {
        LoadingPager.LoadedResult result = LoadingPager.LoadedResult.ERROR;
        int random;
        try{
            Thread.sleep(1*1000);
            random = new Random().nextInt(5);
            Log.d(TAG, "random:" + random);
        }catch (Exception e){
            e.printStackTrace();
            return LoadingPager.LoadedResult.ERROR;
        }
        switch(random){
            case 0:
                result = LoadingPager.LoadedResult.EMPTY;
                break;
            default:
                result = LoadingPager.LoadedResult.SUCCESS;
        }
        return result;
    }

    @Override
    protected View initSuccessView() {
        rootView = View.inflate(getContext(), R.layout.fragment_test, null);
        mCoordinatorTabLayout = (CoordinatorTabLayout) rootView.findViewById(R.id.coordinatortablayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshLayout);
        initView();
        return rootView;
    }

    private void initView(){
        mColorArray = new int[]{
                android.R.color.holo_blue_light};
        mCoordinatorTabLayout.setTitle("teachMe")
                .setBackEnable(true)
                .setContentScrimColorArray(mColorArray)
                .setLoadHeaderImagesListener(null);
//                .setLoadHeaderImagesListener(new LoadHeaderImagesListener() {
//                    @Override
//                    public void loadHeaderImages(ImageView imageView) {
//                        //下面的是网络地址
//                        loadImages(imageView, "https://raw.githubusercontent.com/hugeterry/CoordinatorTabLayout/master/sample/src/main/res/mipmap-hdpi/bg_android.jpg");
//                    }
//                });

        for (int i=0; i<8; i++){
            mDataList.add("dummy");
        }

        askListAdapter = new AskListAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(askListAdapter);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Observable.timer(2*1000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
            }
        });
    }
    private void loadImages(ImageView imageView, String url) {
        Glide.with(FragmentOne.this).load(url).into(imageView);
    }

    private class AskListAdapter extends RecyclerView.Adapter<TeacherHolder> {
        private Context mContext;
        private LayoutInflater mInflater;
        public AskListAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(mContext);
        }
        @Override
        public TeacherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = null;
            switch (viewType) {
                case ITEM_TYPE_NORMAL://这里是普通的类型
                    itemView = mInflater.inflate(R.layout.item_ask_list, parent, false);
                    break;
                case ITEM_TYPE_BLANK://这里是空的类型
                    itemView = mInflater.inflate(R.layout.ask_list_last_holder, parent, false);
                    break;
                case ITEM_TYPE_MORE://这是倒数第二个条目，加载更多:
                    itemView = getLoadMoreView(parent).getRootView();
                    break;
            }
            return new TeacherHolder(itemView, viewType);
        }

        @Override
        public void onBindViewHolder(TeacherHolder holder, int position) {
            if (getItemViewType(position) == ITEM_TYPE_NORMAL) {
                Log.d(TAG," position" +position);
                Log.d(TAG,"mDataList" + mDataList.size()+ " position" +position + " " + mDataList.get(position));
                holder.setDatas(mDataList.get(position));
            }
            //当移动到刷新条目的时候，就可以去加载数据了
            if (position == mDataList.size()) {
                //在这里开始加载数据
                upLoadMore();
            }
        }

        @Override
        public int getItemCount() {
            if (mDataList != null) {
                return mDataList.size() + 2;
                //为什么+3   分别是轮转图片、RefreshItemHolder mHolder、最后一个blank item
            }
            return 0;
        }


        @Override
        public int getItemViewType(int position) {
            //根据位置去返回类型，最一个返回空的类型
            //如果是倒数第二个，则显示加载的
            if (position == mDataList.size()) {
                return ITEM_TYPE_MORE;
            } else if (position == mDataList.size() + 1) {
                return ITEM_TYPE_BLANK;
            } else {
                //否则返回正常的类型
                return ITEM_TYPE_NORMAL;
            }
        }
    }

    private RefreshItemView getLoadMoreView(ViewGroup parent) {

        if (refreshItemView == null) {
            refreshItemView = new RefreshItemView(getActivity(), parent);
        }
        refreshItemView.reflashUI(RefreshItemView.LOADMORE_LOADING);//默认显示加载的状态
        return refreshItemView;
    }

    /**
     * holder
     */
//  extends RecyclerView.ViewHolder
    public class TeacherHolder extends RecyclerView.ViewHolder {

        public ImageView mAvater;
        //名字
        public TextView mName;
        //科目
        public TextView mSubject;
        //辅导年级
        public TextView mGrades;
        //问按钮
        private TextView mAskBtn;
        private View mLine;
        //数据
        private String datas;


        /**
         * 找出里面的孩子
         *
         * @param itemView
         */
        private View itemView;

        public TeacherHolder(View itemView, int viewType) {
            super(itemView);
            this.itemView = itemView;

            if (viewType == ITEM_TYPE_NORMAL) {

                mAvater = (ImageView) itemView.findViewById(R.id.teacher_icon);
                mName = (TextView) itemView.findViewById(R.id.teacher_name);
                mSubject = (TextView) itemView.findViewById(R.id.teacher_subject);
                mGrades = (TextView) itemView.findViewById(R.id.teacher_grade);
                mLine = itemView.findViewById(R.id.teacher_line);
                //这是问的按钮
                mAskBtn = (TextView) itemView.findViewById(R.id.ask_teacher);
                mAskBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentOne.this.getContext(), SyncpadActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }

        public void setDatas(String datas) {
            Log.d(TAG, "in setDatas");
            this.datas = datas;
//            Glide.with(getActivity())
//                    .load(datas.getAvatar()).transform(new GlideCircleTransform(getActivity()))
//                    .override(UIUtils.dip2px(54), UIUtils.dip2px(54)).into(mAvater);

            mName.setText("sdfsafs");
            mSubject.setText("sfsadfsfsdfsf");
            mGrades.setText("没设置年级");

            //还要设置问的状态
            mAskBtn.setBackgroundResource(R.drawable.wen_green_online);
            Log.d(TAG, "out setDatas");
        }
    }

    private boolean isRefreshing = false;
    private void upLoadMore() {

        if (isRefreshing) {
            isRefreshing = false;
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        //更新界面,加载界面
        refreshItemView.reflashUI(RefreshItemView.LOADMORE_LOADING);

        Observable.timer(2*1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        //模拟请求
                        if(mDataList.size()<20){
                            for(int n=0; n<5; n++){
                                mDataList.add("dummy STAGE:" + mDataList.size()/5);
                            }
                        }
                        if(mDataList.size()>=20){
                            refreshItemView.reflashUI(RefreshItemView.LOADMORE_NONE);
                        }else{
                            refreshItemView.reflashUI(RefreshItemView.LOADMORE_LOADING);
                            askListAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }

}
