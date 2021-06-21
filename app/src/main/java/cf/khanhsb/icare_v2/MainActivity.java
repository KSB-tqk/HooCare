package cf.khanhsb.icare_v2;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import cf.khanhsb.icare_v2.Adapter.ViewPagerAdapter;
import cf.khanhsb.icare_v2.Fragment.MealFragment;

public class MainActivity extends AppCompatActivity {
    private TextView username,toolBarTitle;
    private ViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private Toolbar toolbar;
    private ImageView toolBarImageView;
    private Boolean isGymFragment = false;
    private FirebaseAuth mAuth;
    private SlidingRootNav slidingRootNav;
    private LinearLayout logout;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String sleepTime = "sleepTime";

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

    FloatingActionMenu add_floatbtn;
    FloatingActionButton set_weigh;
    FloatingActionButton drink_water_fltbtn;

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
        toolBarImageView = findViewById(R.id.nav_menu_icon);

        /**getting intent*/
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("userEmail");

        /**setting up viewpager*/
        viewPager = findViewById(R.id.view_pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),userEmail);
        viewPager.setAdapter(mViewPagerAdapter);

        /**setting up nav drawer*/
        toolbar = findViewById(R.id.nav_menu_toolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(null);
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withMenuLocked(true)
                .withRootViewScale(0.75f)
                .withRootViewElevation(30)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        ImageView closeNavMenuButton = (ImageView) findViewById(R.id.close_nav_menu_button);
        ImageView openNavMenuButton = (ImageView) findViewById(R.id.nav_menu_icon);

        /**sliding between fragment and activity*/
        int fragmentPosition = intent.getIntExtra("fragmentPosition",0);
        viewPager.setCurrentItem(fragmentPosition);
        if(fragmentPosition==3){
            btmNav.getMenu().findItem(R.id.nav_gym).setChecked(true);
            toolBarTitle.setText(getString(R.string.GymFragTitle));
            toolBarTitle.setTextColor(Color.WHITE);
            toolBarTitle.getBackground().setTint(Color.parseColor("#58C892"));
            toolBarImageView.setColorFilter(Color.parseColor("#58C892"));
            toolbar.setBackground(getDrawable(R.color.white));
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        btmNav.getMenu().findItem(R.id.nav_home).setChecked(true);
                        toolBarTitle.setText(getString(R.string.HomeFragTitle));
                        toolbar.setBackground(getDrawable(R.color.transparent));
                        toolBarTitle.getBackground().setTint(Color.WHITE);
                        toolBarTitle.setTextColor(getResources().getColor(R.color.lime_200));
                        toolBarImageView.setColorFilter(Color.WHITE);
                        break;
                    case 1:
                        btmNav.getMenu().findItem(R.id.nav_archie).setChecked(true);
                        toolBarTitle.setText(getString(R.string.ArchieveFragTitle));
                        toolBarTitle.getBackground().setTint(Color.WHITE);
                        toolBarTitle.setTextColor(getResources().getColor(R.color.lime_200));
                        toolBarImageView.setColorFilter(Color.WHITE);
                        toolbar.setBackground(getDrawable(R.color.transparent));
                        break;
                    case 2:
                        btmNav.getMenu().findItem(R.id.nav_meal).setChecked(true);
                        toolBarTitle.setText(getString(R.string.MealFragTitle));
                        toolBarTitle.setTextColor(Color.WHITE);
                        toolBarTitle.getBackground().setTint(Color.parseColor("#58C892"));
                        toolBarImageView.setColorFilter(Color.parseColor("#58C892"));
                        toolbar.setBackground(getDrawable(R.color.white));
                        MealFragment mealFragment = new MealFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.view_pager,mealFragment);
                        fragmentTransaction.commit();
                        break;
                    case 3:
                        btmNav.getMenu().findItem(R.id.nav_gym).setChecked(true);
                        toolBarTitle.setText(getString(R.string.GymFragTitle));
                        toolBarTitle.setTextColor(Color.WHITE);
                        toolBarTitle.getBackground().setTint(Color.parseColor("#58C892"));
                        toolBarImageView.setColorFilter(Color.parseColor("#58C892"));
                        toolbar.setBackground(getDrawable(R.color.white));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        closeNavMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingRootNav.closeMenu();
                slidingRootNav.setMenuLocked(true);
            }
        });

        openNavMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingRootNav.setMenuLocked(false);
                slidingRootNav.openMenu();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();;
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        logout = findViewById(R.id.linearlogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                //
                LoginManager.getInstance().logOut();
                /////
                mGoogleSignInClient.signOut();
                startActivity(new Intent(MainActivity.this,SigninActivity.class));
                finish();
            }
        });

        add_floatbtn = findViewById(R.id.add_floatbtn);
        set_weigh = findViewById(R.id.set_weigh);
        drink_water_fltbtn= findViewById(R.id.drink_water_fltbtn);

        set_weigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fillForm(Gravity.BOTTOM);
            }
        });

        drink_water_fltbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wateredit(Gravity.BOTTOM);
            }
        });

    }
    private void wateredit(int gravity) {
        final Dialog dialog2 = new Dialog(this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.layout_water_edit);
        Window window = dialog2.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.BOTTOM == gravity){
            dialog2.setCancelable(true);
        } else{
            dialog2.setCancelable(false);
        }
        Button btncancel2 = dialog2.findViewById(R.id.cancel_dialog2);

        btncancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
        dialog2.show();
    }

    private void fillForm(int gravity) {
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

        if(Gravity.BOTTOM == gravity){
            dialog.setCancelable(true);
        } else{
            dialog.setCancelable(false);
        }
        Button btncancel = dialog.findViewById(R.id.cancel_dialog);

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
                            viewPager.setCurrentItem(1);
                            break;
                        case R.id.nav_meal:
                            viewPager.setCurrentItem(2);
                            break;
                        case R.id.nav_gym:
                            viewPager.setCurrentItem(3);
                            break;
                    }
                    return true;
                }
            };

    public void Logout(View view) {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this,SigninActivity.class));
        finish();
    }

    public void replaceFragment(int fragmentPos){
        viewPager.setCurrentItem(fragmentPos);
    }



}