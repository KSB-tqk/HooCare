package cf.khanhsb.icare_v2;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class List_Data_Activity extends AppCompatActivity {
    private ImageView workoutImage, backButton, moreButton, favButton;
    private TextView workoutTitle, workoutTime, exerciseTitle, exerciseDurationValue,
            exerciseDurationText,animTitle,videoTitle,focusArea,workoutBigTitle,
            animCloseBtn,exerciseCount;
    private LinearLayout startButtonAnimExer;
    private ConstraintLayout bottomSheetContainer,selectedBackground;
    private ViewPager2 viewPager2;
    private AnimExerViewPagerAdapter animExerViewPagerAdapter;

    /**
     * animation exercise listview
     */
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
    private String[] anim_exer_ListDurationText = {"Duration (Seconds)",
            "Repeat",
            "Repeat",
            "Repeat",
            "Repeat"};
    private String[] anim_exer_ListDurationValue = {"20",
            "16",
            "20",
            "20",
            "20"};
    private int[] gymListImage = {R.drawable.jumping_jack_list_data_item,
            R.drawable.jumping_jack_list_data_item,
            R.drawable.jumping_jack_list_data_item,
            R.drawable.jumping_jack_list_data_item,
            R.drawable.jumping_jack_list_data_item};
    /**
     * animation exercise listview
     */

    /*animation exercise viewpager*/
    private int[] gymViewPagerImage = {R.drawable.gif_test_image_anim_exer_viewpager,
            R.drawable.gif_test_image_anim_exer_viewpager,
            R.drawable.gif_test_image_anim_exer_viewpager,
            R.drawable.gif_test_image_anim_exer_viewpager,
            R.drawable.gif_test_image_anim_exer_viewpager};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        workoutTitle = (TextView) findViewById(R.id.gym_list_workout_text);
        workoutBigTitle = (TextView) findViewById(R.id.title_list_data);
        workoutTime = (TextView) findViewById(R.id.time_title_list_data);
        exerciseCount = (TextView) findViewById(R.id.exercises_count_list_data);
        focusArea = (TextView) findViewById(R.id.area_focus_title_list_data);
        workoutImage = (ImageView) findViewById(R.id.gym_list_image_view);
        backButton = (ImageView) findViewById(R.id.back_button_list_data);
        bottomSheetContainer = (ConstraintLayout) findViewById(R.id.bottom_sheet_container_exer_anim);
        startButtonAnimExer = (LinearLayout) findViewById(R.id.start_button_anim_exer_linear);

        startButtonAnimExer.bringToFront();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_Data_Activity.this, MainActivity.class);
                intent.putExtra("fragmentPosition", 3);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        /**setting up intent from gym fragment*/
        Intent intent = getIntent();
        workoutTitle.setText(intent.getStringExtra("workoutTitle"));
        workoutImage.setImageResource(intent.getIntExtra("workoutImage", 0));
        workoutTime.setText(intent.getStringExtra("workoutTime"));
        focusArea.setText(intent.getStringExtra("focusBodyPart"));
        workoutBigTitle.setText(intent.getStringExtra("focusBodyPart"));
        exerciseCount.setText("("+String.valueOf(anim_exer_ListText.length)+")");

        /**setting up animation exercise listview*/
        listView = findViewById(R.id.list_view_list_data);
        AnimExerListViewAdapter animExerListViewAdapter = new AnimExerListViewAdapter(this,
                R.layout.item_exercises_list_data, gymListImage, anim_exer_ListTitle, anim_exer_ListText);
        listView.setAdapter(animExerListViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        List_Data_Activity.this, R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.bottom_sheet_dialog_anim_exer,
                                bottomSheetContainer,
                                false
                        );

                exerciseTitle = bottomSheetView.findViewById(R.id.title_animation_exercise);
                exerciseDurationText = bottomSheetView.findViewById(R.id.text_animation_exercise);
                exerciseDurationValue = bottomSheetView.findViewById(R.id.duration_value_text);
                animCloseBtn = bottomSheetView.findViewById(R.id.close_button_animation_exercise);
                animTitle = bottomSheetView.findViewById(R.id.animation_title);
                videoTitle = bottomSheetView.findViewById(R.id.video_title);
                selectedBackground =  bottomSheetView.findViewById(R.id.tab_animation_view);

                animExerViewPagerAdapter = new AnimExerViewPagerAdapter(gymViewPagerImage,anim_exer_ListDurationText);
                viewPager2 = bottomSheetView.findViewById(R.id.animation_exercise_viewPager);
                viewPager2.setAdapter(animExerViewPagerAdapter);

                videoTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int size = videoTitle.getWidth();
                        videoTitle.setTypeface(videoTitle.getTypeface(), Typeface.BOLD);
                        animTitle.setTypeface(null, Typeface.NORMAL);
                        selectedBackground.animate().x(size).setDuration(200);
                        viewPager2.setCurrentItem(1);
                    }
                });

                animTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoTitle.setTypeface(null, Typeface.NORMAL);
                        animTitle.setTypeface(animTitle.getTypeface(), Typeface.BOLD);
                        selectedBackground.animate().x(0).setDuration(200);
                        viewPager2.setCurrentItem(0);
                    }
                });

                animCloseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                exerciseTitle.setText(anim_exer_ListTitle[position]);
                exerciseDurationText.setText(anim_exer_ListDurationText[position]);
                exerciseDurationValue.setText(anim_exer_ListDurationValue[position]);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        /**app bar animation*/
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.gym_list_app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    workoutTitle.setAlpha(1f);
                } else if (verticalOffset == 0) {
                    workoutTitle.setAlpha(0f);
                } else {
                    workoutTitle.setAlpha(0.3f);
                }
            }
        });
    }
}