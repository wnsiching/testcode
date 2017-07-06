package willer.milkit;

/**
 * Created by User on 5/5/2017.
 */

        import android.app.AlarmManager;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.app.Service;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.IntentFilter;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.hardware.Sensor;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.media.RingtoneManager;
        import android.net.Uri;
        import android.os.Binder;
        import android.os.IBinder;
        import android.os.StrictMode;
        import android.os.SystemClock;
        import android.os.Vibrator;
        import android.support.v4.app.NotificationCompat;
        import android.util.Log;
        import android.widget.Toast;
        import android.content.Intent;

        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.List;
        import java.util.Scanner;

public class AutoStartUp extends Service {

    private static final String TAG = "AutoStart";
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    BroadcastReceiver mReceiver;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private  MoveActivityListener MoveListener;

    AutoStartBinder  binder=new AutoStartBinder();
    AutoStartUp services;
    static  Context context;

    public class AutoStartBinder extends Binder
    {
        public AutoStartUp getServiceSystem()
        {
            return AutoStartUp.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.ThreadPolicy policy = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Log.d("AutoStartUp", "AutoStart");
        // do something when the service is created

        try {
            context=getApplicationContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(false) {

            mReceiver = new BroadcastReceiver() {
                @Override public void onReceive( Context context, Intent _ )
                {

                    Toast.makeText(context, "Milkit AutoCheck services is running", Toast.LENGTH_SHORT).show();

                    try {
                        //repeated task here
                        System.gc();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //context.unregisterReceiver( this ); // this == BroadcastReceiver, not Activity
                }
            };

            this.registerReceiver(mReceiver, new IntentFilter("autocheck"));
            alarmIntent = PendingIntent.getBroadcast(this, 0, new Intent("autocheck"), 0);
            alarmMgr = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));

            //loop
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, alarmIntent);

            //schedule next
            //alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60000, alarmIntent);

            //Turn on by time
            /*
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 9);//for testing
            calendar.set(Calendar.MINUTE, 05);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),40000, alarmIntent); //run every 1 mins daily after 6pm
            */
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("AutoStartUp","Background started");
        System.out.println("Init Sensor");
        senSensorManager = (SensorManager) getSystemService(this.getBaseContext().SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        MoveListener = new MoveActivityListener();
        MoveListener.setContext(this.getBaseContext());
        senSensorManager.registerListener(MoveListener, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        //DUMP ALL SENSORS AND POWER USAGE
        List<Sensor> deviceSensors = senSensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.w(TAG, "Found " + deviceSensors.size() + " sensors.");
        for (Sensor element : deviceSensors) {
            Log.w(TAG, "Found " + element.getName() + " Type:"+element.getType() +" Power:"+element.getPower()+ "mA " + " Vendor:"+element.getVendor() );
        }

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    public MoveActivityListener getMoveListener(){
        return MoveListener;
    }

    public void showNotification(String title, String text){

        //http://www.programering.com/a/MTOycDMwATY.html
        Intent notificationIntent = new Intent(this, Milky.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification noti = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setTicker("ticker")
                //.setLargeIcon(largeIcon)
                //.setWhen(System.currentTimeMillis()+1000)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(contentIntent)
                //At most three action buttons can be added
                .setAutoCancel(true).build();
        int notifyID =88798;//just default id, dun mess too much with noti screen
        notificationManager.notify(notifyID, noti);

    }

    public void playSound(Context context) throws IllegalArgumentException,
            SecurityException,
            IllegalStateException,
            IOException {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(context, soundUri);
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

            //mMediaPlayer.release();  //MediaPlayer finalized without being released
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}