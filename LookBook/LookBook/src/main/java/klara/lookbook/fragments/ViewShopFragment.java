package klara.lookbook.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import klara.lookbook.BaseAsyncTask;
import klara.lookbook.R;
import klara.lookbook.adapters.ItemViewAdapter;
import klara.lookbook.adapters.ShopViewAdapter;
import klara.lookbook.dialogs.BaseDialog;
import klara.lookbook.model.Item;
import klara.lookbook.model.Shop;
import klara.lookbook.utils.UriUtil;

public class ViewShopFragment extends BaseFragment implements AbsListView.OnItemClickListener {

    private ListView mListView;
    private ShopViewAdapter mAdapter;

    public static ViewShopFragment newInstance() {
        return new ViewShopFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ShopViewAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_shop_list, container, false);

        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter.getCount() == 0) {
            GetItemsTask task = new GetItemsTask();
            ContentValues values = new ContentValues();
            task.init(this, UriUtil.URL_VIEW_SHOPS, values, true);
            task.execute();
        }
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
            List<Shop> shops = parseItems();
            mAdapter.setShops(shops);
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

        private List<Shop> parseItems() {
            List<Shop> shops = new ArrayList<Shop>();
            if(data != null) {
                try {
                    JSONArray jsonShops = data.getJSONArray("shops");
                    Shop shop;
                    int length = jsonShops.length();
                    for(int i = 0; i < length; i++) {
                        shop = Shop.parseFromJsonObject(getActivity(),
                               Shop.class, jsonShops.getJSONObject(i));
                        shops.add(shop);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return shops;
        }
    }
}
