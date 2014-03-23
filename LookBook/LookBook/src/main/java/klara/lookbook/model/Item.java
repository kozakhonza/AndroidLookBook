package klara.lookbook.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import klara.lookbook.utils.ImageUtil;

public class Item extends BaseDbObject {

    public static abstract class ItemColumns implements BaseColumns {
        public static final String TABLE_NAME = "Item";
        public static final String COLUMN_ITEM_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SHOP_ID = "shop_id";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_CURRENCY = "currency_id";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE = "file_image";
    }

    protected static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ItemColumns.TABLE_NAME + " (" +
                    ItemColumns._ID + " INTEGER PRIMARY KEY," +
                    ItemColumns.COLUMN_ITEM_ID + " INTEGER," +
                    ItemColumns.COLUMN_TITLE + " VARCHAR(50)," +
                    ItemColumns.COLUMN_SHOP_ID + " INTEGER," +
                    ItemColumns.COLUMN_PRICE + " INTEGER," +
                    ItemColumns.COLUMN_CURRENCY + " INTEGER," +
                    ItemColumns.COLUMN_DESCRIPTION + " TEXT," +
                    ItemColumns.COLUMN_IMAGE + " VARCHAR(30)" +
            " );";

    protected String[] colums = new String[] {
            ItemColumns.COLUMN_ITEM_ID, ItemColumns.COLUMN_TITLE, ItemColumns.COLUMN_SHOP_ID,
            ItemColumns.COLUMN_PRICE, ItemColumns.COLUMN_CURRENCY, ItemColumns.COLUMN_DESCRIPTION,
            ItemColumns.COLUMN_IMAGE
    };

    protected int id;
    protected String title;
    protected int shopId;
    protected int price;
    protected int currency;
    protected String description;
    protected String imageUri;

    @Override
    public String getCreateTableString() {
        return SQL_CREATE_ENTRIES;
    }

    @Override
    public String getUpgradeTableString() {
        return "";
    }

    @Override
    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(ItemColumns.COLUMN_ITEM_ID, id);
        values.put(ItemColumns.COLUMN_TITLE, title);
        values.put(ItemColumns.COLUMN_SHOP_ID, shopId);
        values.put(ItemColumns.COLUMN_PRICE, price);
        values.put(ItemColumns.COLUMN_CURRENCY, currency);
        values.put(ItemColumns.COLUMN_DESCRIPTION, description);
        values.put(ItemColumns.COLUMN_IMAGE, imageUri);
        return values;
    }

    @Override
    protected String[] getColumns() {
        return colums;
    }

    @Override
    protected void initFromCursor(Cursor cursor) {
        super.initFromCursor(cursor);
        id = cursor.getInt(cursor.getColumnIndex(ItemColumns.COLUMN_ITEM_ID));
        title = cursor.getString(cursor.getColumnIndex(ItemColumns.COLUMN_TITLE));
        shopId = cursor.getInt(cursor.getColumnIndex(ItemColumns.COLUMN_SHOP_ID));
        price = cursor.getInt(cursor.getColumnIndex(ItemColumns.COLUMN_PRICE));
        currency = cursor.getInt(cursor.getColumnIndex(ItemColumns.COLUMN_CURRENCY));
        description = cursor.getString(cursor.getColumnIndex(ItemColumns.COLUMN_DESCRIPTION));
        imageUri = cursor.getString(cursor.getColumnIndex(ItemColumns.COLUMN_IMAGE));
    }

    @Override
    protected void initFromJsonObject(JSONObject object) {
        super.initFromJsonObject(object);
        try {
            id = object.isNull(ItemColumns.COLUMN_ITEM_ID) ?
                    -1 : object.getInt(ItemColumns.COLUMN_ITEM_ID);

            title = object.isNull(ItemColumns.COLUMN_TITLE) ?
                    "" : object.getString(ItemColumns.COLUMN_TITLE);

            shopId = object.isNull(ItemColumns.COLUMN_SHOP_ID) ?
                    -1 : object.getInt(ItemColumns.COLUMN_SHOP_ID);

            price = object.isNull(ItemColumns.COLUMN_PRICE) ?
                    -1 : object.getInt(ItemColumns.COLUMN_PRICE);

            currency = object.isNull(ItemColumns.COLUMN_CURRENCY) ?
                    -1 : object.getInt(ItemColumns.COLUMN_CURRENCY);

            description = object.isNull(ItemColumns.COLUMN_DESCRIPTION) ?
                    "" : object.getString(ItemColumns.COLUMN_DESCRIPTION);

            imageUri = object.isNull("image") ?
                    null : ImageUtil.saveBase64ImgToFile(object.getString("image"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String image) {
        this.imageUri = image;
    }
}

