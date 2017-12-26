package example.org.lumenstorch;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String VALOR_ACTUAL = "valorActual";
    public static final String VALOR_CHECK_BOX = "valorCheckBox";
    private final int MIN = 0;
    private final int MAX = 500;
    private final int INIT_VALUE = 250;

    private SharedPreferences sharedPreferences;
    private ImageView botonOnOff;
    private CameraManager manager;
    private String idCamara;
    private boolean turnon = false;
    private TextView textLIGHT_available;
    private TextView textLIGHT_reading;
    private TextView textoMedidaBarra;
    private SeekBar seekBar;
    private Integer current;
    private CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        //Casting de elementos.

        botonOnOff = (ImageView) findViewById(R.id.boton);
        textLIGHT_available = (TextView) findViewById(R.id.LIGHT_available);
        textLIGHT_reading = (TextView) findViewById(R.id.LIGHT_reading);
        textoMedidaBarra = (TextView) findViewById(R.id.muestrabarra);
        checkBox = (CheckBox) findViewById(R.id.automatic);
        seekBar = (SeekBar) findViewById(R.id.barralumens);


        //Barra se selección de la cantidad de luz.

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                current = seekBar.getProgress() + MIN;
                textoMedidaBarra.setText("Aplicar a: " + current + " lm");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxChanged(isChecked);
            }
        });

        botonOnOff.setOnClickListener(new View.OnClickListener() {
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

        //Construcción del objeto sensor de luz.

        SensorManager mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (LightSensor != null) {
            textLIGHT_available.setText("(Sensor de luz disponible)");
            mySensorManager.registerListener(LightSensorListener, LightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            textLIGHT_available.setText("(Sensor de luz no disponible)");
        }
        if (checkBox.isChecked() == true) {

        }
    }

    /**
     *  Se inicializará el estado del checkBox y el valor de la seekbar
     */
    @Override
    protected void onResume() {
        super.onResume();

        current = recuperarValorActualDePreferencias();
        seekBar.setMax(MAX - MIN);
        seekBar.setProgress(current - MIN);
        textoMedidaBarra.setText("Aplicar a: " + current + " lm");

        Boolean isChecked = sharedPreferences.getBoolean(VALOR_CHECK_BOX, false);
        checkBox.setChecked(isChecked);
        checkBoxChanged(isChecked);
    }

    /**
     *  Se guardará el estado del checkBox y el valor de la seekbar
     */
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VALOR_ACTUAL, seekBar.getProgress());
        editor.putBoolean(VALOR_CHECK_BOX, checkBox.isChecked());
        editor.commit();
    }

    /**
     *
     * @return el valor guardado de la seekbar
     */
    private Integer recuperarValorActualDePreferencias() {
        Integer valorActual = sharedPreferences.getInt(VALOR_ACTUAL, -1);
        if (valorActual == (-1)) {
            valorActual = INIT_VALUE;
        }

        return valorActual;
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
                float luz = event.values[0];
            }
        }
    };

    public void comparadorLuz(int progress, float luz) {
        Toast.makeText(this, "hola", Toast.LENGTH_LONG).show();
        if (progress > luz) {

        } else {

        }

    }

    public void checkBoxChanged(boolean isChecked) {
        if (isChecked) {
            botonOnOff.setClickable(false);
            seekBar.setVisibility(View.VISIBLE);
            textoMedidaBarra.setVisibility(View.VISIBLE);
        } else {
            botonOnOff.setClickable(true);
            seekBar.setVisibility(View.INVISIBLE);
            textoMedidaBarra.setVisibility(View.INVISIBLE);
        }
    }

}