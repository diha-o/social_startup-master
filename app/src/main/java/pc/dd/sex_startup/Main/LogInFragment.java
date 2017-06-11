package pc.dd.sex_startup.Main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;

import pc.dd.sex_startup.Main.Maps.Map;
import pc.dd.sex_startup.R;

/**
 * Created by UserData on 22.07.2016.
 */

public class LogInFragment extends Fragment {

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private EditText inputEmail, inputPassword;
    private Button btn_logIn;
    public FirebaseAuth auth;
    private ProgressDialog progress;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btn_logIn = (Button) view.findViewById(R.id.btn_log_in);
        inputEmail = (EditText) view.findViewById(R.id.log_in_email);
        inputPassword = (EditText) view.findViewById(R.id.log_in_pass);


        btn_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progress = new ProgressDialog(getActivity());
               // progress.setTitle("Log in");
                progress.setMessage("Wait while we try log in..");
                progress.setCancelable(false);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.show();
                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progress.dismiss();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 4) {
                                        inputPassword.setError("Please write minimum 4 letters");
                                    } else {
                                        Toast.makeText(getActivity(), "Autification error", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent i = new Intent(getActivity(), Map.class);
                                    startActivity(i);
                                    getActivity().finish();
                                }
                            }
                        });
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_in,container,false);
    }
}
