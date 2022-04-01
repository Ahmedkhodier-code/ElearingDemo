package sci.khodier.andriod.elearningdemo;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class regCourse extends Fragment {
    Context context;
    View rootView;
    Spinner collage;
    CheckBox checkBox;
    Button create;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    EditText courseName, password;
    boolean flag;
    DocumentReference ref;
    Boolean checkCourse;
    String sItem;
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
        collage = rootView.findViewById(R.id.collage);
        create = rootView.findViewById(R.id.create);
        password = rootView.findViewById(R.id.password);
        courseName = rootView.findViewById(R.id.courseName);
        String[] items = new String[]{"Arts", "Science", "Commerce", "Engineering", "Computers and Information"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
        collage.setAdapter(adapter);
        collage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(context, sItem, Toast.LENGTH_LONG).show();
                flag = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(context, "please Enter the collage name", Toast.LENGTH_LONG).show();
            }
        });

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
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (courseName.getText().toString().equals("")) {
                    Toast.makeText(context, "please enter name", Toast.LENGTH_LONG).show();
                } else {
                    searchCourse(courseName.getText() + "", password.getText() + "",sItem);
                }
            }
        });

        return rootView;
    }

    public void searchCourse(String courseName, String password, String college) {
        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (courseName.equals(document.getString("name")) && password.equals(document.getString("password"))) {
                            //----------
                            break;
                        }
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(context, "Courses failed.", Toast.LENGTH_SHORT).show();
                    System.out.println("result of failed: " + task.getException());
                }
            }
        });
    }

}