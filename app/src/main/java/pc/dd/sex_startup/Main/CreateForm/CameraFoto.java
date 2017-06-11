package pc.dd.sex_startup.Main.CreateForm;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import pc.dd.sex_startup.Main.RegisterFragment;
import pc.dd.sex_startup.R;

/**
 * Created by UserData on 09.07.2016.
 */

public class CameraFoto extends Fragment {

    public FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.camera_foto_main, container, false);
        ImageView previous_image = (ImageView) v.findViewById(R.id.previous_upload_image);
        previous_image.setImageURI(LoadImage.file_url);

        ImageView upload_image = (ImageView) v.findViewById(R.id.upload);
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_click();
            }
        });
        ImageView disable = (ImageView) v.findViewById(R.id.disable);
        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((ViewGroup)container.getParent()).removeView((LinearLayout)container);
                    getFragmentManager().popBackStack();
                } catch (Exception e) {
                }
                ;
            }
        });
        return v;
    }

    public void upload_click() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // UserData is signed in
            String temp = upload(user);
            Intent i = new Intent(getActivity(), pc.dd.sex_startup.Main.Maps.Map.class);
            startActivity(i);

        } else {
            // No user is signed in
        }
    }

    public String upload(final FirebaseUser user) {

        final String[] dwndurl = new String[1];
        // File or Blob
        Uri file;
        String[] file_u = String.valueOf(LoadImage.file_url).split("file://");
        file = Uri.fromFile(new File(file_u[1]));////"/storage/emulated/0/VK/img.jpg"));

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        //get storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://sexstartup-e3a65.appspot.com");

        // Upload file and metadata to the path 'images/mountains.jpg'
        UploadTask uploadTask = storageRef.child("UserData: " + user.getUid() + "/" + file.getLastPathSegment()).putFile(file, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                System.out.println("Upload is " + progress + "% done");
                Log.d("---", "Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("---", "Upload false " + exception.getMessage());

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = null;
                do {
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    dwndurl[0] = downloadUrl.toString();
                }
                while (dwndurl[0] == null);
                GPSTracker gps = new GPSTracker(getActivity());
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(user.getUid());
                Map<String, User> users = new HashMap<String, User>();
                RegisterFragment test = new RegisterFragment();
                users.put("UserData data: ",
                        new User(String.valueOf(latitude) + "/" + String.valueOf(longitude)
                                , dwndurl[0]
                                , user.getUid()
                                , test.getMain_user_nuckname()
                        ));
                myRef.setValue(users);
                Toast.makeText(getActivity().getApplicationContext(), "Upload done", Toast.LENGTH_SHORT).show();
                getActivity().finish();

            }
        });

        return dwndurl[0];
    }
}

class User {

    public String lat;
    public String url;
    public String uid;
    public String nickname;

    public User(String latitude_longitude, String url, String uid, String nickname) {
        this.lat = latitude_longitude;
        this.url = url;
        this.uid = uid;
        this.nickname = nickname;
    }
}
