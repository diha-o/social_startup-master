package pc.dd.sex_startup.Main.Maps;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pc.dd.sex_startup.Main.Helpers.MyItemContainer;
import pc.dd.sex_startup.Main.Helpers.StatusBarFragment;
import pc.dd.sex_startup.Main.Helpers.UserData;
import pc.dd.sex_startup.R;

/**
 * Created by UserData on 09.07.2016.
 */

public class Map extends FragmentActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap map;
    private android.app.FragmentManager fragmentManager;
    private android.app.FragmentTransaction fragmentTransaction;
    private FragmentImage fragment;
    public ClusterManager<MyItemContainer> mClusterManager;

    private GoogleApiClient client;

    private MaterialProgressBar progressBarMap;

    private HashMap<String, Bitmap> hashmapForBitmap = new HashMap<>();

    Intent i;
    ActivityOptions options;
    ImageView tempImageTransition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_main);

        checkPermission();

        initStatusBarAndGoogleMap();

        // Updates the location and zoom of the MapView

    }

    private void initStatusBarAndGoogleMap() {
        progressBarMap = (MaterialProgressBar) findViewById(R.id.progressBarMap);
        //add status bar

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.statbar_container_map, new StatusBarFragment())
                .commit();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void checkPermission() {
        // Gets to GoogleMap from the MapView and does initialization stuff
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);

            }
            Toast.makeText(this, "Error permission", Toast.LENGTH_LONG).show();
        }

    }

    public void clusterSettings() {
        //claster change
        mClusterManager = new ClusterManager<MyItemContainer>(this, map);
        mClusterManager.setRenderer(new CustomCluster(this, map, mClusterManager));
        mClusterManager.clearItems();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnCameraIdleListener(mClusterManager); //important
        map.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItemContainer>() {
            @Override
            public boolean onClusterClick(Cluster<MyItemContainer> cluster) {

                fragment = new FragmentImage(cluster);

                fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.animator.animation_fragment_up,
                                R.animator.animation_fragment_down,
                                R.animator.animation_fragment_up,
                                R.animator.animation_fragment_down
                        )
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();

                return false;
            }
        });
        //setting marker on map
        setUpMarker();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    map.setMyLocationEnabled(true);

                } else {

                }
                return;
            }

        }
    }

    private void setUpMarker() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mClusterManager.clearItems();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // String latitude_longitude = (String) messageSnapshot.child("UserData data: ").child("lat").getValue();
                    //String url = (String) messageSnapshot.child("url").getValue();
                    UserData user = messageSnapshot.child("User data: ").getValue(UserData.class);
                    Log.d("info:", user.lat + user.url);
                    String[] split = user.lat.split("/");

                    MyItemContainer offsetItem = new MyItemContainer(Double.parseDouble(split[0]), Double.parseDouble(split[1]), user.url, user.uid);//засунуть сюда ссылку на картинку
                    mClusterManager.addItem(offsetItem);
                    mClusterManager.setOnClusterItemClickListener(clusterClickListener);
                }
                progressBarMap.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Map Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());

    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // Customise the styling of the base map using a JSON object defined
        // in a raw resource file.
        boolean success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        clusterSettings();
        if (!success) {
            //fragment animation
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(Map.this,"click",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            Log.e("MapsActivityRaw", "Style parsing failed.");
        }


    }

    ClusterManager.OnClusterItemClickListener<MyItemContainer> clusterClickListener =new ClusterManager.OnClusterItemClickListener<MyItemContainer>() {
        @Override
        public boolean onClusterItemClick(MyItemContainer myItemContainer) {
            goToDescriptionActivity(myItemContainer);
            return true;
        }
    };

    private void goToDescriptionActivity(MyItemContainer myItemContainer) {
        Marker markerToChange = null;
        for (Marker marker : mClusterManager.getMarkerCollection().getMarkers()) {
            if (marker.getPosition().equals(myItemContainer.getPosition())) {
                markerToChange =marker;
                break;
            }
        }

        i = new Intent(this, MapUserMiniProfileActivity.class);//from where
        i.setAction(i.ACTION_VIEW);

        Bundle extras = new Bundle();
        extras.putString("url", myItemContainer.getUrl1());
        i.putExtras(extras);

        Projection projection = map.getProjection();
        Point screenPosition = projection.toScreenLocation(markerToChange.getPosition());


        tempImageTransition = (ImageView) findViewById(R.id.imageMapTransition);
        if (tempImageTransition.getVisibility() == View.INVISIBLE)
            tempImageTransition.setVisibility(View.VISIBLE);
        tempImageTransition.setImageBitmap(hashmapForBitmap.get(markerToChange.getId()));
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        tempImageTransition.setX(screenPosition.x - metrics.widthPixels/16);
        tempImageTransition.setY(screenPosition.y - metrics.heightPixels/8);
        tempImageTransition.setTransitionName("transition");

        Pair[] pairs;
        pairs = new Pair[]{
                Pair.create(tempImageTransition, "transition")};
            options = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    pairs);

        startActivity(i, options.toBundle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(tempImageTransition != null)
            if (tempImageTransition.getVisibility() == View.VISIBLE)
                tempImageTransition.setVisibility(View.INVISIBLE);
    }

    private class CustomCluster extends DefaultClusterRenderer<MyItemContainer> {

        private Context context;

        public CustomCluster(Context context, GoogleMap map, ClusterManager<MyItemContainer> clusterManager) {
            super(context, map, clusterManager);
            this.context = context;
        }

        @Override
        protected void onBeforeClusterItemRendered(final MyItemContainer item, final MarkerOptions markerOptions) {
            Glide.with(context)
                    .load(item.getUrl1())
                    .asBitmap()
                    .fitCenter()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into( new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            // let's find marker for this user
                            Marker markerToChange = null;
                            for (Marker marker : mClusterManager.getMarkerCollection().getMarkers()) {
                                if (marker.getPosition().equals(item.getPosition())) {
                                    markerToChange = marker;
                                    break;
                                }
                            }
                            // if found - change icon
                            if (markerToChange != null) {
                                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, metrics.widthPixels/8, metrics.heightPixels/8, true);
                                Bitmap circleBitmap = Bitmap.createBitmap(resized.getWidth(), resized.getHeight(), Bitmap.Config.ARGB_8888);

                                BitmapShader shader = new BitmapShader(resized, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                                Paint paint = new Paint();
                                paint.setShader(shader);
                                paint.setAntiAlias(true);
                                Canvas c = new Canvas(circleBitmap);
                                c.drawCircle(resized.getWidth() / 2, resized.getHeight() / 2, resized.getWidth() / 2, paint);

                                markerToChange.setIcon(BitmapDescriptorFactory.fromBitmap(circleBitmap));

                                hashmapForBitmap.put(markerToChange.getId(),circleBitmap);
                            }
                        }
                    });
        }

    }
}

