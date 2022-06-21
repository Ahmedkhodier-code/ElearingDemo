package sci.khodier.andriod.elearningdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class assActivityDegree extends AppCompatActivity {
    String courseId;
    String annId;
    announcements currentAnn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "ReadAndWriteSnippets";
    ArrayList<student> myListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ass_degree);
        annId = getIntent().getExtras().getString("annId");
        courseId = getIntent().getExtras().getString("courseId");
        System.out.println("annId2: " + annId);
        currentAnn = (announcements) getIntent().getSerializableExtra("currentAnn");
        String s = getIntent().getExtras().getString("courseName");
        System.out.println("sscourseId" + courseId);
        getStudent();
    }

    public void getStudent() {
        db.collection("courses").document(courseId).collection("Students")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                db.collection("tasks").document(annId).
                                        collection("degree").document(document.getString("StudentEmail")).
                                        get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                    myListData.add(new student(document.getString("name"),
                                                            document.getString("StudentEmail"), task.getResult().getString("degree")));
                                                } else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                    myListData.add(new student(document.getString("name"),
                                                            document.getString("StudentEmail")));
                                                }
                                            }
                                        });
                            }
                            RecyclerView recyclerView = findViewById(R.id.studentsdegree);
                            studentAdapterGrades adapter = new studentAdapterGrades(myListData, assActivityDegree.this, annId);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(assActivityDegree.this));
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(assActivityDegree.this, "Student failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}