package sci.khodier.andriod.elearningdemo;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class fragCourseAss extends Fragment {
    String courseId;
    Button saveTask;
    TextInputLayout myTask;
    TextView addTask, degree, Date;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseMessaging fm = FirebaseMessaging.getInstance();
    final String SENDER_ID = "YOUR_SENDER_ID";
    final int messageId = 0; // Increment for each
    final Calendar myCalendar = Calendar.getInstance();
    EditText editText;

    fragCourseAss(String courseId) {
        this.courseId = courseId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.frag_course_ass, container, false);
        saveTask = rootView.findViewById(R.id.saveTask);
        myTask = rootView.findViewById(R.id.myTask);
        addTask = rootView.findViewById(R.id.task);
        editText = rootView.findViewById(R.id.date);
        degree = rootView.findViewById(R.id.degree);
        Date = rootView.findViewById(R.id.date);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addTask.getText().toString().equals("") || !addTask.getText().toString().isEmpty()) {
                    if (addTask.getText().toString().length() < 10) {
                        Toast.makeText(getContext(), "your message is too short!!", Toast.LENGTH_SHORT).show();

                    } else if (degree.getText().toString().isEmpty() ||
                            degree.getText().toString() == "" ||
                            degree.getText().toString().equals("")) {

                        Toast.makeText(getContext(), "please enter the degree", Toast.LENGTH_SHORT).show();
                    } else if (Date.getText().toString().isEmpty() ||
                            Date.getText().toString() == "" ||
                            Date.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "please enter the date", Toast.LENGTH_SHORT).show();
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());
                        final String TAG = "DocSnippets";
                        Map<String, Object> ann = new HashMap<>();
                        ann.put("message", addTask.getText().toString());
                        ann.put("endDate", Date.getText().toString());
                        ann.put("degree", degree.getText().toString());
                        ann.put("coursed", courseId);
                        ann.put("date", currentDateandTime);
                        // Add a new document with a generated ID
                        db.collection("tasks").document().set(ann)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "tasks added " + task.getResult());
                                            System.out.println("user added in db announcements collection: " + task.getResult());
                                            addTask.setText("");
                                            Date.setText("");
                                            degree.setText("");
                                            Toast.makeText(getContext(), "your message has been uploaded", Toast.LENGTH_SHORT).show();

                                            fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                                                    .setMessageId(Integer.toString(messageId))
                                                    .addData("my_message", addTask.getText().toString())
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
                                        System.out.println("tasks doesn't added " + e.toString());
                                        System.out.println("--------------------------------");
                                    }
                                });
                        db.collection("courses").document(courseId)
                                .collection("tasks").document().set(ann)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //     sendNotification("new task added click to see", "new task");

                                            Log.d(TAG, "tasks added " + task.getResult());
                                            System.out.println("user added in db announcements collection: " + task.getResult());
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        System.out.println("--------------------------------");
                                        System.out.println("tasks doesn't added " + e.toString());
                                        System.out.println("--------------------------------");
                                    }
                                });
                    }
                }

            }
        });

        return rootView;
    }

    private void updateLabel() {
        String myFormat = "yyyy/MM/dd HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(dateFormat.format(myCalendar.getTime()));
    }
}