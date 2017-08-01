package yxf.teachme.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import yxf.teachme.R;
import yxf.teachme.model.LoginUserInfo;
import yxf.teachme.network.Network;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    private String userName;
    private String password;

    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.cv_add)
    CardView cvAdd;
    @Bind(R.id.bt_go)
    Button btn_go;
    @Bind(R.id.et_username)
    EditText etUsername;
    @Bind(R.id.et_password)
    EditText etPassword;

    Observer<String> observer = new Observer<String>() {
        @Override
        public void onCompleted() {
            Log.d(TAG, "onCompleted");

        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "onError");
            //for test
            Intent intent = new Intent(RegisterActivity.this, SyncpadActivity.class);
            startActivity(intent);
        }

        @Override
        public void onNext(String result) {
            Log.d(TAG, "onNext");
            Log.d(TAG, "----------" + result);

        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }
    @OnClick(R.id.bt_go)
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bt_go:
//                userName = etUsername.getText().toString();
//                password = etPassword.getText().toString();
////                password = MD5.MD5(etPassword.getText().toString());
//                Log.d(TAG, "USERNAME:" + userName);
//                Log.d(TAG, "PASSWORD:" + password);
//                subscription = Network.getRegistApi()
//                        .regist(userName, password)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(observer);
                regist();
                break;
        }
    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    // 注册
    private void regist(){
        String strAccount = etUsername.getText().toString();
        String strPwd = etPassword.getText().toString();
        Log.d(TAG, "regist1");
        if (TextUtils.isEmpty(strAccount) || TextUtils.isEmpty(strPwd)){
            Toast.makeText(RegisterActivity.this, "密码或账号不能空", Toast.LENGTH_LONG).show();
            Log.d(TAG, "regist2");
            return;
        }

        ILiveLoginManager.getInstance().tlsRegister(strAccount, strPwd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Log.d(TAG, "regist3");
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.d(TAG, "regist4");
                Toast.makeText(RegisterActivity.this, "这侧失败" + errCode + " " + errMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
