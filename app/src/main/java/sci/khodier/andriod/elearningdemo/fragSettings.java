package sci.khodier.andriod.elearningdemo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.os.Bundle;

public class fragSettings extends Fragment {
    EditText updatedEmail, updatedPassword;
    Button changeEmail, changePassword, removeAccount;
    ProgressBar accountSettingProgress;
    Snackbar snackbar;
    ConstraintLayout constraintLayout;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TransitionInflater inflater0 = TransitionInflater.from(requireContext());
        setExitTransition(inflater0.inflateTransition(R.transition.slide_right));
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.frag_settings, container, false);

        //----------------------------------------------
        changeEmail = rootView.findViewById(R.id.change_email);
        changePassword = rootView.findViewById(R.id.change_password);
        removeAccount = rootView.findViewById(R.id.delete_account);
        accountSettingProgress = rootView.findViewById(R.id.accountsettin_progress);
        updatedEmail = rootView.findViewById(R.id.change_email_text);
        updatedPassword = rootView.findViewById(R.id.change_password_text);
        constraintLayout = rootView.findViewById(R.id.settingContainer);

        //get firebase instance
        firebaseAuth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
//                    Intent i = new Intent(AccountSetting.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(i);
                }
            }
        };

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountSettingProgress.setVisibility(View.VISIBLE);
                if (updatedEmail.getText().toString().trim().equals("")) {
                    snackbar = Snackbar.make(constraintLayout, "Enter Email.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    accountSettingProgress.setVisibility(View.GONE);
                }
                if (user != null && !updatedEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(updatedEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        snackbar = Snackbar.make(constraintLayout, "Email updated successfully.", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        accountSettingProgress.setVisibility(View.GONE);
                                    } else {
                                        snackbar = Snackbar.make(constraintLayout, "Failed to update email. Please try gain later.", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        accountSettingProgress.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountSettingProgress.setVisibility(View.VISIBLE);
                if (changePassword.getText().toString().trim().equals("")) {
                    snackbar = Snackbar.make(constraintLayout, "Enter Password.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    accountSettingProgress.setVisibility(View.GONE);
                }

                if (changePassword.getText().toString().length() < 7) {
                    snackbar = Snackbar.make(constraintLayout, "Please enter minimum 7 character password.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    accountSettingProgress.setVisibility(View.GONE);

                }

                if (user != null && changePassword.getText().toString().length() >= 7) {

                    user.updatePassword(updatedPassword.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        snackbar = Snackbar.make(constraintLayout, "Password updated successfully.", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        accountSettingProgress.setVisibility(View.GONE);
                                    } else {
                                        snackbar = Snackbar.make(constraintLayout, "Failed to update password.", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        accountSettingProgress.setVisibility(View.GONE);
                                    }

                                }
                            });
                }
            }
        });
        removeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountSettingProgress.setVisibility(View.VISIBLE);
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        snackbar = Snackbar.make(constraintLayout, "Your account has been deleted successfully.", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        accountSettingProgress.setVisibility(View.GONE);
//                                        Intent i = new Intent(AccountSetting.this, MainActivity.class);
//                                        startActivity(i);
                                    } else {
                                        snackbar = Snackbar.make(constraintLayout, "Failed to delete account.", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        accountSettingProgress.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });
//----------------------------------------------
        return rootView;
    }
}