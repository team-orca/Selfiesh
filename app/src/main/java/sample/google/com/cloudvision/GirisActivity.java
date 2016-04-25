package sample.google.com.cloudvision;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class GirisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giris);
        switchToMenu();
    }

    private void switchToMenu(){
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        ImageView girisLogo = (ImageView)findViewById(R.id.img_girisLogo);
        anim.reset();
        girisLogo.clearAnimation();
        girisLogo.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                GirisActivity.this.finish();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
}
