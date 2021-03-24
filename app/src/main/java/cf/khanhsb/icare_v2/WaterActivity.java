package cf.khanhsb.icare_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WaterActivity extends AppCompatActivity {

    private int waterHasDrink = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        TextView waterTextView = (TextView) findViewById(R.id.today_water_drinked_text_view);
        Button drinkBtn = (Button) findViewById(R.id.drink_button);
        Button backBtn = (Button) findViewById(R.id.back_button);

        drinkBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                waterHasDrink += 100;
                waterTextView.setText("Total amount of water drunk: "+ String.valueOf(waterHasDrink) +"ml");
            }
        });

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toMain = new Intent(WaterActivity.this,MainActivity.class);
                startActivity(toMain);
            }
        });
    }
}