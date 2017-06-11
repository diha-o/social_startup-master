package pc.dd.sex_startup.Main;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import pc.dd.sex_startup.R;

public class LogIn extends AppCompatActivity {

    FrameLayout layout;
    float pixelDensity;
    int cy,cx =0;
    int x,y,hypotenuse,duration,height,width;
    FrameLayout next_layout;
    ConstraintLayout bgrd;
    ConstraintLayout constaintLayout;
    FloatingActionButton fab;


    private LogInFragment fragment_logIn;
    private RegisterFragment fragment_register;
    private FragmentManager fragmentManager;
    private Boolean btn_click = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        layout = (FrameLayout) findViewById(R.id.container_login);
        constaintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout) ;

        fragmentManager = getSupportFragmentManager();
        fragment_logIn = new LogInFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container_login, fragment_logIn )
                .commit();

        fab = (FloatingActionButton)findViewById(R.id.floatingActionButton);
       // fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
        fab.setImageAlpha(150);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_click) {
                    setAnimation(fab);
                    btn_click = false;
                }
            }
        });



    }

    @Override
    public void onBackPressed() {
        if(!btn_click){
            deleteAnimation(fab);
            btn_click = true;
        }else{
            super.onBackPressed();
        }
    }

    public void deleteAnimation(final View v){
        Animator anim = ViewAnimationUtils.createCircularReveal(next_layout, width / 2, cy,  hypotenuse ,28 * pixelDensity);
        anim.setDuration(duration);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fragmentManager.beginTransaction()
                        .detach(fragment_register)
                        .commit();
                bgrd.removeAllViews();
                constaintLayout.removeView(bgrd);
                v.setVisibility(View.VISIBLE);
                v.animate()
                        .translationY(0f)
                        .translationX(0f)
                        .setDuration(duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .setListener(null)
                        .start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();


    }



    public void setAnimation(final View v){
        //set params to start animation
        fragment_register = new RegisterFragment();
        duration = 300;
        bgrd=null;
        next_layout=null;
        pixelDensity = getResources().getDisplayMetrics().density;
        width  = layout.getWidth();
        height = layout.getHeight();
        x = width / 2;
        y = height / 2;
        cy = (layout.getTop() + layout.getBottom()) / 2;
        cx = (layout.getLeft() + layout.getRight()) / 2;
        hypotenuse = (int) Math.hypot(x, cy);
        x = (int) (x - ((16 * pixelDensity) + (28 * pixelDensity)));
        ////end set params
        // where pixelDensity is to translate dp into pixels

       ConstraintLayout.LayoutParams parameters2 = new ConstraintLayout.LayoutParams(constaintLayout.getLayoutParams());
        bgrd = new ConstraintLayout(LogIn.this);
        bgrd.setLayoutParams(parameters2);
        constaintLayout.addView(bgrd); // add fon


        FrameLayout.LayoutParams parameters =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);

        next_layout = new FrameLayout(LogIn.this);
        next_layout.setLayoutParams(parameters);
        next_layout.requestLayout();
        next_layout.setClickable(true);
        next_layout.setVisibility(View.GONE);
        next_layout.setId(R.id.layout_temp2);


        fragmentManager.beginTransaction()
                .add(next_layout.getId(),fragment_register)
                .commit();
        bgrd.addView(next_layout); // add register fragment

        v.animate()
                .translationY(y)
                .translationX(-x)
                .setDuration(duration)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {


                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                next_layout.setVisibility(View.VISIBLE);
                                Animator anim = ViewAnimationUtils.createCircularReveal(next_layout, width / 2, cy, 28 * pixelDensity, hypotenuse);
                                anim.setDuration(duration);
                                v.setVisibility(View.GONE);
                                bgrd.setBackgroundColor(Color.BLACK);
                                bgrd.getBackground().setAlpha(180);
                                anim.start();
                            }
                        }, duration-100);
                    };

                    @Override
                    public void onAnimationEnd(Animator animation) {
                    layout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();


    }
    /*
    private void change_fragment(Fragment fragment,Boolean lamp){
        if (!lamp)
        {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.animator.animation_fragment_second_right,
                            R.animator.animation_fragment_right,
                            R.animator.animation_fragment_left,
                            R.animator.animation_fragment_second_left
                    )
                    .replace(R.id.container_login, fragment )
                    .commit();
        }else
        if (lamp){
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.animator.animation_fragment_left,
                            R.animator.animation_fragment_second_left,
                            R.animator.animation_fragment_second_right,
                            R.animator.animation_fragment_right
                    )
                    .replace(R.id.container_login, fragment )
                    .commit();
        }

    }

    private void set_up_animator_v2(View v,Boolean lamp){
        if(lamp){
            rotation+=540;
        }else rotation=0;

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v,"rotation",rotation)
                .setDuration(300);
                objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                objectAnimator.start();

    }
*/
}
