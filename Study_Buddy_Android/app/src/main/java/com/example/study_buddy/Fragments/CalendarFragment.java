package com.example.study_buddy.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.study_buddy.Adapter.SelectUserAdapter;
import com.example.study_buddy.Adapter.UserAdapter;
import com.example.study_buddy.MainActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitClientInstance;

import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class CalendarFragment extends Fragment {

    private AppCompatButton schedule_meeting_btn;
    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private RelativeLayout relativeLayout;
    private Dialog mDialog;
    private RecyclerView recyclerView;
    private SelectUserAdapter selectUserAdapter;
    private List<User> mAvailableUsers;
    private List<User> mSelectedUsers;
    private SharedPreferences prefs;
    private String cur_userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        schedule_meeting_btn = view.findViewById(R.id.schedule_meeting_btn);
        mDialog = new Dialog(this.getContext());

        //get current user id
        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(
                "",MODE_PRIVATE);
        cur_userId = prefs.getString("current_user_id","");

        schedule_meeting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showScheduleMeetingStartUp(view);
            }
        });


        return view;
    }

    private void showScheduleMeetingStartUp(View view) {

        mDialog.setContentView(R.layout.schedule_meeting_startup);
        EditText editText;
        ImageButton next_btn;
        Toolbar toolbar;
        mAvailableUsers = new ArrayList<>();
        mSelectedUsers = new ArrayList<>();


        editText = mDialog.findViewById(R.id.search_user);
        next_btn = mDialog.findViewById(R.id.next_btn);
        toolbar = mDialog.findViewById(R.id.toolbar);
        recyclerView = mDialog.findViewById(R.id.available_user_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getAvailableUsers();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedUsers = selectUserAdapter.getSelectedUsers();
                getMeetingDetails(v);

            }
        });

        mDialog.show();
    }

    private void getAvailableUsers(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        /***use the getFriend request for now, will change to getAvailableFriend request when backend's ready***/
        Call<List<User>> call = service.getFriends(cur_userId);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                for(User user: response.body()){
                    mAvailableUsers.add(user);
                }
                selectUserAdapter = new SelectUserAdapter(getContext(), mAvailableUsers);
                recyclerView.setAdapter(selectUserAdapter);

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }

    private void getMeetingDetails(View view){
        mDialog.setContentView(R.layout.schedule_meeting_details);
        ImageButton back_btn;
        TextView meeting_member;
        Button submit_btn;

        back_btn = mDialog.findViewById(R.id.back_btn);
        meeting_member = mDialog.findViewById(R.id.member_names);
        submit_btn = mDialog.findViewById(R.id.submit_btn);

        String members = "";
        if(!mSelectedUsers.isEmpty()){
            for(User user : mSelectedUsers) {
                members += user.getFirstName() + ",  ";
            }
        }

        meeting_member.setText(members);



        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScheduleMeetingStartUp(v);
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * 1. check if all the fields are filled
                * 2. if all field, create event object and store all the details
                * 3. call putEvent request
                * 4. send notifications to invited friends
                * */
            }
        });
    }

//    private  static final String TAG = "CalendarActivity";
//    private CalendarView mCalendarView;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.calendar_layout);
//        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
//        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(CalendarView CalendarView, int year, int month, int dayOfMonth) {
//                String date = year + "/" + month + "/"+ dayOfMonth ;
//                Log.d(TAG, "onSelectedDayChange: yyyy/mm/dd:" + date);
//                Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
//                intent.putExtra("date",date);
//                startActivity(intent);
//
//            }
//        });

    }
