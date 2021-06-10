package cf.khanhsb.icare_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class SigninActivity extends AppCompatActivity {
    private EditText mEmail, mPass;
    private TextView mHaveNoAccount;
    private Button signinButton;
    private TextView mForgotpass;
    private RelativeLayout mProgressbarAuth;
    private static final String tempEmail = "tempEmail";
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private boolean hadSetGoal = false;
    //
    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //
        mEmail = findViewById(R.id.et_email_signin);
        mPass = findViewById(R.id.et_password_signin);
        mHaveNoAccount = findViewById(R.id.jumptosignup);
        signinButton = findViewById(R.id.btSignin);
        mForgotpass = findViewById(R.id.forgotpass);
        mProgressbarAuth = findViewById(R.id.progressbarauth);
        //
        mAuth = FirebaseAuth.getInstance();
        //Move to resetpass
        mForgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this, ResetPWActivity.class));
                finish();
            }
        });
        //Move to sign up
        mHaveNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this, SignupActivity.class));
                finish();
            }
        });

        //Move to sign after sign up
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressbarAuth.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressbarAuth.setVisibility(View.INVISIBLE);
                    }
                }, 5000);
                loginUser();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loginUser() {
        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();

        //set up shareRef
        SharedPreferences sharedPreferences = getSharedPreferences(
                tempEmail, MODE_PRIVATE);

        //set up database date
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));

        //set up firestore
        firestore = FirebaseFirestore.getInstance();
        docRef = firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
                document(email);

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            assert document != null;
                                            if (document.exists()) {
                                                Log.d("LOGGER", "got the document");
                                                Toast.makeText(SigninActivity.this, "Login Successfully !!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                                intent.putExtra("userEmail", email);
                                                SharedPreferences.Editor editor;
                                                editor = sharedPreferences.edit();
                                                editor.putString("Email", email);
                                                editor.apply();
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                //check if goal exist or not
                                                docRef = firestore.collection("users").document(email);
                                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document != null) {
                                                                String temp = document.getString("drink_goal");
                                                                assert temp != null;

                                                                //create dailyData
                                                                docRef = firestore.collection("daily").
                                                                        document("week-of-" + monday.toString()).
                                                                        collection(today.toString()).
                                                                        document(email);
                                                                Map<String, Object> dailyGoal = new HashMap<>();

                                                                if (temp.equals("empty")) {
                                                                    dailyGoal.put("drink", "empty");
                                                                } else {
                                                                    dailyGoal.put("drink", "0");
                                                                }

                                                                //update data to firestore
                                                                firestore = FirebaseFirestore.getInstance();
                                                                firestore.collection("daily").
                                                                        document("week-of-" + monday.toString()).
                                                                        collection(today.toString()).
                                                                        document(email).set(dailyGoal);

                                                                Toast.makeText(SigninActivity.this, "Login Successfully !!", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                                                intent.putExtra("userEmail", email);
                                                                SharedPreferences.Editor editor;
                                                                editor = sharedPreferences.edit();
                                                                editor.putString("Email", email);
                                                                editor.apply();
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Log.d("LOGGER", "No such document");
                                                            }
                                                        } else {
                                                            Log.d("LOGGER", "get failed with ", task.getException());
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            Log.d("LOGGER", "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SigninActivity.this, "Login Fail. Please Try Again !", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mPass.setError("Your Password must not empty");
            }
        } else if (email.isEmpty()) {
            mEmail.setError("Your email must not empty");
        } else {
            mEmail.setError("Please enter correct email");
        }
    }
}