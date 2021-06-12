package com.example.eventsapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsapp.R;
import com.example.eventsapp.models.ChatMessage;
import com.example.eventsapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.core.Context;
import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

import static com.example.eventsapp.MainActivity.TAG;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public ImageView userProfileImage;
    //private Context mContext;
    private List<ChatMessage> mChat;

    FirebaseUser fUser;
    private Context mContext;

    public MessageAdapter(Context mContext, List<ChatMessage> mChat){
        this.mChat = mChat;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if(viewType == MSG_TYPE_RIGHT) {
           View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_me, parent, false);
           return new MessageAdapter.ViewHolder(view);
       }
       else{
           View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_other, parent, false);
           return new MessageAdapter.ViewHolder(view);
       }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapter.ViewHolder holder, int position) {
        ChatMessage chat = mChat.get(position);

        holder.show_message.setText(chat.getMessageText());
        holder.chat_user.setText(chat.getMessageUser());

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public TextView chat_user;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            chat_user = itemView.findViewById(R.id.chat_user);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "Yo yo gabba gabba " + fUser.getUid());
        if(mChat.get(position).getMessageUserID().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
