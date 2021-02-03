package com.example.chattingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
public class UserAdapter extends ArrayAdapter<ActiveUser> implements View.OnClickListener  {

    private ArrayList<ActiveUser> users;
    Context mycontext;

    private static class ViewHolder{
        TextView userNameTextView, userDateTextView;
    }

    public UserAdapter (ArrayList<ActiveUser> activeUsers, Context context){
        super(context, R.layout.user_row,activeUsers);
        this.users = activeUsers;
        this.mycontext = context;
    }

    @Override
    public void onClick(View v) { }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ActiveUser activeUser = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        viewHolder = new ViewHolder();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.user_row, parent, false);
        // connect Text views to the xml file
        viewHolder.userNameTextView = (TextView) convertView.findViewById(R.id.user_row_name);
       //viewHolder.userEmailTextView = (TextView) convertView.findViewById(R.id.user_row_email);
        viewHolder.userDateTextView = (TextView)convertView.findViewById(R.id.user_row_date);
        // viewHolder.imageView = convertView.findViewById(R.id.user_row_image);

        // set values of message text and data/time value
        viewHolder.userNameTextView.setText(activeUser.user);
//        viewHolder.userEmailTextView.setText(activeUser.created);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        viewHolder.userDateTextView.setText( String.valueOf(formatter.format( Long.valueOf(activeUser.created))));

        // Load pictures from storage db , resize and tup to image view
        //      Picasso.get().load(user.getImageUrl()).into(viewHolder.imageView);

        // attach user id to the view
        convertView.setTag(position);

        // Return the completed view to render on screen
        return convertView;
    }
}
