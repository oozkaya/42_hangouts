package com.example.hangouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {
    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = getItem(position);

        if (convertView == null) {
            // Inflate XML layout
            convertView = View.inflate(getContext(), R.layout.contact_item, null);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.imageItem);
        if (contact.getImage() == null) {
            // Set default avatar if no image given from user
            image.setImageResource(R.drawable.default_avatar);
        } else {
            // Else, decode the base64 string image into bitmap
            String base64Image = contact.getImage();
            byte[] data = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap;
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opt);

            image.setImageBitmap(bitmap);
        }

        TextView firstName = (TextView) convertView.findViewById(R.id.firstNameItem);
        TextView number = (TextView) convertView.findViewById(R.id.numberItem);
        if (firstName != null && number != null) {
            // Populate the data into the template view using the data object
            firstName.setText(contact.getFirstName());
            number.setText(contact.getNumber());
        }

        return convertView;
    }
}
