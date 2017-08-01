package yxf.teachme.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yxf.teachme.manager.ThreadPoolProxy;

/**
 * Created by Administrator on 2017/7/17.
 */

public class ThreadFactory {
    static ThreadPoolProxy mNormalPool;// 只需初始化一次就行了
    static ThreadPoolProxy mDownLoadPool;// 只需初始化一次就行了
    static ThreadPoolProxy mFixedPool;// 只需初始化一次就行了
    static ExecutorService mCachedThreadPool;
    static ExecutorService mSingleThreadExecutor;


    /**
     * 创建了一个普通的线程池
     */
    public static ThreadPoolProxy getNormalPool() {

        if (mNormalPool == null) {
            synchronized (ThreadFactory.class) {
                if (mNormalPool == null) {
                    mNormalPool = new ThreadPoolProxy(5, 5, 3000);
                }
            }
        }
        return mNormalPool;
    }

    /**
     * 创建了下载线程池
     */
    public static ThreadPoolProxy getDownLoadPool() {
        if (mDownLoadPool == null) {
            synchronized (ThreadFactory.class) {
                if (mDownLoadPool == null) {
                    mDownLoadPool = new ThreadPoolProxy(3, 3, 3000);
                }
            }
        }
        return mDownLoadPool;
    }


    /**
     * 创建了用户制定大小的线程池
     */
    public static ThreadPoolProxy getFixedPool(int corePoolSize, int maxPoolSize, long keepAliveTime) {
        if (mFixedPool == null) {
            synchronized (ThreadFactory.class) {
                if (mFixedPool == null) {
                    mFixedPool = new ThreadPoolProxy(corePoolSize, maxPoolSize, keepAliveTime);
                }
            }
        }
        return mFixedPool;
    }

    /**
     * 创建一个可缓存线程池
     *
     * @return
     */
    public static ExecutorService getCachedThreadPool() {

        if (mCachedThreadPool == null) {
            synchronized (ThreadFactory.class) {
                if (mCachedThreadPool == null) {
                    mCachedThreadPool = Executors.newCachedThreadPool();
                }
            }
        }
        return mCachedThreadPool;
    }


    /**
     * 创建一个单一线程池
     * @return
     */
    public static ExecutorService getSingleThreadExecutor() {
        if (mSingleThreadExecutor == null) {
            synchronized (ThreadFactory.class) {
                if (mSingleThreadExecutor == null) {
                    mSingleThreadExecutor = Executors.newSingleThreadExecutor();
                }
            }
        }
        return mSingleThreadExecutor;
    }
}
