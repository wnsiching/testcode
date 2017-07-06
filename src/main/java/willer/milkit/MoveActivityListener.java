package willer.milkit;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by User on 5/5/2017.
 */

public class MoveActivityListener implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 1000;

    private Context context = null;
    private long LastMove = 0;
    private HandleMove hmv = null;

    public void setContext(Context context){
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            //System.out.println(x);
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 500) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 300000;

                if (speed > SHAKE_THRESHOLD) {
                    System.out.println("Moving, MoveActivityListener");

                    if(context!=null){
                        vibratePhone(context );

                        try{

                            LastMove = System.currentTimeMillis();

                            if(hmv != null) {
                                hmv.onMove();
                            }


                            /*
                            URL yahoo = new URL("http://www.yahoo.com/");
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                            yahoo.openStream()));

                            String inputLine;

                            while ((inputLine = in.readLine()) != null)
                                System.out.println(inputLine);

                            in.close();
                            */


                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }


                }

                last_x = x;
                last_y = y;
                last_z = z;
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean setHandler(HandleMove hmv){
        this.hmv = hmv;
        return true;
    }



    public long getLastMove(){
        return LastMove;
    }

    public void vibratePhone(Context context){

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);

    }

}
