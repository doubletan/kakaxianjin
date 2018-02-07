package com.xinhe.kakaxianjin;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.umeng.commonsdk.UMConfigure;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.bean.CardList;
import com.xinhe.kakaxianjin.bean.CashRecords;
import com.xinhe.kakaxianjin.bean.CreditProduct;

import java.util.ArrayList;

/**
 * Created by tantan on 2018/1/15.
 */

public class MyApplication extends Application{



    /**
     * release=true 软件发布 false:开发中
     */
    public static boolean isRelease = true;
    //信用卡产品
    public static CreditProduct creditProduct;
    //用户token
    public static String token;
    //用户是否登录
    public static boolean isLogin;
    //手机号
    public static String phone;
    //姓名
    public static String name;
    //身份证号
    public static String ID;
    //银行卡列表
    public static CardList cardList;
    //取现记录
    public static CashRecords cashRecords;
    private ArrayList<Activity> myActivity = new ArrayList<>();
    private static MyApplication instance;

    /**
     * 主线程Handler.
     */
    public static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        //获取实例
        instance = this;

        mHandler = new Handler();

        //网络请求初始化
        OkGo.init(this);
        try {
            OkGo.getInstance()
                    .setCertificates()
//                    .debug("OkGo", Level.INFO, true)
                    .setCacheMode(CacheMode.NO_CACHE);


        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }

        //友盟
        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数3:Push推送业务的secret
         */
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "5a727dcff43e4807e5000030");

    }

    public static MyApplication getApp(){
        return instance;
    }

    public void addToList(Activity activity){
        myActivity.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (Activity activity : myActivity){
            if (activity!=null){
                activity.finish();
            }
        }
//        android.os.Process.killProcess(android.os.Process.myPid());   //获取PID
//        System.exit(0);
    }

}
