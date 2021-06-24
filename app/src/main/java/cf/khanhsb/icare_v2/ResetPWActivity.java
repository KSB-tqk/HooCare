package cf.khanhsb.icare_v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPWActivity extends AppCompatActivity {
    private EditText mEmailResetPW;
    private Button mResetPW;
    private TextView mBack;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        //
        mEmailResetPW = (EditText) findViewById(R.id.et_email_resetPW);
        mResetPW = (Button) findViewById(R.id.btResetPW);
        mBack = (TextView) findViewById(R.id.tv_go_back_to_login);
        mAuth = FirebaseAuth.getInstance();
        //
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPWActivity.this, SigninActivity.class));
            }
        });
        mResetPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }

            private void resetPassword() {
                String email = mEmailResetPW.getText().toString().trim();
                //
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPWActivity.this, "Please check your email to reset password!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ResetPWActivity.this, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    mEmailResetPW.setError("Please enter valid email!");
                    mEmailResetPW.requestFocus();
                    return;
                }
                /////////////

            }
        });
    }
}
