package sci.khodier.andriod.elearningdemo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class fragCourseAss extends Fragment {
    String courseId;

    fragCourseAss(String courseId) {
        this.courseId = courseId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.frag_course_ass, container, false);

        return rootView;
    }
}