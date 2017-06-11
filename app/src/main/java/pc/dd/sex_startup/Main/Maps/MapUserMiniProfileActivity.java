package pc.dd.sex_startup.Main.Maps;

import android.app.Activity;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_user_map);
        //get image from back activity
        Bundle extras = getIntent().getExtras();
        url = extras.getString("url");

        iconSend = (ImageView) findViewById(R.id.icon_send_pop);
        iconLike = (ImageView) findViewById(R.id.icon_like_pop);
        iconSend.setOnClickListener(iconSendOnclick);

        profileImage = (ImageView) findViewById(R.id.image_mini_user_profile);
        postponeEnterTransition();
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

        }
    };
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
}
