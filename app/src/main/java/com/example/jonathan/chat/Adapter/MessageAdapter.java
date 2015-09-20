package com.example.jonathan.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jonathan.chat.Model.Message;
import com.example.jonathan.chat.R;

import java.util.ArrayList;

/**
 * Created by Jonathan on 17/09/15.
 */
public class MessageAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private ArrayList<Message> messages;

    public MessageAdapter(Context context, ArrayList<Message> messages){
        this.context = context;
        this.messages = messages;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.author = (TextView) convertView.findViewById(R.id.author);
            holder.message = (TextView) convertView.findViewById(R.id.message);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.author.setText(messages.get(position).getAuthor());
        holder.message.setText(messages.get(position).getMessage());
        holder.message.setTextColor(messages.get(position).getColor());

        return convertView;
    }

    class ViewHolder {
        public TextView author;
        public TextView message;
    }
}
