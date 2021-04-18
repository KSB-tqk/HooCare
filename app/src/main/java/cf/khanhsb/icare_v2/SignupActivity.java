package cf.khanhsb.icare_v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends Activity {
    private EditText mEmail, mPass;
    private TextView mTextView;
    private Button signupButton;
    //
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mEmail = findViewById(R.id.et_email_signup);
        mPass = findViewById(R.id.et_password_signup);
        mTextView = findViewById(R.id.jumptosignin);
        signupButton = findViewById(R.id.btSignup);
        //
        mAuth = FirebaseAuth.getInstance();
        //Already have account
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,SigninActivity.class));
            }
        });

        //Move to signin after sign up
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }
    private void createUser(){
        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();

        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            if(!pass.isEmpty()){
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "Sign Up Successfully !!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this,SigninActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, "Registration Error !!", Toast.LENGTH_SHORT).show();
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
