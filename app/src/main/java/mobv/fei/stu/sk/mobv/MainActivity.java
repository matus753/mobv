package mobv.fei.stu.sk.mobv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import cz.msebera.android.httpclient.Header;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobv.fei.stu.sk.mobv.model.Post;
import mobv.fei.stu.sk.mobv.rest.HttpUtils;
import mobv.fei.stu.sk.mobv.rest.UploadResponse;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 69;
    private static final int RC_SELECT_PICTURE = 1;
    private static final int RC_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 2;

    private static final String TAG = "MAIN";
    private static final String UPLOAD_FILE_PATH = "/upload/index.php";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private UsersRecyclerViewFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        List<AuthUI.IdpConfig> providers = Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "*/*");
                String[] mimetypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(Intent.createChooser(intent, "Select Picture or Video"), RC_SELECT_PICTURE);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    RC_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null) {
                    db.collection("users").document(user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    if(document.getData() == null) {
                                        // Create a new user with a first and last name
                                        Map<String, Object> userDb = new HashMap<>();
                                        userDb.put("username", user.getDisplayName());
                                        userDb.put("date", new Date());
                                        userDb.put("numberOfPosts", 0);
                                        // Add a new document with a generated ID
                                        db.collection("users").document(user.getUid())
                                                .set(userDb)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void avoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error adding document", e);
                                                    }
                                                });
                                    }

                                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                    fragment = new UsersRecyclerViewFragment();
                                    transaction.replace(R.id.users_fragment, fragment);
                                    transaction.commit();
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }

        if (requestCode == RC_SELECT_PICTURE) {
            if(data != null) {
                Uri selectedMediaUri = data.getData();
                if(selectedMediaUri != null) {
                    //error
                    Uri selectedUri = data.getData();
                    String[] columns = {MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.MIME_TYPE};

                    Cursor cursor = getContentResolver().query(selectedUri, columns, null, null, null);
                    cursor.moveToFirst();

                    int mimeTypeColumnIndex = cursor.getColumnIndex(columns[1]);

                    String mimeType = cursor.getString(mimeTypeColumnIndex);
                    cursor.close();

                    if(selectedMediaUri.getPath() != null) {
                        if (mimeType.startsWith("image")) {
                            //It's an image
                            Log.i(TAG, "Image path:" + selectedMediaUri.getPath());
                            uploadFile(selectedMediaUri.getPath().replace("/document/raw:", ""), "image");
                        } else if (mimeType.startsWith("video")) {
                            //It's a video
                            Log.i(TAG, "Video path:" + selectedMediaUri.getPath());
                            uploadFile(selectedMediaUri.getPath().replace("/document/raw:", ""), "video");
                        }
                    }
                }
            }
        }
    }

    private void uploadFile(String path, final String type){
        File file = new File(path);

        RequestParams rp = new RequestParams();

        try {
            rp.put("upfile", file);
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return;
        }

        HttpUtils.post(UPLOAD_FILE_PATH, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("File upload", "---------------- this is response : " + response);

                UploadResponse resp = new Gson().fromJson(response.toString(), UploadResponse.class);

                if(statusCode == 200) {
                    if(!"Invalid parameters.".equals(resp.getMessage())) {
                        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                        final Map<String, Object> post = new HashMap<>();

                        post.put("date", new Date());
                        post.put("type", type);
                        post.put("url", "http://mobv.mcomputing.eu/upload/v/".concat(resp.getMessage()));
                        post.put("userid", currentUser.getUid());
                        post.put("username", currentUser.getDisplayName());
                        // Add a new document with a generated ID
                        db.collection("posts").document()
                                .set(post)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void avoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");

                                        fragment.newPost(new Post(
                                                            (String) post.get("userid"),
                                                            (String) post.get("username"),
                                                            (String) post.get("type"),
                                                            (String) post.get("url"),
                                                            (Date) post.get("date")));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
}
