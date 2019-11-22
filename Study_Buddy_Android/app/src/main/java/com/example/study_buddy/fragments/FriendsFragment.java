package com.example.study_buddy.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.AddFriendActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.adapter.NewUserAdapter;
import com.example.study_buddy.adapter.SelectUserAdapter;
import com.example.study_buddy.adapter.UserAdapter;
import com.example.study_buddy.helper.RecyclerItemTouchHelper;
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
    private RecyclerView newUserRecyclerView;


    private UserAdapter userAdapter;
    private NewUserAdapter newUserAdapter;
    private List<User> mUsers;
    private List<User> mNewUsers;
    private List<User> mSelectedUsers;
    private String cur_userId;
    private PopupWindow popupWindow;
    private SelectUserAdapter selectUserAdapter;

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
        User currentUser = gson.fromJson(user, User.class);
        cur_userId = currentUser.getid();

        Log.e("FriendFragment", cur_userId);

        mUsers = new ArrayList<>();
        mNewUsers = new ArrayList<>();

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


//        newUserRecyclerView = view.findViewById(R.id.suggested_friend_list);
//        newUserRecyclerView.setHasFixedSize(true);
//        newUserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        readSuggestedUsers();
//
//
//        newUserAdapter = new NewUserAdapter(getContext(), mNewUsers);
//        newUserRecyclerView.setAdapter(newUserAdapter);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow();
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.setFocusable(true);

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
                Toast.makeText(getContext(), "Please check internet connection",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    public void readGroup() {

        final GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

        Call<List<String >> call = service.getSuggestFriends(cur_userId);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                List<String> suggest_friend_list = response.body();
                assert suggest_friend_list != null;
                Log.d("FriendFragment", suggest_friend_list.toString());
                for(String friend : suggest_friend_list){
                   Call<User> get_user_call = service.getCurrentUser(friend);
                   get_user_call.enqueue(new Callback<User>() {
                       @Override
                       public void onResponse(Call<User> call, Response<User> response) {
                           mNewUsers.add(response.body());
                           newUserAdapter = new NewUserAdapter(getContext(), mNewUsers);
                           newUserRecyclerView.setAdapter(newUserAdapter);
                       }

                       @Override
                       public void onFailure(Call<User> call, Throwable t) {
                           Toast.makeText(getContext(), "Please check internet connection",
                                   Toast.LENGTH_LONG).show();
                       }
                   });
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Please check internet connection",
                        Toast.LENGTH_LONG).show();
            }
        });
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


        //editText = popupView.findViewById(R.id.search_user);
        create_btn = popupView.findViewById(R.id.next_btn);
        popup_recyclerView = popupView.findViewById(R.id.popup_user_list);
        popup_recyclerView.setHasFixedSize(true);
        popup_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        selectUserAdapter = new SelectUserAdapter(getContext(), mUsers);
        popup_recyclerView.setAdapter(selectUserAdapter);

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedUsers = selectUserAdapter.getSelectedUsers();
                popupWindow.dismiss();

                createGroup();

            }
        });

    }

    private void createGroup() {
        /**
         * 1. Create group
         * 2. Display group
         * */
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
