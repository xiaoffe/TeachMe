package yxf.teachme;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.StrictMode;

import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.qalsdk.sdk.MsfSdkUtils;

import ilive.model.MessageObservable;

/**
 * Created by Administrator on 2017/7/17.
 */

public class MainApplication extends Application {
    private static Handler mHandler;
    private static long mMainThreadId;
    private static Thread mMainThread;
    public static Context applicationContext;
    private String peerName;
    public static MainApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog()
                .build());
        super.onCreate();

        if(MsfSdkUtils.isMainProcess(this)){    // 仅在主线程初始化
            // 初始化LiveSDK
            ILiveSDK.getInstance().initSdk(this, 1400028096, 11851);
            ILVLiveManager.getInstance().init(new ILVLiveConfig()
                    .setLiveMsgListener(MessageObservable.getInstance()));
        }

        //得到handler
        mHandler = new Handler();
        //得到主线程id
        mMainThreadId = android.os.Process.myTid();
        //得到主线程
        mMainThread = Thread.currentThread();
        applicationContext = this;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static long getMainThreadId() {
        return mMainThreadId;
    }
    public static Thread getMainThread() {
        return mMainThread;
    }

    public static Context getAppContext() {
        return applicationContext;
    }

    public String getPeerName(){
        return peerName;
    }
    public void setPeerName(String name){
        peerName = name;
    }
    public static MainApplication getInstance() {
        return mInstance;
    }
}
