package cf.khanhsb.icare_v2.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cf.khanhsb.icare_v2.MainActivity;
import cf.khanhsb.icare_v2.R;
import cf.khanhsb.icare_v2.SigninActivity;

import static android.content.Context.MODE_PRIVATE;

public class UserProfileFragment extends Fragment {
    private TextView mName, mId, mEmail, mDateOfBirth,mGender;
    ImageView mUserImage;
    private static final String tempEmail = "tempEmail";
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private LinearLayout logout;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    public UserProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        mName = (TextView) rootview.findViewById(R.id.id_fullname);
        mEmail = (TextView) rootview.findViewById(R.id.id_email);
        mUserImage = rootview.findViewById(R.id.id_userimage);
        logout = rootview.findViewById(R.id.linearlogout);
        mDateOfBirth = rootview.findViewById(R.id.id_date_of_birth);
        mGender = rootview.findViewById(R.id.id_gender);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        ;
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                //
                LoginManager.getInstance().logOut();
                /////
                mGoogleSignInClient.signOut();
                Intent i = new Intent(getActivity(), SigninActivity.class);
                startActivity(i);
                getActivity().finish();
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        SharedPreferences sharedPreferences = this.getActivity().
                getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        mEmail.setText(theTempEmail);
        firestore = FirebaseFirestore.getInstance();

        Runnable getUserInfoFromFirebase = new Runnable() {
            @Override
            public void run() {
                docRef = firestore.collection("users").document(theTempEmail);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                String temp = document.getString("name");
                                String tempBirth = document.getString("date_of_birth");
                                String tempGender = document.getString("gender");
                                mName.setText(temp);

                                assert tempBirth != null;
                                if(!tempBirth.equals("empty")){
                                    mDateOfBirth.setText(tempBirth);
                                } else {
                                    mDateOfBirth.setText("Choose date of birth");
                                }

                                assert tempGender != null;
                                if(!tempGender.equals("empty")){
                                    mGender.setText(tempGender);
                                }else {
                                    mGender.setText("Choose Gender");
                                }
                            }
                        }
                    }
                });
            }
        };

        Thread backgroundThread = new Thread(getUserInfoFromFirebase);
        backgroundThread.start();

        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openEditNameDialog(Gravity.CENTER,3);
            }
        });

        mDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openEditBirthdayDialog(Gravity.CENTER,3);
            }
        });

        mGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openEditGenderDialog(Gravity.CENTER,3);
            }
        });
        return rootview;
    }
}

