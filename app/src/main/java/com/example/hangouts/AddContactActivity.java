package com.example.hangouts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class AddContactActivity extends AppCompatActivity {

    Context context;
    DBHandler dbHandler;

    EditText et_firstName, et_lastName, et_number, et_email, et_address;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_add_contact);

        context = this;
        dbHandler = new DBHandler(context);

        et_firstName = findViewById(R.id.firstName);
        et_lastName = findViewById(R.id.lastName);
        et_number = findViewById(R.id.number);
        et_email = findViewById(R.id.email);
        et_address = findViewById(R.id.address);

        add = findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = et_firstName.getText().toString();
                String lastName = et_lastName.getText().toString();
                String number = et_number.getText().toString();
                String email = et_email.getText().toString();
                String address = et_address.getText().toString();
                ArrayList<Message> messages = new ArrayList<Message>();

                try {
                    if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(number)) {
                        Contact contact = new Contact(null, firstName, lastName, number, email, address, messages);
                        dbHandler.addContact(contact);
                        startActivity(new Intent(context, MainActivity.class));
                        finish();
                    } else {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                        builder.setMessage(getString(R.string.fields_missing))
                                .setNegativeButton("OK", null)
                                .show();
                    }
                } catch (Exception e) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    builder.setMessage(e.getMessage())
                            .setNegativeButton("OK", null)
                            .show();
                    e.printStackTrace();
                }
            }
        });
    }
}