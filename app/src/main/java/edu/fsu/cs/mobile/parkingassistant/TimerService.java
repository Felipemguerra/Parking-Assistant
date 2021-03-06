package edu.fsu.cs.mobile.parkingassistant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Felipe on 3/23/2018.
 */

public class TimerService extends Service {

    private Timer timer;

    private int delay;

    private final IBinder binder = new LocalBinder();
    private MainActivity.TimerInterface interf;

    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    public IBinder onBind(Intent i) {
        return binder;
    }

    public void connect(MainActivity.TimerInterface iface, int d) {
        interf = iface;
        delay = d;
        start();
    }

    public void start() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerNotification(), 0, 1000);
    }

    public void cancel() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
        onDestroy();
    }

    private void makeNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setOnlyAlertOnce(false);
        builder.setContentTitle("ParkingAssistant");
        if(delay == 0){builder.setContentText("Times Up!");}
        else {
            int hour = delay/1000/60/60, min = (delay/1000-hour*60*60)/60, sec = (delay/1000-min*60 - hour*60*60);
            builder.setContentText("You have "+hour+" hours, "+min+" minutes and "+sec+" seconds left");
            builder.setOngoing(true);
        }
        nm.notify(1, builder.build());
        if(delay == 0) {interf.finish();}
    }

    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        stopSelf();
    }

    private class TimerNotification extends TimerTask {
        public void run() {
            delay -= 1000;
            makeNotification();
        }
    }
}
