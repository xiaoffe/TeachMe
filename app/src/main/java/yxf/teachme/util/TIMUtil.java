package yxf.teachme.util;

import android.util.Log;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMImageElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;
import ilive.model.UserInfo;
import yxf.teachme.Constants;
import yxf.teachme.MainApplication;

/**
 * Created by xiaoluo on 2017/1/19.
 */
public class TIMUtil {
    private static final String TAG = "TIMUtil";
    private static TIMConversation conversation;
    public static void sendCode(String text){
        String myAccount = UserInfo.getInstance().getAccount();
        Log.d(TAG, "set myAccount " + myAccount);
        if(myAccount.equals("15700071533a")){
            MainApplication.getInstance().setPeerName("15700071533b");
            Log.d(TAG, "MainApplication.getInstance().setPeerName(\"15700071533b\");");
        }else if(myAccount.equals("15700071533b")){
            MainApplication.getInstance().setPeerName("15700071533a");
            Log.d(TAG, "MainApplication.getInstance().setPeerName(\"15700071533a\");");
        }
        Log.d(TAG, "here1");
        String peerName = MainApplication.getInstance().getPeerName();
        if(peerName == null){
            Log.d(TAG, "here2");
            return;
        }
        conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C, peerName);
        TIMMessage msg = new TIMMessage();
        Long timeStamp = System.currentTimeMillis();
        msg.setTimestamp(timeStamp);
        System.out.println("xiaoluo TIMUtils.sendCode() timeStamp:"+timeStamp);
        TIMTextElem elem = new TIMTextElem();
        elem.setText(text);
        int iRet = msg.addElement(elem);
        if (iRet != 0) {
            Log.d(TAG, "add element error:" + iRet);
            return;
        }
        Log.d(TAG, "ready send text msg");
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {// 发送消息回调
            public void onError(int code, String desc) {// 发送消息失败
                Log.i(TAG, "====发消息失败");
            }

            public void onSuccess(TIMMessage arg0) {
                Log.i(TAG, "====发消息成功");
            }
        });
    }

    public static void sendBitmap(){
        String myAccount = UserInfo.getInstance().getAccount();
        Log.d(TAG, "set myAccount " + myAccount);
        if(myAccount.equals("15700071533a")){
            MainApplication.getInstance().setPeerName("15700071533b");
            Log.d(TAG, "MainApplication.getInstance().setPeerName(\"15700071533b\");");
        }else if(myAccount.equals("15700071533b")){
            MainApplication.getInstance().setPeerName("15700071533a");
            Log.d(TAG, "MainApplication.getInstance().setPeerName(\"15700071533a\");");
        }
        String peerName = MainApplication.getInstance().getPeerName();
        if(peerName == null){
            return;
        }
        conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C, peerName);
        TIMMessage msg = new TIMMessage();
        Long timeStamp = System.currentTimeMillis();
        msg.setTimestamp(timeStamp);
        TIMImageElem imageElem = new TIMImageElem();
        imageElem.setPath(Constants.CHOOSE_PIC_PATH + "temp.jpg");
        int iRet = msg.addElement(imageElem);
        if (iRet != 0) {
            Log.d(TAG, "add element error:" + iRet);
            return;
        }
        Log.d(TAG, "ready send image msg");
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {// 发送消息回调
            public void onError(int code, String desc) {// 发送消息失败
                Log.i(TAG, "====发图片消息失败");
            }

            public void onSuccess(TIMMessage arg0) {
                Log.i(TAG, "====发图片消息成功");
            }
        });
    }

}
