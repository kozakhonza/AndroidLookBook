package klara.lookbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import klara.lookbook.R;
import klara.lookbook.model.Message;
import klara.lookbook.utils.Formater;

public class MessagesAdapter extends BaseAdapter {

    private ArrayList<Message> messageList = new ArrayList<Message>();
    private LayoutInflater inflater;

    public MessagesAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    private void addMessage(Message message) {
        this.messageList.add(message);
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int sectionNumber) {
        return messageList.get(sectionNumber);
    }

    @Override
    public long getItemId(int sectionNumber) {
        return sectionNumber;
    }

    @Override
    public View getView(int sectionNumber, View view, ViewGroup viewGroup) {
        Holder holder;
        Message message = (Message) this.getItem(sectionNumber);
        if(view == null || view.getTag() == null) {
            holder = new Holder();
            view = inflater.inflate(R.layout.component_message, viewGroup, false);
            holder.text = (TextView) view.findViewById(R.id.textViewMessage);
            holder.time = (TextView) view.findViewById(R.id.textViewTime);
            holder.authorNick = (TextView) view.findViewById(R.id.textViewNick);
            view.setTag(holder);
        }else {
            holder = (Holder) view.getTag();
        }
        holder.text.setText(message.getText());
        holder.time.setText(Formater.formatTime(message.getTime(), Formater.FORMAT_DATE_TIME));
        holder.authorNick.setText(message.getAuthorNick());
        return view;
    }

    public void setMessageList(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }

    private static class Holder {
        public TextView text;
        public TextView time;
        public TextView authorNick;
    }
}
