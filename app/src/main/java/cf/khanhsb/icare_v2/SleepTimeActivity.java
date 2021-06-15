package cf.khanhsb.icare_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import cf.khanhsb.icare_v2.Model.ProgressBarAnimation;

public class SleepTimeActivity extends AppCompatActivity {

    private ImageView backButton,moreButton,plusButton,minusButton;
    private ConstraintLayout bottomSheetContainer;
    private ProgressBar progressBar;
    private int numOfHours = 0;
    private FirebaseFirestore firestore;
    private static final String tempEmail = "tempEmail";
    private TextView numOfHoursTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_time);

        backButton = findViewById(R.id.button_back_sleep_time);
        moreButton = findViewById(R.id.more_menu_sleep_time);
        bottomSheetContainer = findViewById(R.id.bottom_sheet_container_sleep_time);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(SleepTimeActivity.this, MainActivity.class);
                startActivity(toMain);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        SleepTimeActivity.this,
                        R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.bottom_sheet_dialog_sleep_time,
                                bottomSheetContainer,
                                false
                        );
                firestore = FirebaseFirestore.getInstance();

                plusButton = (ImageView) bottomSheetView.findViewById(R.id.plus_button_sleep_menu);
                minusButton = (ImageView) bottomSheetView.findViewById(R.id.minus_button_sleep_menu);
                progressBar = (ProgressBar) bottomSheetView.findViewById(R.id.progressbar_sleep_time);
                numOfHoursTextView = (TextView) bottomSheetView.findViewById(R.id.num_of_hours_progress);

                SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
                String theTempEmail = sharedPreferences.getString("Email", "");

                plusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numOfHours = Integer.parseInt(numOfHoursTextView.getText().toString());
                        if (numOfHours == 11) {
                            plusButton.setVisibility(View.GONE);
                        }
                        if (numOfHours < 12) {
                            numOfHours += 1;
                            if (numOfHours == 5) {
                                minusButton.setVisibility(View.VISIBLE);
                            }
                            numOfHoursTextView.setText(String.valueOf(numOfHours));

                            //progress bar animation
                            float progress = Float.parseFloat(String.valueOf(numOfHours)) * 1000;
                            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                                    progress - 1000,
                                    progress);
                            anim.setDuration(1000);
                            progressBar.startAnimation(anim);

                            //update value
                            firestore.collection("users").document(theTempEmail).
                                    update("sleep_goal", String.valueOf(numOfHours));
                        }
                    }
                });

                minusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numOfHours = Integer.parseInt(numOfHoursTextView.getText().toString());
                        if (numOfHours == 5) {
                            minusButton.setVisibility(View.GONE);
                        }
                        if (numOfHours > 4) {
                            numOfHours -= 1;
                            if (numOfHours == 11) {
                                plusButton.setVisibility(View.VISIBLE);
                            }
                            numOfHoursTextView.setText(String.valueOf(numOfHours));

                            //progress bar animation
                            float progress = Float.parseFloat(String.valueOf(numOfHours)) * 1000;
                            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                                    progress + 1000,
                                    progress);
                            anim.setDuration(1000);
                            progressBar.startAnimation(anim);

                            //update value
                            firestore.collection("users").document(theTempEmail).
                                    update("sleep_goal", String.valueOf(numOfHours));
                        }
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

    }
}