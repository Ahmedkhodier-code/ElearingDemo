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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class fragAddCourse extends Fragment {
    Context context;
    View rootView;
    Spinner collage;
    CheckBox checkBox;
    Button create;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    EditText courseName;
    boolean flag;
    Boolean checkCourse;
    String sItem;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public fragAddCourse(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.addcourse_frag, container, false);
        TransitionInflater inflater0 = TransitionInflater.from(requireContext());
        setExitTransition(inflater0.inflateTransition(R.transition.slide_right));
        collage = rootView.findViewById(R.id.collage);
        create = rootView.findViewById(R.id.create);
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
                    addCourse(courseName.getText()+"",sItem);
                }
            }
        });

        return rootView;
    }
    public void addCourse(String name, String college) {
        final String TAG = "DocSnippets";
        Map<String, Object> course = new HashMap<>();
        course.put("name", name);
        course.put("college", college);
        course.put("timestamp", FieldValue.serverTimestamp());
        course.put("img", "");
        course.put("active" , true);
        // Add a new document with a generated ID
        db.collection("users").document(currentUser.getEmail()).collection("courses").document().set(course)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "user added " + task.getResult());
                            System.out.println("user added in db: " + task.getResult());
                            checkCourse=true;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        checkCourse=false;
                        System.out.println("--------------------------------");
                        System.out.println("Course doesn't added " + e.toString());
                        System.out.println("--------------------------------");
                    }
                });
        // [END add_ada_lovelace]
    }

}