package com.example.study_buddy.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.study_buddy.LoginActivity;
import com.example.study_buddy.MainActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.study_buddy.LoginActivity.isStringOnlyAlphabet;
import static com.example.study_buddy.LoginActivity.isValidEmail;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String TAG = LoginActivity.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SIGNUP_STATUS_IDLE = "";
    private static final String SIGNUP_NO_CONNECTION = "Please check internet connection. ";
    private static final String SIGNUP_STATUS_BUG = "Client error, please contact Albert at albertyanyy@gmail.com. ";
    private static final String SIGNUP_STATUS_INVALID_EMAIL = "Invalid email address. ";
    private static final String SIGNUP_STATUS_INVALID_USERNAME = "User name can only contains characters. ";
    private static final String SIGNUP_STATUS_INVALID_PASSWORD_SHORT = "Password has to be at least 8 characters. ";
    private static final String SIGNUP_STATUS_INVALID_PASSWORD_LONG = "Password has to be at least 16 characters. ";
    private static final String SIGNUP_STATUS_INVALID_REPEAT_PASSWORD = "Unmatched password, please enter again. ";
    private static final String SIGNUP_STATUS_EMAIL_ALREADY_TAKE = "Email address already in use. ";
    private static final String SIGNUP_STATUS_SUCCESS = "Sign up successful ";
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 16;

    // TODO: Rename and change types of parameters
    private TextView signup_status;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText repeatedPassword;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        signup_status = view.findViewById(R.id.tv_signup_status);
        firstName = view.findViewById(R.id.et_first_name);
        lastName = view.findViewById(R.id.et_last_name);
        email = view.findViewById(R.id.signup_email);
        password = view.findViewById(R.id.signup_password);
        repeatedPassword = view.findViewById(R.id.et_repassword);

        Button signUpButton = view.findViewById(R.id.btn_signup);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Check if the inputs are valid.
                 */
                signup_status.setTextColor(Color.RED);
                signup_status.setText(SIGNUP_STATUS_IDLE);
                if (!isValidEmail(email.getText().toString())){
                    signup_status.append(SIGNUP_STATUS_INVALID_EMAIL);
                }
                if ( !(isStringOnlyAlphabet(firstName.getText().toString())
                        && isStringOnlyAlphabet(lastName.getText().toString()))){
                    signup_status.append(SIGNUP_STATUS_INVALID_USERNAME);
                }
                if (!password.getText().toString().equals(repeatedPassword.getText().toString())){
                    signup_status.append(SIGNUP_STATUS_INVALID_REPEAT_PASSWORD);
                }
                if (password.getText().toString().length() <= PASSWORD_MIN_LENGTH) {
                    signup_status.append(SIGNUP_STATUS_INVALID_PASSWORD_SHORT);
                }
                if (password.getText().toString().length() > PASSWORD_MAX_LENGTH) {
                    signup_status.append(SIGNUP_STATUS_INVALID_PASSWORD_LONG);
                }

                if (signup_status.getText().toString().isEmpty()){
                    onButtonPressed();
                }
                Log.d(TAG, "invalid user inputs");
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    private void onButtonPressed() {
        GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

        Call<User> call = service.postSignupUser(
                firstName.getText().toString(),
                lastName.getText().toString(),
                email.getText().toString(),
                password.getText().toString());

        call.enqueue(new Callback<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "get response" + response.raw());

                if (response.body() != null) {
                    User user = response.body();
                    signup_status.setTextColor(Color.GREEN);
                    signup_status.setText(SIGNUP_STATUS_SUCCESS);

                    Log.d(TAG, user.getid());

                    /* Go to the main activity. Upon success
                     */
                    Intent intent = new Intent(Objects.requireNonNull(getView()).getContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    switch (response.code()) {
                        case HTTP_FORBIDDEN: {
                            signup_status.setText(SIGNUP_STATUS_EMAIL_ALREADY_TAKE);
                            break;
                        }
                        case HTTP_BAD_REQUEST: break;
                        default: {
                            Log.e(TAG, "Unknown exception!");
                            signup_status.setText(SIGNUP_STATUS_BUG);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.toString());
                signup_status.setText(SIGNUP_NO_CONNECTION);

                // delay for 2 seconds
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Please try again later",
                                Toast.LENGTH_LONG).show();
                    }
                }, 2000);

            }
        });
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        OnFragmentInteractionListener mListener;
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
