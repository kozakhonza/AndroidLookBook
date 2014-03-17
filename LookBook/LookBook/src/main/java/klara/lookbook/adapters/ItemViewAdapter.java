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
import klara.lookbook.model.Item;
import klara.lookbook.utils.Formater;
import klara.lookbook.utils.ImageUtil;

public class ItemViewAdapter extends BaseAdapter {

    private List<Item> items = new ArrayList<Item>();
    private LayoutInflater inflater;

    public ItemViewAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemHolder holder;
        Item item = (Item)getItem(i);

        if(view == null || view.getTag() == null) {
            view = inflater.inflate(R.layout.component_item, viewGroup, false);
            holder = new ItemHolder();
            holder.image = (ImageView) view.findViewById(R.id.imageViewItem);
            holder.price = (TextView) view.findViewById(R.id.textViewPrice);
            view.setTag(holder);
        }else {
            holder = (ItemHolder)view.getTag();
        }

        ImageUtil.showImageInImgeView(holder.image, item.getImageUri(),
                Formater.dpToPx(150, inflater.getContext()), Formater.dpToPx(200, inflater.getContext()));

        holder.image.setImageBitmap(BitmapFactory.decodeFile(item.getImageUri()));
        holder.price.setText(item.getPrice() + " Kč");
        return view;
    }

    private static class ItemHolder {
        public ImageView image;
        public TextView price;
    }
}
