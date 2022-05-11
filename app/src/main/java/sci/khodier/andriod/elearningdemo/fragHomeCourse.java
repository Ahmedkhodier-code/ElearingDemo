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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class fragHomeCourse extends Fragment {
    View rootView;
    TextView courseName, announcements, addAnnouncements;
    TextInputLayout ann;
    Button saveAnn;
    String courseId, nameOfCourse;
    DocumentReference ref;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseMessaging fm = FirebaseMessaging.getInstance();
    final String SENDER_ID = "YOUR_SENDER_ID";
    final int messageId = 0; // Increment for each
    String role;
    LinearLayout ll;
    ArrayList<announcements> myListData = new ArrayList<>();
    private static final String TAG = "ReadAndWriteSnippets";

    fragHomeCourse(String courseId) {
        this.courseId = courseId;
    }

    private void sendNotification(String messageBody, String title) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getContext(), channelId)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public void getAnn() {
        myListData = new ArrayList<>();
        db.collection("announcements").whereEqualTo("courseId", courseId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        myListData.add(new announcements(document.getString("message"), document.get("date") + "",
                                document.getString("courseName"), "announcements", document.getId()));
                        System.out.println("-------------------/////----------------");
                    }
                    RecyclerView recyclerView = rootView.findViewById(R.id.AnnAndTask);
                    AnnTaskAdapter adapter = new AnnTaskAdapter(myListData, getContext());
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(getContext(), "Student failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getRole() {
        ref = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    System.out.println("task isSuccessful ");
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        System.out.println("task isExists ");

                        System.out.println("Document" + doc.getData().toString());
                        role = ("" + doc.get("role"));
                    } else {
                        Log.d("Document", "No data");
                    }
                } else {
                    System.out.println("task isn'tSuccessful ");
                }
            }
        });
        System.out.println("the role is :" + role);
        return role;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.frag_home_course, container, false);
        ll = rootView.findViewById(R.id.annCont);

        ref = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        role = ("" + doc.get("role"));
                        if (role.equals("Student") || role == "Student") {
                            ll.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });
        announcements = rootView.findViewById(R.id.announcements);
        ann = rootView.findViewById(R.id.ann);
        saveAnn = rootView.findViewById(R.id.saveAnn);
        courseName = rootView.findViewById(R.id.courseName);
        loadCourse();
        getAnn();
        RecyclerView recyclerView = rootView.findViewById(R.id.AnnAndTask);
        AnnTaskAdapter adapter = new AnnTaskAdapter(myListData, getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
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
                        ann.put("courseName", nameOfCourse);
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
                                            sendNotification("new announcement added click to see", "new announcement");

                                            fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                                                    .setMessageId(Integer.toString(messageId))
                                                    .addData("my_message", announcements.getText().toString())
                                                    .addData("my_action", "CLICK TO SEE")
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
                                            sendNotification("new announcement added click to see", "new announcement");
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
                        nameOfCourse = s;
                        courseName.setText("welcome to " + s);
                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });

    }
}