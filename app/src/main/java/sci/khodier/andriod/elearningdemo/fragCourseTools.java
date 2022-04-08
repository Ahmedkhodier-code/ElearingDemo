package sci.khodier.andriod.elearningdemo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class fragCourseTools extends Fragment implements View.OnClickListener {
    String courseId;
    Button deleteCourse;
    View rootView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "ReadAndWriteSnippets";
    ArrayList<student> myListData = new ArrayList<>();

    fragCourseTools(String courseId) {
        this.courseId = courseId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_course_tools, container, false);
        // Inflate the layout for this fragment
        deleteCourse = rootView.findViewById(R.id.deleteCourse);
        getStudent();
        System.out.println("myListData"+myListData.toArray().toString());
        return rootView;
    }

    public void getStudent() {
        db.collection("courses").document(courseId).collection("Students")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        myListData.add(new student(document.getString("name"),
                                document.getString("StudentEmail")));
                        System.out.println("-------------------/////----------------");
                    }
                    RecyclerView recyclerView = rootView.findViewById(R.id.students);
                    studentAdapter adapter = new studentAdapter(myListData, getContext());
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);} else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(getContext(), "Student failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == deleteCourse) {
            if (false) {
                db.collection("courses").document(courseId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
            }
        }
    }
}