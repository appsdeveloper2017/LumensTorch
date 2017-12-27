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

public class MainActivity extends AppCompatActivity {

    public static final String VALOR_ACTUAL = "valorActual";
    public static final String VALOR_CHECK_BOX = "valorCheckBox";
    public static final String TAG = "Error: ";
    public static final int STRING_DEF_VALUE = -1;
    public static final boolean CHECKBOX_DEF_VALUE = false;
    public static final String SPACE = " ";
    private final int MIN = 0;
    private final int MAX = 250;
    private final int INIT_VALUE = 125;

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
    private SensorManager mySensorManager;
    private Sensor lightSensor;

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
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            idCamara = manager.getCameraIdList()[0];
        } catch (CameraAccessException cae) {
            Log.e(TAG, "Error al acceder a la cámara: " + cae.toString());
        }

        //Barra se selección de la cantidad de luz.
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                current = seekBar.getProgress() + MIN;
                textoMedidaBarra.setText(getResources().getString(R.string.aplicar)
                        + SPACE + current + SPACE + getResources().getString(R.string.lumens));
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
                comprobarCheckBox(isChecked);
            }
        });

        botonOnOff.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
            @Override
            public void onClick(View view) {

                try {
                    manager.setTorchMode(idCamara, !turnon);
                } catch (CameraAccessException cae) {
                    Log.e(TAG, "Error al acceder a la cámara: " + cae.toString());
                } catch (IllegalArgumentException iae) {
                    Log.e(TAG, "No hay flash: " + iae.toString());
                }
                turnon = !turnon;
            }
        });

        //Construcción del objeto sensor de luz.
        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            textLIGHT_available.setText(getResources().getString(R.string.sensor_disponible));
        } else {
            textLIGHT_available.setText(getResources().getString(R.string.sensor_no_disponible));
        }
    }

    /**
     * Se inicializará el estado del checkBox y el valor de la seekbar
     */
    @Override
    protected void onResume() {
        super.onResume();

        current = recuperarValorActualDePreferencias();
        seekBar.setMax(MAX - MIN);
        seekBar.setProgress(current - MIN);
        textoMedidaBarra.setText(getResources().getString(R.string.aplicar)
                + SPACE + current + SPACE + getResources().getString(R.string.lumens));

        Boolean isChecked = sharedPreferences.getBoolean(VALOR_CHECK_BOX, CHECKBOX_DEF_VALUE);
        checkBox.setChecked(isChecked);
        comprobarCheckBox(isChecked);
    }

    /**
     * Se guardará el estado del checkBox y el valor de la seekbar
     */
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VALOR_ACTUAL, seekBar.getProgress());
        editor.putBoolean(VALOR_CHECK_BOX, checkBox.isChecked());
        editor.commit();

        mySensorManager.unregisterListener(lightSensorListener);
    }

    /**
     * @return el valor guardado de la seekbar
     */
    private Integer recuperarValorActualDePreferencias() {
        Integer valorActual = sharedPreferences.getInt(VALOR_ACTUAL, STRING_DEF_VALUE);
        if (valorActual == STRING_DEF_VALUE) {
            valorActual = INIT_VALUE;
        }

        return valorActual;
    }

    private final SensorEventListener lightSensorListener
            = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // NO HACE NADA EN EL CODIGO, LO GENERA EL METODO.

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                Float luz = event.values[0];
                textLIGHT_reading.setText(getResources().getString(R.string.luz) + luz.toString());
                compararLuz(current, luz);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void compararLuz(int seleccionada, float luzSensor) {
        if (seleccionada > luzSensor) {
            if (!turnon) {
                turnon = true;
                toggleFlash(turnon);
            }
        } else {
            if (turnon) {
                turnon = false;
                toggleFlash(turnon);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void toggleFlash(boolean newState) {
        try {
            manager.setTorchMode(idCamara, turnon);
        } catch (CameraAccessException cae) {
            Log.e(TAG, "Error al acceder a la cámara: " + cae.toString());
        }
    }

    public void comprobarCheckBox(boolean isChecked) {
        if (isChecked) {
            activarAuto();
        } else {
            activarManual();
        }
    }

    public void activarManual() {
        botonOnOff.setClickable(true);
        seekBar.setVisibility(View.INVISIBLE);
        textoMedidaBarra.setVisibility(View.INVISIBLE);
        mySensorManager.unregisterListener(lightSensorListener);
    }

    public void activarAuto() {
        botonOnOff.setClickable(false);
        seekBar.setVisibility(View.VISIBLE);
        textoMedidaBarra.setVisibility(View.VISIBLE);
        mySensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

}