package com.example.study_buddy.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.study_buddy.Adapter.UserAdapter;
import com.example.study_buddy.LoginActivity;
import com.example.study_buddy.MainActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitClientInstance;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

import static com.example.study_buddy.LoginActivity.isValidEmail;
import static java.net.HttpURLConnection.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    /*
    Login status related constants
     */
    private static final String LOGIN_STATUS_SUCCESS = "Login successful";
    private static final String LOGIN_STATUS_FAILED_EMAIL = "Login failed, the email doesn't not exist";
    private static final String LOGIN_STATUS_INVALID_EMAIL = "Invalid email address";
    private static final String LOGIN_STATUS_FAILED_PASSWORD = "Login failed, the password doesn't not match";
    private static final String LOGIN_STATUS_IDLE = "";
    private static final String LOGIN_NO_CONNECTION = "Please check internet connection.";
    private static final String LOGIN_STATUS_BUG = "Client error, please contact Albert at albertyanyy@gmail.com";


    private static final String TAG = LoginActivity.class.getSimpleName();
    // TODO: Rename and change types of parameters
    private Button login;
    private EditText email, password;
    private TextView loginStatus;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        login = view.findViewById(R.id.btn_login);
        email = view.findViewById(R.id.et_email);
        password = view.findViewById(R.id.et_password);
        loginStatus = view.findViewById(R.id.tv_login_status);
        loginStatus.setText(LOGIN_STATUS_IDLE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                /*
                 * Check user inputs
                 */
                if (!isValidEmail(email.getText().toString())) {
                    loginStatus.setTextColor(Color.RED);
                    loginStatus.setText(LOGIN_STATUS_INVALID_EMAIL);
                }else{
                    // Make a post request
                    onButtonPressed();
                }
             }
        });
        return view;
    }

    private void onButtonPressed(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<User> call = service.postLoginUser(
                email.getText().toString(),
                password.getText().toString());

        call.enqueue(new Callback<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "get response" + response.raw());

                if (response.body()!= null) {
                    User user = response.body();
                    loginStatus.setTextColor(Color.GREEN);
                    loginStatus.setText(LOGIN_STATUS_SUCCESS);

                    Log.d(TAG, user.get_id());
                    /* Go to the main activity. Upon success
                     */
                    Intent intent = new Intent(Objects.requireNonNull(getView()).getContext(), MainActivity.class);
                    startActivity(intent);
                }else{
                    switch (response.code()){
                        case HTTP_BAD_REQUEST:
                        case HTTP_FORBIDDEN:{
                            loginStatus.setTextColor(Color.RED);
                            loginStatus.setText(LOGIN_STATUS_FAILED_PASSWORD);
                            break;
                        }
                        case HTTP_NOT_FOUND:{
                            loginStatus.setTextColor(Color.RED);
                            loginStatus.setText(LOGIN_STATUS_FAILED_EMAIL);
                            break;
                        }
                        default: {
                            Log.e(TAG, "Unknown exception!");
                            loginStatus.setTextColor(Color.RED);
                            loginStatus.setText(LOGIN_STATUS_BUG);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.toString());
                loginStatus.setTextColor(Color.RED);
                loginStatus.setText(LOGIN_NO_CONNECTION);

                // delay for 2 seconds
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

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
