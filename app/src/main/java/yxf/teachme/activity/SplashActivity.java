package yxf.teachme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import yxf.teachme.R;
import yxf.teachme.model.ZhuangbiImage;
import yxf.teachme.network.Network;


public class SplashActivity extends BaseActivity {
    @Bind(R.id.versionCode) TextView versionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }
//    @OnClick(R.id.versionCode)
//    void clicktTest(){
//        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
    private void initView(){
        //todo:模拟一个网络请求获取versioncode并显示
        getVersionCode();
    }
    private void initEvent(){
        // for test
        Observable.timer(3*1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    }
                });

    }
    private void getVersionCode(){
        subscription = Network.getVersionApi()
                .search("装逼")
                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ZhuangbiImage>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<ZhuangbiImage> zhuangbiImages) {
//                        String text = versionCode.getText().toString();
//                        text += zhuangbiImages.size();
//                        versionCode.setText(text);
                    }
                });
    }

}
