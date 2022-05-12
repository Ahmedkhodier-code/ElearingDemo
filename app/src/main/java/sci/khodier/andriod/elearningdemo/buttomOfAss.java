package sci.khodier.andriod.elearningdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class buttomOfAss extends Fragment {
    ImageView upload;
    TextInputEditText material_Name;
    Uri imageuri = null;
    ProgressDialog dialog;
    String courseId, materialName, taskId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    View rootView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String nameOfCourse;
    private static final String TAG = "ReadAndWriteSnippets";

    buttomOfAss(String courseId, String taskId) {
        this.courseId = courseId;
        this.taskId = taskId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_buttom_of_ass, container, false);
        material_Name = rootView.findViewById(R.id.nameMaterial);
        // Inflate the layout for this fragment
        loadCourse();
        upload = rootView.findViewById(R.id.uploadpdf);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Here we are initialising the progress dialog box
        if (material_Name.getText().toString().isEmpty() || material_Name.getText().toString().equals("") || material_Name.getText().toString() == "") {
            Toast.makeText(getContext(), "Please Enter the name of material", Toast.LENGTH_SHORT).show();
        } else {
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
                        material.put("name", material_Name.getText().toString() + "." + getExt(mimeType));
                        material.put("id", materialName);
                        material.put("timestamp", FieldValue.serverTimestamp());
                        material.put("extension", getExt(mimeType));
                        material.put("courseId", courseId);
                        material.put("taskId", taskId);
                        material.put("time", currentDateandTime);
                        material.put("degree", -1);

                        db.collection("courses").document(courseId).collection("Students").
                                document(currentUser.getEmail()).collection("Tasks").document().set(material)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
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
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "UploadedFailed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

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