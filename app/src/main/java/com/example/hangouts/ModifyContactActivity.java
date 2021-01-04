package com.example.hangouts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ModifyContactActivity extends AppCompatActivity {

    Context context;
    Activity activity;
    DBHandler dbHandler;

    Contact contact;
    ImageButton imageEdit;
    String base64Image;
    EditText firstNameEdit, lastNameEdit, numberEdit, emailEdit, addressEdit;
    Button save, delete;

    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_modify_contact);

        context = this;
        activity = this;
        dbHandler = new DBHandler(context);

        imageEdit = (ImageButton) findViewById(R.id.imageEdit);
        firstNameEdit = (EditText) findViewById(R.id.firstNameEdit);
        lastNameEdit = (EditText) findViewById(R.id.lastNameEdit);
        numberEdit = (EditText) findViewById(R.id.numberEdit);
        emailEdit = (EditText) findViewById(R.id.emailEdit);
        addressEdit = (EditText) findViewById(R.id.addressEdit);
        contact = (Contact) getIntent().getSerializableExtra("Contact");

        save = (Button) findViewById(R.id.save);
        delete = (Button) findViewById(R.id.delete);

        if (contact.getImage() == null) {
            Drawable avatar = ContextCompat.getDrawable(context, R.drawable.default_avatar);
            imageEdit.setBackground(avatar);
        } else {
            base64Image = contact.getImage();
            byte[] data = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap;
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);

            imageEdit.setBackground(drawable);
        }

        firstNameEdit.setText(contact.getFirstName());
        lastNameEdit.setText(contact.getLastName());
        numberEdit.setText(contact.getNumber());
        emailEdit.setText(contact.getEmail());
        addressEdit.setText(contact.getAddress());

        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int readPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (readPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_STORAGE);
                } else {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = contact.getId();
                String firstName = firstNameEdit.getText().toString();
                String lastName = lastNameEdit.getText().toString();
                String number = numberEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String address = addressEdit.getText().toString();
                ArrayList<Message> messages = contact.getMessages();

                try {
                    if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(number)) {
                            Contact contact = new Contact(id, base64Image, firstName, lastName, number, email, address, messages);
                            dbHandler.updateContact(contact);
                            startActivity(new Intent(context, MainActivity.class));
                    } else {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                        builder.setMessage(getString(R.string.fields_missing))
                                .setNegativeButton("OK", null)
                                .show();
                    }
                }
                catch (Exception e) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    builder.setMessage(e.getMessage())
                            .setNegativeButton("OK", null)
                            .show();
                    e.printStackTrace();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHandler.deleteContact(contact);
                Intent intent = new Intent(context, MainActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos); // Could be Bitmap.CompressFormat.PNG or Bitmap.CompressFormat.WEBP
            byte[] bai = baos.toByteArray();

            base64Image = Base64.encodeToString(bai, Base64.DEFAULT);
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);

            imageEdit.setBackground(drawable);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
    }
}