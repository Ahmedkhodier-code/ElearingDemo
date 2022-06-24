package sci.khodier.andriod.elearningdemo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;

public class fragEditProfile extends Fragment {
    ImageView profilePic, changePic;
    TextInputEditText userName, phoneNum;
    Button save;

    public fragEditProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_edit_profile, container, false);
        profilePic = rootView.findViewById(R.id.layout_image);
        changePic = rootView.findViewById(R.id.change_pic);
        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        userName=rootView.findViewById(R.id.user_name);
        phoneNum=rootView.findViewById(R.id.contact_no);

        return rootView;
    }
}