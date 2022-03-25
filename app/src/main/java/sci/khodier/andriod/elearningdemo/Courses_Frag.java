package sci.khodier.andriod.elearningdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class Courses_Frag extends Fragment implements View.OnClickListener {

    private final Context context;
    boolean f;
    TextView name;
    FrameLayout signOut , addCourse;
    private FirebaseAuth mAuth;
    private ProgressBar progressbar;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference ref ;
    ArrayList<Course> myListData = new ArrayList<>();
    private static final String TAG = "ReadAndWriteSnippets";
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public Courses_Frag(Context context , FirebaseUser currentUser) {
        this.context=context;
        this.currentUser=currentUser;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_courses, container, false);
        progressbar = rootView.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.VISIBLE);
        name=rootView.findViewById(R.id.name);
        DocumentReference docRef = FirebaseFirestore.getInstance().
                collection("courses").document(  "course1");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                            Log.d(  "Document", Objects.requireNonNull(doc.getData()).toString());
                } else {
                        Log.d(  "Document"  , "No data");
            }}
            }
        });
        myListData = new ArrayList<>();
        db.collection("courses").whereEqualTo("Active",true)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                myListData.add(new Course(Objects.requireNonNull(document.get("name")).toString(),
                                        document.getId(), Objects.requireNonNull(document.get("img")).toString()));
                                System.out.println("-----------------------------------");
//                                System.out.println(document.getData().get("course1").toString());
                                System.out.println("-----------------------------------");
                                progressbar.setVisibility(View.GONE);

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(context, "Courses failed.", Toast.LENGTH_SHORT).show();
                            System.out.println("result of failed: "+task.getException());
                            progressbar.setVisibility(View.GONE);

                        }
                    }
                });
        mAuth = FirebaseAuth.getInstance();
        addCourse =  rootView.findViewById(R.id.add);
        System.out.println("currentUser is: "+currentUser.getEmail());
        ref =  FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
             ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                     if(task.isSuccessful()){
                         DocumentSnapshot doc = task.getResult();
                         if (doc.exists()){
                           name.setText("Hi "+doc.get("username"));
                         } else {
                             Log.d(  "Document"  , "No data");
                         }
                     }else{

                     }
                 }
             });

        addCourse.setOnClickListener(this);
        signOut = rootView.findViewById(R.id.toogle);
        signOut.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        MyListAdapter adapter = new MyListAdapter(myListData , context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

    return rootView;
    }


    @Override
    public void onClick(View v) {
        if(v==addCourse){

        }
        if(v==signOut) {
            progressbar.setVisibility(View.VISIBLE);
            mAuth.getInstance().signOut();
            Intent intent = new Intent(context ,LoginActivity2.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);;
            progressbar.setVisibility(View.GONE);
            startActivity(intent);

        }
    }
}