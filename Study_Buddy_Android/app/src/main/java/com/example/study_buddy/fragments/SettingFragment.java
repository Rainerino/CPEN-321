package com.example.study_buddy.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.study_buddy.App;
import com.example.study_buddy.LoadingActivity;
import com.example.study_buddy.LoginActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.model.AccessToken;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;


public class SettingFragment extends Fragment {
    private TextView username;
    private GoogleSignInClient mGoogleSignInClient;
    private AccessToken sendToBackEnd;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        SharedPreferences prefs;

        //get current user
        prefs = Objects.requireNonNull(getContext()).getSharedPreferences("",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("current_user", "");
        user = gson.fromJson(json, User.class);

        username = view.findViewById(R.id.username);
        username.setText(user.getFirstName());

        Button importCalendar = view.findViewById(R.id.ImportCalendar);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(App.getContext().getResources().getString(R.string.server_client_id))
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar.readonly"),
                        new Scope("https://www.googleapis.com/auth/userinfo.email"),
                        new Scope("https://www.googleapis.com/auth/userinfo.profile"),
                        new Scope(Scopes.DRIVE_APPFOLDER)
                )
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        importCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 236);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 236) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String authCode = account.getServerAuthCode();
            Log.d("AUTHCODEAUTHCODE!!!!!! ", authCode);

            GetDataService service = RetrofitInstance.getAccessTokenFromGoogle().create(GetDataService.class);

            Call<AccessToken> call = service.postGoogleAuthCode(authCode,
                    App.getContext().getResources().getString(R.string.server_client_id),
                    App.getContext().getResources().getString(R.string.server_client_secret),
                    "",
                    App.getContext().getResources().getString(R.string.grant_type),
                    "");

            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    Log.d( "ACCESSTOKENRAW", " " + response.raw());

                    if (response.body() != null) {
                        AccessToken a = response.body();

                        a.setEmail(account.getEmail());
                        a.setFirstName(account.getGivenName());
                        a.setLastName(account.getFamilyName());

                        sendToBackEnd = new AccessToken(a);

                        sendAccessTokenToBackEnd(sendToBackEnd);

                    }else{
                        Log.e("ACCESSTOKENERROR", " " + response.raw());
                        Toast.makeText(getContext(),"GCal Link Error",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.e("AUTHCODE POST FAILED ", t.toString());
                    Toast.makeText(getContext(),"GCal Link Error",Toast.LENGTH_SHORT).show();
                }
            });
            updateUI(account);
        } catch (ApiException e) {
            Log.e( "handleSignInResult:", " " + e);
            updateUI(null);
        }
    }

    private void  updateUI(GoogleSignInAccount account){
        if(account != null){
            Toast.makeText(getContext(),"Google Calendar Linked",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(),"GCal Link Failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void sendAccessTokenToBackEnd(AccessToken accessToken){
        GetDataService postToBackEnd = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
        Call<User> call = postToBackEnd.postAccessToken(
                user.getJwt(),
                accessToken.getAccessToken(),
                accessToken.getScope(),
                accessToken.getExpiresIn(),
                accessToken.getTokenType(),
                accessToken.getIdToken(),
                accessToken.getRefreshToken(),
                accessToken.getFirstName(),
                accessToken.getLastName(),
                accessToken.getEmail());

        Log.d("AccTok POSTED", " " + accessToken.getAccessToken());
        Log.d("JWTJWT POSTED", " " + user.getJwt());

        call.enqueue(new Callback<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("AccTok POST RESPONSE", " " + response.raw());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("AccessToken POST ERROR", t.toString());
                Toast.makeText(getContext(),"GCal Link Error",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(false);
        Button gsign_out = view.findViewById(R.id.LogOutButton);
        gsign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.LogOutButton:
                        onButtonClickSignOut();
                        break;
                    default: break;
                }
            }

            private void onButtonClickSignOut() {
                SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences(
                        "", MODE_PRIVATE);
                SharedPreferences.Editor editor  = prefs.edit();
                editor.putString("current_user", "");
                editor.putString("current_user_id", "");
                editor.apply();
                mGoogleSignInClient.signOut();
                // upon sign out, go to the login page
                Intent intent = new Intent(Objects.requireNonNull(getView()).getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
