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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class fragHomeCourse extends Fragment {
    View rootView;
    TextView courseName, addTask, announcements, addAnnouncements;
    TextInputLayout ann, myTask;
    Button saveTask, saveAnn;
    String courseId , nameOfCourse;
    DocumentReference ref;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseMessaging fm = FirebaseMessaging.getInstance();
    final String SENDER_ID = "YOUR_SENDER_ID";
    final int messageId = 0; // Increment for each
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
        addTask = rootView.findViewById(R.id.task);
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
                        SimpleDateFormat sdf = new SimpleDateFormat("   yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());
                        final String TAG = "DocSnippets";
                        Map<String, Object> ann = new HashMap<>();
                        ann.put("courseName",nameOfCourse);
                        ann.put("message", announcements.getText().toString());
                        ann.put("courseId", courseId);
                        ann.put("date", currentDateandTime);
                        // Add a new document with a generated ID
                        db.collection("announcements").document().set(ann)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "announcements added " + task.getResult());
                                            System.out.println("user added in db announcements collection: " + task.getResult());
                                            announcements.setText("");
                                            Toast.makeText(getContext(), "your message has been uploaded", Toast.LENGTH_SHORT).show();

                                            fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                                                    .setMessageId(Integer.toString(messageId))
                                                    .addData("my_message", announcements.getText().toString())
                                                    .addData("my_action","CLICK TO SEE")
                                                    .build());
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
                                        System.out.println("-----   ---------------------------");
                                    }
                                });
                    }
                }

            }
        });
        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addTask.getText().toString().equals("") || !addTask.getText().toString().isEmpty()) {
                    if (addTask.getText().toString().length() < 10) {
                        Toast.makeText(getContext(), "your message is too short!!", Toast.LENGTH_SHORT).show();

                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("   yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());
                        final String TAG = "DocSnippets";
                        Map<String, Object> ann = new HashMap<>();
                        ann.put("message", addTask.getText().toString());
                        ann.put("courseId", courseId);
                        ann.put("date", currentDateandTime);
                        // Add a new document with a generated ID
                        db.collection("tasks").document().set(ann)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "tasks added " + task.getResult());
                                            System.out.println("user added in db announcements collection: " + task.getResult());
                                            addTask.setText("");
                                            Toast.makeText(getContext(), "your message has been uploaded", Toast.LENGTH_SHORT).show();
                                            fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                                                    .setMessageId(Integer.toString(messageId))
                                                    .addData("my_message", addTask.getText().toString())
                                                    .addData("my_action","CLICK TO SEE")
                                                    .build());
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
                        nameOfCourse= s;
                        courseName.setText("welcome to " + s);
                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });

    }
}