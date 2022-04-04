package sci.khodier.andriod.elearningdemo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class fragSettings extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TransitionInflater inflater0 = TransitionInflater.from(requireContext());
        setExitTransition(inflater0.inflateTransition(R.transition.slide_right));
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.frag_settings, container, false);
        return rootView;
    }
}