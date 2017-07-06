package willer.milkit;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.TextView;

public class Milky extends AppCompatActivity {

    private TextView tvLog ;
    private AutoStartUp services;
    private MoveActivityListener moveListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milky);

        //
        tvLog = (TextView)findViewById(R.id.milklog);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if(moveListener != null){
                    Log.d("Main Activity",moveListener.getLastMove()+"");
                }else{
                    Log.d("Main Activity","moveListener IS NULL ");
                }

            }
        });

        //Intent intent= new Intent(this, AutoStartUp.class);
        //startService(intent);

        doBindService();

    }

    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.d("Main Activity", "Service Connected");
            AutoStartUp.AutoStartBinder binderr=(AutoStartUp.AutoStartBinder)service;
            services=binderr.getServiceSystem();
            moveListener = services.getMoveListener();

            if(moveListener != null){
                Log.d("Main Activity", "moveListener not null");
            }

                /*
                HandleMove hmv = new HandleMove() {
                    @Override
                    public void onMove() {
                        Log.d("Main Activity", "HandleMove got movement");
                    }
                }
                moveListener.setHandler(hmv);
                */

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    void doBindService() {
        bindService(new Intent(this, AutoStartUp.class), connection, this.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        // Detach our existing connection.
        unbindService(connection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_milky, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
