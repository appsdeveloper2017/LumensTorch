package example.org.lumenstorch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class FrontTorch extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener {

    public final String VALOR_COLOR_ACTUAL = "valorActual";
    public final String TAG = "Error: ";
    public final String SPACE = " ";
    public final int STRING_DEF_VALUE = -1;
    private final int MIN = 0;
    private final int MAX = 5;
    private final int INIT_VALUE = MAX;

    private SharedPreferences sharedPreferences;
    private ImageView botonOnOff;
    private TextView textColorSelector;
    private SeekBar colorSelector;
    private ImageView imgCambioLinterna;
    private ConstraintLayout screen;
    private TextView title;

    private boolean isFlashActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_torch);
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        //Casting de elementos.
        screen = (ConstraintLayout) findViewById(R.id.screen);
        imgCambioLinterna = (ImageView) findViewById(R.id.toggle_front_back_flash);
        botonOnOff = (ImageView) findViewById(R.id.boton);
        textColorSelector = (TextView) findViewById(R.id.text_color_selector);
        // TODO: Poner colores al selector de color
        // TODO: Personalizar la barra selectora de color
        colorSelector = (SeekBar) findViewById(R.id.color_selector);
        title = (TextView) findViewById(R.id.title);

        // Listeners settings
        imgCambioLinterna.setOnClickListener(this);
        botonOnOff.setOnClickListener(this);
        colorSelector.setOnSeekBarChangeListener(this);

        textColorSelector.setText(getResources().getString(R.string.text_color_selector));
        screen.setBackgroundColor(Color.BLACK);
        drawItemsWhite();
    }

    /**
     * Se inicializará el valor de la seekbar
     */
    @Override
    protected void onResume() {
        super.onResume();

        Integer valorBarraColor = recuperarValorActualDePreferencias();
        colorSelector.setMax(MAX - MIN);
        colorSelector.setProgress(valorBarraColor - MIN);
    }

    /**
     * Se guardará el valor de la seekbar
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VALOR_COLOR_ACTUAL, colorSelector.getProgress());
        editor.commit();

        toggleOnOffScreen(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.boton:
                isFlashActivated = !isFlashActivated;
                toggleOnOffScreen(isFlashActivated);
                break;
            case R.id.toggle_front_back_flash:
                openBackTorch();
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isFlashActivated) {
            takeScreenColorFromColorSelector();
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void openBackTorch() {
        Intent intent = new Intent(getApplicationContext(), BackTorch.class);
        startActivity(intent);
        // TODO: Transición lateral para dar la sensación del cambio de frontal a trasera
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
        Integer seekbarValue = colorSelector.getProgress();

        switch (seekbarValue) {
            case 0:
                screen.setBackgroundColor(Color.WHITE);
                drawItemsBlack();
                break;
            case 1:
                screen.setBackgroundColor(Color.YELLOW);
                drawItemsBlack();
                break;
            case 2:
                screen.setBackgroundColor(Color.GREEN);
                drawItemsWhite();
                break;
            case 3:
                screen.setBackgroundColor(Color.BLUE);
                drawItemsWhite();
                break;
            case 4:
                screen.setBackgroundColor(Color.MAGENTA);
                drawItemsWhite();
                break;
            case 5:
                screen.setBackgroundColor(Color.RED);
                drawItemsWhite();
                break;
            default:
                screen.setBackgroundColor(Color.BLACK);
                drawItemsWhite();
                break;
        }
    }

    private void drawItemsWhite() {
        title.setTextColor(Color.WHITE);
        botonOnOff.setImageDrawable(getResources().getDrawable(R.drawable.imagen_desbloqueado));
        imgCambioLinterna.setImageDrawable(getResources().getDrawable(R.drawable.linterna_frontal));
        textColorSelector.setTextColor(Color.WHITE);
    }

    private void drawItemsBlack() {
        title.setTextColor(Color.BLACK);
        botonOnOff.setImageDrawable(getResources().getDrawable(R.drawable.imagen_black));
        imgCambioLinterna.setImageDrawable(getResources().getDrawable(R.drawable.linterna_frontal_black));
        textColorSelector.setTextColor(Color.BLACK);
    }

    public void toggleOnOffScreen(boolean activated) {
        if(activated) {
            takeScreenColorFromColorSelector();
        } else {
            screen.setBackgroundColor(Color.BLACK);
            drawItemsWhite();
        }
    }

}