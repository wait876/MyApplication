package com.zjut.navigationdrawerdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Date;

public class MyService extends Service {

    private IBinder binder=new MyBinder();
    private String string;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    public class MyBinder extends Binder
    {
        MyService getService()
        {
            return MyService.this;
        }
    }

    public String getString()
    {
        string=new Date().toLocaleString();
        return string;
    }
}
