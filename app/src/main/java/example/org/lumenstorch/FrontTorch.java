package example.org.lumenstorch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class FrontTorch extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    public static final String VALOR_COLOR_ACTUAL = "valorActual";
    public static final String VALOR_CHECK_BOX = "valorCheckBox";
    public static final String TAG = "Error: ";
    public static final int STRING_DEF_VALUE = -1;
    public static final boolean CHECKBOX_DEF_VALUE = false;
    public static final String SPACE = " ";
    //    public static final String REAR = "Rear";
//    public static final String FRONT = "Front";
    private final int MIN = 0;
    private final int MAX = 5;
    private final int INIT_VALUE = MAX;

    private SharedPreferences sharedPreferences;
    private ImageView botonOnOff;
    private CameraManager manager;
    private String idCamara;
    private TextView textLightAvailable;
    private TextView textLightReading;
    private TextView textoMedidaBarra;
    private SeekBar colorSelector;
    private CheckBox checkBox;
    private SensorManager mySensorManager;
    private Sensor lightSensor;
    private ImageView imgLinternaFrontal;
    //    private TextView textoSelectorColor;
//    private SeekBar colorSelector;
    private ConstraintLayout screen;

    //    private boolean isFrontScreenActivated = false;
    private boolean isFlashActivated = false;
//    private String activatedTorch;

//    private final Integer MAX_INTEGER_COLOR = 16777215;
//    private final int MAX_COLOR_SCREEN = MAX_INTEGER_COLOR;
//    private final int INIT_COLOR_SCREEN_VALUE = MAX_COLOR_SCREEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_torch);
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
//        activatedTorch = REAR;

        //Casting de elementos.
        screen = (ConstraintLayout) findViewById(R.id.screen);
        imgLinternaFrontal = (ImageView) findViewById(R.id.toggle_front_back_flash);
        botonOnOff = (ImageView) findViewById(R.id.boton);
        textLightAvailable = (TextView) findViewById(R.id.LIGHT_available);
        textLightReading = (TextView) findViewById(R.id.LIGHT_reading);
        textoMedidaBarra = (TextView) findViewById(R.id.muestrabarra);
        checkBox = (CheckBox) findViewById(R.id.automatic);
        colorSelector = (SeekBar) findViewById(R.id.color_selector);
//        textoSelectorColor = (TextView) findViewById(R.id.texto_selector_color);
//        colorSelector = (SeekBar) findViewById(R.id.color_selector);
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        try {
            idCamara = manager.getCameraIdList()[0];
        } catch (CameraAccessException cae) {
            Log.e(TAG, "Error al acceder a la cámara: " + cae.toString());
        }

        // Listeners settings
        imgLinternaFrontal.setOnClickListener(this);
        botonOnOff.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(this);
        colorSelector.setOnSeekBarChangeListener(this);
//        colorSelector.setOnSeekBarChangeListener(this);
        lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

//        setupScreenByActivatedFlash(activatedTorch);

        screen.setBackgroundColor(Color.BLACK);
    }

