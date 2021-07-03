package cf.khanhsb.icare_v2;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.$Gson$Preconditions;
import com.yarolegovich.slidingrootnav.SlidingRootNav;

import java.time.LocalDate;
import java.util.Calendar;

import cf.khanhsb.icare_v2.Adapter.ViewPagerAdapter;
import cf.khanhsb.icare_v2.Fragment.MealFragment;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class MainActivity extends AppCompatActivity {
    private TextView username, toolBarTitle;
    private ViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private Toolbar toolbar;
    private ImageView toolBarImageView;
    private Boolean isGymFragment = false;
    private FirebaseAuth mAuth;
    private SlidingRootNav slidingRootNav;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private static final String sleepTime = "sleepTime";
    private static final String tempEmail = "tempEmail";
    private String numberOfCupHadDrink;
    private String mDate = "";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    int pagePos;

    @Override
    protected void onPause() {
        super.onPause();
        long end_time = System.currentTimeMillis();
        SharedPreferences sharedPreferences = getSharedPreferences(sleepTime, MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long start_time = System.currentTimeMillis();
        SharedPreferences sharedPreferences = getSharedPreferences(sleepTime, MODE_PRIVATE);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    FloatingActionMenu add_floatbtn;
    FloatingActionButton set_weigh;
    FloatingActionButton drink_water_fltbtn;
    EditText edit_weight;
    EditText edit_height;
    Button save_btn_dialog;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        /**settting up bottom nav*/
        BottomNavigationView btmNav = findViewById(R.id.bottom_nav);
        btmNav.setBackground(null);
        btmNav.setItemIconTintList(null);
        btmNav.getMenu().getItem(2).setEnabled(false);
        btmNav.setOnNavigationItemSelectedListener(navListener);

        /**setting up toolbar*/
        toolBarTitle = findViewById(R.id.tool_bar_title);

        /**getting intent*/
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("userEmail");

        /**setting up viewpager*/
        viewPager = findViewById(R.id.view_pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), userEmail);
        viewPager.setAdapter(mViewPagerAdapter);

        /**setting up nav drawer*/
        toolbar = findViewById(R.id.nav_menu_toolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(null);

        /**sliding between fragment and activity*/
        int fragmentPosition = intent.getIntExtra("fragmentPosition", 0);
        viewPager.setCurrentItem(fragmentPosition);
        if (fragmentPosition == 2) {
            btmNav.getMenu().findItem(R.id.nav_gym).setChecked(true);
            toolBarTitle.setText(getString(R.string.GymFragTitle));
            toolBarTitle.setTextColor(Color.WHITE);
            toolBarTitle.getBackground().setTint(Color.parseColor("#58C892"));
            toolbar.setBackground(getDrawable(R.color.light_grey));
        } else if (fragmentPosition == 3){
            btmNav.getMenu().findItem(R.id.nav_archie).setChecked(true);
            toolBarTitle.setText(getString(R.string.ArchieveFragTitle));
            toolBarTitle.getBackground().setTint(Color.WHITE);
            toolBarTitle.setTextColor(getResources().getColor(R.color.lime_200));
            toolbar.setBackground(getDrawable(R.color.transparent));
        } else if (fragmentPosition == 1) {
            btmNav.getMenu().findItem(R.id.nav_meal).setChecked(true);
            toolBarTitle.setText(getString(R.string.MealFragTitle));
            toolBarTitle.setTextColor(Color.WHITE);
            toolBarTitle.getBackground().setTint(Color.parseColor("#58C892"));
            toolbar.setBackground(getDrawable(R.color.light_grey));
            MealFragment mealFragment = new MealFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.view_pager, mealFragment);
            fragmentTransaction.commit();
        } else if (fragmentPosition == 0) {
            btmNav.getMenu().findItem(R.id.nav_home).setChecked(true);
            toolBarTitle.setText(getString(R.string.HomeFragTitle));
            toolbar.setBackground(getDrawable(R.color.transparent));
            toolBarTitle.getBackground().setTint(Color.WHITE);
            toolBarTitle.setTextColor(getResources().getColor(R.color.lime_200));
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pagePos = position;
            }

            @Override
            public void onPageSelected(int position) {
                pagePos = position;
                switch (position) {
                    case 0:
                        btmNav.getMenu().findItem(R.id.nav_home).setChecked(true);
                        toolBarTitle.setText(getString(R.string.HomeFragTitle));
                        toolbar.setBackground(getDrawable(R.color.transparent));
                        toolBarTitle.getBackground().setTint(Color.WHITE);
                        toolBarTitle.setTextColor(getResources().getColor(R.color.lime_200));
                        break;
                    case 3:
                        btmNav.getMenu().findItem(R.id.nav_archie).setChecked(true);
                        toolBarTitle.setText(getString(R.string.ArchieveFragTitle));
                        toolBarTitle.getBackground().setTint(Color.WHITE);
                        toolBarTitle.setTextColor(getResources().getColor(R.color.lime_200));
                        toolbar.setBackground(getDrawable(R.color.transparent));
                        break;
                    case 1:
                        btmNav.getMenu().findItem(R.id.nav_meal).setChecked(true);
                        toolBarTitle.setText(getString(R.string.MealFragTitle));
                        toolBarTitle.setTextColor(Color.WHITE);
                        toolBarTitle.getBackground().setTint(Color.parseColor("#58C892"));
                        toolbar.setBackground(getDrawable(R.color.light_grey));
                        MealFragment mealFragment = new MealFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.view_pager, mealFragment);
                        fragmentTransaction.commit();
                        break;
                    case 2:
                        btmNav.getMenu().findItem(R.id.nav_gym).setChecked(true);
                        toolBarTitle.setText(getString(R.string.GymFragTitle));
                        toolBarTitle.setTextColor(Color.WHITE);
                        toolBarTitle.getBackground().setTint(Color.parseColor("#58C892"));
                        toolbar.setBackground(getDrawable(R.color.light_grey));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        add_floatbtn = findViewById(R.id.add_floatbtn);
        set_weigh = findViewById(R.id.set_weigh);
        drink_water_fltbtn = findViewById(R.id.drink_water_fltbtn);

        set_weigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fillForm(Gravity.CENTER,pagePos);
            }
        });

        drink_water_fltbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wateredit(Gravity.CENTER,pagePos);
            }
        });


    }

    private void wateredit(int gravity, int fragmentPos) {
        final Dialog drinkWaterDialog = new Dialog(this);
        drinkWaterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        drinkWaterDialog.setContentView(R.layout.layout_water_edit);
        Window window = drinkWaterDialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if (Gravity.CENTER == gravity) {
            drinkWaterDialog.setCancelable(true);
        } else {
            drinkWaterDialog.setCancelable(false);
        }
        Button drinkWaterCancelBtn = drinkWaterDialog.findViewById(R.id.cancel_dialog2);
        AppCompatButton drinkWaterBtn = drinkWaterDialog.findViewById(R.id.drink_btn_dialog);
        TextView cupOfWater = drinkWaterDialog.findViewById(R.id.cup_of_water);
        ImageView waterCupIcon = drinkWaterDialog.findViewById(R.id.water_cup_icon);

        Runnable setUpWaterDialogRunnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                SetUpWaterDialogFirebase(cupOfWater, waterCupIcon);
            }
        };

        Thread backgroundThread = new Thread(setUpWaterDialogRunnable);
        backgroundThread.start();

        drinkWaterBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String cupOfWaterLabel = String.valueOf(cupOfWater.getText());
                if (!cupOfWaterLabel.equals("Set a Goal First")) {
                    int updateDrinkValue = Integer.parseInt(cupOfWaterLabel) + 1;
                    numberOfCupHadDrink = String.valueOf(updateDrinkValue);
                    String updateDrinkValueToFirebase = String.valueOf(updateDrinkValue * 250);

                    LocalDate today = LocalDate.now();
                    LocalDate monday = today.with(previousOrSame(MONDAY));

                    SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
                    String theTempEmail = sharedPreferences.getString("Email", "");

                    firestore = FirebaseFirestore.getInstance();

                    firestore.collection("daily").
                            document("week-of-" + monday.toString()).
                            collection(today.toString()).
                            document(theTempEmail).
                            update("drink", String.valueOf(updateDrinkValueToFirebase));
                    cupOfWater.setText(numberOfCupHadDrink);
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });

        drinkWaterCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drinkWaterDialog.dismiss();
            }
        });
        drinkWaterDialog.show();
    }

    public void fillForm(int gravity, int fragmentPos) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_edit);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if (Gravity.CENTER == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }

        AppCompatButton btncancel = dialog.findViewById(R.id.cancel_dialog);
        EditText weightEditText = dialog.findViewById(R.id.edit_weight);
        EditText heightEditText = dialog.findViewById(R.id.edit_height);
        Button saveButton = dialog.findViewById(R.id.save_btn_dialog);

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2 && !String.valueOf(weightEditText.getText()).contains(".")) {
                    String weightInput = weightEditText.getText().toString();
                    String bigWeight = weightInput.substring(0, 2);
                    String smallWeight = weightInput.substring(2);
                    weightEditText.setText(bigWeight + "." + smallWeight);
                    weightEditText.setSelection(4);
                }
            }
        });

        Runnable setUpBMIRunnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                SetUpBMIDialogFirebase(weightEditText, heightEditText);
            }
        };

        Thread backgroundBMIThread = new Thread(setUpBMIRunnable);
        backgroundBMIThread.start();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String checkWeight = weightEditText.getText().toString();
                String checkHeight = heightEditText.getText().toString();
                if(checkHeight.trim().equals("") || checkWeight.trim().equals("")){
                    Toast.makeText(MainActivity.this, "Please fill out the form!", Toast.LENGTH_SHORT).show();
                }
                else {
                    SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
                    String theTempEmail = sharedPreferences.getString("Email", "");

                    firestore = FirebaseFirestore.getInstance();

                    LocalDate today = LocalDate.now();
                    LocalDate monday = today.with(previousOrSame(MONDAY));

                    docRef = firestore.collection("daily").
                            document("week-of-" + monday.toString()).
                            collection(today.toString()).
                            document(theTempEmail);
                    docRef.update("weight", String.valueOf(weightEditText.getText()));
                    docRef.update("height", String.valueOf(heightEditText.getText()));

                    docRef = firestore.collection("users").document(theTempEmail);
                    docRef.update("weight", String.valueOf(weightEditText.getText()));
                    docRef.update("height", String.valueOf(heightEditText.getText()));

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("fragmentPosition", fragmentPos);
                    startActivity(intent);
                    finish();

                    overridePendingTransition(0, 0);
                }
            }
        });

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setText("Update");
            }
        });

        heightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setText("Update");
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.nav_home:
                            viewPager.setCurrentItem(0);
                            break;
                        case R.id.nav_archie:
                            viewPager.setCurrentItem(3);
                            break;
                        case R.id.nav_meal:
                            viewPager.setCurrentItem(1);
                            break;
                        case R.id.nav_gym:
                            viewPager.setCurrentItem(2);
                            break;
                    }
                    return true;
                }
            };

    public void Logout(View view) {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, SigninActivity.class));
        finish();
    }

    public void replaceFragment(int fragmentPos) {
        viewPager.setCurrentItem(fragmentPos);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetUpWaterDialogFirebase(TextView cupOfWater, ImageView waterCupIcon) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        firestore = FirebaseFirestore.getInstance();

        docRef = firestore.collection("users").document(theTempEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String temp = document.getString("drink_goal");
                        assert temp != null;
                        if (temp.equals("empty")) {
                            cupOfWater.setText("Set a Goal First");
                            waterCupIcon.setVisibility(View.INVISIBLE);
                        } else {
                            docRef = firestore.collection("daily").
                                    document("week-of-" + monday.toString()).
                                    collection(today.toString()).
                                    document(theTempEmail);

                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null) {
                                            String temp = document.getString("drink");
                                            if (!"empty".equals(temp)) {
                                                float waterHadDrink = Float.parseFloat(temp) / 250;
                                                cupOfWater.setText(String.valueOf((int) waterHadDrink));
                                                waterCupIcon.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            Log.d("LOGGER", "No such document");
                                        }
                                    } else {
                                        Log.d("LOGGER", "get failed with ", task.getException());
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetUpBMIDialogFirebase(EditText weightEditText, EditText heightEditText) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        firestore = FirebaseFirestore.getInstance();

        docRef = firestore.collection("users").document(theTempEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String tempWeight = document.getString("weight");
                        String tempHeight = document.getString("height");

                        assert tempWeight != null;
                        if(!tempWeight.equals("empty")) {
                            weightEditText.setText(tempWeight);
                        }
                        assert tempHeight != null;
                        if(!tempHeight.equals("empty")) {
                            heightEditText.setText(tempHeight);
                        }

                        if(tempHeight.equals("empty") && tempWeight.equals("empty")){
                            docRef = firestore.collection("daily").
                                    document("week-of-" + monday.toString()).
                                    collection(today.toString()).
                                    document(theTempEmail);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null) {
                                            String weight = document.getString("weight");
                                            String height = document.getString("height");
                                            if (!"empty".equals(weight)) {
                                                weightEditText.setText(weight);
                                            }
                                            if (!"empty".equals(height)) {
                                                heightEditText.setText(height);
                                            }

                                        } else {
                                            Log.d("LOGGER", "No such document");
                                        }
                                    } else {
                                        Log.d("LOGGER", "get failed with ", task.getException());
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });


    }

    public void openEditNameDialog(int gravity, int fragmentPos) {
        final Dialog nameDialog = new Dialog(this);
        nameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nameDialog.setContentView(R.layout.layout_dialog_name);

        Window window = nameDialog.getWindow();
        if(window == null){
            return ;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.CENTER == gravity){
            nameDialog.setCancelable(true);
        } else {
            nameDialog.setCancelable(false);
        }

        EditText editTextName = nameDialog.findViewById(R.id.edit_text_name);
        AppCompatButton cancelButton = nameDialog.findViewById(R.id.cancel_button_name_edit);
        Button saveButton = nameDialog.findViewById(R.id.save_button_name_edit);

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        firestore = FirebaseFirestore.getInstance();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameDialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextName.getText().toString().isEmpty()){
                   docRef =  firestore.collection("users").document(theTempEmail);
                   docRef.update("name",editTextName.getText().toString());
                   Intent intent = new Intent(MainActivity.this, MainActivity.class);
                   intent.putExtra("fragmentPosition", fragmentPos);
                   startActivity(intent);
                   finish();
                } else {
                    Toast.makeText(MainActivity.this, "Name can not be empty!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        nameDialog.show();
    }

    public void openEditGenderDialog(int gravity, int fragmentPos) {
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
            @Override
            public void onClick(View v) {
                if(!(!maleCheckbox.isChecked() && !femaleCheckbox.isChecked())){
                    docRef =  firestore.collection("users").document(theTempEmail);
                    String tempGender =  getGender(maleCheckbox,femaleCheckbox);

                    docRef.update("gender",tempGender);
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("fragmentPosition", fragmentPos);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Please choose a gender", Toast.LENGTH_SHORT).show();
                }

            }
        });
        genderDialog.show();
    }

    public void openEditBirthdayDialog(int gravity, int fragmentPos) {
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
                        MainActivity.this,
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
                    Toast.makeText(MainActivity.this,
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
            @Override
            public void onClick(View v) {
                if(!datePicker.getText().toString().equals("Select your date of birth")){
                    docRef =  firestore.collection("users").document(theTempEmail);
                    docRef.update("date_of_birth",mDate);
                } else {
                    Toast.makeText(MainActivity.this, "Please choose your date of birth", Toast.LENGTH_SHORT).show();
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