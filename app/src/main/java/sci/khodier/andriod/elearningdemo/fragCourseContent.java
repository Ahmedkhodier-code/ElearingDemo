package sci.khodier.andriod.elearningdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.SQLOutput;

public class fragCourseContent extends Fragment {
    private static final int RESULT_OK = 1;
    ImageView upload;
    Uri imageuri = null;
    ProgressDialog dialog;
    String courseId;
    String materialId;
    fragCourseContent(String courseId) {
        this.courseId = courseId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rooView = inflater.inflate(R.layout.frag_course_content, container, false);
        // Inflate the layout for this fragment
        upload = rooView.findViewById(R.id.uploadpdf);
        // After Clicking on this we will be
        // redirected to choose pdf
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("application/*");
                startActivityForResult(galleryIntent, 1);
            }
        });
        return rooView;
    }

    public String getExt(String type) {
        String res = "";
        String ser = "";
        for (int i = type.length() - 1; i > 0; i--) {
            if (type.charAt(i) == '/' || type.charAt(i) == '-' ||type.charAt(i)=='.') {
                break;
            }
            ser += type.charAt(i);
        }
        for (int i = ser.length() - 1; i > -1; i--) {
            res += ser.charAt(i);
        }
        if(res.equals("msword")){
         res="doc";
        }
        if(res.equals("powerpoint")||res.equals("mspowerpoint")){
            res="ppt";
        }
        if(res.equals("excel")||res.equals("msexcel")||res.equals("sheet")){
            res="xls";
        }
        System.out.println("extension: " + res);
        return res;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Here we are initialising the progress dialog box
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
                System.out.println("filepath.getDownloadUrl(): "+ filepath.getDownloadUrl());
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                dialog.setProgress(80);
                if (task.isSuccessful()) {
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