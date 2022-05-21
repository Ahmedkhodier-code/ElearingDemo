package sci.khodier.andriod.elearningdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class chatActivity extends AppCompatActivity {
    Context context;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        courseId = getIntent().getExtras().getString("courseId");
        System.out.println("courseId: " + courseId);
        message_frag message = new message_frag(context, currentUser, courseId);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(fm).beginTransaction();
        fragmentTransaction.replace(R.id.chat_fragment, message);
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}