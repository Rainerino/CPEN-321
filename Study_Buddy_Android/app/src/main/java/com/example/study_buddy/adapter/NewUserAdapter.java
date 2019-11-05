package com.example.study_buddy.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.R;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NewUserAdapter extends RecyclerView.Adapter<NewUserAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mUser;


    public NewUserAdapter(Context mContext, List<User> mUser){
        this.mUser = mUser;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public NewUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.new_user_item, parent, false);
        return new NewUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewUserAdapter.ViewHolder holder, int position) {
        final User user = mUser.get(position);
        String name = user.getFirstName() + " " + user.getLastName();

        SharedPreferences prefs = mContext.getSharedPreferences(
                "",
                MODE_PRIVATE);

        final String cur_userId = prefs.getString(
                "current_user_id",
                "");

        holder.username.setText(name);
        holder.profile_img.setImageResource(R.drawable.ic_profile_pic_name);

        holder.add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Toast.makeText(view.getContext(), "TRY PUT REQUEST",
                        Toast.LENGTH_LONG).show();
                GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
                Call<User> call = service.addFriend(cur_userId, user.getid());
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Toast.makeText(view.getContext(),"new User put",Toast.LENGTH_LONG).show();
                        holder.add_button.setText("Request Sent");
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        holder.username.setText(t.toString());
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_img;
        public Button add_button;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_img = itemView.findViewById(R.id.profile_image);
            add_button = itemView.findViewById((R.id.add_btn));
        }
    }
}
