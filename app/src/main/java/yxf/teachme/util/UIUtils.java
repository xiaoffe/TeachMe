package yxf.teachme.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;

import yxf.teachme.MainApplication;

/**
 * Created by Administrator on 2017/7/17.
 */

public class UIUtils {
    /**
     * 得到主线程id
     */
    public static long getMainThreadId() {
        return MainApplication.getMainThreadId();
    }
    /**
     * 得到一个主线程的handler
     */
    public static Handler getMainThreadHandler() {
        return MainApplication.getHandler();
    }
    /**
     * 安全的执行一个task
     */
    public static void postTaskSafely(Runnable task) {
        long curThreadId = android.os.Process.myTid();
        long mainThreadId = getMainThreadId();
        // 如果当前线程是主线程
        if (curThreadId == mainThreadId) {
            task.run();
        } else {// 如果当前线程不是主线程
            getMainThreadHandler().post(task);
        }
    }

    /**
     * bitmap旋转
     * @param b
     * @param degrees
     * @return
     */
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();  //Android开发网再次提示Bitmap操作完应该显示的释放
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // Android123建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
            }
        }
        return b;
    }
}
