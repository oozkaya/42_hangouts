package com.example.hangouts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static final String DB_NAME = "ContactDB";

    private static final String CONTACTS_TABLE = "contacts";

    private static final String ID = "id";
    private static final String IMAGE = "image";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String NUMBER = "number";
    private static final String EMAIL = "email";
    private static final String ADDRESS = "address";
    private static final String MESSAGES = "messages";

    private static String ERROR_CONTACT_EXISTS;

    public DBHandler(Context context) {
        super(context, DB_NAME, null, VERSION);
        ERROR_CONTACT_EXISTS = context.getResources().getString(R.string.error_contact_exists);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + CONTACTS_TABLE
                + "(" + ID + " integer PRIMARY KEY autoincrement,"
                + IMAGE + " TEXT,"
                + FIRST_NAME + " TEXT,"
                + LAST_NAME + " TEXT,"
                + NUMBER + " TEXT,"
                + EMAIL + " TEXT,"
                + ADDRESS + " TEXT,"
                + MESSAGES + " BLOB)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE " + CONTACTS_TABLE;
        db.execSQL(sql);
        onCreate(db);
    }

    public void addContact(Contact contact) throws Exception {
        SQLiteDatabase db = getWritableDatabase();

        List<Contact> contacts = getAllContacts();

        for (Contact c : contacts) {
            String formattedNumber = "+33" + c.getNumber().substring(1);
            String normalNumber = c.getNumber();
            boolean numberExists = normalNumber.equals(contact.getNumber()) || formattedNumber.equals(contact.getNumber());
            if (numberExists)
                throw new Exception(ERROR_CONTACT_EXISTS);
        }

        ContentValues values = new ContentValues();
        values.put(IMAGE, contact.getImage());
        values.put(FIRST_NAME, contact.getFirstName());
        values.put(LAST_NAME, contact.getLastName());
        values.put(NUMBER, contact.getNumber());
        values.put(EMAIL, contact.getEmail());
        values.put(ADDRESS, contact.getAddress());
        Gson gson = new Gson();
        values.put(MESSAGES, gson.toJson(contact.getMessages()).getBytes());

        db.insert(CONTACTS_TABLE, null, values);
        db.close();
    }

    public Contact getContact(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                CONTACTS_TABLE,
                new String[]{ID, IMAGE, FIRST_NAME, LAST_NAME, NUMBER, EMAIL, ADDRESS, MESSAGES},
                ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null
        );

        Contact contact;

        if (cursor != null) {
            cursor.moveToFirst();

            byte[] blob = cursor.getBlob(7);
            String json = blob == null ? null : new String(blob);
            Gson gson = new Gson();
            ArrayList<Message> messages = gson.fromJson(json, new TypeToken<ArrayList<Message>>(){}.getType());

            contact = new Contact(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    messages
            );
            cursor.close();
            return contact;
        }
        return null;
    }

    public List<Contact> getAllContacts() {
        SQLiteDatabase db = getReadableDatabase();

        List<Contact> contacts = new ArrayList<>();
        String query = "SELECT * FROM " + CONTACTS_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getString(0) != null
                        ? Integer.parseInt(cursor.getString(0))
                        : 0;

                byte[] blob = cursor.getBlob(7);
                String json = blob == null ? null : new String(blob);
                Gson gson = new Gson();
                ArrayList<Message> messages = gson.fromJson(json, new TypeToken<ArrayList<Message>>(){}.getType());

                Contact contact = new Contact();
                contact.setId(id);
                contact.setImage(cursor.getString(1));
                contact.setFirstName(cursor.getString(2));
                contact.setLastName(cursor.getString(3));
                contact.setNumber(cursor.getString(4));
                contact.setEmail(cursor.getString(5));
                contact.setAddress(cursor.getString(6));
                contact.setMessages(messages);
                contacts.add(contact);
            }
            while(cursor.moveToNext());
        }

        cursor.close();
        return contacts;
    }

    public void updateContact(Contact contact) throws Exception {
        SQLiteDatabase db = getWritableDatabase();

        List<Contact> contacts = getAllContacts();

        for (Contact c : contacts) {
            String formattedNumber = "+33" + c.getNumber().substring(1);
            String normalNumber = c.getNumber();
            boolean numberExists = (normalNumber.equals(contact.getNumber())
                    || formattedNumber.equals(contact.getNumber()))
                    && c.getId() != contact.getId();
            if (numberExists)
                throw new Exception(ERROR_CONTACT_EXISTS);
        }

        ContentValues values = new ContentValues();
        values.put(IMAGE, contact.getImage());
        values.put(FIRST_NAME, contact.getFirstName());
        values.put(LAST_NAME, contact.getLastName());
        values.put(NUMBER, contact.getNumber());
        values.put(EMAIL, contact.getEmail());
        values.put(ADDRESS, contact.getAddress());
        Gson gson = new Gson();
        values.put(MESSAGES, gson.toJson(contact.getMessages()).getBytes());

        db.update(
                CONTACTS_TABLE,
                values,
                ID + " = ?",
                new String[]{String.valueOf(contact.getId())}
        );
    }

    public void deleteContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(
                CONTACTS_TABLE,
                ID + " = ?",
                new String[]{String.valueOf(contact.getId())}
        );
        db.close();
    }
}
