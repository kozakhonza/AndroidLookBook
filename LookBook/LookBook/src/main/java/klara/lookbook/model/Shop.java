package klara.lookbook.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

public class Shop extends BaseDbObject {

    public static abstract class ShopColumns implements BaseColumns {
        public static final String TABLE_NAME = "Shop";
        public static final String COLUMN_SHOP_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SHOPING_CENTER = "shoppingCenter";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STREET = "street";
        public static final String COLUMN_LAT= "lat";
        public static final String COLUMN_LNG = "lng";
        public static final String COLUMN_IMAGE = "file_image";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ShopColumns.TABLE_NAME + " (" +
                    ShopColumns._ID + " INTEGER PRIMARY KEY," +
                    ShopColumns.COLUMN_SHOP_ID + " INTEGER," +
                    ShopColumns.COLUMN_TITLE + " VARCHAR(50)," +
                    ShopColumns.COLUMN_SHOPING_CENTER + " VARCHAR(50)," +
                    ShopColumns.COLUMN_CITY + " VARCHAR(50)," +
                    ShopColumns.COLUMN_STREET + " VARCHAR(50)," +
                    ShopColumns.COLUMN_LAT + " DOUBLE," +
                    ShopColumns.COLUMN_LNG + " DOUBLE," +
                    ShopColumns.COLUMN_IMAGE + " VARCHAR(30)" +
            " )";

    private String[] colums = new String[] {
            ShopColumns.COLUMN_SHOP_ID, ShopColumns.COLUMN_TITLE, ShopColumns.COLUMN_SHOPING_CENTER,
            ShopColumns.COLUMN_CITY,  ShopColumns.COLUMN_STREET, ShopColumns.COLUMN_LAT,
            ShopColumns.COLUMN_LNG, ShopColumns.COLUMN_IMAGE
    };

    private int id;
    private String title;
    private String shopingCenter;
    private String city;
    private String street;
    private double lat;
    private double lng;
    private String imageUri;

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
        values.put(ShopColumns.COLUMN_SHOP_ID, id);
        values.put(ShopColumns.COLUMN_TITLE, title);
        values.put(ShopColumns.COLUMN_SHOPING_CENTER, shopingCenter);
        values.put(ShopColumns.COLUMN_CITY, city);
        values.put(ShopColumns.COLUMN_STREET, street);
        values.put(ShopColumns.COLUMN_LAT, lng);
        values.put(ShopColumns.COLUMN_LNG, lat);
        values.put(ShopColumns.COLUMN_IMAGE, imageUri);
        return values;
    }

    @Override
    protected String[] getColumns() {
        return colums;
    }

    @Override
    protected void initFromCursor(Cursor cursor) {
        id = cursor.getInt(0);
        title = cursor.getString(1);
        shopingCenter = cursor.getString(2);
        city = cursor.getString(3);
        street = cursor.getString(4);
        lat = cursor.getDouble(5);
        lng = cursor.getDouble(6);
        imageUri = cursor.getString(7);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShopingCenter() {
        return shopingCenter;
    }

    public void setShopingCenter(String shopingCenter) {
        this.shopingCenter = shopingCenter;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}

