package sci.khodier.andriod.elearningdemo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class fragHomeCourse extends Fragment {
    View rootView;
    TextView courseName, addTask, announcements, addAnnouncements, task;
    TextInputLayout ann, myTask;
    Button saveTask, saveAnn;
    String courseId;
    DocumentReference ref;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    fragHomeCourse(String courseId) {
        this.courseId = courseId;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.frag_home_course, container, false);
        announcements = rootView.findViewById(R.id.announcements);
        ann = rootView.findViewById(R.id.ann);
        task = rootView.findViewById(R.id.task);
        myTask = rootView.findViewById(R.id.myTask);
        saveTask = rootView.findViewById(R.id.saveTask);
        saveAnn = rootView.findViewById(R.id.saveAnn);
        courseName = rootView.findViewById(R.id.courseName);
        loadCourse();
        saveAnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!announcements.getText().toString().equals("") || !announcements.getText().toString().isEmpty()) {
                    if (announcements.getText().toString().length() < 10) {
                        Toast.makeText(getContext(), "your message is too short!!", Toast.LENGTH_SHORT).show();

                    } else {
                        final String TAG = "DocSnippets";
                        Map<String, Object> ann = new HashMap<>();
                        ann.put("message", announcements.getText().toString());
                        ann.put("courseId", courseId);
                        ann.put("timestamp", FieldValue.serverTimestamp());
                        // Add a new document with a generated ID
                        db.collection("announcements").document().set(ann)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "announcements added " + task.getResult());
                                            System.out.println("user added in db announcements collection: " + task.getResult());
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        System.out.println("--------------------------------");
                                        System.out.println("announcements doesn't added " + e.toString());
                                        System.out.println("--------------------------------");
                                    }
                                });
                        db.collection("courses").document(courseId)
                                .collection("announcements").document().set(ann)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "announcements added " + task.getResult());
                                            System.out.println("user added in db announcements collection: " + task.getResult());
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        System.out.println("--------------------------------");
                                        System.out.println("announcements doesn't added " + e.toString());
                                        System.out.println("--------------------------------");
                                    }
                                });
                    }
                }

            }
        });
        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!task.getText().toString().equals("") || !task.getText().toString().isEmpty()) {
                    if (task.getText().toString().length() < 10) {
                        Toast.makeText(getContext(), "your message is too short!!", Toast.LENGTH_SHORT).show();

                    } else {
                        final String TAG = "DocSnippets";
                        Map<String, Object> ann = new HashMap<>();
                        ann.put("message", task.getText().toString());
                        ann.put("courseId", courseId);
                        ann.put("timestamp", FieldValue.serverTimestamp());
                        // Add a new document with a generated ID
                        db.collection("tasks").document().set(ann)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "tasks added " + task.getResult());
                                            System.out.println("user added in db announcements collection: " + task.getResult());
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        System.out.println("--------------------------------");
                                        System.out.println("tasks doesn't added " + e.toString());
                                        System.out.println("--------------------------------");
                                    }
                                });
                        db.collection("courses").document(courseId)
                                .collection("tasks").document().set(ann)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "tasks added " + task.getResult());
                                            System.out.println("user added in db announcements collection: " + task.getResult());
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        System.out.println("--------------------------------");
                                        System.out.println("tasks doesn't added " + e.toString());
                                        System.out.println("--------------------------------");
                                    }
                                });
                    }
                }

            }
        });

        return rootView;
    }

    public void loadCourse() {
        ref = FirebaseFirestore.getInstance().collection("courses").document(courseId);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String s = doc.getString("name");
                        System.out.println("name is " + s);
                        courseName.setText("welcome to " + s);
                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });

    }
}