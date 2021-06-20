package cf.khanhsb.icare_v2.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import cf.khanhsb.icare_v2.Model.UserHelperClass;
import cf.khanhsb.icare_v2.R;

public class UserProfileFragment extends Fragment {
    TextView mName, mId, mEmail, mUsername;
    ImageView mUserImage;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    public UserProfileFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_user_profile,container,false);

        mName =(TextView) rootview.findViewById(R.id.id_fullname);
        mId = rootview.findViewById(R.id.id_user);
        mEmail = (TextView) rootview.findViewById(R.id.id_email);
        mUsername = (TextView) rootview.findViewById(R.id.id_username);
        mUserImage = rootview.findViewById(R.id.id_userimage);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        userID = user.getUid();

//        if (user != null) {
//            for (UserInfo profile : user.getProviderData()) {
//                // Id of the provider (ex: google.com)
//                String providerId = profile.getProviderId();
//
//                // UID specific to the provider
//                String uid = profile.getUid();
//
//                // Name, email address, and profile photo Url
//                String name = profile.getDisplayName();
//                String email = profile.getEmail();
//                Uri photoUrl = profile.getPhotoUrl();
//
//                mName.setText(name);
//                mEmail.setText(email);
//                mUsername.setText(name);
//            }
//        }


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if(userProfile!=null){
                    String Username = userProfile.getmUsername();
                    String Email = userProfile.getmEmail();
                    String Name = userProfile.getmName();

                    mName.setText(Name);
                    mEmail.setText(Email);
                    mUsername.setText(Username);
                } else {
                    mName.setText("dflgkjfdklhjkgfl");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    return rootview;}
    }

