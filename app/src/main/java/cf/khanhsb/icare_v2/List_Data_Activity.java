package cf.khanhsb.icare_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class List_Data_Activity extends AppCompatActivity {
    private ImageView workoutImage,backButton,moreButton,favButton;
    private TextView workoutTitle,workoutTime;

    /**animation exercise listview*/
    private ListView listView;
    private String[] anim_exer_ListTitle = {"JUMPING JACKS",
            "ABSDOMINAL CRUNCHES",
            "RUSSIAN TWIST",
            "MOUNTAIN CLIMBER",
            "HEEL TOUCH"};
    private int[] gymListImage = {R.drawable.jumping_jack_list_data_item,
            R.drawable.jumping_jack_list_data_item,
            R.drawable.jumping_jack_list_data_item,
            R.drawable.jumping_jack_list_data_item,
            R.drawable.jumping_jack_list_data_item};
    /**animation exercise listview*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        workoutTitle = (TextView) findViewById(R.id.gym_list_workout_text);
        workoutImage = (ImageView) findViewById(R.id.gym_list_image_view);
        backButton = (ImageView) findViewById(R.id.back_button_list_data);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_Data_Activity.this,MainActivity.class);
                intent.putExtra("fragmentPosition",3);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        Intent intent = getIntent();

        workoutTitle.setText(intent.getStringExtra("workoutTitle"));
        workoutImage.setImageResource(intent.getIntExtra("workoutImage",0));

        /**setting up animation exercise listview*/
        listView = findViewById(R.id.list_view_list_data);


    }
}