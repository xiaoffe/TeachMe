package yxf.teachme.ui.view.doodle;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Doodle extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder = null;

    //当前所选画笔的形状
    private Action curAction = null;
    private Action curPeerAction = null;
    //默认画笔为黑色
    private int currentColor = Color.BLACK;
    private int currentPeerColor = Color.BLACK;
    //画笔的粗细
    private int currentSize = 4;
    private int currentPeerSize = 4;
    private Paint mPaint;
    //记录画笔的列表
    private List<Action> mActions = new ArrayList<>();

    private Bitmap bmp;
    private Bitmap peerBmp;
    private ActionType type = ActionType.Path;
    private ActionType peerType = ActionType.Path;

    public Doodle(Context context) {
        super(context);
        init();
    }

    public Doodle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Doodle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(currentSize);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.parseColor("#ffcccccc"));
//        canvas.drawColor(Color.WHITE);
        for (Action a : mActions) {
            a.draw(canvas);
        }
        mSurfaceHolder.unlockCanvasAndPost(canvas);

        Log.d("look", "surface created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d("look", "surface changed");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("look", "surface destroyed");
//        recycleBitmap();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }

        float touchX = event.getRawX();
        float touchY = event.getRawY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setCurAction(touchX, touchY);

                if(mDrawListener != null){
                    mDrawListener.onDown(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Canvas canvas = mSurfaceHolder.lockCanvas();
//                if(canvas == null){
//                    Log.d("look", "ACTION MOVE canvas==null");
//                }else{
//                    Log.d("look", "ACTION MOVE canvas!=null");
//                }
                canvas.drawColor(Color.parseColor("#ffcccccc"));
//                canvas.drawColor(Color.WHITE);
                for (Action a : mActions) {
                    a.draw(canvas);
                }
                curAction.move(touchX, touchY);
                curAction.draw(canvas);
//                if(curPeerAction != null){
//                    curPeerAction.move(touchX+50, touchY);
//                    curPeerAction.draw(canvas);
//                }
                mSurfaceHolder.unlockCanvasAndPost(canvas);
                if(mDrawListener != null){
                    mDrawListener.onMove(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                mActions.add(curAction);
                curAction = null;
                if(mDrawListener != null){
                    mDrawListener.onUp(event);
                }
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    // 得到当前画笔的类型，并进行实例
    public void setCurAction(float x, float y) {
        switch (type) {
            case Point:
                curAction = new MyPoint(x, y, currentColor);
                break;
            case Path:
                curAction = new MyPath(x, y, currentSize, currentColor);
                break;
            case Line:
                curAction = new MyLine(x, y, currentSize, currentColor);
                break;
            case Rect:
                curAction = new MyRect(x, y, currentSize, currentColor);
                break;
            case Circle:
                curAction = new MyCircle(x, y, currentSize, currentColor);
                break;
            case FillecRect:
                curAction = new MyFillRect(x, y, currentSize, currentColor);
                break;
            case FilledCircle:
                curAction = new MyFillCircle(x, y, currentSize, currentColor);
                break;
//            case Pic:
//                curAction = new MyPic();
//                break;
        }
    }
    public void setPeerCurAction(float x, float y) {
        switch (peerType) {
            case Point:
                curPeerAction = new MyPoint(x, y, currentPeerColor);
                break;
            case Path:
                curPeerAction = new MyPath(x, y, currentPeerSize, currentPeerColor);
                break;
            case Line:
                curPeerAction = new MyLine(x, y, currentPeerSize, currentPeerColor);
                break;
            case Rect:
                curPeerAction = new MyRect(x, y, currentPeerSize, currentPeerColor);
                break;
            case Circle:
                curPeerAction = new MyCircle(x, y, currentPeerSize, currentPeerColor);
                break;
            case FillecRect:
                curPeerAction = new MyFillRect(x, y, currentPeerSize, currentPeerColor);
                break;
            case FilledCircle:
                curPeerAction = new MyFillCircle(x, y, currentPeerSize, currentPeerColor);
                break;
//            case Pic:
//                curPeerAction = new MyPic();
//                break;
        }
    }

    /**
     * 设置画笔的颜色
     * @param color
     */
    public void setColor(String color) {
        currentColor = Color.parseColor(color);
    }
    public int getColor(){
        return currentColor;
    }
    public void setPeerColor(int color){
        currentPeerColor = color;
    }
    /**
     * 设置画笔的粗细
     * @param size
     */
    public void setSize(int size) {
        currentSize = size;
    }
    public int getSize(){
        return currentSize;
    }
    public void setPeerSize(int size){
        currentPeerSize = size;
    }
    public int getPeerSize(){
        return currentPeerSize;
    }

    /**
     * 获取画布的截图
     * @return
     */
    public Bitmap getBitmap() {
        bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        doDraw(canvas);
        return bmp;
    }

    public void doDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        for (Action a : mActions) {
            a.draw(canvas);
        }
        canvas.drawBitmap(bmp, 0, 0, mPaint);
    }

    /**
     * 回退
     * @return
     */
    public boolean back() {
        if (mActions != null && mActions.size() > 0) {
            mActions.remove(mActions.size() - 1);
            Canvas canvas = mSurfaceHolder.lockCanvas();
            canvas.drawColor(Color.parseColor("#ffcccccc"));
//            canvas.drawColor(Color.WHITE);
            for (Action a : mActions) {
                a.draw(canvas);
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
            return true;
        }
        return false;
    }

    public enum ActionType {
        Point, Path, Line, Rect, Circle, FillecRect, FilledCircle, Eraser, Pic
    }

    public ActionType getCurType() {
        return type;
    }
    public void setCurType(ActionType actionType){
        type = actionType;
    }

    public ActionType getCurPeerType(){
        return peerType;
    }

    public void setCurPeerType(ActionType actionType){
        peerType = actionType;
    }

    public void drawPic(Bitmap bitmap){
        ActionType tempActionType = getCurType();
        //设置当前的类型
        setCurType(ActionType.Pic);
        curAction = new MyPic(bitmap, mPaint);

        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.parseColor("#ffcccccc"));
//        canvas.drawColor(Color.WHITE);
        for (Action a : mActions) {
            a.draw(canvas);
        }
        curAction.draw(canvas);
        mActions.add(curAction);
        curAction = null;
        mSurfaceHolder.unlockCanvasAndPost(canvas);

        //设置回默认的类型
        setCurType(tempActionType);
    }

    public interface OnDrawListener{
        void onDown(MotionEvent event);
        void onMove(MotionEvent event);
        void onUp(MotionEvent event);
    }
    private OnDrawListener mDrawListener;

    public void setOnDrawListener(OnDrawListener onDrawListener){
        mDrawListener = onDrawListener;
    }

    public void downPeer(ActionType peerType, int peerSize, int peerColor, float peerX, float peerY){
        setCurPeerType(peerType);
        setPeerSize(peerSize);
        setPeerColor(peerColor);
        setPeerCurAction(peerX, peerY);
    }

    public void upPeer(){
        mActions.add(curPeerAction);
        curPeerAction = null;
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.parseColor("#ffcccccc"));
        for (Action a : mActions) {
            a.draw(canvas);
        }
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }
    public void movePeer(float x, float y){
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.parseColor("#ffcccccc"));
        for (Action a : mActions) {
            a.draw(canvas);
        }
        if(curPeerAction != null){
            curPeerAction.move(x, y);
            curPeerAction.draw(canvas);
        }
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    public void justMovePeer(float x, float y){
        if(curPeerAction != null){
            curPeerAction.move(x, y);
        }
    }

    public void recycleBitmap(){
        for (Action action : mActions) {
            if (action instanceof MyPic) {
                ((MyPic)action).recycleAskBitmap();
            }
        }
        if(bmp!=null && !bmp.isRecycled()){
            bmp.recycle();
        }
    }
}

