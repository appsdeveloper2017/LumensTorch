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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class BackTorch extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    public final String VALOR_ACTUAL = "valorActual";
    public final String VALOR_CHECK_BOX = "valorCheckBox";
    public final String TAG = "Error: ";
    public final String SPACE = " ";
    public final boolean CHECKBOX_DEF_VALUE = false;
    public final int STRING_DEF_VALUE = -1;
    private final int MIN = 0;
    private final int MAX = 250;
    private final int INIT_VALUE = MAX / 2;

    private SharedPreferences sharedPreferences;

    private ImageView botonOnOff;
    private CameraManager manager;
    private String idCamara;
    private TextView textLightAvailable;
    private TextView textLightReading;
    private TextView textoMedidaBarra;
    private SeekBar barraLumens;
    private CheckBox checkBox;
    private SensorManager mySensorManager;
    private Sensor lightSensor;
    private ImageView imgLinternaFrontal;
    private ConstraintLayout screen;

    private boolean isFlashActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_torch);
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        // TODO: Incluir banner publicidad

        //Casting de elementos.
        screen = (ConstraintLayout) findViewById(R.id.screen);
        imgLinternaFrontal = (ImageView) findViewById(R.id.toggle_front_back_flash);
        botonOnOff = (ImageView) findViewById(R.id.main_button);
        textLightAvailable = (TextView) findViewById(R.id.LIGHT_available);
        textLightReading = (TextView) findViewById(R.id.LIGHT_reading);
        textoMedidaBarra = (TextView) findViewById(R.id.muestrabarra);
        checkBox = (CheckBox) findViewById(R.id.automatic);
        barraLumens = (SeekBar) findViewById(R.id.color_selector);
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
        barraLumens.setOnSeekBarChangeListener(this);
        lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        screen.setBackgroundColor(Color.BLACK);
    }

    /**
     * Se inicializará el estado del checkBox y el valor de la seekbar
     */
    @Override
    protected void onResume() {
        super.onResume();

        Integer valorBarraLumens = recuperarValorActualDePreferencias();
        barraLumens.setMax(MAX - MIN);
        barraLumens.setProgress(valorBarraLumens - MIN);
        textoMedidaBarra.setText(getResources().getString(R.string.aplicar)
                + SPACE + valorBarraLumens + SPACE + getResources().getString(R.string.lumens));

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
        editor.putInt(VALOR_ACTUAL, barraLumens.getProgress());
        editor.putBoolean(VALOR_CHECK_BOX, checkBox.isChecked());
        editor.commit();

        toggleOnOffFlash(false);
        mySensorManager.unregisterListener(lightSensorListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_button:
                isFlashActivated = !isFlashActivated;
                animBotonOnOff(view);
                break;
            case R.id.toggle_front_back_flash:
                animFrontTorchButton(view);
                break;
            default:
                break;
        }
    }

    private void animFrontTorchButton(final View view) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.image_torch_mirror_left);
        view.startAnimation(anim);
        openFrontTorch();
    }

    private void animBotonOnOff(View view) {
        Animation anim;
        if (isFlashActivated) {
            anim = AnimationUtils.loadAnimation(this, R.anim.boton_on);
        } else {
            anim = AnimationUtils.loadAnimation(this, R.anim.boton_off);
        }
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toggleOnOffFlash(isFlashActivated);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        comprobarCheckBox(isChecked);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Integer seekbarValue = seekBar.getProgress();
        seekbarValue += MIN;
        textoMedidaBarra.setText(getResources().getString(R.string.aplicar)
                + SPACE + seekbarValue + SPACE + getResources().getString(R.string.lumens));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void openFrontTorch() {
        Intent intent = new Intent(getApplicationContext(), FrontTorch.class);
        startActivity(intent);
        overridePendingTransition(R.anim.appear_from_right_to_left, R.anim.disappear_from_right_to_left);
        finish();
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

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                Float luz = event.values[0];
                textLightReading.setText(getResources().getString(R.string.luz) + luz.toString());
                compararLuz(barraLumens.getProgress(), luz);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void compararLuz(int seleccionada, float luzSensor) {
        if (seleccionada > luzSensor) {
            if (!isFlashActivated) {
                isFlashActivated = true;
                toggleOnOffFlash(isFlashActivated);
            }
        } else {
            if (isFlashActivated) {
                isFlashActivated = false;
                toggleOnOffFlash(isFlashActivated);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void toggleOnOffFlash(boolean activated) {
        try {
            manager.setTorchMode(idCamara, activated);
        } catch (CameraAccessException cae) {
            Log.e(TAG, "Error al acceder a la cámara: " + cae.toString());
        } catch (IllegalArgumentException iae) {
            Log.e(TAG, "No hay flash: " + iae.toString());
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
        barraLumens.setVisibility(View.INVISIBLE);
        textoMedidaBarra.setVisibility(View.INVISIBLE);
        mySensorManager.unregisterListener(lightSensorListener);
        isFlashActivated = false;
        toggleOnOffFlash(isFlashActivated);
    }

    public void activarAuto() {
        botonOnOff.setClickable(false);
        botonOnOff.setImageDrawable(getResources().getDrawable(R.drawable.imagen_bloqueado));
        barraLumens.setVisibility(View.VISIBLE);
        textoMedidaBarra.setVisibility(View.VISIBLE);
        mySensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}