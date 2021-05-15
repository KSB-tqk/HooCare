package cf.khanhsb.icare_v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SigninActivity extends AppCompatActivity {
    private EditText mEmail, mPass;
    private TextView mHaveNoAccount;
    private Button signinButton;
    private TextView mForgotpass;
    //
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //
        mEmail = findViewById(R.id.et_email_signin);
        mPass = findViewById(R.id.et_password_signin);
        mHaveNoAccount = findViewById(R.id.jumptosignup);
        signinButton = findViewById(R.id.btSignin);
        mForgotpass = findViewById(R.id.forgotpass);
        //
        mAuth = FirebaseAuth.getInstance();
        //Move to resetpass
        mForgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this,ResetPWActivity.class));
                finish();
            }
        });
        //Move to sign up
        mHaveNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this,SignupActivity.class));
                finish();
            }
        });


        //Move to sign after sign up
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }
    private void loginUser(){
        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();

        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            if(!pass.isEmpty()){
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(SigninActivity.this, "Login Successfully !!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SigninActivity.this,MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SigninActivity.this, "Login Fail. Please Try Again !", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                mPass.setError("Your Password must not empty");
            }
        }else if(email.isEmpty()){
            mEmail.setError("Your email must not empty");
        }else{
            mEmail.setError("Please enter correct email");
        }
    }
}