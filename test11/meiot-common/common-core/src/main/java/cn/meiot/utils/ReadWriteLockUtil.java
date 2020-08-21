package cn.meiot.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁操作
 */
public class ReadWriteLockUtil {

    private static final ReadWriteLock rwl;

    private static final Lock readLock;

    private static final Lock writeLock;



    /**
     * 获取项目类型时加锁
     */
    public static final ReadWriteLock projectNameLock = new ReentrantReadWriteLock();

    /**
     * 通过企业id获取企业名称时加锁
     */
    public static final ReadWriteLock enterpriseNameLock = new ReentrantReadWriteLock();


    /**
     * 获取accessToken
     */
    public static final ReadWriteLock ACCESSTOKENLOCK = new ReentrantReadWriteLock();

    /**
     * 获取accessToken
     */
    public static final ReadWriteLock qrCodeTicketLock = new ReentrantReadWriteLock();


    /**
     * 获取配置信息
     */
    public static final ReadWriteLock CONFIG_VALUE = new ReentrantReadWriteLock();


    /**
     *获取读锁
     * @param readWriteLock
     * @return
     */
    public static Lock readLock(ReadWriteLock readWriteLock){

        return readWriteLock.readLock();
    }

    /**
     *获取写锁
     * @param readWriteLock
     * @return
     */
    public static Lock writeLock(ReadWriteLock readWriteLock){

        return readWriteLock.writeLock();
    }



    static {
        rwl = new ReentrantReadWriteLock();
        readLock =  rwl.readLock();
        writeLock =  rwl.writeLock();
    }

    /**
     * 读锁
     * @return
     */
    public static void getReadLock(){
        readLock.lock();
       // return  readLock;
    }

    /**
     * 读锁
     * @return
     */
    public static void unReadLock(){
        readLock.unlock();
        // return  readLock;
    }

    /**
     * 写锁
     * @return
     */
    public static void getWriteLock(){
        writeLock.lock();
        //return  writeLock;
    }

    /**
     * 写锁
     * @return
     */
    public static void untWriteLock(){
        writeLock.unlock();
        //return  writeLock;
    }
}
