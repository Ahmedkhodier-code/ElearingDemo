package sci.khodier.andriod.elearningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        //------------------------------------------
//        next = (ImageView)findViewById(R.id.toogle);
//        next.setOnClickListener(this);

        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragments,new fragLogin(this));
        fragmentTransaction.commit();
        initialize();

    }
    Handler handler;
    Runnable runnable;
    private void initialize()
    {
        handler = new Handler();
        runnable = new Runnable()
        {
            @Override
            public void run()
            {
                //start your activity here
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
        finish();
    }



}
