package klara.lookbook.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import klara.lookbook.R;
import klara.lookbook.model.Item;
import klara.lookbook.model.ItemHome;
import klara.lookbook.utils.Formater;
import klara.lookbook.utils.ImageUtil;

public class HomeItemViewAdapter extends ItemViewAdapter{

    public HomeItemViewAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemHomeHolder holder;
        ItemHome item = (ItemHome) getItem(i);

        if(view == null || view.getTag() == null) {
            view = inflater.inflate(R.layout.component_home_item, viewGroup, false);
            holder = new ItemHomeHolder();
            holder.image = (ImageView) view.findViewById(R.id.imageViewItem);
            holder.price = (TextView) view.findViewById(R.id.textViewPrice);
            holder.header = (TextView) view.findViewById(R.id.textViewHeader);
            view.setTag(holder);
        }else {
            holder = (ItemHomeHolder)view.getTag();
        }

        ImageUtil.showImageInImgeView(holder.image, item.getImageUri(),
                Formater.dpToPx(150, inflater.getContext()), Formater.dpToPx(200, inflater.getContext()));

        holder.image.setImageBitmap(BitmapFactory.decodeFile(item.getImageUri()));
        holder.price.setText(item.getPrice() + " Kč");
        holder.header.setText(item.getUserTitle());
        return view;
    }

    protected static class ItemHomeHolder extends ItemHolder{
        public TextView header;
    }
}
