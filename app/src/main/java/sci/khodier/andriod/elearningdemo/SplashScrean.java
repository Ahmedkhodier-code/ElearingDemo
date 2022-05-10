package sci.khodier.andriod.elearningdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScrean extends Activity {
    Animation anim;
    ImageView imageView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscrean_main);
        imageView=(ImageView)findViewById(R.id.img1); // Declare an imageView to show the animation.

        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.side_slide); // Create the animation.
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    Intent intent =new Intent(SplashScrean.this ,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);;
                    startActivity(intent);

                }
            }
        };
        //remove title

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        },1500L); //1.5seconds
//        anim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                FirebaseUser currentUser = mAuth.getCurrentUser();
//                if(currentUser != null){
//                    mAuth.addAuthStateListener(mAuthListener);
//                }
//                startActivity(new Intent(SplashScrean.this,LoginActivity2.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                finish();
//                // HomeActivity.class is the activity to go after showing the splash screen.
//            }
//
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        imageView.startAnimation(anim);
    }
}