package sci.khodier.andriod.elearningdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class fragChangePassword extends Fragment {
    Button save;
    EditText oldPass, newPass, repeatPass;
    String oPass, nPass, rPass;
    private static final String TAG = "changingPassword";
    private ProgressBar progressbar;
    TextView forget;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public fragChangePassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.frag_change_password, container, false);
        oldPass = (EditText) rootView.findViewById(R.id.oldPass);
        newPass = (EditText) rootView.findViewById(R.id.newPass);
        repeatPass = (EditText) rootView.findViewById(R.id.repeatPass);
        forget=rootView.findViewById(R.id.forget);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = user.getEmail();
                System.out.println("emailAddress"+emailAddress);
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                }
                            }
                        });
                new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog)
                        .setTitle("Email sent")
                        .setMessage("Please Check Your Email!")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })
                        .setIcon(R.drawable.envelope)
                        .show();
            }
        });
        save = rootView.findViewById(R.id.save);
        progressbar = rootView.findViewById(R.id.progressbar);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);
                oPass = oldPass.getText().toString();
                nPass = newPass.getText().toString();
                rPass = repeatPass.getText().toString();
                if (TextUtils.isEmpty(oPass)) {
                    Toast.makeText(getContext(), "please Enter old password", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(nPass)) {
                    Toast.makeText(getContext(), "please Enter new password", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(rPass)) {
                    Toast.makeText(getContext(), "please repeat new password", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                    return;
                }
                if (!nPass.equals(rPass)) {
                    Toast.makeText(getContext(), "mismatch password", Toast.LENGTH_SHORT).show();
                    repeatPass.setText("");
                    progressbar.setVisibility(View.GONE);
                    return;
                }
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), oPass);
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "User re-authenticated.");
                                user.updatePassword(rPass)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User password updated.");
                                                }
                                            }
                                        });
                            }
                        });
                Toast.makeText(getContext(), "password changed successfully", Toast.LENGTH_SHORT).show();
                progressbar.setVisibility(View.GONE);
                loadFragment(new fragSettings());
            }
        });

        return rootView;

    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(fm).beginTransaction();
        fragmentTransaction.replace(R.id.home_fragment, fragment);
        fragmentTransaction.commit();
    }
}