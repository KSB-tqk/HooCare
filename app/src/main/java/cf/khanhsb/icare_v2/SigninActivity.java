package cf.khanhsb.icare_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cf.khanhsb.icare_v2.SignupActivity;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class SigninActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 120;
    private EditText mEmail, mPass;
    private TextView mHaveNoAccount;
    private Button signinButton;
    private TextView mForgotpass;
    private Button btGoogle;
    private RelativeLayout mProgressbarAuth;
    private static final String tempEmail = "tempEmail";
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean hadSetGoal = false;
    //
    private Button btFacebook;
    CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        FacebookSdk.sdkInitialize(SigninActivity.this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mEmail = findViewById(R.id.et_email_signin);
        mPass = findViewById(R.id.et_password_signin);
        mHaveNoAccount = findViewById(R.id.jumptosignup);
        signinButton = findViewById(R.id.btSignin);
        mForgotpass = findViewById(R.id.forgotpass);
        btGoogle = findViewById(R.id.btGoogle);
        ////////Configure Facebook Sign IN
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        btFacebook = findViewById(R.id.btFB);
        btFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(SigninActivity.this, Arrays.asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

            }
        });


        //////////////////////
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        ///////
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
        ////////
        btGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInwithGoogle();
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

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            result();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void result() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                String email,name;
                try {
                    FirebaseUser user = mAuth.getCurrentUser();
                    email = object.getString("email");
                    name = object.getString("name");

                    CreateUserOnFirebase(email,name);
                    //set up shareRef
                    SharedPreferences sharedPreferences = getSharedPreferences(
                            tempEmail, MODE_PRIVATE);

                    Toast.makeText(SigninActivity.this, "Login Successfully !!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);

                    SharedPreferences.Editor editor;
                    editor = sharedPreferences.edit();
                    editor.putString("Email", email);
                    editor.apply();

                    startActivity(intent);
                    finish();
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields","email , name");
        request.setParameters(bundle);
        request.executeAsync();
    }


    private void signInwithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Exception exception = task.getException();
            if (task.isSuccessful()) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d("SigninActivity", "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());

                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                    if (acct != null) {
                        String googleEmail = acct.getEmail();
                        String userName = acct.getDisplayName();
                        CreateUserOnFirebase(googleEmail,userName);
                        //set up shareRef
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                tempEmail, MODE_PRIVATE);

                        Toast.makeText(SigninActivity.this, "Login Successfully !!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);

                        SharedPreferences.Editor editor;
                        editor = sharedPreferences.edit();
                        editor.putString("Email", googleEmail);
                        editor.apply();

                        startActivity(intent);
                        finish();
                    }
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SigninActivity", "Google sign in failed", e);
                }
            } else {
                Log.w("SigninActivity", exception.toString());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SigninActivity", "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SigninActivity", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loginUser() {
        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                SharedPreferences sharedPreferences = getSharedPreferences(
                                        tempEmail, MODE_PRIVATE);

                                Toast.makeText(SigninActivity.this, "Login Successfully !!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SigninActivity.this, MainActivity.class);

                                SharedPreferences.Editor editor;
                                editor = sharedPreferences.edit();
                                editor.putString("Email", email);
                                editor.apply();

                                startActivity(intent);
                                finish();
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

    private void CreateUserOnFirebase(String userEmail, String userName) {
        //Set up firestore
        firestore = FirebaseFirestore.getInstance();

        // Save user data to firestore
        Map<String, Object> user = new HashMap<>();
        user.put("name", userName);
        user.put("email", userEmail);
        user.put("weight", "empty");
        user.put("height", "empty");
        user.put("step_goal", "empty");
        user.put("drink_goal", "empty");
        user.put("calories_burn_goal", "empty");
        user.put("sleep_goal", "empty");
        user.put("on_screen_goal", "empty");
        user.put("health_point", "empty");
        user.put("time_to_sleep","empty");
        user.put("time_to_wake","empty");
        firestore.collection("users").document(userEmail)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SigninActivity.this, "Fail to save data to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}