//    private void setupScreenByActivatedFlash(String activatedTorch) {
//        if (activatedTorch.equals(REAR)) {
//            activateBackTorch();
//        } else {
//            activateFrontTorch();
//        }
//    }

    /**
     * Se inicializará el estado del checkBox y el valor de la seekbar
     */
    @Override
    protected void onResume() {
        super.onResume();

        Integer valorBarraColor = recuperarValorActualDePreferencias();
        colorSelector.setMax(MAX - MIN);
        colorSelector.setProgress(valorBarraColor - MIN);
//        colorSelector.setMax(MAX_COLOR_SCREEN);
//        colorSelector.setProgress(INIT_COLOR_SCREEN_VALUE);
        textoMedidaBarra.setText(getResources().getString(R.string.aplicar)
                + SPACE + valorBarraColor + SPACE + getResources().getString(R.string.lumens));

        if (lightSensor != null) {
            textLightAvailable.setText(getResources().getString(R.string.sensor_disponible));
        } else {
            checkBox.setVisibility(View.INVISIBLE);
            textLightAvailable.setText(getResources().getString(R.string.sensor_no_disponible));
        }

        Boolean isChecked = sharedPreferences.getBoolean(VALOR_CHECK_BOX, CHECKBOX_DEF_VALUE);
        checkBox.setChecked(isChecked);
        comprobarCheckBox(isChecked);
    }

    /**
     * Se guardará el estado del checkBox y el valor de la seekbar
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VALOR_COLOR_ACTUAL, colorSelector.getProgress());
        editor.putBoolean(VALOR_CHECK_BOX, checkBox.isChecked());
        editor.commit();

        toggleOnOffScreen(false);
        mySensorManager.unregisterListener(lightSensorListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.boton:
                isFlashActivated = !isFlashActivated;
                toggleOnOffScreen(isFlashActivated);
                break;
            case R.id.toggle_front_back_flash:
//                toggleActivateFrontScreen();
                openBackTorch();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        comprobarCheckBox(isChecked);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Integer seekbarValue = seekBar.getProgress();
//        seekbarValue += MIN;
//        textoMedidaBarra.setText(getResources().getString(R.string.aplicar)
//                + SPACE + seekbarValue + SPACE + getResources().getString(R.string.lumens));
        takeScreenColorFromColorSelector();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
//        takeScreenColorFromColorSelector();
    }

    private void openBackTorch() {
        Intent intent = new Intent(getApplicationContext(), BackTorch.class);
        startActivity(intent);
    }

    /**
     * @return el valor guardado de la seekbar
     */
    private Integer recuperarValorActualDePreferencias() {
        Integer valorActual = sharedPreferences.getInt(VALOR_COLOR_ACTUAL, STRING_DEF_VALUE);
        if (valorActual == STRING_DEF_VALUE) {
            valorActual = INIT_VALUE;
        }

        return valorActual;
    }

    private void takeScreenColorFromColorSelector() {
//        Integer seekbarValue = colorSelector.getProgress();
//        Integer color = ((MAX_INTEGER_COLOR / MAX) * seekbarValue);
//        String hexColor = String.format("#%06X", (0xFFFFFF & color));
//
//        screen.setBackgroundColor(Color.parseColor(hexColor));

        Integer seekbarValue = colorSelector.getProgress();
        switch (seekbarValue) {
            case 0:
                screen.setBackgroundColor(Color.YELLOW);
                break;
            case 1:
                screen.setBackgroundColor(Color.GREEN);
                break;
            case 2:
                screen.setBackgroundColor(Color.BLUE);
                break;
            case 3:
                screen.setBackgroundColor(Color.RED);
                break;
            case 4:
                screen.setBackgroundColor(Color.WHITE);
                break;
            default:
                break;
        }

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
                textLightReading.setText(getResources().getString(R.string.luz) + luz.toString());
                compararLuz(colorSelector.getProgress(), luz);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void compararLuz(int seleccionada, float luzSensor) {
        if (seleccionada > luzSensor) {
            if (!isFlashActivated) {
                isFlashActivated = true;
                toggleOnOffScreen(isFlashActivated);
            }
        } else {
            if (isFlashActivated) {
                isFlashActivated = false;
                toggleOnOffScreen(isFlashActivated);
            }
        }

    }

//    private void toggleActivateFrontScreen() {
//        if (isFrontScreenActivated) {
//            setupScreenByActivatedFlash(FRONT);
//        } else {
//            setupScreenByActivatedFlash(REAR);
//        }
//
//        isFrontScreenActivated = !isFrontScreenActivated;
//    }

//    private void activateFrontTorch() {
//        toggleOnOffScreen(true);
//        setFrontVisibility(View.VISIBLE);
//        setBackVisibility(View.GONE);
//    }

//    private void activateBackTorch() {
//        screen.setBackgroundColor(Color.BLACK);
//        isFlashActivated = false;
//        toggleOnOffScreen(isFlashActivated);
//        setFrontVisibility(View.GONE);
//        setBackVisibility(View.VISIBLE);
//    }

//    private void setFrontVisibility(int visibility) {
//        textoSelectorColor.setVisibility(visibility);
////        colorSelector.setVisibility(visibility);
//    }

//    private void setBackVisibility(int visibility) {
//        checkBox.setVisibility(visibility);
//        textoMedidaBarra.setVisibility(visibility);
//        colorSelector.setVisibility(visibility);
//        textLightAvailable.setVisibility(visibility);
//        textLightReading.setVisibility(visibility);
//    }

//    private void toggleOnOffScreen(boolean activate) {
//        if (activate) {
////            takeScreenColorFromColorSelector();
//            screen.setBackgroundColor(Color.WHITE);
//        } else {
//            screen.setBackgroundColor(Color.BLACK);
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void toggleOnOffScreen(boolean activated) {
//        try {
//            manager.setTorchMode(idCamara, activated);
//        } catch (CameraAccessException cae) {
//            Log.e(TAG, "Error al acceder a la cámara: " + cae.toString());
//        } catch (IllegalArgumentException iae) {
//            Log.e(TAG, "No hay flash: " + iae.toString());
//        }
        if(activated) {
            takeScreenColorFromColorSelector();
        } else {
            screen.setBackgroundColor(Color.BLACK);
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
        botonOnOff.setImageDrawable(getResources().getDrawable(R.drawable.imagen_desbloqueado));
        colorSelector.setVisibility(View.INVISIBLE);
        textoMedidaBarra.setVisibility(View.INVISIBLE);
//        mySensorManager.unregisterListener(lightSensorListener);
        isFlashActivated = false;
        toggleOnOffScreen(isFlashActivated);
    }

    public void activarAuto() {
        botonOnOff.setClickable(false);
        botonOnOff.setImageDrawable(getResources().getDrawable(R.drawable.imagen_bloqueado));
        colorSelector.setVisibility(View.VISIBLE);
        textoMedidaBarra.setVisibility(View.VISIBLE);
//        mySensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}