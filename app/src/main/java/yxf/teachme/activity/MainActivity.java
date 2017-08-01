package yxf.teachme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import yxf.teachme.R;
import yxf.teachme.factory.FragmentFactory;
import yxf.teachme.network.Network;
import yxf.teachme.service.MsgChatService;
import yxf.teachme.ui.base.BaseFragment;
import yxf.teachme.ui.view.ChangeColorIconWithText;
import yxf.teachme.ui.view.LeftMenuHolder;

public class MainActivity extends BaseActivity {

    private static final String TAG = "main";
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tab_online_people)
    ChangeColorIconWithText onlinePeople;
    @Bind(R.id.tab_message)
    ChangeColorIconWithText messages;
    @Bind(R.id.tab_my_package)
    ChangeColorIconWithText myPackage;

    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<>();
    private LinearLayout mLeftMenuContainer;
    private LeftMenuHolder mLeftMenuHolder;
//    public static long startTime = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
//        startService();
//        startTime = System.currentTimeMillis();
}

    private void initView(){
        ViewPagerAdapter mAdapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BaseFragment baseFragment = FragmentFactory.getFragment(position);
                baseFragment.baseLoadingDatas();

                resetOthresTab();
                mTabIndicators.get(position).setIconAlpha(1.0f);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabIndicators.add(onlinePeople);
        mTabIndicators.add(messages);
        mTabIndicators.add(myPackage);
        mTabIndicators.get(0).setIconAlpha(1.0f);

        mLeftMenuContainer = (LinearLayout) this.findViewById(R.id.left_menu_container);
        //创建一个菜单的Holder
        mLeftMenuHolder = new LeftMenuHolder(this);
        //没有需要从mainactivity加载的数据。调用这个方法。无非为了刷新一下
        mLeftMenuHolder.setDatas("fake");
        View leftMenuView = mLeftMenuHolder.getRootView();
        //把返回的界面设置到坑上即可
        mLeftMenuContainer.addView(leftMenuView);
    }

    private void resetOthresTab() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }

    @OnClick({ R.id.tab_online_people, R.id.tab_message, R.id.tab_my_package })
    public void clickTab(ChangeColorIconWithText tab) {
        resetOthresTab();

        switch(tab.getId()){
            case R.id.tab_online_people:
                mTabIndicators.get(0).setIconAlpha(1.0f);
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.tab_message:
                mTabIndicators.get(1).setIconAlpha(1.0f);
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.tab_my_package:
                mTabIndicators.get(2).setIconAlpha(1.0f);
                viewPager.setCurrentItem(2, false);
                break;
            default:
                break;
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.getFragment(position);
        }

        @Override
        public int getCount() {
            //for test
            return 3;
        }
    }

    private Subscription uploadAvatar(String username,File file){
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);
        return Network.getUploadAvatarApi().uploadAvatar(username, part)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(String response) {
                    }
                });

    }
    private void startService(){
        Intent intent = new Intent(MainActivity.this, MsgChatService.class);
        startService(intent);
    }
}
