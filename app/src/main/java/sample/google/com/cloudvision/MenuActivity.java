package sample.google.com.cloudvision;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_activity);

        Button button_beforeHunt = (Button)findViewById(R.id.beforehunt_button);
        Button button_onHunt = (Button)findViewById(R.id.onhunt_button);

        button_beforeHunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, BeforeHunt.class);
                startActivity(intent);
            }
        });

        button_onHunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), GetLocationActivity.class);
                startActivity(intent);
            }
        });


    }
}
