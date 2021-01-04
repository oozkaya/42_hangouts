package com.example.hangouts;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context context;
    Activity activity;
    private DBHandler dbHandler;

    private ContactAdapter adapterContacts;
    private List<Contact> contacts;

    private static final int MY_PERMISSIONS_REQUEST_SMS = 1;
    private static  final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);

        context = this;
        activity = this;
        dbHandler = new DBHandler(context);

        ListView contactsList = findViewById(R.id.contact_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton add = findViewById(R.id.add);

        contacts = dbHandler.getAllContacts();
        // Create new adapter to list all the contacts formatted by ContactAdapter
        adapterContacts = new ContactAdapter(context, (ArrayList<Contact>) contacts);
        contactsList.setAdapter(adapterContacts);

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contacts.get(position);
                // Create a new Dialog builder to handle "Modify", "Call" and "Sms" actions
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                builder.setTitle(contact.getFirstName() + " " + contact.getLastName().toUpperCase())
                        .setMessage(
                                 getString(R.string.builder_number) + " " + contact.getNumber() + "\n"
                                        + getString(R.string.builder_email) + " " + contact.getEmail() + "\n"
                                        + getString(R.string.builder_address) + " " + contact.getAddress()
                        )
                        .setNeutralButton(R.string.action_modify, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, ModifyContactActivity.class);
                                intent.putExtra("Contact", contact);
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton(R.string.action_call, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Check for Call permission, ask if not granted, else start Call intent
                                int callPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
                                if (callPermission != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(activity,
                                            new String[]{Manifest.permission.CALL_PHONE},
                                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                                } else {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + contact.getNumber()));
                                    startActivity(callIntent);
                                }
                            }
                        })
                        .setNegativeButton(R.string.action_sms, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Check for Sms permissions, ask if not granted, else start Message intent
                                int sendPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
                                int receivePermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
                                int readPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
                                int permissionGranted = PackageManager.PERMISSION_GRANTED;

                                boolean hasPermissions = sendPermission == permissionGranted
                                        && receivePermission == permissionGranted
                                        && readPermission == permissionGranted;

                                if (!hasPermissions) {
                                    ActivityCompat.requestPermissions(activity,
                                            new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                                            MY_PERMISSIONS_REQUEST_SMS);
                                } else {
                                    Intent intent = new Intent(context, MessageActivity.class);
                                    intent.putExtra("Contact", contact);
                                    startActivity(intent);
                                }
                            }
                        })
                        .show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddContactActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_red_theme) {
            Utils.changeToTheme(this, Utils.THEME_RED);
            return true;
        } else if (id == R.id.action_dark_theme) {
            Utils.changeToTheme(this, Utils.THEME_DARK);
            return true;
        } else {
            Utils.changeToTheme(this, Utils.THEME_BLUE);
            return true;
        }
        /*return super.onOptionsItemSelected(item);*/
    }

    // Receiver to handle events coming from SMSReceiver
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            contacts.clear();
            contacts.addAll(dbHandler.getAllContacts());
            adapterContacts.notifyDataSetChanged();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Register the receiver
        String action = "SmsReceived";
        registerReceiver(receiver, new IntentFilter(action));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the receiver to save unnecessary system overhead
        // Paused activities cannot receive broadcasts anyway
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}