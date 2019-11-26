package com.example.study_buddy.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.AddFriendActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.adapter.GroupAdapter;
import com.example.study_buddy.adapter.SelectUserAdapter;
import com.example.study_buddy.adapter.UserAdapter;
import com.example.study_buddy.helper.RecyclerItemTouchHelper;
import com.example.study_buddy.model.Group;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class FriendsFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private RecyclerView recyclerView;
    private RecyclerView popup_recyclerView;
    private RecyclerView group_recyclerView;


    private UserAdapter userAdapter;
    private GroupAdapter groupAdapter;
    private List<User> mUsers;
    private List<Group> mGroups;
    private List<User> mSelectedUsers;
    private List<User> filteredUsers;
    private String cur_userId;
    private PopupWindow popupWindow;
    private PopupWindow deletePopup;
    private SelectUserAdapter selectUserAdapter;
    private User cur_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(
                "",MODE_PRIVATE);
        String user = sharedPref.getString("current_user", "");
        Gson gson = new Gson();
        cur_user = gson.fromJson(user, User.class);
        cur_userId = cur_user.getid();

        Log.e("FriendFragment", cur_userId);

        mUsers = new ArrayList<>();
        mGroups = new ArrayList<>();
        filteredUsers = new ArrayList<>();

        recyclerView = view.findViewById(R.id.friend_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        readUsers();

        userAdapter = new UserAdapter(getContext(), mUsers);
        recyclerView.setAdapter(userAdapter);


        group_recyclerView = view.findViewById(R.id.group_list);
        group_recyclerView.setHasFixedSize(true);
        group_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        groupAdapter = new GroupAdapter(getContext(), mGroups);
        group_recyclerView.setAdapter(groupAdapter);

        readGroup();

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow();
        popupWindow.setWidth(width);
        popupWindow.setHeight(1200);
        popupWindow.setFocusable(true);

        deletePopup = new PopupWindow();
        deletePopup.setWidth(width);
        deletePopup.setHeight(height);
        deletePopup.setOutsideTouchable(false);


        return view;
    }



    private  void readUsers() {
        GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

        Log.d("Friend fragment", cur_userId);
        Call<List<User>> call = service.getFriends(cur_userId);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.body() != null) {
                    for(User user: response.body()){
                        mUsers.add(user);
                    }
                    userAdapter = new UserAdapter(getContext(), mUsers);
                    recyclerView.setAdapter(userAdapter);
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Can't get friends. Please check internet connection",
                        Toast.LENGTH_LONG).show();
                Log.e("Get Friend List", "onFailure: " + t.toString() );
            }
        });

    }

    public void readGroup() {

        final GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

        if (!cur_user.getGroupList().isEmpty()) {
            for (String groupId : cur_user.getGroupList()) {
                Call<Group> call = service.getGroup(groupId);
                call.enqueue(new Callback<Group>() {
                    @Override
                    public void onResponse(Call<Group> call, Response<Group> response) {
                        if (response.isSuccessful()) {
                            Group group = response.body();
                            mGroups.add(group);
                            groupAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getContext(), response.message(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Group> call, Throwable t) {
                        Toast.makeText(getContext(), t.toString(),
                                Toast.LENGTH_LONG).show();
                        Log.e("getting group", "onFailure: " + t.toString());
                    }
                });
            }
        }
    }

    private void showCreateGroupPopup(View view) {
        LayoutInflater inflater = (LayoutInflater)
                view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.create_group_popup, null);
        popupWindow.setContentView(popupView);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, -100);

        //EditText editText;
        Button create_btn;
        mSelectedUsers = new ArrayList<>();


        EditText search_bar = popupView.findViewById(R.id.search_user);
        create_btn = popupView.findViewById(R.id.next_btn);
        popup_recyclerView = popupView.findViewById(R.id.popup_user_list);
        popup_recyclerView.setHasFixedSize(true);
        popup_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(!mUsers.isEmpty()){
            for(User user : mUsers){
                filteredUsers.add(user);
            }
        }
        selectUserAdapter = new SelectUserAdapter(getContext(), filteredUsers);
        popup_recyclerView.setAdapter(selectUserAdapter);

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedUsers = selectUserAdapter.getSelectedUsers();
                popupWindow.dismiss();

                createGroup();

            }
        });

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    InitUser();
                }
                else {
                    searchUser(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void InitUser() {
        filteredUsers.clear();
        if(!mUsers.isEmpty()){
            for(User user : mUsers){
                filteredUsers.add(user);
            }
        }
        selectUserAdapter.notifyDataSetChanged();
    }

    private void searchUser(String s) {
        filteredUsers.clear();
        for(User user : mUsers) {
            if(user.getFirstName().toLowerCase().contains(s.toLowerCase())){
                filteredUsers.add(user);
            }
        }

        selectUserAdapter.notifyDataSetChanged();
    }

    private void showDeleteConfirm(User user, int position) {

        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.delete_confirm_popup, null);
        deletePopup.setContentView(popupView);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        deletePopup.showAtLocation(this.getView(), Gravity.CENTER, 0, -100);

        TextView message = popupView.findViewById(R.id.confirm_message);
        Button delete_btn = popupView.findViewById(R.id.delete_btn);
        Button cancel_btn = popupView.findViewById(R.id.cancel_btn);
        String confirm_message = "Delete " + user.getFirstName() + " from your contacts?";
        message.setText(confirm_message);

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAdapter.removeUser(position);
                //TODO: delete request
                deletePopup.dismiss();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAdapter.restoreItem(position);
                deletePopup.dismiss();
            }
        });


    }

    private void createGroup() {
        /**
         * 1. Create group
         * 2. Display group
         * */
        List<User> groupMember = selectUserAdapter.getSelectedUsers();
        Log.e("Selected members", "createGroup: " + groupMember );
        if(groupMember.isEmpty()){
            Toast.makeText(getContext(), "Can't create a group without any group member.",
                    Toast.LENGTH_LONG).show();
        }
        popupWindow.dismiss();

    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof UserAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            User user = mUsers.get(position);
            showDeleteConfirm(user, position);

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.add_friend :
                Intent intent = new Intent(getContext(), AddFriendActivity.class);
                startActivity(intent);
                return true;
            case R.id.create_group : showCreateGroupPopup(getView());
                return true;
        }
        return true;
    }
}
