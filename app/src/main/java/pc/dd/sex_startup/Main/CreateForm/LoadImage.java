package pc.dd.sex_startup.Main.CreateForm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import pc.dd.sex_startup.R;

;

/**
 * Created by UserData on 30.07.2016.
 */

public class LoadImage extends Activity {

    private CropImageView mCropView;
    private ImageView load_img, clear_img, openCamera;
    private Boolean click = true;
    static Uri file_url = null;
    private ImageView next_step = null;
    private  ViewGroup parentView;
    private long rotationOpenCameraAnimation;
    private LinearLayout linearLayout_toLoadImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_image_act);
        parentView = (ViewGroup) findViewById(R.id.constraintLayout3);

        next_step = (ImageView) findViewById(R.id.next_step);

        next_step.setAlpha(0.2f);
        next_step.setClickable(false);

        mCropView = (CropImageView) findViewById(R.id.cropImageView);
        clear_img = (ImageView) findViewById(R.id.clear);
        load_img = (ImageView) findViewById(R.id.chose_img);
        openCamera = (ImageView) findViewById(R.id.open_cameta);

        openCamera.setOnClickListener(openCameraOnClick);

        load_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simple_animation_img(v);
                if (click) {
                    pickImage();
                    click = false;
                } else {
                    cropImage(mCropView);
                }
            }
        });
        clear_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simple_animation_img(v);
                clear_click();
            }
        });
        next_step.setOnClickListener(nextStepOnClick);

    }

    View.OnClickListener nextStepOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            linearLayout_toLoadImg = new LinearLayout(LoadImage.this);
            linearLayout_toLoadImg.setId(R.id.layout_temp);
            linearLayout_toLoadImg.setClickable(true);
            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            parentView.addView(linearLayout_toLoadImg, relativeParams);

            CameraFoto cameraFoto = new CameraFoto();
            final android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(linearLayout_toLoadImg.getId(), cameraFoto, "cameraFoto")
                    .addToBackStack("cameraFoto")
                    .commit();

        }
    };
    View.OnClickListener openCameraOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            rotationOpenCameraAnimation+=90;
            v.animate().rotation(rotationOpenCameraAnimation).setDuration(800).start();
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                clear_click();
                return;
            }
            try {
                InputStream inputStream = LoadImage.this.getContentResolver().openInputStream(data.getData());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                //get img storage
                file_url = data.getData();
                mCropView.setImageUriAsync(file_url);
                //if load
                mCropView.setOnSetImageUriCompleteListener(new CropImageView.OnSetImageUriCompleteListener() {
                    @Override
                    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
                        load_img.setImageResource(R.drawable.ic_done_white_24dp);
                        clear_img.setVisibility(View.VISIBLE);
                    }
                });
                Log.d("\npath:----------\n-", file_url.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void cropImage(CropImageView v) {
        try {
            if (file_url == null) {
                clear_click();
            } else {
                final Uri myUri = Uri.fromFile(new File(getExternalCacheDir(), "meeImage.jpeg"));
                // Bitmap cropped = v.getCroppedImage();
                v.setOnSaveCroppedImageCompleteListener(new CropImageView.OnSaveCroppedImageCompleteListener() {
                    @Override
                    public void onSaveCroppedImageComplete(CropImageView view, Uri uri, Exception error) {
                        Toast.makeText(getApplicationContext(), "Cropped", Toast.LENGTH_SHORT).show();
                        Log.d("saved", "img saved");
                        file_url = uri;
                        //next step true
                        next_step.setAlpha(0.9f);
                        next_step.setClickable(true);
                    }
                });
                v.saveCroppedImageAsync(myUri);
                Log.d("Uri-----", myUri.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clear_click() {
        mCropView.setImageResource(android.R.color.transparent);//null img
        click = true;//clik lamp
        clear_img.setVisibility(View.GONE);//gone clear img
        load_img.setImageResource(R.drawable.ic_crop_original_white_24dp);
        next_step.setAlpha(0.2f);
        next_step.setClickable(false);
    }

    private void simple_animation_img(final View v) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.8f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.8f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(50);
        animSet.playTogether(scaleDownX, scaleDownY);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 0.8f, 1f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 0.8f, 1f);
                AnimatorSet animSet = new AnimatorSet();
                animSet.setDuration(50);
                animSet.playTogether(scaleDownX, scaleDownY);
                animSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void pickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        }
    }
}