package sci.khodier.andriod.elearningdemo;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class fragRegCourse extends Fragment {
    boolean std;
    Context context;
    View rootView;
    CheckBox checkBox;
    Button add;
    boolean flage;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    TextInputEditText courseName, password , creatorName;
    private static final String TAG = "ReadAndWriteSnippets";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public fragRegCourse(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.frag_reg_course, container, false);
        TransitionInflater inflater0 = TransitionInflater.from(requireContext());
        setExitTransition(inflater0.inflateTransition(R.transition.slide_right));
        add = rootView.findViewById(R.id.addCourse);
        password = rootView.findViewById(R.id.password);
        creatorName=rootView.findViewById(R.id.creatorName);
        courseName = rootView.findViewById(R.id.courseName);
        checkBox = rootView.findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    Toast.makeText(context, "checked", Toast.LENGTH_LONG).show();
                    Transition transition = new Slide(Gravity.RIGHT);
                    transition.setDuration(700);
                    transition.addTarget(R.id.code);
                    TransitionManager.beginDelayedTransition(rootView.findViewById(R.id.code), transition);
                    rootView.findViewById(R.id.code).setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(context, "unchecked", Toast.LENGTH_LONG).show();
                    Transition transition = new Slide(Gravity.LEFT);
                    transition.setDuration(300);
                    transition.addTarget(R.id.code);
                    TransitionManager.beginDelayedTransition(rootView.findViewById(R.id.code), transition);
                    rootView.findViewById(R.id.code).setVisibility(View.INVISIBLE);
                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("add clicked");
                Toast.makeText(context, "you clicked add", Toast.LENGTH_LONG).show();
                if (searchCourse(courseName.getText() + "", password.getText() + "" , creatorName.getText()+"")) {
                    loadFragment(new fragCourse(context));
                }
            }
        });
        return rootView;
    }

    public boolean searchCourse(String courseName, String password , String creator) {
        db.collection("courses").whereEqualTo("name", courseName).whereEqualTo("password", password).whereEqualTo("creator",creator)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Log.d(TAG, "the img" + " => " + document.getString("img"));
                        Map<String, Object> course = new HashMap<>();
                        course.put("courseId", document.getId());
                        course.put("courseName", courseName);
                        flage = addCourse(course , document.getId() );
                        break;
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(context, "Courses failed.", Toast.LENGTH_SHORT).show();
                    System.out.println("result of failed: " + task.getException());
                }
            }
        });
        return flage;
    }

    public boolean addCourse(Map<String, Object> course, String id) {
        db.collection("users").document(currentUser.getEmail()).collection("courses").document(id).set(course)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()&&!std) {
                            std=true;
                            Map<String, Object> Student = new HashMap<>();
                            Student.put("StudentEmail",currentUser.getEmail());
                            db.collection("courses").document(id).collection("Students").
                                    document(currentUser.getEmail()).set(Student)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "user added " + task.getResult());
                                                flage = true;
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            flage = false;
                                            Log.w(TAG, "Error adding document", e);
                                            System.out.println("--------------------------------");
                                            System.out.println("Course doesn't added " + e.toString());
                                            System.out.println("--------------------------------");
                                        }
                                    });
                            Log.d(TAG, "user added " + task.getResult());
                            flage = true;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        flage = false;
                        Log.w(TAG, "Error adding document", e);
                        System.out.println("--------------------------------");
                        System.out.println("Course doesn't added " + e.toString());
                        System.out.println("--------------------------------");
                    }
                });
        return flage;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(fm).beginTransaction();
        fragmentTransaction.replace(R.id.home_fragment, fragment);
        fragmentTransaction.commit(); // save the changes
    }
}