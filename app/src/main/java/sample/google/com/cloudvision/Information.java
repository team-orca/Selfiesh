package sample.google.com.cloudvision;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Information extends AppCompatActivity {
    private TextView textView,mass,length,minlength;
    private ImageView imageView;
    private Button addButton;
    private EditText editText;
    private String currentFish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);
        Intent intent =getIntent();
        currentFish = intent.getStringExtra("fish");
        textView = (TextView)findViewById(R.id.text_information);
        imageView = (ImageView)findViewById(R.id.image_information);
        editText = (EditText)findViewById(R.id.length_editText);
        //editText.setText(" ");
        addButton = (Button)findViewById(R.id.addButton);
        switch (currentFish){
            case "RedMullet":
                imageView.setImageResource(R.drawable.redmullet);
                setInformation(currentFish, 13);
                mass = (TextView) findViewById(R.id.mass);
                mass.setText("Average mass: 50-75 grams.");
                length = (TextView) findViewById(R.id.length);
                length.setText("Average length: 17-20 cms.");
                minlength = (TextView) findViewById(R.id.minlength);
                minlength.setText("Minimum length: 13 cms.");
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String length = editText.getText().toString();
                        if(TextUtils.isEmpty(length)){
                            editText.setError("Can not be empty");}
                        else{
                        if (Integer.parseInt(editText.getText().toString()) > 13) {
                            new Description().execute();
                            Toast.makeText(Information.this, "Your hunt data added to database!", Toast.LENGTH_LONG).show();
                            finish();
                        } else if(Integer.parseInt(editText.getText().toString()) < 13)
                            createAndShowDialog("Hunting is FORBIDDEN for this specie according to size of your prey", "FORBIDDEN");
                        else if(checkLegal(currentFish))
                            createAndShowDialog("Hunting is FORBIDDEN for this specie according to hunting date", "FORBIDDEN");
                    }}
                });

                break;
            case "Redporgy":
                imageView.setImageResource(R.drawable.redporgy);
                setInformation(currentFish, 18);
                mass = (TextView) findViewById(R.id.mass);
                mass.setText("Average mass: 90-120 grams.");
                length = (TextView) findViewById(R.id.length);
                length.setText("Average length: 20-30 cms.");
                minlength = (TextView) findViewById(R.id.minlength);
                minlength.setText("Minimum length: 18 cms.");
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String length = editText.getText().toString();
                        if(TextUtils.isEmpty(length)){
                            editText.setError("Can not be empty");}
                        else{
                        if (Integer.parseInt(editText.getText().toString()) > 18) {
                            new Description().execute();
                            Toast.makeText(Information.this, "Your hunt data added to database!", Toast.LENGTH_LONG).show();
                            finish();
                        }else if(Integer.parseInt(editText.getText().toString()) < 18)
                            createAndShowDialog("Hunting is FORBIDDEN for this specie according to size of your prey", "FORBIDDEN");
                        else if(checkLegal(currentFish))
                            createAndShowDialog("Hunting is FORBIDDEN for this specie according to hunting date", "FORBIDDEN");
                    }}
                });

                break;
            case "Bluefish":
                imageView.setImageResource(R.drawable.bluefish);
                setInformation(currentFish, 20);
                mass = (TextView) findViewById(R.id.mass);
                mass.setText("Average mass: 125-333 grams.");
                length = (TextView) findViewById(R.id.length);
                length.setText("Average length: 21-30 cms.");
                minlength = (TextView) findViewById(R.id.minlength);
                minlength.setText("Minimum length: 20 cms.");
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String length = editText.getText().toString();
                        if(TextUtils.isEmpty(length)){
                            editText.setError("Can not be empty");}
                        else{
                        if (Integer.parseInt(editText.getText().toString()) >= 20) {
                            new Description().execute();
                            Toast.makeText(Information.this, "Your hunt data added to database!", Toast.LENGTH_LONG).show();
                            finish();
                        } else if(Integer.parseInt(editText.getText().toString()) < 20)
                            createAndShowDialog("Hunting is FORBIDDEN for this specie according to size of your prey", "FORBIDDEN");
                        else if(checkLegal(currentFish))
                            createAndShowDialog("Hunting is FORBIDDEN for this specie according to hunting date", "FORBIDDEN");

                    }}
                });
                break;
            case "SeaBass":
                imageView.setImageResource(R.drawable.seabass);
                setInformation(currentFish, 20);
                mass = (TextView) findViewById(R.id.mass);
                mass.setText("Average mass: 220-300 grams.");
                length = (TextView) findViewById(R.id.length);
                length.setText("Average length: 50-60 cms.");
                minlength = (TextView) findViewById(R.id.minlength);
                minlength.setText("Minimum length: 20 cms.");
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String length = editText.getText().toString();
                        if(TextUtils.isEmpty(length)){
                            editText.setError("Can not be empty");}
                        else{
                        if (Integer.parseInt(editText.getText().toString()) > 20) {
                            new Description().execute();
                            Toast.makeText(Information.this, "Your hunt data added to database!", Toast.LENGTH_LONG).show();
                            finish();
                        } else if(Integer.parseInt(editText.getText().toString()) < 20)
                            createAndShowDialog("Hunting is FORBIDDEN for this specie according to size of your prey", "FORBIDDEN");
                        else if(checkLegal(currentFish))
                            createAndShowDialog("Hunting is FORBIDDEN for this specie according to hunting date", "FORBIDDEN");
                    }}
                });
                break;
            case "BlueWhiting":
                imageView.setImageResource(R.drawable.bluewhiting);
                setInformation(currentFish, 15);
                mass = (TextView) findViewById(R.id.mass);
                mass.setText("Average mass: 110-125 grams.");
                length = (TextView) findViewById(R.id.length);
                length.setText("Average length: 20-30 cms.");
                minlength = (TextView) findViewById(R.id.minlength);
                minlength.setText("Minimum length: 15 cms.");
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String length = editText.getText().toString();
                        if(TextUtils.isEmpty(length)){
                            editText.setError("Can not be empty");}
                        else{
                        if (Integer.parseInt(editText.getText().toString()) > 15) {
                            new Description().execute();
                            Toast.makeText(Information.this, "Your hunt data added to database!", Toast.LENGTH_LONG).show();
                            finish();
                        } else if(Integer.parseInt(editText.getText().toString()) < 15)
                            createAndShowDialog("Hunting is FORBIDDEN for this specie according to size of your prey", "FORBIDDEN");
                        else if(checkLegal(currentFish))
                            createAndShowDialog("Hunting is FORBIDDEN for this specie according to hunting date", "FORBIDDEN");
                    }}
                });
                break;
                    default: setInformation("default case",0);
                break;
        }
    }
    public void setInformation(final String currentFish, final int size){
        textView.setText(currentFish);
        }

    public String getDate() {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(c.getTime());

        return date;

    }

    /* compareDate returns false if current date is not between allowed dates */

    public boolean compareDate(String forbiddenDateStart, String forbiddenDateEnd){

        String [] date = getDate().split("-");

        boolean isAllowed = false;

        int currentDay = Integer.parseInt(date[0]);
        int currentMonth = Integer.parseInt(date[1]);

        String [] start = forbiddenDateStart.split("-");

        int startDay = Integer.parseInt(start[0]);
        int startMonth = Integer.parseInt(start[1]);

        String [] end = forbiddenDateEnd.split("-");

        int endDay = Integer.parseInt(end[0]);
        int endMonth = Integer.parseInt(end[1]);

        if((currentMonth < startMonth) || (currentMonth > endMonth)) {
            isAllowed = true;
        }else if(currentMonth == startMonth){
            if(currentDay < startDay)
                isAllowed = true;
            else
                isAllowed = false;
        }else if(currentMonth == endMonth){
            if(currentDay > endDay)
                isAllowed = true;
            else
                isAllowed = false;
        }

        return isAllowed;
    }

    public boolean checkLegal(String fishName){

        String [] species = {"RedMullet", "1-04", "31-08", "RedPorgy", "15-03", "15-09", "Bluefish", "15-05", "15-09", "SeaBass", "01-05", "30-06", "BlueWhiting", "01-10", "30-11",};

        ArrayList<String> list = new ArrayList<String>(Arrays.asList(species));
        int foundIndex = 0;

        for(int i = 0; i < list.size(); i++){
            if(i % 3 == 0){
                if(list.get(i).matches(fishName)) {
                    foundIndex = i;
                }
            }
        }

        return compareDate(list.get(foundIndex+1), list.get(foundIndex+2));

    }

    @Override
    public void onBackPressed(){
        finish();
    }
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    ProgressDialog progressDialog;

    private class Description extends AsyncTask<Void, Void, Void> {
        DataBase db;
        Connection con;
        Statement st;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Information.this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            progressDialog.dismiss();
            String myUrl = "jdbc:mysql://db4free.net:3306/fish";
            String username = "fish_user";
            String pass = "Aliveli1010*";
            try{
                Class.forName("com.mysql.jdbc.Driver").newInstance();}
            catch(Exception e){
                e.printStackTrace();
            }
            try {
                db = new DataBase();
                con = db.retConnection(myUrl, username, pass);
                st = con.createStatement();

            }
            catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }
        String sql;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sql = "UPDATE avlar SET tutulan=CONCAT(tutulan, ' "+currentFish+"') ORDER BY AI DESC LIMIT 1";
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        st.executeUpdate(sql);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.schedule(task, 2, TimeUnit.SECONDS);

        }
    }
}
