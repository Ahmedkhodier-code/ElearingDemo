package sci.khodier.andriod.elearningdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class profile_frag extends Fragment implements View.OnClickListener {
    Context context;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference ref;
    ImageView profilePic;
    TextView college, uni, level, username, phone, gender, Email , courseCount , role;
    Button signOut;
    Bitmap bitmap = null;
    Bitmap bt = null;

    public profile_frag(Context context) {
        this.context = context;

    }

    public profile_frag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.frag_profile, container, false);
        TransitionInflater inflater0 = TransitionInflater.from(requireContext());
        setExitTransition(inflater0.inflateTransition(R.transition.slide_right));
        profilePic = rootView.findViewById(R.id.profileImg);
        college = rootView.findViewById(R.id.collegeName);
        courseCount=rootView.findViewById(R.id.course_number);
        uni = rootView.findViewById(R.id.uniName);
        signOut = rootView.findViewById(R.id.signOut);
        signOut.setOnClickListener(this);
        role=rootView.findViewById(R.id.role);
        phone = rootView.findViewById(R.id.phone_number);
        Email = rootView.findViewById(R.id.Email);
        username = rootView.findViewById(R.id.username);
        level = rootView.findViewById(R.id.level);
        gender = rootView.findViewById(R.id.gender);

        ref = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        username.setText("" + doc.get("username"));
                        uni.setText("" + doc.get("University"));
                        level.setText("" + doc.get("level"));
                        phone.setText("" + doc.get("phone"));
                        college.setText("" + doc.get("college"));
                        gender.setText("" + doc.get("Gender"));
                        Email.setText("" + currentUser.getEmail());
                        courseCount.setText(""+ doc.get("courses"));
                        role.setText(""+doc.get("role"));
                        LoadImage loadImage = new LoadImage(profilePic);
                        if (doc.get("profImage") + "" != "") {
                            loadImage.execute(doc.get("profImage") + "");
                        }
                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v==signOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(context ,LoginActivity2.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;

        public LoadImage(ImageView imageView) {
            this.imageView = imageView;
        }

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
            imageView.setImageBitmap(bitmap);
        }
    }
}