package klara.lookbook.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import klara.lookbook.BaseAsyncTask;
import klara.lookbook.R;
import klara.lookbook.adapters.ItemViewAdapter;
import klara.lookbook.dialogs.BaseDialog;
import klara.lookbook.model.Item;
import klara.lookbook.utils.UriUtil;

public class ViewItemFragment extends BaseFragment implements AbsListView.OnItemClickListener {

    private ListView mListView;
    private ItemViewAdapter mAdapter;

    public static ViewItemFragment newInstance() {
        return new ViewItemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ItemViewAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_item_list, container, false);

        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        GetItemsTask task = new GetItemsTask();
        ContentValues values = new ContentValues();
        task.init(this, UriUtil.URL_VIEW_ITEMS, values, true);
        task.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


    private class GetItemsTask extends BaseAsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            List<Item> items = parseItems();
            mAdapter.setItems(items);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTryAgainOk(BaseDialog dialog) {
            dialog.dismiss();
        }

        @Override
        public void onTryAgainCancel(BaseDialog dialog) {
            dialog.dismiss();
        }

        private List<Item> parseItems() {
            List<Item> items = new ArrayList<Item>();
            if(data != null) {
                try {
                    JSONArray jsonItems = data.getJSONArray("items");
                    Item item;
                    int length = jsonItems.length();
                    for(int i = 0; i < length; i++) {
                        item = Item.parseFromJsonObject(getActivity(),
                                Item.class, jsonItems.getJSONObject(i));
                        items.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return items;
        }
    }
}
