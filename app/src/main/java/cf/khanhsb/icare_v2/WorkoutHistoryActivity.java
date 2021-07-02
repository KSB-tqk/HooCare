package cf.khanhsb.icare_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.Objects;

import cf.khanhsb.icare_v2.Adapter.WorkoutHistoryRecyclerViewAdapter;
import cf.khanhsb.icare_v2.Model.ExerciseHistoryItem;

import static android.content.ContentValues.TAG;

public class WorkoutHistoryActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private static final String tempEmail = "tempEmail";
    private ArrayList<ExerciseHistoryItem> exerciseHistoryItems;
    private WorkoutHistoryRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private ImageView backbutton;
    private TextView noHistoryLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        recyclerView = findViewById(R.id.history_recycler_view);
        backbutton = findViewById(R.id.button_backtohomefrag_history);
        noHistoryLabel = findViewById(R.id.no_history_label);

        exerciseHistoryItems = new ArrayList<>();

        Runnable setUpHistory = new Runnable() {
            @Override
            public void run() {
                firestore.collection("workoutHistory")
                        .document(theTempEmail).collection("History").orderBy("firebaseTime", Query.Direction.DESCENDING)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ExerciseHistoryItem tempItem = new ExerciseHistoryItem(
                                        document.getString("workoutTitle"),
                                        document.getString("workoutDate"),
                                        document.getString("workoutDayTime"),
                                        document.getString("workoutTime"),
                                        document.getString("workoutKcal"),
                                        Integer.parseInt(Objects.requireNonNull(document.getString("workoutImage")))
                                );
                                exerciseHistoryItems.add(tempItem);
                            }

                            if(exerciseHistoryItems.size() == 0) {
                                noHistoryLabel.setVisibility(View.VISIBLE);
                            } else {
                                recyclerViewAdapter = new WorkoutHistoryRecyclerViewAdapter(WorkoutHistoryActivity.this,exerciseHistoryItems);
                                recyclerView.setAdapter(recyclerViewAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(WorkoutHistoryActivity.this));
                                noHistoryLabel.setVisibility(View.GONE);
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        };

        Thread backgroundThread = new Thread(setUpHistory);
        backgroundThread.start();

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutHistoryActivity.this, MainActivity.class);
                intent.putExtra("fragmentPosition", 2);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }
}