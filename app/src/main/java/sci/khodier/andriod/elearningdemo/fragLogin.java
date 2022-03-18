package sci.khodier.andriod.elearningdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class fragLogin extends Fragment implements View.OnClickListener {
    Button loginFrame;
    ImageView next;
    Context context;
    private ProgressBar progressbar;
    EditText  Email  ,password;
    TextInputLayout pass;
    String Email0,password0;
//-------------------

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth.AuthStateListener mAuthListener;

    public fragLogin(Context context) {
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(R.layout.frag_login, container, false);
        loginFrame=rootView.findViewById(R.id.signup);
        loginFrame.setOnClickListener(this);
        next = (ImageView) rootView.findViewById(R.id.toogle);
        next.setOnClickListener(this);
        Email = rootView.findViewById(R.id.Email);
        password = rootView.findViewById(R.id.Password);
        pass = rootView.findViewById(R.id.etPasswordLayout);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        // initialising all views through id defined above
        progressbar = rootView.findViewById(R.id.progressbar);

        //----------------

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
        }
    }

    @Override
    public void onClick(View v) {
        if(v==loginFrame){
            loadFragment(new fragSignup(context));
        }
        if(v==next) {
            Email0 = Email.getText().toString();
            password0 = password.getText().toString();
            signIn(Email0,password0);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragments, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    private void signIn(String email, String password) {
        mAuth = FirebaseAuth.getInstance();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(context, "Please enter email!!", Toast.LENGTH_LONG).show();
            return;
        }else
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Please enter password!!", Toast.LENGTH_LONG).show();
            return;
        }else
        {
        // [START sign_in_with_email]
        progressbar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            System.out.println("-----------------------------------");
                            System.out.println(currentUser.getEmail() +""+ currentUser.getUid());
                            System.out.println("-----------------------------------");
                            Intent intent =new Intent(context ,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            progressbar.setVisibility(View.GONE);
                            intent.putExtra("currentUser",currentUser);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context,task.getException().toString(), Toast.LENGTH_SHORT).show();
                            System.out.println("result of failed: "+task.getException());
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });
        // [END sign_in_with_email]
        }
    }

}
