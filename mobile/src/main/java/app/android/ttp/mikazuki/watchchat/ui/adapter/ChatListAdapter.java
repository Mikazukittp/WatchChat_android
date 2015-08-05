package app.android.ttp.mikazuki.watchchat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import app.android.ttp.mikazuki.watchchat.R;
import app.android.ttp.mikazuki.watchchat.domain.entity.Message;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by haijimakazuki on 15/07/14.
 */
public class ChatListAdapter extends ArrayAdapter<Message> {

    private LayoutInflater layoutInflater;

    public ChatListAdapter(Context c, int id, List<Message> payments) {
        super(c, id, payments);
        this.layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.chat_row, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message message = getItem(position);
        holder.name.setText(message.getSenderId()+"");
        holder.message.setText(message.getContent());
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        holder.date.setText(sdf.format(message.getCreatedAt()));

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.message)
        TextView message;
        @Bind(R.id.date)
        TextView date;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
