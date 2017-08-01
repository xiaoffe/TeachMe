package yxf.teachme;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * Created by Administrator on 2017/7/17.
 */

public class Constants {
    public static final class FragmentFactorConstant {
        public static final int FRAGMENT_ONE = 0;
        public static final int FRAGMENT_TWO = 1;
        public static final int FRAGMENT_THR = 2;
    }
    public static final String CUT_PIC_PATH = Environment.getExternalStorageDirectory().toString() + "/yxf/";
    public static final String CHOOSE_PIC_PATH = Environment.getExternalStorageDirectory().toString() + "/teachme/";
}
