package cf.khanhsb.icare_v2;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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

    FloatingActionMenu add_floatbtn;
    FloatingActionButton set_weight_btn;

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
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), userEmail);
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
        int fragmentPosition = intent.getIntExtra("fragmentPosition", 0);
        viewPager.setCurrentItem(fragmentPosition);
        if (fragmentPosition == 3) {
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
                        fragmentTransaction.add(R.id.view_pager, mealFragment);
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
                .build();
        ;
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        logout = findViewById(R.id.linearlogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                //
                mGoogleSignInClient.signOut();
                startActivity(new Intent(MainActivity.this, SigninActivity.class));
                finish();
            }
        });

        add_floatbtn = findViewById(R.id.add_floatbtn);
        set_weight_btn = findViewById(R.id.set_weight_btn);

        set_weight_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Clicked");
                add_floatbtn.close(true);

            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
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