package sci.khodier.andriod.elearningdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class Courses_Frag extends Fragment implements View.OnClickListener {

    private final Context context;
    TextView name;
    FrameLayout addCourse;
    private ProgressBar progressbar;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference ref;
    ArrayList<Course> myListData = new ArrayList<>();
    Bitmap img, bitmap, bt;
    View rootView;
    private static final String TAG = "ReadAndWriteSnippets";

    public Courses_Frag(Context context, FirebaseUser currentUser) {
        this.context = context;
        this.currentUser = currentUser;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.home_fragment, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         rootView = inflater.inflate(R.layout.fragment_courses, container, false);
        TransitionInflater inflater0 = TransitionInflater.from(requireContext());
        setExitTransition(inflater0.inflateTransition(R.transition.slide_right));
        progressbar = rootView.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.VISIBLE);
        name = rootView.findViewById(R.id.name);
        myListData = new ArrayList<>();
        db.collection("users").document(Objects.requireNonNull(currentUser.getEmail())).collection("courses").whereEqualTo("active", true)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        LoadImage loadImage = new LoadImage();
                        loadImage.execute(Objects.requireNonNull(document.getString("img")));
                        myListData.add(new Course(Objects.requireNonNull(document.getString("name")),
                                document.getId(), bt));
                        System.out.println("-----------------------------------");
                        progressbar.setVisibility(View.GONE);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(context, "Courses failed.", Toast.LENGTH_SHORT).show();
                    System.out.println("result of failed: " + task.getException());
                    progressbar.setVisibility(View.GONE);
                }
            }
        });
        addCourse = rootView.findViewById(R.id.add);
        System.out.println("currentUser is: " + currentUser.getEmail());
        ref = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        name.setText("Hi " + doc.get("username"));
                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });

        addCourse.setOnClickListener(this);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        MyListAdapter adapter = new MyListAdapter(myListData, context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == addCourse) {
            Toast.makeText(context, "add Courses.", Toast.LENGTH_SHORT).show();
            loadFragment(new fragAddCourse(context));

        }
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            System.out.println("doInBackground");
            String urllink = strings[0];
            try {
                InputStream inputStream = new java.net.URL(urllink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                System.out.println("try");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bt = bitmap;
            System.out.println("bt has the image");
        }
    }
}