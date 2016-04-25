package sample.google.com.cloudvision;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.sql.SQLException;
import java.util.Locale;

public class BeforeHunt extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDJS30xCOXRUpuARGrdsU7e1J3xMkkSGsA";
    public static final String FILE_NAME = "temp.jpg";
    ArrayList<String> featurelist;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    String fishName;
    String lengit;
    private TextView mImageDetails;
    private ImageView mMainImage;
    private ListView listView;
    private ArrayList<String> listViewItems= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_hunt);

        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    uploadImage(null);
                    view.setVisibility(View.INVISIBLE);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        });
        mImageDetails = (TextView) findViewById(R.id.image_details);
        mMainImage = (ImageView) findViewById(R.id.main_image);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void startGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                GALLERY_IMAGE_REQUEST);
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                uploadImage(data.getData());
            } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
                uploadImage(Uri.fromFile(getCameraFile()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
            startCamera();
        }
    }

    public void uploadImage(Uri uri) throws SQLException {
        if (uri == null) {
            try {
                Uri uri1 = Uri.parse("android.resource://sample.google.com.cloudvision/drawable/beforehunt");
                // scale the image to 800px to save on bandwidth
                Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), uri1), 1200);

                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException, SQLException {
        // Switch text to loading
        mImageDetails.setText("Fetching information from Database...");

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(20);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();

                    return convertResponseToString(response);



                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                catch(SQLException e){
                    e.printStackTrace();

                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                mImageDetails.setText(result);
                populateListView();
            }
        }.execute();
    }
    public void getAddress(){
        String string="";
        Geocoder geocoder = new Geocoder(BeforeHunt.this, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(Double.parseDouble(fishName) ,Double.parseDouble(lengit),1);
            Address address = addressList.get(0);
            string = address.getAddressLine(0) + " " + address.getAddressLine(1) + " " + address.getAddressLine(2);
            listViewItems.add(string);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void populateListView(){
        listView = (ListView) findViewById(R.id.fish_details);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                listViewItems);
        arrayAdapter.notifyDataSetChanged();
        listView.setAdapter(arrayAdapter);

    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response)throws SQLException{
        String message = "Bunları buldum:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();

        DataBase db = new DataBase();//DB obj
        ArrayList<String> visionList = new ArrayList<String>();
        boolean check = true;

        if (labels != null) {
            //if (labels.get(0).getDescription().equals("fish")) {
            for (EntityAnnotation label : labels) {
                visionList.add(label.getDescription());
            }
            //} else {
            //   message = "Fotoğraf algılanamadı, tekrar deneyin..";
            //  check = false;
            //}
        }

        ArrayList<Fish> fish = new ArrayList<Fish>();//Fish set

        String myUrl = "jdbc:mysql://db4free.net:3306/fish";
        String username = "fish_user";
        String pass = "Aliveli1010*";
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();}
        catch(Exception e){
            e.printStackTrace();
        }

        Connection con = db.retConnection(myUrl, username, pass);
        Statement st = db.retStatement(con);
        ResultSet rs = st.executeQuery("SELECT * FROM `avlar` WHERE 1");

        while (rs.next()) {
            int score = 0;
            fishName = rs.getString("lat");
            String zaman = rs.getString("zaman");
            String endtime = rs.getString("endtime");
            lengit = rs.getString("longit");
            String feature = rs.getString("tutulan");
            String forecast = rs.getString("forecast");
            featurelist = new ArrayList<String>(
                    Arrays.asList(feature.split(" ")));
            featurelist.add(zaman);
            featurelist.add(endtime);
            featurelist.add(forecast);
            if(check){
            }
            else
                return message;
            if(score>0)
                fish.add(new Fish(score, fishName));
        }
        getAddress();
        Collections.sort(fish, new Comparator<Fish>() {
            @Override
            public int compare(Fish o1, Fish o2) {
                if (o1 == null || o2 == null)
                    throw new NullPointerException(
                            "Can not compare null fishes");

                long id1 = o1.getId();
                long id2 = o2.getId();
                if (id1 > id2)
                    return -1;
                else if (id1 < id2)
                    return +1;
                else
                    return 0;
            }
        });
        if (labels != null) {
            //if (labels.get(0).getDescription().equals("fish")) {
            for (EntityAnnotation label : labels) {
                message += String.format("%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }

        } else {
            message += "nothing";
        }

        //listViewItems.clear();
        for(String s: featurelist){
            if(!s.equals("EMPTY"))
                listViewItems.add(s);
        }

        return "Last fishing session informations!";
    }







}