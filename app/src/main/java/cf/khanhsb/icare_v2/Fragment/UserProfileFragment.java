package cf.khanhsb.icare_v2.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import cf.khanhsb.icare_v2.R;

public class UserProfileFragment extends Fragment {
    TextView mName, mId, mEmail, mUsername;
    ImageView mUserImage;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

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

        mName = (TextView) rootview.findViewById(R.id.id_fullname);
        mId = rootview.findViewById(R.id.id_user);
        mEmail = (TextView) rootview.findViewById(R.id.id_email);
        mUsername = (TextView) rootview.findViewById(R.id.id_username);
        mUserImage = rootview.findViewById(R.id.id_userimage);

        return rootview;
    }
}

