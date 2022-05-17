package sci.khodier.andriod.elearningdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

public class assActivity extends AppCompatActivity {
    TextView courseName, points, message;
    TextInputEditText pdfName;
    ImageView commentBtn, uploadFile;
    announcements currentAnn;
    EditText comment;
    public RecyclerView recyclerView;
    public RecyclerView recyclerView2;
    private static final String TAG = "commentRead";
    private ArrayList<comment> listComment;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference ref;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String annId, username, materialName;
    TextInputEditText material_Name;
    ProgressDialog dialog;
    Uri imageuri = null;
    ArrayList<material> myListData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ass);
        courseName = findViewById(R.id.courseName);
        comment = findViewById(R.id.comment);
        uploadFile = findViewById(R.id.uploadpdf);
        points = findViewById(R.id.point);
        message = findViewById(R.id.message);
        pdfName = findViewById(R.id.pdfName);
        commentBtn = findViewById(R.id.commentBtn);
        material_Name = findViewById(R.id.pdfName);
        annId = getIntent().getExtras().getString("annId");
        System.out.println("annId2: " + annId);
        currentAnn = (announcements) getIntent().getSerializableExtra("currentAnn");
        String s = getIntent().getExtras().getString("courseName");
        System.out.println("courseName: " + s);
        courseName.setText(s);
        message.setText(currentAnn.getMessage());
        getInfo();
        getInfo2();
        recyclerView = findViewById(R.id.comments);
        recyclerView2 = findViewById(R.id.material2);
        getMaterial();
        getComments(recyclerView, annId, currentAnn.getType());
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                addComment(comment.getText().toString(), annId, currentDateandTime, username, currentAnn.getType());

            }
        });
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("application/*");
                startActivityForResult(galleryIntent, 1);
            }
        });

    }

    public void getInfo() {
        ref = FirebaseFirestore.getInstance().collection("tasks").document(annId);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        points.setText("Points Possible: " + doc.getString("degree"));
                    }
                }
            }
        });
    }

    public void getInfo2() {
        ref = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        username = ("" + doc.get("username"));
                    }
                }
            }
        });
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Here we are initialising the progress dialog box
        if (material_Name.getText().toString().isEmpty() || material_Name.getText().toString().equals("") || material_Name.getText().toString() == "") {
            Toast.makeText(this, "Please Enter the name of material", Toast.LENGTH_SHORT).show();
        } else {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading pdf");
            // this will show message uploading
            // while pdf is uploading
            dialog.show();
            dialog.setProgress(0);
            imageuri = data.getData();
            String mimeType = getContentResolver().getType(imageuri);
            System.out.println("mimeType: " + mimeType);
            String temp = data.getStringExtra("path");
            System.out.println("temp: " + temp);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final String messagePushID = "" + System.currentTimeMillis();
            materialName = messagePushID + "." + getExt(mimeType);
            // Here we are uploading the pdf in firebase storage with the name of current time
            final StorageReference filepath = storageReference.child(messagePushID + "." + getExt(mimeType));
            Toast.makeText(this, filepath.getName(), Toast.LENGTH_SHORT).show();
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
                        material.put("name", material_Name.getText().toString() + "." + getExt(mimeType));
                        material.put("id", materialName);
                        material.put("timestamp", FieldValue.serverTimestamp());
                        material.put("extension", getExt(mimeType));
                        material.put("annId", annId);
                        material.put("time", currentDateandTime);
                        db.collection("tasks").document(annId).collection("material").
                                document().set(material)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "assignments added " + task.getResult());
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
                        db.collection("courses").document(currentAnn.getCourseId()).
                                collection("tasks").document(annId).collection("material").
                                document().set(material)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "assignments added " + task.getResult());
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
                        Toast.makeText(assActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        getMaterial();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(assActivity.this, "UploadedFailed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        pdfName.setText("");
    }

    public void addComment(String text, String annId, String time, String userName, String type) {
        final String TAG = "DocSnippets";
        // [START add_ada_lovelace]
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("username", userName);
        user.put("annId", annId);
        user.put("time", time);
        user.put("commentText", text);

        // Add a new document with a generated ID
        db.collection(type).document(annId).collection("comments").document().set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "comment added " + task.getResult());
                            System.out.println("comment added in db: " + task.getResult());
                            comment.setText("");
                            getComments(recyclerView, annId, currentAnn.getType());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void getComments(RecyclerView recyclerView, String id, String type) {
        System.out.println("get comment from assActivity");
        listComment = new ArrayList<comment>();
        db.collection(type).document(id).collection("comments")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        listComment.add(new comment(document.getString("commentText"), document.get("time") + "",
                                document.getString("username")));
                        System.out.println("-------------------/////----------------");
                    }
                    commentAdapter adapter = new commentAdapter(listComment, assActivity.this);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(assActivity.this));
                    recyclerView.setAdapter(adapter);
                    listComment = new ArrayList<comment>();
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(assActivity.this, "Student failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getMaterial() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println("from content");
        db.collection("tasks").document(annId).collection("material")
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
                                , document.getString("annId"),
                                document.getString("time")));
                        System.out.println("-----------------------------------");
                    }
                    RecyclerView recyclerView = findViewById(R.id.material2);
                    materialAdapter adapter = new materialAdapter(myListData, assActivity.this,"ass");
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(assActivity.this));
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(assActivity.this, "documents failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}