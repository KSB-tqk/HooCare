package cf.khanhsb.icare_v2.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    TextView mName, mId, mEmail, mUsername;
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
        mId = rootview.findViewById(R.id.id_user);
        mEmail = (TextView) rootview.findViewById(R.id.id_email);
        mUsername = (TextView) rootview.findViewById(R.id.id_username);
        mUserImage = rootview.findViewById(R.id.id_userimage);
        logout = rootview.findViewById(R.id.linearlogout);

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
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        SharedPreferences sharedPreferences = this.getActivity().
                getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        mEmail.setText(theTempEmail);
        firestore = FirebaseFirestore.getInstance();
        docRef = firestore.collection("users").document(theTempEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String temp = document.getString("name");
                        mName.setText(temp);
                    }
                }
            }
        });
        return rootview;
    }
}

