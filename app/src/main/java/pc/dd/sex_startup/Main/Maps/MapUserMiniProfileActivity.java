package pc.dd.sex_startup.Main.Maps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pc.dd.sex_startup.Main.Helpers.UserData;
import pc.dd.sex_startup.R;

/**
 * Created by dd.pc on 05.06.2017.
 */

public class MapUserMiniProfileActivity extends Activity {
    String url;
    ImageView profileImage;
    ImageView iconSend;
    ImageView iconLike;
    protected Boolean isAnimationBack =false;
    private UserData userData ;
    private String myUid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_user_map);
        //get image from back activity
        Bundle extras = getIntent().getExtras();
        url = extras.getString("url");
        myUid = extras.getString("myUid");

        iconSend = (ImageView) findViewById(R.id.icon_send_pop);
        iconLike = (ImageView) findViewById(R.id.icon_like_pop);
        iconSend.setOnClickListener(iconSendOnclick);

        profileImage = (ImageView) findViewById(R.id.image_mini_user_profile);
        postponeEnterTransition();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            userData = new UserData();
            fast_get_innfouser(FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        }
        loadImage();
    }
    View.OnClickListener iconSendOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AnimatedVectorDrawable mBackDrawable = (AnimatedVectorDrawable)
                    getDrawable(R.drawable.animated_vector_drawable_mail);
            iconSend.setImageDrawable(mBackDrawable);
            mBackDrawable.start();
            isAnimationBack = true;

            if(userData.getNickname()!=null)
                start_new_act(userData, myUid);

        }
    };

    private void  start_new_act(UserData me, String uid){
        Intent intent = new Intent(getApplicationContext(),pc.dd.sex_startup.Main.Chat.mainChat.class);
        intent.putExtra("second_uid",uid);
        intent.putExtra("user_name",me.getNickname());
        intent.putExtra("img_prof",me.getUrl());
        intent.putExtra("root", FirebaseDatabase.getInstance().getReference(uid).toString());
        intent.putExtra("from_chatRooms","0");

        startActivity(intent);
        this.overridePendingTransition(R.animator.animation_act_left,R.animator.animation_act_right);
        this.finish();
    }
    private void loadImage() {
        Glide.with(getApplicationContext())
                .load(url)
                .asBitmap()
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        DisplayMetrics metrics = MapUserMiniProfileActivity.this.getResources().getDisplayMetrics();
                        Bitmap resized = Bitmap.createScaledBitmap(bitmap, metrics.widthPixels / 2, metrics.heightPixels / 2, true);
                        Bitmap circleBitmap = Bitmap.createBitmap(resized.getWidth(), resized.getHeight(), Bitmap.Config.ARGB_8888);

                        BitmapShader shader = new BitmapShader(resized, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                        Paint paint = new Paint();
                        paint.setShader(shader);
                        paint.setAntiAlias(true);
                        Canvas c = new Canvas(circleBitmap);
                        c.drawCircle(resized.getWidth() / 2, resized.getHeight() / 2, resized.getWidth() / 2, paint);

                        profileImage.setImageBitmap(circleBitmap);
                        startPostponedEnterTransition();
                    }
                });
    }

    @Override
    protected void onResume() {
        if((isAnimationBack)&&(iconSend.getDrawable()!=null)){
            AnimatedVectorDrawable mBackDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.animated_vector_drawable_mail_close);
            iconSend.setImageDrawable(mBackDrawable);
            mBackDrawable.start();
            isAnimationBack =false;
        }
        super.onResume();
    }

    private void fast_get_innfouser(DatabaseReference myRef) {

        final Integer[] temp = {1};
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    if (temp[0] == 1) {

                        //  userData[0] = messageSnapshot.child("UserData data: ").getValue(UserData.class);
                        String url = (String) messageSnapshot.child("url").getValue();
                        String nickname = (String) messageSnapshot.child("nickname").getValue();
                        userData.setNickname(nickname);
                        userData.setUrl(url);

                        temp[0] = 2;
                        continue;
                    } else {
                    }
                    ;

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapUserMiniProfileActivity.this,"Error "+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
