package yxf.teachme.activity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.av.opengl.ui.GLView;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import butterknife.Bind;
import butterknife.ButterKnife;
import ilive.model.Constants;
import ilive.model.MessageObservable;
import ilive.model.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import yxf.teachme.R;
import yxf.teachme.service.MsgChatService;
import yxf.teachme.ui.view.doodle.Doodle;
import yxf.teachme.ui.view.expandableselector.ExpandableItem;
import yxf.teachme.ui.view.expandableselector.ExpandableSelector;
import yxf.teachme.ui.view.expandableselector.OnExpandableItemClickListener;
import yxf.teachme.util.TIMUtil;

/**
 * Created by Administrator on 2017/7/23.
 */

public class SyncpadActivity extends BaseActivity implements ILVLiveConfig.ILVLiveMsgListener{

    @Override
    public void onNewTextMsg(ILVText text, String SenderId, TIMUserProfile userProfile) {
        addMessage(SenderId, text.getText());
        if(text.equals("closeRoom")){
            ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    openCall = true;
                    front.setVisibility(View.VISIBLE);
                    buttonTest.setBackgroundResource(R.drawable.tv_turn_up);
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }
            });

        }
    }

    @Override
    public void onNewCustomMsg(ILVCustomCmd cmd, String id, TIMUserProfile userProfile) {

    }

    @Override
    public void onNewOtherMsg(TIMMessage message) {

    }

    private static final String TAG = "SyncpadActivity";
    private View colorsHeaderButton;
    private ExpandableSelector sizesExpandableSelector;
    private ExpandableSelector colorsExpandableSelector;
    private Doodle mDoodle;
    private ImageView button;
    private ImageView buttonTest;
