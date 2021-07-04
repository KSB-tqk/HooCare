package cf.khanhsb.icare_v2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Calendar;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class CompleteUserInfoActivity extends AppCompatActivity {
    private TextView editGender,editDateOfBirth,continueButton;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private static final String tempEmail = "tempEmail";
    private String mDate = "";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private boolean allDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_user_info);

        editDateOfBirth = findViewById(R.id.complete_date_of_birth);
        editGender = findViewById(R.id.complete_gender);
        continueButton = findViewById(R.id.navigation_button_complete);

        editDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditBirthdayDialogProfile(Gravity.CENTER);
            }
        });

        editGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditGenderDialogProfile(Gravity.CENTER);
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompleteUserInfoActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    public void openEditGenderDialogProfile(int gravity) {
        final Dialog genderDialog = new Dialog(this);
        genderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        genderDialog.setContentView(R.layout.layout_dialog_gender);

        Window window = genderDialog.getWindow();
        if(window == null){
            return ;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.CENTER == gravity){
            genderDialog.setCancelable(true);
        } else {
            genderDialog.setCancelable(false);
        }

        ImageView maleImage = genderDialog.findViewById(R.id.male_image_edit);
        ImageView femaleImage = genderDialog.findViewById(R.id.female_image_edit);
        CheckBox femaleCheckbox = genderDialog.findViewById(R.id.female_checkbox_edit);
        CheckBox maleCheckbox = genderDialog.findViewById(R.id.male_checkbox_edit);
        AppCompatButton cancelButton = genderDialog.findViewById(R.id.cancel_button_name_edit);
        Button saveButton = genderDialog.findViewById(R.id.save_button_name_edit);

        maleCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(maleCheckbox.isChecked()){
                    femaleCheckbox.setChecked(false);
                    maleImage.setImageDrawable(getResources().getDrawable(R.drawable.male_checkbox_icon_color));
                    femaleImage.setImageDrawable(getResources().getDrawable(R.drawable.female_checkbox_icon_black));
                } else {
                    maleImage.setImageDrawable(getResources().getDrawable(R.drawable.male_checkbox_icon_black));
                }
            }
        });

        femaleCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(femaleCheckbox.isChecked()){
                    maleCheckbox.setChecked(false);
                    maleImage.setImageDrawable(getResources().getDrawable(R.drawable.male_checkbox_icon_black));
                    femaleImage.setImageDrawable(getResources().getDrawable(R.drawable.female_checkbox_icon_color));
                } else {
                    femaleImage.setImageDrawable(getResources().getDrawable(R.drawable.female_checkbox_icon_black));
                }
            }
        });

        maleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleCheckbox.setChecked(!maleCheckbox.isChecked());
            }
        });

        femaleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femaleCheckbox.setChecked(!femaleCheckbox.isChecked());
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        firestore = FirebaseFirestore.getInstance();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderDialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(!(!maleCheckbox.isChecked() && !femaleCheckbox.isChecked())){
                    docRef =  firestore.collection("users").document(theTempEmail);
                    String tempGender =  getGender(maleCheckbox,femaleCheckbox);

                    docRef.update("gender",tempGender);

                    editGender.setText(tempGender);
                    editGender.setTextColor(getResources().getColor(R.color.lime_100));
                    editGender.setBackground(AppCompatResources.getDrawable(CompleteUserInfoActivity.this,R.drawable.step_count_label_background));

                    if(allDone){
                        continueButton.setText("Continue");
                        continueButton.setAlpha(1);
                        continueButton.setBackground(AppCompatResources.
                                getDrawable(CompleteUserInfoActivity.this,R.drawable.cardview_background_gradient));
                    } else {
                        allDone = true;
                    }
                    genderDialog.dismiss();
                } else {
                    Toast.makeText(CompleteUserInfoActivity.this, "Please choose a gender", Toast.LENGTH_SHORT).show();
                }

            }
        });
        genderDialog.show();
    }

    public void openEditBirthdayDialogProfile(int gravity) {
        final Dialog birthDayDialog = new Dialog(this);
        birthDayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        birthDayDialog.setContentView(R.layout.layout_dialog_date_of_birth);

        Window window = birthDayDialog.getWindow();
        if(window == null){
            return ;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.CENTER == gravity){
            birthDayDialog.setCancelable(true);
        } else {
            birthDayDialog.setCancelable(false);
        }

        TextView datePicker = birthDayDialog.findViewById(R.id.date_picker_edit);
        AppCompatButton cancelButton = birthDayDialog.findViewById(R.id.cancel_button_name_edit);
        Button saveButton = birthDayDialog.findViewById(R.id.save_button_name_edit);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CompleteUserInfoActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d("AddToDoItemActivity","onDateSet: date" + dayOfMonth + "/" + month + "/" + year);
                mDate = dayOfMonth + "/" + month + "/" + year;

                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int age = currentYear - year;
                if(age >= 13){
                    datePicker.setText(mDate);
                } else {
                    Toast.makeText(CompleteUserInfoActivity.this,
                            "You need to be older than 12 years old to use this application!"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        };

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        firestore = FirebaseFirestore.getInstance();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birthDayDialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(!datePicker.getText().toString().equals("Select your date of birth")){
                    docRef =  firestore.collection("users").document(theTempEmail);
                    docRef.update("date_of_birth",mDate);

                    editDateOfBirth.setText(mDate);
                    editDateOfBirth.setTextColor(getResources().getColor(R.color.lime_100));
                    editDateOfBirth.setBackground(AppCompatResources.getDrawable(CompleteUserInfoActivity.this,R.drawable.step_count_label_background));

                    if(allDone){
                        continueButton.setText("Continue");
                        continueButton.setAlpha(1);
                        continueButton.setBackground(AppCompatResources.
                                getDrawable(CompleteUserInfoActivity.this,R.drawable.cardview_background_gradient));
                    } else {
                        allDone = true;
                    }

                    birthDayDialog.dismiss();
                } else {
                    Toast.makeText(CompleteUserInfoActivity.this, "Please choose your date of birth", Toast.LENGTH_SHORT).show();
                }
            }
        });
        birthDayDialog.show();
    }

    private String getGender(CheckBox maleCheckbox,CheckBox femaleCheckbox){
        if(maleCheckbox.isChecked()){
            return "Male";
        }
        else {
            return "Female";
        }
    }
}