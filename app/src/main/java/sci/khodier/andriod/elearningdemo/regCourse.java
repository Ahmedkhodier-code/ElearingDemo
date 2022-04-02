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

public class regCourse extends Fragment implements View.OnClickListener{
    Context context;
    View rootView;
    CheckBox checkBox;
    Button add;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    EditText courseName, password;
    private static final String TAG = "ReadAndWriteSnippets";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public regCourse(Context context) {
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


        return rootView;
    }

    public void searchCourse(String courseName, String password) {
        db.collection("courses").whereEqualTo("name", courseName).whereEqualTo("password",password)
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
                        addCourse(course);
                        break;

                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(context, "Courses failed.", Toast.LENGTH_SHORT).show();
                    System.out.println("result of failed: " + task.getException());
                }
            }
        });
        //------------------------------
    }
    public void addCourse(Map<String, Object> course){
        db.collection("courses").document().set(course)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "user added " + task.getResult());
                            System.out.println("user added in db courses collection: " + task.getResult());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        System.out.println("--------------------------------");
                        System.out.println("Course doesn't added " + e.toString());
                        System.out.println("--------------------------------");
                    }
                });
    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(fm).beginTransaction();
        fragmentTransaction.replace(R.id.home_fragment, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public void onClick(View v) {
        if(v==add){
            Toast.makeText(context, "you clicked add", Toast.LENGTH_LONG).show();
            searchCourse(courseName.getText() + "", password.getText() + "");
    }
    }
}