package sci.khodier.andriod.elearningdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    fragCourse Courses = new fragCourse(this ,currentUser);
    profile_frag profile = new profile_frag(this , currentUser);
    fragSettings settings = new fragSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.course);
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
                Toast.makeText(this, "you clicked on messages", Toast.LENGTH_LONG).show();
                return true;
        }
        return false;
    }
}