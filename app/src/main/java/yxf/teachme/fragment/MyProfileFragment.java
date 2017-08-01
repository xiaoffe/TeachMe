package yxf.teachme.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import de.hdodenhof.circleimageview.CircleImageView;
import yxf.teachme.Constants;
import yxf.teachme.R;
import yxf.teachme.ui.base.BaseFragment;
import yxf.teachme.ui.base.LoadingPager;
import yxf.teachme.util.UIUtils;

/**
 * Created by Administrator on 2017/7/21.
 */

public class MyProfileFragment extends BaseFragment{
    private final String TAG = "MyProfileFragment";
    private View rootView;
    private CircleImageView mAvatar;
//    @Bind(R.id.my_avatar)
//    CircleImageView mAvatar;
    @Override
    protected LoadingPager.LoadedResult initData() {
        LoadingPager.LoadedResult result = LoadingPager.LoadedResult.ERROR;
        int random;
        try{
            Thread.sleep(1*1000);
            random = new Random().nextInt(5);
            Log.d(TAG, "random:" + random);
        }catch (Exception e){
            e.printStackTrace();
            return LoadingPager.LoadedResult.ERROR;
        }
        switch(random){
            case 0:
                result = LoadingPager.LoadedResult.EMPTY;
                break;
            default:
                result = LoadingPager.LoadedResult.SUCCESS;
                break;
        }
        return result;
    }

    @Override
    protected View initSuccessView() {
        //获取到界面
        rootView = View.inflate(getContext(), R.layout.fragment_myprofile, null);
//        ButterKnife.bind(rootView);
        mAvatar = (CircleImageView) rootView.findViewById(R.id.my_avatar);
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });
        return rootView;
    }

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private Bitmap mBitmap;
    protected static Uri tempUri;
    private static final int CROP_SMALL_PICTURE = 2;

//    @OnClick({ R.id.my_avatar})
//    public void click(CircleImageView circleImageView) {
//        switch(circleImageView.getId()){
//            case R.id.my_avatar:
//                Log.d(TAG, "here");
//                showChoosePicDialog();
//                break;
//            default:
//                break;
//        }
//    }
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileFragment.this.getContext());
        builder.setTitle("添加图片");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        //用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "temp_image.jpg"));
                        // 将拍照所得的相片保存到SD卡根目录
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    cutImage(tempUri); // 对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    cutImage(data.getData()); // 对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }
    /**
     * 裁剪图片方法实现
     */
    protected void cutImage(Uri uri) {
        if (uri == null) {
            Log.i("alanjet", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        //将后面的参数从1改为0.1之后呢，就成为任意比例了20170724
        intent.putExtra("aspectX", 0.1);
        intent.putExtra("aspectY", 0.1);
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }
    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");
            //这里图片是方形的，可以用一个工具类处理成圆形（很多头像都是圆形，这种工具类网上很多不再详述）
            mAvatar.setImageBitmap(mBitmap);//显示图片
            //在这个地方可以写上上传该图片到服务器的代码，后期将单独写一篇这方面的博客，敬请期待...


            final Bitmap bitmap = UIUtils.rotate(mBitmap, -90);
            saveBitmap(bitmap);
//            bitmap.recycle();
        }
    }

    public void saveBitmap(Bitmap mBitmap) {
        File filePic;

        try {
            filePic = new File(Constants.CUT_PIC_PATH + "temp.jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}