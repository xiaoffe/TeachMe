package yxf.teachme.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ilive.model.UserInfo;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import yxf.teachme.R;
import yxf.teachme.model.LoginUserInfo;
import yxf.teachme.network.Network;
import yxf.teachme.util.MD5;
import yxf.teachme.util.WaitDialogUtil;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private String userName;
    private String password;

    @Bind(R.id.et_username)
    EditText etUsername;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.bt_go)
    Button btGo;
    @Bind(R.id.cv)
    CardView cv;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    Observer<LoginUserInfo> observer = new Observer<LoginUserInfo>() {
        @Override
        public void onCompleted() {
            Log.d(TAG, "onCompleted");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "onError");
        }

        @Override
        public void onNext(LoginUserInfo loginUserInfo) {
            Log.d(TAG, "onNext");
            Log.d(TAG, "----------" + loginUserInfo);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        UserInfo.getInstance().getCache(getApplicationContext());
        etUsername.setText(UserInfo.getInstance().getAccount());
        etPassword.setText(UserInfo.getInstance().getPassword());
    }

    @OnClick({R.id.bt_go, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
            case R.id.bt_go:
//                userName = etUsername.getText().toString();
//                password = etPassword.getText().toString();
//
////                password = MD5.MD5(etPassword.getText().toString());
//
//                Log.d(TAG, "USERNAME:" + userName);
//                Log.d(TAG, "PASSWORD:" + password);
//                subscription = Network.getLoginApi()
//                        .getLoginInfo(userName, password)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(observer);

                login();
                break;

        }
    }

    // 登录成功
    private void afterLogin(){
        UserInfo.getInstance().setAccount(etUsername.getText().toString());
        UserInfo.getInstance().setPassword(etPassword.getText().toString());
        UserInfo.getInstance().writeToCache(getApplicationContext());
        Log.d(TAG, "getAccount: " + UserInfo.getInstance().getAccount());
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void login(){
        String strAccount = etUsername.getText().toString();
        String strPwd =etPassword.getText().toString();

        if (TextUtils.isEmpty(strAccount) || TextUtils.isEmpty(strPwd)){
//                    DlgMgr.showMsg(getContenxt(), R.string.msg_input_empty);
            Toast.makeText(LoginActivity.this, "密码或账号不能空", Toast.LENGTH_LONG).show();
            return;
        }

        WaitDialogUtil.waitDialog(this, "登录中...");

        ILiveLoginManager.getInstance().tlsLoginAll(strAccount, strPwd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                WaitDialogUtil.dismiss();
                afterLogin();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                WaitDialogUtil.dismiss();
                Toast.makeText(LoginActivity.this, "登录出错" + errCode + "" + errMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

}
