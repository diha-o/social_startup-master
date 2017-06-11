package pc.dd.sex_startup.Main.Helpers;

import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import pc.dd.sex_startup.Main.Chat.chatRooms;
import pc.dd.sex_startup.R;

/**
 * Created by UserData on 14.08.2016.
 */

public class StatusBarFragment extends Fragment {
    private ImageView gotoChat;
    private ImageView createForm;
    private ImageView settings;
    private long mRotationSettingsAnimation = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_bar, container, false);

        settings = (ImageView) view.findViewById(R.id.statbar_settings);
        createForm = (ImageView) view.findViewById(R.id.statbar_form);
        gotoChat = (ImageView) view.findViewById(R.id.statbar_message);

        gotoChat.setClickable(true);
        gotoChat.setOnClickListener(goToChatClick);

        createForm.setClickable(true);
        createForm.setOnClickListener(goToCreateForm);

        settings.setClickable(true);
        settings.setOnClickListener(goToSettings);
        return view;
    }
    View.OnClickListener goToSettings = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRotationSettingsAnimation+=180;
            settings.animate().rotation(mRotationSettingsAnimation).setDuration(800).start();
        }
    };

    View.OnClickListener goToCreateForm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AnimatedVectorDrawable mBackDrawable = (AnimatedVectorDrawable) v.getContext().getDrawable(R.drawable.animated_vector_drawable_location);
            createForm.setImageDrawable(mBackDrawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBackDrawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationStart(Drawable drawable) {
                        super.onAnimationStart(drawable);
                    }

                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        AnimatedVectorDrawable mBackDrawable = (AnimatedVectorDrawable) createForm.getContext().getDrawable(R.drawable.animated_vector_drawable_location_close);
                        createForm.setImageDrawable(mBackDrawable);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mBackDrawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                                @Override
                                public void onAnimationStart(Drawable drawable) {
                                    super.onAnimationStart(drawable);
                                }

                                @Override
                                public void onAnimationEnd(Drawable drawable) {
                                    super.onAnimationEnd(drawable);

                                }
                            });
                            mBackDrawable.start();
                        }

                        super.onAnimationEnd(drawable);
                    }
                });
                mBackDrawable.start();
            }
        }
    };
    View.OnClickListener goToChatClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                    AnimatedVectorDrawable mBackDrawable = (AnimatedVectorDrawable) v.getContext().getDrawable(R.drawable.animated_vector_drawable_mail);
                    gotoChat.setImageDrawable(mBackDrawable);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mBackDrawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                            @Override
                            public void onAnimationStart(Drawable drawable) {
                                super.onAnimationStart(drawable);
                            }

                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                AnimatedVectorDrawable mBackDrawable = (AnimatedVectorDrawable) gotoChat.getContext().getDrawable(R.drawable.animated_vector_drawable_mail_close);
                                gotoChat.setImageDrawable(mBackDrawable);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    mBackDrawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                                        @Override
                                        public void onAnimationStart(Drawable drawable) {
                                            super.onAnimationStart(drawable);
                                        }

                                        @Override
                                        public void onAnimationEnd(Drawable drawable) {
                                            super.onAnimationEnd(drawable);
                                            Intent i = new Intent(getActivity().getApplicationContext(), chatRooms.class);
                                            startActivity(i);
                                        }
                                    });
                                    mBackDrawable.start();
                                }

                                super.onAnimationEnd(drawable);
                            }
                        });
                        mBackDrawable.start();
                    }
            };
    };
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            final int type = getArguments().getInt("visibility");
            final int icon = getArguments().getInt("icon");
            if (icon == 1) {
                setVisibility(createForm, type);
            } else if (icon == 2) {
                setVisibility(gotoChat, type);
            } else if (icon == 3) {
                setVisibility(settings, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setVisibility(View v, int visibility) {
        switch (visibility) {
            case View.GONE: {
                v.setVisibility(View.GONE);
            }
            case View.VISIBLE: {
                v.setVisibility(View.VISIBLE);
            }
        }
    }
}
