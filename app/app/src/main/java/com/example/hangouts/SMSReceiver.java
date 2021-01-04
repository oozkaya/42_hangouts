package com.example.hangouts;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class SMSReceiver extends BroadcastReceiver {

    DBHandler dbHandler;
    List<Contact> contacts;

    public void onReceive(Context context, Intent intent) {

        dbHandler = new DBHandler(context);
        contacts = dbHandler.getAllContacts();
        String contactName = null;

        // Retrieves a map of extended data from the intent. Needed for the pdus object which contains the SMS
        Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object pdus : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus);
                    String senderNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    boolean found = false;
                    // Find the correct contact to add the SMS
                    for (int y = 0; y < contacts.size(); y++) {
                        String formattedNumber = "+33" + contacts.get(y).getNumber().substring(1);
                        String normalNumber = contacts.get(y).getNumber();
                        boolean numberExists = normalNumber.equals(senderNumber) || formattedNumber.equals(senderNumber);

                        if (numberExists) {
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
                            String formattedDate = formatter.format(cal.getTime());

                            Message newMessage = new Message(message, formattedDate, Message.Types.RECEIVED);

                            // If no SMS in the current contact, create an empty array
                            if (contacts.get(y).getMessages() == null) {
                                contacts.get(y).setMessages(new ArrayList<Message>());
                            }
                            contacts.get(y).addMessage(newMessage);
                            dbHandler.updateContact(contacts.get(y));

                            // The contact has been found and SMS list updated
                            found = true;
                            contactName = contacts.get(y).getFirstName();

                            // Now inform MessageActivity to refresh its SMS list
                            Intent i = new Intent("SmsReceived");
                            i.putExtra("message", message);
                            i.putExtra("senderNumber", senderNumber);
                            context.sendBroadcast(i);

                            break;
                        }
                    }

                    // If no contact found then create a new one + his SMS
                    if (!found) {
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
                        String formattedDate = formatter.format(cal.getTime());

                        ArrayList<Message> messages = new ArrayList<Message>();
                        messages.add(new Message(message, formattedDate, Message.Types.RECEIVED));

                        Contact contact = new Contact(null, senderNumber, "", senderNumber, "", "", messages);
                        dbHandler.addContact(contact);

                        Intent i = new Intent("SmsReceived");
                        i.putExtra("message", message);
                        i.putExtra("senderNumber", senderNumber);
                        context.sendBroadcast(i);
                    }

                    // Show Alert except on the SMS activity
                    if (!context.getClass().getName().equals(MessageActivity.class.getName())) {
                        if (contactName == null)
                            contactName = senderNumber;
                        Toast.makeText(context, context.getString(R.string.sms_received) + " " + contactName, Toast.LENGTH_LONG).show();
                    }
                } // end for loop
            } // bundle is null
        } catch (Exception e) {
            Log.e("SmsReceiver", e.getMessage());
        }
    }
}