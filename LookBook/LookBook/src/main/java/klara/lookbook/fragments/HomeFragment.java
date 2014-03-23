package klara.lookbook.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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

public class HomeFragment extends BaseFragment {

    private GridView gridView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        gridView = (GridView) rootView.findViewById(R.id.grid);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter.getCount() == 0) {
            GetItemsTask task = new GetItemsTask();
            ContentValues values = new ContentValues();
            task.init(this, UriUtil.URL_VIEW_ITEMS, values, true);
            task.execute();
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
