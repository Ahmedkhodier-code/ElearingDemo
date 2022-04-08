package sci.khodier.andriod.elearningdemo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class fragHomeCourse extends Fragment {
    View rootView;
    TextView courseName, addTask, announcements, addAnnouncements, task;
    TextInputLayout ann, myTask;
    Button saveTask, saveAnn;
    String courseId;
    fragHomeCourse(String courseId){
        this.courseId=courseId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.frag_home_course, container, false);
        announcements = rootView.findViewById(R.id.announcements);
        ann = rootView.findViewById(R.id.ann);
        task = rootView.findViewById(R.id.task);
        myTask = rootView.findViewById(R.id.myTask);
        saveTask = rootView.findViewById(R.id.saveTask);
        saveAnn = rootView.findViewById(R.id.saveAnn);

        saveAnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }
}