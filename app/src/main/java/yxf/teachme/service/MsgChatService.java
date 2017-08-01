package yxf.teachme.service;

import java.util.ArrayList;
import java.util.List;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserStatusListener;
import com.tencent.TIMValueCallBack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import yxf.teachme.activity.SyncpadActivity;

public class MsgChatService extends Service {
	private final static String TAG = "MsgChatService";
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		setMessageListener();
//		setForceLogout();
		Log.d(TAG, "MsgChatService启动");
	}

	// 消息监听器
	private TIMMessageListener msgListener = new TIMMessageListener() {
		@Override
		public boolean onNewMessages(List<TIMMessage> list) {
			Log.d(TAG, "msgchatservice 收到信息");
			for(TIMMessage timmsg : list){
				long time = timmsg.timestamp();
//				现在时间：1486517006037 信息时间1486517005  前者长3位
				Log.d(TAG, "现在时间：" + System.currentTimeMillis() + " 信息时间" + time + "时间差 ：" + (System.currentTimeMillis()/1000 - time));
				if(SyncpadActivity.startTime/1000 > time){
					Log.d(TAG, "舍弃旧信息：");
					continue;
				}else{
					Log.d(TAG, "新的信息：");
				}
				TIMElem elem = timmsg.getElement(0);
				// 如何知道elem里面有的时间呢？？以前发现过的是时间戳  	timmsg.timestamp();
				Log.d(TAG, "消息类型：" + elem.getType());

				if(elem.getType().equals(TIMElemType.Text)){
					Log.d(TAG, ((TIMTextElem) elem).getText());
					TIMTextElem textElem = (TIMTextElem) elem;
					String elemText = textElem.getText();
					if(elemText.equals(SyncpadActivity.FIRST_CREATE_ROOM_OK)) {
						//对方创建房间成功，那我也去创建房间
						Intent intent = new Intent(SyncpadActivity.FIRST_CREATE_ROOM_OK);
						sendBroadcast(intent);
					}else if(elemText.equals(SyncpadActivity.SECOND_CREATE_ROOM_OK)){
						//我创建成功后，对方也创建成功，并告知我。我要去crossroom
						Intent intent = new Intent(SyncpadActivity.SECOND_CREATE_ROOM_OK);
						sendBroadcast(intent);
					}else if(elemText.startsWith("sendPeer")){
						String[] arr = elemText.split("#");
						Intent intent = new Intent(SyncpadActivity.SEND_PEER);
						intent.putExtra("down", arr[1]);
						intent.putExtra("move", arr[2]);
						sendBroadcast(intent);
					}
				}else if(elem.getType().equals(TIMElemType.Image)){
					TIMImageElem imageElem = (TIMImageElem) elem;
					ArrayList<TIMImage> timImages = imageElem.getImageList();
					for(TIMImage timImage : timImages){
						timImage.getImage(new TIMValueCallBack<byte[]>() {
							@Override
							public void onError(int i, String s) {

							}

							@Override
							public void onSuccess(byte[] bytes) {
								Intent intent = new Intent(SyncpadActivity.SEND_PEER_DRAW);
								intent.putExtra("sendPeerDraw", bytes);
								sendBroadcast(intent);
							}
						});
					}
				}
			}
			return false;
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		removeMessageListener();
	}

	private void setForceLogout() {
		TIMManager.getInstance().setUserStatusListener(
				new TIMUserStatusListener() {

					public void onForceOffline() {

					}

					@Override
					public void onUserSigExpired() {

					}
				}
		);
	}

	private void setMessageListener() {
		TIMManager.getInstance().addMessageListener(msgListener);
	}

	private void removeMessageListener(){
		TIMManager.getInstance().removeMessageListener(msgListener);
	}

}
