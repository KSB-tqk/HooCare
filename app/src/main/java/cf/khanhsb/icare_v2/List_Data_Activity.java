package cf.khanhsb.icare_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Objects;

public class List_Data_Activity extends AppCompatActivity {
    private ImageView workoutImage,backButton,moreButton,favButton;
    private TextView workoutTitle,workoutTime;

    /**animation exercise listview*/
    private NonScrollListView listView;
    private String[] anim_exer_ListTitle = {"JUMPING JACKS",
            "ABSDOMINAL CRUNCHES",
            "RUSSIAN TWIST",
            "MOUNTAIN CLIMBER",
            "HEEL TOUCH"};
    private String[] anim_exer_ListText = {"00:20",
            "x16",
            "x20",
            "x20",
            "x20"};
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
        AnimExerListViewAdapter animExerListViewAdapter = new AnimExerListViewAdapter(this,
                R.layout.item_exercises_list_data,gymListImage,anim_exer_ListTitle,anim_exer_ListText);
        listView.setAdapter(animExerListViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(List_Data_Activity.this,List_Exercises_Activity.class);
                intent.putExtra("exerciseTitle",anim_exer_ListTitle[position]);
                intent.putExtra("exerciseDuration",anim_exer_ListText[position]);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });

    }
}