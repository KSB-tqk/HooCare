package cf.khanhsb.icare_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ActionBar toolbar;
    ViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView btnNav = findViewById(R.id.bottom_nav);
        btnNav.setBackground(null);
        btnNav.getMenu().getItem(2).setEnabled(false);
        btnNav.setOnNavigationItemSelectedListener(navListener);


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
                        btnNav.getMenu().findItem(R.id.nav_home).setChecked(true);
                        break;
                    case 1:
                        btnNav.getMenu().findItem(R.id.nav_archie).setChecked(true);
                        break;
                    case 2:
                        btnNav.getMenu().findItem(R.id.nav_data_detail).setChecked(true);
                        break;
                    case 3:
                        btnNav.getMenu().findItem(R.id.nav_profile).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, new HomeFragment()).commit();*/
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

    /*private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            //toolbar.setTitle("Home");
                            fragment = new HomeFragment();

                            break;
                        case R.id.nav_archie:
                            //toolbar.setTitle("Archie");
                            fragment = new ArchieveFragment();

                            break;
                        case R.id.nav_data_detail:
                            //toolbar.setTitle("Data");
                            fragment = new FoodFragment();

                            break;
                        case R.id.nav_profile:
                            //toolbar.setTitle("Profile");
                            fragment = new GymFragment();

                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
                    return true;
                }
            };*/

}