//    @Bind(R.id.es_sizes)
//    ExpandableSelector sizesExpandableSelector;
    public static long startTime = -1;
    private AVRootView arvRoot;
    private OkHttpClient okHttpClient;
    //对方创建房间成功，那我也去创建
    public static String FIRST_CREATE_ROOM_OK = "first_create_room_ok";
    //我创建房间成功后告诉对方，对方也跟着创建房间成功。
    public static String SECOND_CREATE_ROOM_OK = "second_create_room_ok";
    public static String SEND_PEER = "send_peer";
    public static String SEND_PEER_DRAW = "send_peer_draw";

    private boolean justOnce = true;
    private boolean openCall = true;
    private ImageView front;
    private Button dumyButton;
    private String sendPeer;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (FIRST_CREATE_ROOM_OK.equals(action)) {
                Observable.timer(5*100, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                // 放到主线程里面去开启房间
                                createRoom(true);
                            }
                        });

            }else if(SECOND_CREATE_ROOM_OK.equals(action)){
                //todo: crossRoom
                if(justOnce){
                    Toast.makeText(SyncpadActivity.this, "收到 SECOND IN SYNCPAD", Toast.LENGTH_LONG).show();
                    Observable.timer(5*100, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    // 放到主线程里面去开启房间
                                    crossRoom();
                                }
                            });
                justOnce = false;
                }
            }else if(SEND_PEER.equals(action)){
                String downContent = intent.getStringExtra("down");
                String moveContent = intent.getStringExtra("move");
                Log.d(TAG, downContent + "..." + moveContent);

                String[] downContentArr = downContent.split(" ");
                float peerX = Float.parseFloat(downContentArr[0]);
                float peerY = Float.parseFloat(downContentArr[1]);
                Doodle.ActionType peerType = Doodle.ActionType.valueOf(downContentArr[2]);
                int peerSize = Integer.parseInt(downContentArr[3]);
                int peerColor = Integer.parseInt(downContentArr[4]);
                mDoodle.downPeer(peerType, peerSize, peerColor, peerX, peerY);

                String[] moveContentArr = moveContent.split("@");
                for(String s : moveContentArr){
                    if(s!=null){
                        String[] arr = s.split(" ");
                        float moveX = Float.parseFloat(arr[0]);
                        float moveY = Float.parseFloat(arr[1]);
                        mDoodle.justMovePeer(moveX, moveY);
                    }
                }
                mDoodle.upPeer();
            }else if(SEND_PEER_DRAW.equals(action)){
                byte[] bytes = intent.getByteArrayExtra("sendPeerDraw");
//                if(peerBitmap!=null && !peerBitmap.isRecycled()){
//                    peerBitmap.recycle();
//                    peerBitmap = null;
//                }
                Bitmap peerBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mDoodle.drawPic(peerBitmap);
            }
        }
    };
    private Doodle.OnDrawListener mDrawListener = new Doodle.OnDrawListener() {
        float x;
        float y;
        String type;
        int size;
        int color;

        @Override
        public void onDown(MotionEvent event) {
            x = event.getRawX();
            y = event.getRawY();
            type = mDoodle.getCurType().toString();
            size = mDoodle.getSize();
            color = mDoodle.getColor();
            String downContent = x + " " + y + " " + type + " " + size + " " + color;
            sendPeer = "sendPeer";
            sendPeer += "#";
            sendPeer += downContent;
            sendPeer += "#";
        }

        @Override
        public void onMove(MotionEvent event) {
            x = event.getRawX();
            y = event.getRawY();
            String moveContent = x + " " + y + "@";
            sendPeer += moveContent;

        }

        @Override
        public void onUp(MotionEvent event) {
            TIMUtil.sendCode(sendPeer);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syncpad);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        ButterKnife.bind(SyncpadActivity.this);
        UserInfo.getInstance().getCache(getApplicationContext());
        initView();
        initEvent();
        startService();
        startTime = System.currentTimeMillis();
        dumyButton = new Button(SyncpadActivity.this);
    }
    private void initView(){
        sizesExpandableSelector = (ExpandableSelector)findViewById(R.id.es_sizes);
        colorsExpandableSelector = (ExpandableSelector)findViewById(R.id.es_colors);
        mDoodle = (Doodle)findViewById(R.id.mDoodle);
        button = (ImageView)findViewById(R.id.es_backward);
        buttonTest = (ImageView)findViewById(R.id.iv_test);
        arvRoot = (AVRootView)findViewById(R.id.arv_root);
        front = (ImageView)findViewById(R.id.front);
    }

    private void initColorExpSelector() {
        List<ExpandableItem> expandableItems = new ArrayList<ExpandableItem>();
        expandableItems.add(new ExpandableItem(R.drawable.item_brown));
        expandableItems.add(new ExpandableItem(R.drawable.item_green));
        expandableItems.add(new ExpandableItem(R.drawable.item_orange));
        expandableItems.add(new ExpandableItem(R.drawable.item_pink));
        expandableItems.add(new ExpandableItem(R.drawable.item_light_red));
        expandableItems.add(new ExpandableItem(R.drawable.item_light_green));
        expandableItems.add(new ExpandableItem(R.drawable.item_light_yellow));
        expandableItems.add(new ExpandableItem(R.drawable.item_light_blue));
        colorsExpandableSelector.showExpandableItems(expandableItems);
        colorsHeaderButton = findViewById(R.id.bt_colors);
        colorsHeaderButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                colorsHeaderButton.setVisibility(View.INVISIBLE);
                colorsExpandableSelector.expand();
            }
        });
        colorsExpandableSelector.setOnExpandableItemClickListener(new OnExpandableItemClickListener() {
            @Override public void onExpandableItemClickListener(int index, View view) {
                colorsHeaderButton.setVisibility(View.VISIBLE);
                switch (index) {
                    case 0:
                        colorsHeaderButton.setBackgroundResource(R.drawable.item_brown);
                        mDoodle.setColor("#FF4B2D29");
                        break;
                    case 1:
                        colorsHeaderButton.setBackgroundResource(R.drawable.item_green);
                        mDoodle.setColor("#FF42543C");
                        break;
                    case 2:
                        colorsHeaderButton.setBackgroundResource(R.drawable.item_orange);
                        mDoodle.setColor("#FFE29B33");
                        break;
                    case 3:
                        colorsHeaderButton.setBackgroundResource(R.drawable.item_pink);
                        mDoodle.setColor("#FFE9C7C7");
                        break;
                    case 4:
                        colorsHeaderButton.setBackgroundResource(R.drawable.item_light_red);
                        mDoodle.setColor("#FFCC0000");
                        break;
                    case 5:
                        colorsHeaderButton.setBackgroundResource(R.drawable.item_light_green);
                        mDoodle.setColor("#ff00CC00");
                        break;
                    case 6:
                        colorsHeaderButton.setBackgroundResource(R.drawable.item_light_yellow);
                        mDoodle.setColor("#FFFFCC00");
                        break;
                    default:
                        colorsHeaderButton.setBackgroundResource(R.drawable.item_light_blue);
                        mDoodle.setColor("#ff6699ff");
                        break;
                }
                colorsExpandableSelector.collapse();
            }
        });
    }
    private void initSizeExpSelector(){
        List<ExpandableItem> expandableItems = new ArrayList<ExpandableItem>();
        expandableItems.add(new ExpandableItem("中"));
        expandableItems.add(new ExpandableItem("细"));
        expandableItems.add(new ExpandableItem("粗"));
        sizesExpandableSelector.showExpandableItems(expandableItems);
        sizesExpandableSelector.setOnExpandableItemClickListener(new OnExpandableItemClickListener() {
            @Override public void onExpandableItemClickListener(int index, View view) {
                String title = sizesExpandableSelector.getExpandableItem(index).getTitle();
                if(title.equals("细")){
                    mDoodle.setSize(2);
                }else if(title.equals("中")){
                    mDoodle.setSize(4);
                }else if(title.equals("粗")){
                    mDoodle.setSize(6);
                }
                switch (index) {
                    case 1:
                        ExpandableItem firstItem = sizesExpandableSelector.getExpandableItem(1);
                        swipeFirstItem(1, firstItem);
                        break;
                    case 2:
                        ExpandableItem secondItem = sizesExpandableSelector.getExpandableItem(2);
                        swipeFirstItem(2, secondItem);
                        break;
                    default:
                }

                sizesExpandableSelector.collapse();
            }

            private void swipeFirstItem(int position, ExpandableItem clickedItem) {
                ExpandableItem firstItem = sizesExpandableSelector.getExpandableItem(0);
                sizesExpandableSelector.updateExpandableItem(0, clickedItem);
                sizesExpandableSelector.updateExpandableItem(position, firstItem);
            }
        });
    }

    private void initEvent(){
        initSizeExpSelector();
        initColorExpSelector();
//        mDoodle.setSize(dip2px(4));
        mDoodle.setSize(4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(null)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SyncpadActivity.this);
            }
        });
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(openCall){
                    TIMUtil.sendCode("你收到了吗？？");
                    createRoom(false);
                }else{
                    closeRoom();
                }

            }
        });

        mDoodle.setOnDrawListener(mDrawListener);

        ILVLiveManager.getInstance().setAvVideoView(arvRoot);
        MessageObservable.getInstance().addObserver(this);

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDoodle.onTouchEvent(event);
    }

    private String strMsg = "";
    // 添加消息
    private void addMessage(String sender, String msg){
        strMsg += "["+sender+"]  "+msg+"\n";
    }
    @Override
    protected void onPause() {
        super.onPause();
        ILVLiveManager.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ILVLiveManager.getInstance().onResume();
        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageObservable.getInstance().deleteObserver(this);
        ILVLiveManager.getInstance().onDestory();
        removeReceiver();
        mDoodle.recycleBitmap();
    }

    // 加入房间
    private void createRoom(final boolean secondCreateRoom){
        ILVLiveRoomOption option = new ILVLiveRoomOption(ILiveLoginManager.getInstance().getMyUserId())
                .controlRole(Constants.ROLE_MASTER)
                .autoFocus(true);
        int homeNum=0;
        if(UserInfo.getInstance().getAccount().equals("15700071533a")){
            homeNum = 1570007;
        }else if(UserInfo.getInstance().getAccount().equals("15700071533b")){
            homeNum = 1570008;
        }
        ILVLiveManager.getInstance().createRoom(homeNum,
                option, new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        Log.d(TAG, "create room ok");
                        afterCreate(secondCreateRoom);
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        Log.d(TAG, "create room failed:"+module+"|"+errCode+"|"+errMsg);
                    }
                });
    }

    private void closeRoom(){
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                openCall = true;
                front.setVisibility(View.VISIBLE);
                buttonTest.setBackgroundResource(R.drawable.tv_turn_up);
                ILVText text = new ILVText();
                text.setText("closeRoom");
                ILVLiveManager.getInstance().sendOnlineText(text,null);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    private void afterCreate(boolean secondCreateRoom){
        openCall = false;
        front.setVisibility(View.GONE);
        buttonTest.setBackgroundResource(R.drawable.tv_turn_down);
        UserInfo.getInstance().setRoom(ILiveRoomManager.getInstance().getRoomId());
        UserInfo.getInstance().writeToCache(this);
        // 设置点击小屏切换及可拖动
        for (int i=1; i<ILiveConstants.MAX_AV_VIDEO_NUM; i++){
            final int index = i;
            AVVideoView minorView = arvRoot.getViewByIndex(i);
            if (ILiveLoginManager.getInstance().getMyUserId().equals(minorView.getIdentifier())){
                minorView.setMirror(true);      // 本地镜像
            }
            minorView.setDragable(true);    // 小屏可拖动
            minorView.setGestureListener(new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    arvRoot.swapVideoView(0, index);     // 与大屏交换
                    return false;
                }
            });
        }
        if(!secondCreateRoom){
            TIMUtil.sendCode(SyncpadActivity.FIRST_CREATE_ROOM_OK);
        }else{
            TIMUtil.sendCode(SyncpadActivity.SECOND_CREATE_ROOM_OK);
        }

    }
    private void startService(){
        Intent intent = new Intent(SyncpadActivity.this, MsgChatService.class);
        startService(intent);
    }

    private void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FIRST_CREATE_ROOM_OK);
        intentFilter.addAction(SECOND_CREATE_ROOM_OK);
        intentFilter.addAction(SEND_PEER);
        intentFilter.addAction(SEND_PEER_DRAW);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void removeReceiver(){
        unregisterReceiver(mBroadcastReceiver);
    }

    private void crossRoom(){
        int dstRoom=0;
        String dstUser="";
        if(UserInfo.getInstance().getAccount().equals("15700071533a")){
            dstRoom = 1570008;
            dstUser = "15700071533b";
        }else if(UserInfo.getInstance().getAccount().equals("15700071533b")){
            dstRoom = 1570007;
            dstUser = "15700071533a";
        }
        requestSign(dstRoom, dstUser);
    }
    private void requestSign(final int dstRoomId, final String dstUser){
        String postBody = "";
        try {
            JSONObject jsonReq = new JSONObject();
            jsonReq.put("mygroup", ILiveRoomManager.getInstance().getRoomId());
            jsonReq.put("myid", ILiveLoginManager.getInstance().getMyUserId());
            jsonReq.put("remotegroup", dstRoomId);
            jsonReq.put("remotehost", dstUser);
            postBody = jsonReq.toString();
        }catch (JSONException e){
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                postBody);
        Request request = new Request.Builder()
                .url("https://sxb.qcloud.com/easy/encode")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                ILiveSDK.getInstance().runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Request fail: "+e.toString());
                    }
                }, 0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                ILiveSDK.getInstance().runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ILVLiveManager.getInstance().linkRoom(dstRoomId, dstUser, data, new ILiveCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                for (int i=1; i<ILiveConstants.MAX_AV_VIDEO_NUM; i++){
                                    final int index = i;
                                    AVVideoView minorView = arvRoot.getViewByIndex(i);
                                    if (ILiveLoginManager.getInstance().getMyUserId().equals(minorView.getIdentifier())){
                                        minorView.setMirror(true);      // 本地镜像
                                    }
                                    minorView.setDragable(true);    // 小屏可拖动
                                    minorView.setGestureListener(new GestureDetector.SimpleOnGestureListener(){
                                        @Override
                                        public boolean onSingleTapConfirmed(MotionEvent e) {
                                            arvRoot.swapVideoView(0, index);     // 与大屏交换
                                            return false;
                                        }
                                    });
                                    minorView.setOnTouchListener(new GLView.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(GLView glView, MotionEvent motionEvent) {
                                            return false;
                                        }
                                    });
//                                    dumyButton.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            arvRoot.swapVideoView(0, index);
//                                        }
//                                    });
//                                    dumyButton.performClick();                                   //已经是在主线程里操作了，可以直接切换大小屏幕吧？
                                }
                            }

                            @Override
                            public void onError(String module, int errCode, String errMsg) {
                                Log.d(TAG, "linkRoom->Failed: "+module+"|"+errCode+"|"+errMsg);
                            }
                        });

                    }
                }, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("look", "syncpadactivity onactivieyresult");
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
//                ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                final Bitmap bitmap = decodeUri(SyncpadActivity.this, result.getUri(), 500, 500);
//                ((ImageView) findViewById(R.id.show)).setImageBitmap(bitmap);
//                mDoodle.drawPic(bitmap);

