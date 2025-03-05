package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;

    // 定义不同的 ViewHolder
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userMessage;

        public UserViewHolder(View view) {
            super(view);
            userMessage = view.findViewById(R.id.tv_user_message);
        }
    }

    public static class ModelViewHolder extends RecyclerView.ViewHolder {
        public TextView modelMessage;

        public ModelViewHolder(View view) {
            super(view);
            modelMessage = view.findViewById(R.id.tv_model_message);
        }
    }

    public MessageAdapter(List<Message> messages) {
        this.messageList = messages;
    }

    // 根据消息类型返回对应的 ViewType
    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getSenderType();
    }

    // 创建不同类型的 ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Message.TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_message, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_model_message, parent, false);
            return new ModelViewHolder(view);
        }
    }

    // 绑定数据到 ViewHolder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).userMessage.setText(message.getContent());
        } else if (holder instanceof ModelViewHolder) {
            ((ModelViewHolder) holder).modelMessage.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}