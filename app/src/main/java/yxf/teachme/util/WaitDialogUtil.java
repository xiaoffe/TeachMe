package yxf.teachme.util;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import yxf.teachme.R;

public class WaitDialogUtil {

	static Dialog waitDialog;
	public static boolean isShow = false;

	public static void waitDialog(Context context, String text_dialog) {
		if (isShow == true) {
			dismiss();
		}
		waitDialog = new Dialog(context, R.style.MyDialog);
		View recdialog = View.inflate(context, R.layout.dialog_wait, null);
		TextView tv = (TextView) recdialog.findViewById(R.id.tv_dialog_wait);
		tv.setText(text_dialog);
		waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		waitDialog.setContentView(recdialog);
		waitDialog.setCanceledOnTouchOutside(false);
		waitDialog.setCancelable(true);
		waitDialog.show();
		isShow = true;
	}

	public static void dismiss() {
		if (waitDialog != null && waitDialog.isShowing()) {
			isShow = false;
			waitDialog.dismiss();
		}
	}

}
