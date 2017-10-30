package com.vasyaevstropov.runmanager.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vasyaevstropov.runmanager.Models.ChatMessage;
import com.vasyaevstropov.runmanager.R;

import java.util.ArrayList;


public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerChatAdapter.ChatViewHolder> {

    private ArrayList<ChatMessage> messages;
    private LayoutInflater layoutInflater;
    public RecyclerChatAdapter(Context context, ArrayList<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.chat_card, null);
        ChatViewHolder viewHolder = new ChatViewHolder(view);
        return null;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage message= messages.get(position);
        holder.tvLogin.setText(message.getUserName());
        holder.tvTime.setText(String.valueOf(message.getMessageTime()));
        holder.tvMessage.setText(message.getMessageText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        public TextView tvLogin;
        public TextView tvMessage;
        public TextView tvTime;


        public ChatViewHolder(View itemView) {
            super(itemView);
            tvLogin = (TextView)itemView.findViewById(R.id.tvLogin);
            tvMessage=(TextView)itemView.findViewById(R.id.tvMessage);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);

        }
    }
}
