package klara.lookbook.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

public class Item extends BaseDbObject {

    public static abstract class ItemColumns implements BaseColumns {
        public static final String TABLE_NAME = "Item";
        public static final String COLUMN_ITEM_ID = "item_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SHOP_ID = "shop_id";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_CURRENCY = "currency";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE = "image";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ItemColumns.TABLE_NAME + " (" +
                    ItemColumns._ID + " INTEGER PRIMARY KEY," +
                    ItemColumns.COLUMN_ITEM_ID + " INTEGER," +
                    ItemColumns.COLUMN_TITLE + " VARCHAR(50)," +
                    ItemColumns.COLUMN_SHOP_ID + " INTEGER," +
                    ItemColumns.COLUMN_PRICE + " INTEGER," +
                    ItemColumns.COLUMN_CURRENCY + " INTEGER," +
                    ItemColumns.COLUMN_DESCRIPTION + " TEXT," +
                    ItemColumns.COLUMN_IMAGE + " VARCHAR(30)," +
            " )";

    private String[] colums = new String[] {
            ItemColumns.COLUMN_ITEM_ID, ItemColumns.COLUMN_TITLE, ItemColumns.COLUMN_SHOP_ID,
            ItemColumns.COLUMN_PRICE, ItemColumns.COLUMN_CURRENCY, ItemColumns.COLUMN_DESCRIPTION,
            ItemColumns.COLUMN_IMAGE
    };

    private int id;
    private String title;
    private int shopId;
    private int price;
    private int currency;
    private String description;
    private String image;

    @Override
    public String getCreateTableString() {
        return SQL_CREATE_ENTRIES;
    }

    @Override
    public String getUpgradeTableString() {
        return "";
    }

    @Override
    protected ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(ItemColumns.COLUMN_ITEM_ID, id);
        values.put(ItemColumns.COLUMN_TITLE, title);
        values.put(ItemColumns.COLUMN_SHOP_ID, shopId);
        values.put(ItemColumns.COLUMN_PRICE, price);
        values.put(ItemColumns.COLUMN_CURRENCY, currency);
        values.put(ItemColumns.COLUMN_DESCRIPTION, description);
        values.put(ItemColumns.COLUMN_IMAGE, image);
        return values;
    }

    @Override
    protected String[] getColumns() {
        return colums;
    }

    @Override
    protected void initFromCursor(Cursor cursor) {

    }
}

