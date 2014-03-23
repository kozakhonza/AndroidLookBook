package klara.lookbook.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import klara.lookbook.R;
import klara.lookbook.model.Shop;
import klara.lookbook.utils.Formater;
import klara.lookbook.utils.ImageUtil;

public class ShopViewAdapter extends BaseAdapter {

    protected List<Shop> shops = new ArrayList<Shop>();
    protected LayoutInflater inflater;

    public ShopViewAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    @Override
    public int getCount() {
        return shops.size();
    }

    @Override
    public Object getItem(int i) {
        return shops.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ShopHolder holder;
        Shop shop = (Shop)getItem(i);

        if(view == null || view.getTag() == null) {
            view = inflater.inflate(R.layout.component_item, viewGroup, false);
            holder = new ShopHolder();
            holder.image = (ImageView) view.findViewById(R.id.imageView);
            holder.name = (TextView) view.findViewById(R.id.textViewName);
            holder.addresse = (TextView) view.findViewById(R.id.textViewAddresse);
            view.setTag(holder);
        }else {
            holder = (ShopHolder)view.getTag();
        }

        ImageUtil.showImageInImgeView(holder.image, shop.getImageUri(),
                Formater.dpToPx(150, inflater.getContext()), Formater.dpToPx(200, inflater.getContext()));

        holder.image.setImageBitmap(BitmapFactory.decodeFile(shop.getImageUri()));
        holder.name.setText(shop.getTitle());
        holder.addresse.setText(shop.getCity() + " "+ shop.getCity());
        return view;
    }

    protected static class ShopHolder {
        public ImageView image;
        public TextView name;
        public TextView addresse;
    }
}
