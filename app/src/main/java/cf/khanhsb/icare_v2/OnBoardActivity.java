package cf.khanhsb.icare_v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import cf.khanhsb.icare_v2.Adapter.OnboardingAdapter;
import cf.khanhsb.icare_v2.Model.OnboardingItem;

public class OnBoardActivity extends Activity {
    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
    private MaterialButton buttonOnboardingAction;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardIndicator);
        buttonOnboardingAction = findViewById(R.id.buttonOnboard);
        setupOnboardingItems();
        ViewPager2 onboardingViewPager = findViewById(R.id.onboardViewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);
        setupOnboardingIndicators();
        setCurrentOnboardingIndicator(0);
        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });
        buttonOnboardingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onboardingViewPager.getCurrentItem()+1 < onboardingAdapter.getItemCount()){
                    onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem()+1);
                }else{
                    startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                    finish();
                }
            }
        });
    }
    private void setupOnboardingItems(){
        List<OnboardingItem> onboardingItems = new ArrayList<>();
        //1
        OnboardingItem itemFeature1 = new OnboardingItem();
        itemFeature1.setImage(R.drawable.onboard1);
        //2
        OnboardingItem itemFeature2 = new OnboardingItem();
        itemFeature2.setImage(R.drawable.onboard2);
        //3
        OnboardingItem itemFeature3 = new OnboardingItem();
        itemFeature3.setImage(R.drawable.onboard3);
        //
        onboardingItems.add(itemFeature1);
        onboardingItems.add(itemFeature2);
        onboardingItems.add(itemFeature3);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);

    }
        private void setupOnboardingIndicators(){
            ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(8, 0 ,8,0);
            for(int i = 0;i < indicators.length;i++){
                indicators[i]=new ImageView(getApplicationContext());
                indicators[i].setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.onboard_indicate_inactive
                ));
                indicators[i].setLayoutParams(layoutParams);
                layoutOnboardingIndicators.addView(indicators[i]);
            }
    }
    private  void  setCurrentOnboardingIndicator( int index){
        int childCount = layoutOnboardingIndicators.getChildCount();
        for ( int i=0 ; i<childCount ; i++){
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if (i==index){
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboard_indicate_active)
                );
            }else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboard_indicate_inactive)
                );
            }
        }
        if (index==onboardingAdapter.getItemCount()-1){
            buttonOnboardingAction.setText("Start");
        }else {
            buttonOnboardingAction.setText("Next");
        }
    }

}
