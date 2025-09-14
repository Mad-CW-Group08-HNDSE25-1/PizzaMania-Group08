package com.androidapp.pizzamania;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class AIChatBot extends AppCompatActivity {

    private ListView chatList;
    private EditText txtInput;
    private Button btnSend;
    private ChatAdapter chatAdapter;
    private List<ChatMsgAdapter> chatMsgsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aichat_bot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chatList = findViewById(R.id.chatListView);
        txtInput = findViewById(R.id.txtMsgInput);
        btnSend = findViewById(R.id.sendBtn);

        chatMsgsList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMsgsList);
        chatList.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> {
            String msg = txtInput.getText().toString().trim();

            if (!msg.isEmpty()){
                addMsg(new ChatMsgAdapter(true, msg));
            }
        });

    }

    private void addMsg(ChatMsgAdapter chatMsgAdapter){
        chatMsgsList.add(chatMsgAdapter);
        chatAdapter.notifyDataSetChanged();
        chatList.setSelection(chatMsgsList.size() - 1);
    }

    private void handleBotResponse(String userMsg){

        if(userMsg.toLowerCase().contains("hello")){
            addMsg(new ChatMsgAdapter(false, "Hi there, how can i assist you today?"));
        } else if (userMsg.toLowerCase().contains("pizza")) {
            addMsg(new ChatMsgAdapter(false, "We have a delicious cheese chicken pizza, tap below to see details"));
            chatMsgsList.add(new ChatMsgAdapter(false, "[PIZZA_CARD]cheese chicken pizza\""));
            chatAdapter.notifyDataSetChanged();
        }else {
            addMsg(new ChatMsgAdapter(false, "Sorry, try asking about pizzas"));
        }

    }
}