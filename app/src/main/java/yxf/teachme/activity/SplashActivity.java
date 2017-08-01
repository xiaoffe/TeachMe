package yxf.teachme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ilive.model.UserInfo;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import yxf.teachme.R;
import yxf.teachme.model.ZhuangbiImage;
import yxf.teachme.network.Network;
import yxf.teachme.util.WaitDialogUtil;


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
        autoLogin();
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
    private void autoLogin(){
        UserInfo.getInstance().getCache(SplashActivity.this);
        String userName = UserInfo.getInstance().getAccount();
        String password = UserInfo.getInstance().getPassword();
        Toast.makeText(SplashActivity.this, userName+" "+password, Toast.LENGTH_LONG).show();
        if(userName!=null && password!=null){
            ILiveLoginManager.getInstance().tlsLoginAll(userName, password, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(SplashActivity.this, "登录出错" + errCode + "" + errMsg, Toast.LENGTH_LONG).show();
                }
            });
        }else{
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

    }
}