//                Handler handler = new Handler(getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mDoodle.drawPic(bitmap);
//                    }
//                }, 5*100);

                Observable.timer(5*100, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                mDoodle.drawPic(bitmap);
                                saveAndSendBitmap(bitmap);
                            }
                        });
                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 读取一个缩放后的图片，限定图片大小，避免OOM
     * @param uri       图片uri，支持“file://”、“content://”
     * @param maxWidth  最大允许宽度
     * @param maxHeight 最大允许高度
     * @return  返回一个缩放后的Bitmap，失败则返回null
     */
    public static Bitmap decodeUri(Context context, Uri uri, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //只读取图片尺寸
        resolveUri(context, uri, options);

        //计算实际缩放比例
        int scale = 1;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if ((options.outWidth / scale > maxWidth &&
                    options.outWidth / scale > maxWidth * 1.4) ||
                    (options.outHeight / scale > maxHeight &&
                            options.outHeight / scale > maxHeight * 1.4)) {
                scale++;
            } else {
                break;
            }
        }

        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;//读取图片内容
        options.inPreferredConfig = Bitmap.Config.RGB_565; //根据情况进行修改
        Bitmap bitmap = null;
        try {
            bitmap = resolveUriForBitmap(context, uri, options);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    private static void resolveUri(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return;
        }

        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUri", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUri", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUri", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUri", "Unable to close content: " + uri);
        }
    }

    private static Bitmap resolveUriForBitmap(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return null;
        }

        Bitmap bitmap = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUriForBitmap", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUriForBitmap", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        }

        return bitmap;
    }

    public void saveAndSendBitmap(Bitmap mBitmap) {
        File filePic;

        try {
            filePic = new File(yxf.teachme.Constants.CHOOSE_PIC_PATH + "temp.jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            TIMUtil.sendBitmap();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
