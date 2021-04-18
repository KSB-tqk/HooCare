package cf.khanhsb.icare_v2;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    //setting up nav_drawer item
    private static final int POS_CLOSE = 0;
    private static final int POS_PROFILES = 1;
    private static final int POS_SETTING = 2;
    private static final int POS_ABOUT_US = 3;
    private static final int POS_LOGOUT = 4;

    private String[] nav_drawer_title;
    private Drawable[] nav_drawer_icon;

    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //settting up bottom nav
        BottomNavigationView btmNav = findViewById(R.id.bottom_nav);
        btmNav.setBackground(null);
        btmNav.setItemIconTintList(null);
        btmNav.getMenu().getItem(2).setEnabled(false);
        btmNav.setOnNavigationItemSelectedListener(navListener);

        //setting up viewpager
        viewPager = findViewById(R.id.view_pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mViewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        btmNav.getMenu().findItem(R.id.nav_home).setChecked(true);
                        break;
                    case 1:
                        btmNav.getMenu().findItem(R.id.nav_archie).setChecked(true);
                        break;
                    case 2:
                        btmNav.getMenu().findItem(R.id.nav_data_detail).setChecked(true);
                        break;
                    case 3:
                        btmNav.getMenu().findItem(R.id.nav_profile).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //setting up nav drawer
        Toolbar toolbar = findViewById(R.id.nav_menu_toolbar);
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
                        case R.id.nav_data_detail:
                            viewPager.setCurrentItem(2);
                            break;
                        case R.id.nav_profile:
                            viewPager.setCurrentItem(3);
                            break;
                    }
                    return true;
                }
            };

    /*@Override
    public void onBackPressed(){
        finish();
    }*/
}