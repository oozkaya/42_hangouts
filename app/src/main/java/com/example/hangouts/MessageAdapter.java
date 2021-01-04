package com.example.hangouts;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {
    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
    }

    // Return an integer representing the type by fetching the enum type ordinal
    @Override
    public int getItemViewType(int position) {
        return getItem(position).type.ordinal();
    }

    // Total number of types is the number of enum values
    @Override
    public int getViewTypeCount() {
        return Message.Types.values().length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Message message = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            // Get the data item type for this position
            int type = getItemViewType(position);
            // Inflate XML layout based on the type
            convertView = getInflatedLayoutForType(type);
        }
        // Lookup view for data population
        assert convertView != null;
        TextView content = (TextView) convertView.findViewById(R.id.content);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        if (content != null && date != null) {
            // Populate the data into the template view using the data object
            content.setText(message.message);
            date.setText(message.date);
        }
        // Return the completed view to render on screen
        return convertView;
    }

    // Given the item type, responsible for returning the correct inflated XML layout file
    private View getInflatedLayoutForType(int type) {
        if (type == Message.Types.SENT.ordinal()) {
            return View.inflate(getContext(), R.layout.message_sent, null);
        } else if (type == Message.Types.RECEIVED.ordinal()) {
            return View.inflate(getContext(), R.layout.message_received, null);
        } else {
            return null;
        }
    }
}
