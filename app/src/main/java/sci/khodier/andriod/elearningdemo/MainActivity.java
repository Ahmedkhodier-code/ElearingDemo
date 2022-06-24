package sci.khodier.andriod.elearningdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    fragCourse Courses = new fragCourse(this);
    profile_frag profile = new profile_frag(this);
    fragMessage message = new fragMessage(this);

    fragSettings settings = new fragSettings();
    frag_ActivityStream activity = new frag_ActivityStream(this);
    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.course);
        createNotificationChannel();
        getToken();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.course:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, Courses).commit();
                return true;

            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, profile).commit();
                return true;

            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, settings).commit();
                return true;
            case R.id.home:
                Toast.makeText(this, "you clicked on home", Toast.LENGTH_LONG).show();
                return true;
            case R.id.message:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, message).commit();
                Toast.makeText(this, "you clicked on messages", Toast.LENGTH_LONG).show();
                return true;
            case R.id.activity:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, activity).commit();
                return true;
        }
        return false;
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                //If task is failed then
                if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Failed to get the Token");
                } else {
                    //Token
                    String token = task.getResult();
                    Log.d(TAG, "onComplete: " + token);
                }
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotifChannel";
            String description = "Receve Firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}