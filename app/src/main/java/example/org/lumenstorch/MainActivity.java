package example.org.lumenstorch;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView boton;
    private CameraManager manager;
    private String idCamara;
    public boolean turnon = false;
    TextView textLIGHT_available;
    TextView textLIGHT_reading;
    TextView pantalla;
    SeekBar seekBar;
    int min = 0, max = 500, current = 250;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        boton = (ImageView) findViewById(R.id.boton);

        textLIGHT_available = (TextView) findViewById(R.id.LIGHT_available);
        textLIGHT_reading = (TextView) findViewById(R.id.LIGHT_reading);

        seekBar = (SeekBar) findViewById(R.id.barralumens);
        pantalla = (TextView) findViewById(R.id.muestrabarra);

        seekBar.setMax(max-min);
        seekBar.setProgress(current-min);

        pantalla.setText(""+current);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                current = progress + min;
                pantalla.setText(""+current);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Construcción del objeto sensor de luz.

        SensorManager mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (LightSensor != null) {
            textLIGHT_available.setText("(Sensor de luz disponible)");
            mySensorManager.registerListener(LightSensorListener, LightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            textLIGHT_available.setText("(Sensor de luz no disponible)");
        }
    }


    private final SensorEventListener LightSensorListener
            = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // NO HACE NADA EN EL CODIGO, LO GENERA EL METODO.

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                textLIGHT_reading.setText("LIGHT: " + event.values[0]);


                boton.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
                    @Override
                    public void onClick(View view) {


                            try {
                                CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                                idCamara = manager.getCameraIdList()[0];
                                manager.setTorchMode(idCamara, !turnon);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            } catch (IllegalArgumentException iae) {
                                Log.e("Error", "No hay flash");
                            }
                            turnon = !turnon;
                    }
                });
            }
        }
    };
}