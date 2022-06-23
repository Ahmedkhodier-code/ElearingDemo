package sci.khodier.andriod.elearningdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class fragAddCourse extends Fragment implements View.OnClickListener {
    Context context;
    View rootView;
    Spinner collage;
    CheckBox checkBox;
    Button create;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    TextInputEditText courseName , password;
    boolean flag;
    Boolean checkCourse;
    String sItem , username;
    DocumentReference ref;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public fragAddCourse(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.frag_addcourse, container, false);
        TransitionInflater inflater0 = TransitionInflater.from(requireContext());
        setExitTransition(inflater0.inflateTransition(R.transition.slide_right));
        collage = rootView.findViewById(R.id.collage);
        password=rootView.findViewById(R.id.password);
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
        create.setOnClickListener(this);
        return rootView;
    }

    public void addCourse(String courseName, String college , String password) {
        ref = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        username=doc.get("username")+"";
                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });
            final String TAG = "DocSnippets";
        Map<String, Object> course = new HashMap<>();
        course.put("name", courseName);
        course.put("college", college);
        course.put("timestamp", FieldValue.serverTimestamp());
        course.put("img", "");
        course.put("creator", currentUser.getEmail());
        course.put("creatorName", username);
        course.put("active", true);
        course.put("password", password);

        // Add a new document with a generated ID
        db.collection("courses").document().set(course)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "user added " + task.getResult());
                            System.out.println("user added in db courses collection: " + task.getResult());
                            checkCourse = true;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        checkCourse = false;
                        System.out.println("--------------------------------");
                        System.out.println("Course doesn't added " + e.toString());
                        System.out.println("--------------------------------");
                    }
                });
        // [END add_ada_lovelace]
    }

    @Override
    public void onClick(View v) {
        if (v == create) {
            System.out.println("buttom create clicked");
            Toast.makeText(context, courseName.getText(), Toast.LENGTH_LONG).show();
            addCourse(courseName.getText() + "", sItem ,password.getText()+"");
            loadFragment(new fragCourse(context));
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.home_fragment, fragment);
        fragmentTransaction.commit(); // save the changes
    }
    private void sendNotification(String messageBody , String title) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =(NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}