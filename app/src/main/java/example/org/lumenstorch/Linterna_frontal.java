package example.org.lumenstorch;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

public class Linterna_frontal extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    SeekBar select_color;

    private final int MAX = 10;
    private final int INIT_VALUE = MAX / 2;
    public ConstraintLayout pantalla_luz;
    int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linterna_frontal);

        //Casting de objetos.
        select_color = (SeekBar) findViewById(R.id.selector_color);
        pantalla_luz = (ConstraintLayout) findViewById(R.id.panatalla_luz);

        //Configuración del inicio de la barra de colores y del valor máximo.
        select_color.setMax(MAX);
        select_color.setProgress(INIT_VALUE);


        select_color.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Integer current = seekBar.getProgress();
        //Llamar a la función actualizar.
        actualizar();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void actualizar() {

        color = select_color.getProgress();

        //Solo para ver si le llegan los parametros bien. Luego eliminar.
        String dato = String.valueOf(color);
        Toast.makeText(this,dato,Toast.LENGTH_SHORT).show();


        if (color > 5){

            //No funciona, la lógica si pero no hace caso en los colores. Es solo una prueba.
            pantalla_luz.setBackgroundColor(3);
        }
    }
}


