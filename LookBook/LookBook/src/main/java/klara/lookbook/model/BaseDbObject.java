package klara.lookbook.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDbObject {

    private static DatabaseHandler dbHandler;

    protected Context context;
    protected int _id = -1;

    public static <T extends BaseDbObject> T newInstance(Context context, Class<T> c) {
        try {
            T object = c.newInstance();
            object.context = context;
            return object;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends BaseDbObject> T getById(Context context, Class<T> c, int id) {
        try {
            T object = c.newInstance();
            object.context = context;
            object._initById(id);
            return object;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends BaseDbObject> List<T> getByWhere(Context context, Class<T> c, String where) {
        List<T> list = new ArrayList<T>();
        try {
            if(where != null) {
                T object = c.newInstance();
                object.context = context;
                String selectQuery = "SELECT * FROM " + object.getTableName();
                if(where != null && !where.isEmpty()) {
                    selectQuery += " WHERE "+where;
                }
                SQLiteDatabase db = object.getDbHandler().getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        object = c.newInstance();
                        object.context = context;
                        object.initFromCursor(cursor);
                        list.add(object);
                    } while (cursor.moveToNext());
                }

            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static <T extends BaseDbObject> T parseFromJsonObject(Context context, Class<T> c, JSONObject jsonObject) {
        try {
            T object = c.newInstance();
            object.context = context;
            object.initFromJsonObject(jsonObject);
            return object;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save() {
        SQLiteDatabase db = getDbHandler().getWritableDatabase();

        ContentValues values = getValues();

        if(_id == -1) { // new Object
            db.insert(getTableName(), null, values);
        }else {
            db.update(getTableName(), values, BaseColumns._ID + " = ?",
                    new String[] { String.valueOf(_id)});
        }
        db.close();
    }

    protected void _initById(int id) {
        SQLiteDatabase db = getDbHandler().getWritableDatabase();

        Cursor cursor = db.query(getTableName(), getColumns(), BaseColumns._ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        initFromCursor(cursor);
        db.close();
    }

    protected DatabaseHandler getDbHandler() {
        if(BaseDbObject.dbHandler == null) {
            BaseDbObject.dbHandler = new DatabaseHandler(context);
        }
        return BaseDbObject.dbHandler;
    }

    public void delete() {
        SQLiteDatabase db = getDbHandler().getWritableDatabase();
        db.delete(getTableName(), BaseColumns._ID + " = ?",
                new String[] { String.valueOf(_id) });
        db.close();
    }

//
//    // Getting contacts Count
//    public int getContactsCount() {
//        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
//
//        // return count
//        return cursor.getCount();
//    }

    public void setContext(Context context) {
        this.context = context;
    }
    protected String getTableName() {
        return getClass().getSimpleName();
    }

    public abstract String getCreateTableString();
    public abstract String getUpgradeTableString();

    public abstract ContentValues getValues();
    protected abstract String[] getColumns();

    protected void initFromCursor(Cursor cursor){
        _id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
    }

    protected void initFromJsonObject(JSONObject object) {

    }
}
