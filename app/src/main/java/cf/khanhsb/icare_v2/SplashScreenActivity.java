package cf.khanhsb.icare_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final int SPLASH_SCREEN = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user != null){
                    Intent mainintent = new Intent(SplashScreenActivity.this,MainActivity.class);
                    startActivity(mainintent);
                    finish();
                }else{
                Intent intent = new Intent(SplashScreenActivity.this,OnBoardActivity.class);
                startActivity(intent);
                finish();}
            }
        },SPLASH_SCREEN);
    }
}