package com.androidapp.pizzamania;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatMsgAdapter> {

    public ChatAdapter(@NonNull Context context, @NonNull List<ChatMsgAdapter> messages){
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatMsgAdapter msg = getItem(position);
        View view;

        if (msg.isUser()) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.user_chat, parent, false);
            TextView textView = view.findViewById(R.id.chatMsg);
            textView.setText(msg.getMessage());
        }else{
            if (msg.getMessage().startsWith(("[PIZZA_CARD]"))){
                view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(msg.getMessage().replace("[PIZZA_CARD]", ""));
                textView.setBackgroundColor(0xFFE8AC41);
                textView.setPadding(20, 20, 20, 20);

                textView.setOnClickListener(v -> {
                    Intent i = new Intent(getContext(), PizzaDetails.class);
                    i.putExtra("pizzaName", msg.getMessage().replace("[PIZZA_CARD]", ""));
                    getContext().startActivity(i);
                });
            }else{
                view = LayoutInflater.from(getContext()).inflate(R.layout.bot_chat, parent, false);
                TextView textView = view.findViewById(R.id.chatMsg);
                textView.setText(msg.getMessage());
            }
        }

        if (msg.isUser()){
            TextView textView = view.findViewById(R.id.chatMsg);
        }

        return view;
    }
}
