package com.example.hangouts;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

@SuppressLint("SimpleDateFormat")
public class MessageActivity extends AppCompatActivity {

    Context context;
    Activity activity;
    DBHandler dbHandler;
    MessageAdapter adapterMessages;

    Contact contact;
    ListView messagesList;
    EditText messageInput;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_message);

        context = this;
        activity = this;
        dbHandler = new DBHandler(context);

        contact = (Contact) getIntent().getSerializableExtra("Contact");
        messagesList = (ListView) findViewById(R.id.messages_list);
        messageInput = (EditText) findViewById(R.id.messageInput);
        send = (Button) findViewById(R.id.send);

        if (contact.getMessages() == null) {
            contact.setMessages(new ArrayList<Message>());
        }
        adapterMessages = new MessageAdapter(context, contact.getMessages());
        messagesList.setAdapter(adapterMessages);
        messagesList.setDivider(null);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
                    String formattedDate = formatter.format(cal.getTime());
                    Message message = new Message(messageInput.getText().toString(), formattedDate, Message.Types.SENT);

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contact.getNumber(), null, messageInput.getText().toString(), null, null);

                    contact.addMessage(message);
                    dbHandler.updateContact(contact);
                    messageInput.setText(null);
                    messageInput.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    adapterMessages.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(context, getString(R.string.error_send), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    // Receiver to handle events coming from SMSReceiver
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String senderNumber = intent.getStringExtra("senderNumber");
            String formattedNumber = "+33" + contact.getNumber().substring(1);
            String normalNumber = contact.getNumber();
            boolean numberExists = normalNumber.equals(senderNumber) || formattedNumber.equals(senderNumber);

            if (numberExists) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
                String formattedDate = formatter.format(cal.getTime());
                Message newMessage = new Message(message, formattedDate, Message.Types.RECEIVED);

                contact.addMessage(newMessage);
                adapterMessages.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        String action = "SmsReceived";
        registerReceiver(receiver, new IntentFilter(action));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }
}