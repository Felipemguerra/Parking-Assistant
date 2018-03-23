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

    public void connect(MainActivity.TimerInterface iface) {
        interf = iface;
        start();
    }

    public void start() {
        timer = new Timer();
        timer.schedule(new TimerNotification(), 5000);
    }

    public void cancel() {
        onDestroy();
    }

    private void makeNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setOnlyAlertOnce(false);
        builder.setContentTitle("ParkingAssistant");
        builder.setContentText("You have _ minutes left");
        nm.notify(1, builder.build());
        interf.finish();
    }

    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        stopSelf();
    }

    private class TimerNotification extends TimerTask {
        public void run() {
            makeNotification();
        }
    }
}
