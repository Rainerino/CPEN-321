package com.example.study_buddy.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.study_buddy.LoadingActivity;
import com.example.study_buddy.LoginActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.gson.Gson;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.study_buddy.LoginActivity.isValidEmail;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;


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
    private EditText email;
    private EditText password;
    private TextView loginStatus;
    private SharedPreferences data;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button login = view.findViewById(R.id.btn_login);
        email = view.findViewById(R.id.login_email);
        password = view.findViewById(R.id.login_password);
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
        GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

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
                    user.setJwt(response.headers().get("Authorization"));

                    loginStatus.setTextColor(Color.GREEN);
                    loginStatus.setText(LOGIN_STATUS_SUCCESS);

                    /*Save the current user id*/
                    data = Objects.requireNonNull(getContext()).getSharedPreferences(
                            "", Context.MODE_PRIVATE);
                    editor  = data.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(user); // myObject - instance of MyObject
                    Log.e(TAG, "onResponse: "+ json);
                    editor.putString("current_user", json);
                    editor.putString("current_user_id", user.getid());
                    editor.apply();

                    Log.d("JWTJWTJWTJWTJWT", user.getJwt());
                    /* Go to the main activity. Upon success
                     */
                    Intent intent = new Intent(Objects.requireNonNull(getView()).getContext(), LoadingActivity.class);
                    startActivity(intent);
                }else{
                    switch (response.code()){
                        case HTTP_BAD_REQUEST: break;
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
                            break;
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
                        Toast.makeText(getContext(), "Please try again later",
                                Toast.LENGTH_LONG).show();
                    }
                }, 2000);

            }
        });

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
