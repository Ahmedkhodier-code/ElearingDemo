package sci.khodier.andriod.elearningdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class fragCourseContent extends Fragment {
    private static final int RESULT_OK = 1;
    ImageView upload;
    TextInputEditText material_Name;
    Uri imageuri = null;
    ProgressDialog dialog;
    String courseId, materialName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<material> myListData = new ArrayList<>();
    View rootView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    final String SENDER_ID = "YOUR_SENDER_ID";
    final int messageId = 0; // Increment for each
    String role = "", nameOfCourse;
    FirebaseMessaging fm = FirebaseMessaging.getInstance();
    private static final String TAG = "ReadAndWriteSnippets";

    fragCourseContent(String courseId) {
        this.courseId = courseId;
    }

    public String getRule() {
        db.collection("users").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "currentUser data: " + document.getData());
                        role = document.getString("role");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        System.out.println("the role is :" + role);
        return role;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_course_content, container, false);
        material_Name=rootView.findViewById(R.id.nameMaterial);
        // Inflate the layout for this fragment
        loadCourse();
        upload = rootView.findViewById(R.id.uploadpdf);
        if (getRule() == "Student") {
            rootView.findViewById(R.id.upload).setVisibility(View.INVISIBLE);
        }
        getMaterial();
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("application/*");
                startActivityForResult(galleryIntent, 1);
            }
        });
        return rootView;
    }

    public String getExt(String type) {
        String res = "";
        String ser = "";
        for (int i = type.length() - 1; i > 0; i--) {
            if (type.charAt(i) == '/' || type.charAt(i) == '-' || type.charAt(i) == '.') {
                break;
            }
            ser += type.charAt(i);
        }
        for (int i = ser.length() - 1; i > -1; i--) {
            res += ser.charAt(i);
        }
        if (res.equals("msword")) {
            res = "doc";
        }
        if (res.equals("powerpoint") || res.equals("mspowerpoint")) {
            res = "ppt";
        }
        if (res.equals("excel") || res.equals("msexcel") || res.equals("sheet")) {
            res = "xls";
        }
        System.out.println("extension: " + res);
        return res;
    }


    public void getMaterial() {
        System.out.println("from content");
        db.collection("courses").document(courseId).collection("material")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        myListData.add(new material(document.getId(),
                                Objects.requireNonNull(document.getString("name"))
                                , document.getString("extension")
                                , document.getString("type")
                                , document.getString("courseId"),
                                document.getString("time")));
                        System.out.println("-----------------------------------");
                    }
                    RecyclerView recyclerView = rootView.findViewById(R.id.material);
                    materialAdapter adapter = new materialAdapter(myListData, getContext(), "mat");
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(getContext(), "documents failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Here we are initialising the progress dialog box
        if(material_Name.getText().toString().isEmpty()||material_Name.getText().toString().equals("")||material_Name.getText().toString()==""){
            Toast.makeText(getContext(), "Please Enter the name of material", Toast.LENGTH_SHORT).show();
        }else{
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Uploading pdf");
            // this will show message uploading
            // while pdf is uploading
            dialog.show();
            dialog.setProgress(0);
            imageuri = data.getData();
            String mimeType = getContext().getContentResolver().getType(imageuri);
            System.out.println("mimeType: " + mimeType);
            String temp = data.getStringExtra("path");
            System.out.println("temp: " + temp);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final String messagePushID = "" + System.currentTimeMillis();
            materialName = messagePushID + "." + getExt(mimeType);
            // Here we are uploading the pdf in firebase storage with the name of current time
            final StorageReference filepath = storageReference.child(messagePushID + "." + getExt(mimeType));
            Toast.makeText(getContext(), filepath.getName(), Toast.LENGTH_SHORT).show();
            filepath.putFile(imageuri).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    dialog.setProgress(30);
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    dialog.setProgress(50);
                    System.out.println("filepath.getDownloadUrl(): " + filepath.getDownloadUrl());
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    dialog.setProgress(80);
                    if (task.isSuccessful()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());
                        Map<String, Object> material = new HashMap<>();
                        material.put("name", material_Name.getText().toString()+"."+getExt(mimeType));
                        material.put("id", materialName);
                        material.put("timestamp", FieldValue.serverTimestamp());
                        material.put("extension", getExt(mimeType));
                        material.put("courseId", courseId);
                        material.put("time", currentDateandTime);
                        db.collection("courses").document(courseId).collection("material").
                                document().set(material)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //    sendNotification("new matrial added click to see","new content");
                                            uploadAnn();
                                            Log.d(TAG, "user added " + task.getResult());
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
                        // After uploading is done it progress
                        // dialog box will be dismissed
                        dialog.dismiss();
                        Uri uri = task.getResult();
                        String myurl;
                        myurl = uri.toString();
                        System.out.println("myurl" + myurl);
                        dialog.setProgress(100);
                        Toast.makeText(getContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        getMaterial();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "UploadedFailed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

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

    public void uploadAnn() {
        SimpleDateFormat sdf = new SimpleDateFormat("   yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        final String TAG = "DocSnippets";
        Map<String, Object> ann = new HashMap<>();
        ann.put("courseName", nameOfCourse);
        ann.put("message", "new material added");
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
                            Toast.makeText(getContext(), "your message has been uploaded", Toast.LENGTH_SHORT).show();
                            sendNotification("new announcement added click to see", "new announcement");

                            fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                                    .setMessageId(Integer.toString(messageId))
                                    .addData("my_message", "new material added")
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

    public void loadCourse() {
        DocumentReference ref = FirebaseFirestore.getInstance().collection("courses").document(courseId);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String s = doc.getString("name");
                        nameOfCourse = s;
                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });

    }

}