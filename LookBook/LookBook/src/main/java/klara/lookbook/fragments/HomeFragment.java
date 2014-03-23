package klara.lookbook.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import klara.lookbook.BaseAsyncTask;
import klara.lookbook.R;
import klara.lookbook.activities.MainActivity;
import klara.lookbook.adapters.HomeItemViewAdapter;
import klara.lookbook.adapters.ItemViewAdapter;
import klara.lookbook.dialogs.BaseDialog;
import klara.lookbook.model.Item;
import klara.lookbook.model.ItemHome;
import klara.lookbook.utils.UriUtil;

public class HomeFragment extends BaseFragment {
    private HomeItemViewAdapter mAdapter;
    private View rootView;

    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("sectionNumber", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new HomeItemViewAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        GridView grid = (GridView) rootView.findViewById(R.id.grid);
        grid.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt("sectionNumber"));
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter.getCount() == 0) {
            GetItemsTask task = new GetItemsTask();
            ContentValues values = new ContentValues();
            task.init(this, UriUtil.URL_VIEW_LEADERS_ITEMS, values, true);
            task.execute();
        }
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = rootView.findViewById(R.id.empty);

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
            emptyView.setVisibility(View.VISIBLE);
        }
    }


    public class GetItemsTask extends BaseAsyncTask {

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
                    ItemHome item;
                    int length = jsonItems.length();
                    if(length == 0) {
                        setEmptyText(getString(R.string.home_frag_empty));
                    }else {
                        for(int i = 0; i < length; i++) {
                            item = ItemHome.parseFromJsonObject(getActivity(),
                                    ItemHome.class, jsonItems.getJSONObject(i));
                            items.add(item);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return items;
        }
    }
}
