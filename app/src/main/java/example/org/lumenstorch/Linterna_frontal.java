package example.org.lumenstorch;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

public class Linterna_frontal extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    SeekBar selectColor;

    private final Integer MAX_INTEGER_COLOR = 16777215;
    private final int MAX = MAX_INTEGER_COLOR;
    private final int INIT_VALUE = MAX;
    public ConstraintLayout pantallaLuz;
    public String hexColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linterna_frontal);

        //Casting de objetos.
        selectColor = (SeekBar) findViewById(R.id.selector_color);
        pantallaLuz = (ConstraintLayout) findViewById(R.id.panatalla_luz);

        //Configuración del inicio de la barra de colores y del valor máximo.
        selectColor.setMax(MAX);
        selectColor.setProgress(INIT_VALUE);

        selectColor.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Integer valorSeekbar = seekBar.getProgress();
        actualizarColor(valorSeekbar);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
//        Toast.makeText(this,valorSeekbar.toString(),Toast.LENGTH_SHORT).show();

    }

    private void actualizarColor(Integer valorSeekbar) {
        Integer color = ((MAX_INTEGER_COLOR / MAX) * valorSeekbar);
        hexColor = String.format("#%06X", (0xFFFFFF & color));

        pantallaLuz.setBackgroundColor(Color.parseColor(hexColor));
    }
}


