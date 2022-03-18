package sci.khodier.andriod.elearningdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class fragSignup extends Fragment implements View.OnClickListener{
    Button signupFrame;
    EditText username, Email, password, phone;
    String username0, Email0, password0, phone0, college ;
    Context context;
    ImageView next;
    RadioGroup Type;
    String type;
    private DatabaseReference mDatabase;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    boolean flage=true ;
//    private static final String TAG = "PhoneAuthActivity";
//    private EditText emailTextView, passwordTextView;
//    private Button Btn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    public fragSignup(Context context) {
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.frag_signup, container, false);
        signupFrame=rootView.findViewById(R.id.login);
        mAuth =  FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressbar = rootView.findViewById(R.id.progressbar);
        username = rootView.findViewById(R.id.username);
        Email = rootView.findViewById(R.id.Email);
        password = rootView.findViewById(R.id.Password1);
        phone = rootView.findViewById(R.id.editTextPhone);
        Type=rootView.findViewById(R.id.type);
        signupFrame.setOnClickListener(this);
        next = rootView.findViewById(R.id.toogle);
        next.setOnClickListener(this);
        Type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.Instructor:
                        type="Instructor";Toast.makeText(context, "Instructor", Toast.LENGTH_LONG).show();
                        flage=false;
                    break;
                    case R.id.Student:
                        Toast.makeText(context, "Student", Toast.LENGTH_LONG).show();
                        flage = false;
                        type="Student";
                        break;
                }
            }
        });
        //--------------------------------

        Spinner dropdown = rootView.findViewById(R.id.spinner1);
        String[] items = new String[]{"choose","كليه العلوم", "كليه التجاره", "كليه الهندسه"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
               college= (String) parent.getItemAtPosition(position);
               Toast.makeText(context , college+" is selected",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                if (TextUtils.equals(college,"choose")) {
                    Toast.makeText(context, "Please enter your college!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        //--------------------------------
        return rootView;
    }
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
        }
    }
    @Override
    public void onClick(View v) {
        if(v==signupFrame){
            loadFragment(new fragLogin(context));
        }
        if(v==next) {
            registerNewUser();
        }
    }
    private void registerNewUser()
    {
        // show the visibility of progress bar to show loading
        // Take the value of two edit texts in Strings
        Email0 = Email.getText().toString();
        password0 = password.getText().toString();
        username0 = username.getText().toString();
        phone0 = phone.getText().toString();
        // Validations for input email and password
        if (TextUtils.isEmpty(username0)) {
            Toast.makeText(context, "Please enter username!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(Email0)) {
            Toast.makeText(context, "Please enter email!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password0)) {
            Toast.makeText(context, "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(phone0)) {
            Toast.makeText(context, "Please enter phone!", Toast.LENGTH_LONG).show();
            return;
        }
        if (flage) {
            Toast.makeText(context, "Please enter your type!", Toast.LENGTH_LONG).show();
            return;
        }
        progressbar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(Email0, password0)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                         final String TAG = "DocSnippets";
                        if (task.isSuccessful()) {
                            FirebaseUser fbUser = mAuth.getCurrentUser();

                            addUser(username0,Email0,college,phone0,type);

                            System.out.println("result of success: "+task.getResult());
                            Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show();
                            // hide the progress bar
                            // if the user created intent to login activity
                            Intent intent = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            intent.putExtra("currentUser",currentUser);
                            progressbar.setVisibility(View.GONE);
                            startActivity(intent);
                        }
                        else {
                       //     System.out.println("result of failed: "+task.getResult());
                            // Registration failed
                            Toast.makeText(context, "Registration failed!!" + " Please try another Email", Toast.LENGTH_LONG).show();
                            // hide the progress bar
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    public void addUser(String userName ,String Email,String college ,String phone,String role) {
        final String TAG = "DocSnippets";
        // [START add_ada_lovelace]
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("Email", Email);
        user.put("username", userName);
        user.put("phone", phone);
        user.put("college" , college);
        user.put("role" , role);
        user.put("timestamp" , FieldValue.serverTimestamp());

        // Add a new document with a generated ID
        db.collection("users").document(Email0).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "user added " + task.getResult());
                            System.out.println("user added in db: " + task.getResult());
                        }else{
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        System.out.println("--------------------------------");
                        System.out.println("user doesn't added "+e.toString());
                        System.out.println("--------------------------------");
                    }
                });
        // [END add_ada_lovelace]
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragments, fragment);
        fragmentTransaction.commit(); // save the changes
    }

}

