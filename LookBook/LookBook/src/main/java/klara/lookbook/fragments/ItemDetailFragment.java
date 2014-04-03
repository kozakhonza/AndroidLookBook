package klara.lookbook.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import klara.lookbook.BaseAsyncTask;
import klara.lookbook.R;
import klara.lookbook.activities.MainActivity;
import klara.lookbook.adapters.MessagesAdapter;
import klara.lookbook.dialogs.BaseDialog;
import klara.lookbook.model.Item;
import klara.lookbook.model.ItemHome;
import klara.lookbook.model.Message;
import klara.lookbook.utils.AppPref;
import klara.lookbook.utils.ImageUtil;
import klara.lookbook.utils.UriUtil;

public class ItemDetailFragment extends BaseFragment {

    private int itemId;
    private Item item;
    private MessagesAdapter adapter;
    private Message message;
    private int favoriteId;

    private TextView description;
    private EditText messageBox;
    private ImageView imageView;
    private Button favoriteBtn;

    public static ItemDetailFragment newInstance(int sectionNumber,int itemId) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putInt("sectionNumber", sectionNumber);
        args.putInt("itemId", itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new MessagesAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        ListView list = (ListView) rootView.findViewById(R.id.listViewMessages);
        list.addHeaderView(inflater.inflate(R.layout.component_item_detail_header, null));
        list.setAdapter(adapter);

        favoriteBtn = (Button) rootView.findViewById(R.id.favoriteBtn);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavorite();
            }
        });
        description = (TextView) rootView.findViewById(R.id.textViewDescription);
        rootView.findViewById(R.id.imageViewSendMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = messageBox.getText().toString();
                if(text != null && !text.trim().equals("")) {
                    message = new Message(AppPref.get(getActivity(), AppPref.KEY_USER_NICK, ""), text, 0);
                    sendMessage(message);
                }else {
                    //todo error message
                }
            }
        });

        messageBox = (EditText)rootView.findViewById(R.id.editTextMessageBox);
        imageView = (ImageView) rootView.findViewById(R.id.imageViewItem);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt("sectionNumber"));
        itemId = getArguments().getInt("itemId");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(item == null) {
            downloadData();
        }
    }

    private void downloadData() {
        GetItemsTask task = new GetItemsTask();
        ContentValues values = new ContentValues();
        values.put(UriUtil.PARAM_ITEM_ID, itemId);
        task.init(this, UriUtil.URL_VIEW_ITEM_DETAILS, values, true);
        task.execute();
    }

    /**
     * @param lastMessageId -1 znamena ze zadne predchozi zpravy jeste nejsou nactene
     */
    private void loadMessages(int lastMessageId) {
        GetMessagesTask messagesTask = new GetMessagesTask();
        ContentValues values = new ContentValues();
        values.put(UriUtil.PARAM_LAST_MESSAGE_ID, lastMessageId);
        values.put(UriUtil.PARAM_ITEM_ID, itemId);
        messagesTask.init(this, UriUtil.URL_VIEW_MESSAGES, values, false);
        messagesTask.execute();
    }

    private void sendMessage(Message message) {
        SendMessageTask sendMessageTask = new SendMessageTask();
        ContentValues values = new ContentValues();
        values.put(UriUtil.PARAM_ITEM_ID, itemId);
        values.put(UriUtil.PARAM_MESSAGE_TEXT, message.getText());
        sendMessageTask.init(this, UriUtil.URL_ADD_MESSAGE, values, true);
        sendMessageTask.execute();
    }

    private void toggleFavorite() {
        ToggleLikeTask toggleLikeTask = new ToggleLikeTask();
        ContentValues values = new ContentValues();
        values.put(UriUtil.PARAM_ITEM_ID, itemId);
        values.put(UriUtil.PARAM_ID, favoriteId);
        toggleLikeTask.init(this, UriUtil.URL_FAVORITE_TOGGLE, values, true);
        toggleLikeTask.execute();
    }

    public class GetItemsTask extends BaseAsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                item = ItemHome.parseFromJsonObject(getActivity(),
                        ItemHome.class, data.getJSONObject("item"));
                description.setText(item.getDescription());
                favoriteId = data.getInt("favorite_id");
                if( favoriteId == -1) {
                    favoriteBtn.setText("like");
                }else {
                    favoriteBtn.setText("dislike");
                }
                ImageUtil.showImageInImgeView(imageView, item.getImageUri(), 220, 400);
                loadMessages(-1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTryAgainOk(BaseDialog dialog) {
            dialog.dismiss();
            downloadData();
        }

        @Override
        public void onTryAgainCancel(BaseDialog dialog) {
            dialog.dismiss();
        }
    }

    public class SendMessageTask extends BaseAsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                ArrayList<Message> messageList = new ArrayList<Message>();
                JSONArray messages = data.getJSONArray("messages");
                int count = messages.length();
                JSONObject message;
                for(int i = 0; i < count; i++) {
                    message = messages.getJSONObject(i);
                    messageList.add(new Message(message.getString("userNick"),
                            message.getString("text"), message.getInt("created")));
                }
                adapter.setMessageList(messageList);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTryAgainOk(BaseDialog dialog) {
            dialog.dismiss();
        }

        @Override
        public void onTryAgainCancel(BaseDialog dialog) {
            dialog.dismiss();
        }
    }
    public class GetMessagesTask extends BaseAsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                ArrayList<Message> messageList = new ArrayList<Message>();
                JSONArray messages = data.getJSONArray("messages");
                int count = messages.length();
                JSONObject message;
                for(int i = 0; i < count; i++) {
                    message = messages.getJSONObject(i);
                    messageList.add(new Message(message.getString("userNick"),
                            message.getString("text"), message.getInt("created")));
                }
                adapter.setMessageList(messageList);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTryAgainOk(BaseDialog dialog) {
            dialog.dismiss();
        }

        @Override
        public void onTryAgainCancel(BaseDialog dialog) {
            dialog.dismiss();
        }
    }

    public class ToggleLikeTask extends BaseAsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                favoriteId = data.getInt("favorite_id");
                if( favoriteId == -1) {
                    favoriteBtn.setText("like");
                }else {
                    favoriteBtn.setText("dislike");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTryAgainOk(BaseDialog dialog) {
            dialog.dismiss();
            downloadData();
        }

        @Override
        public void onTryAgainCancel(BaseDialog dialog) {
            dialog.dismiss();
        }
    }

}
