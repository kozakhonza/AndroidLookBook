package klara.lookbook.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import klara.lookbook.utils.AppPref;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "LookBook";

    private static final BaseDbObject[] tableList = new BaseDbObject[]{
            new Shop(),
            new Item()
    };

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(BaseDbObject table : tableList) {
            db.execSQL( table.getCreateTableString());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String UPGRADE_TABLES = "";
        for(BaseDbObject table : tableList) {
            UPGRADE_TABLES += table.getUpgradeTableString() + ";";
        }
        db.execSQL(UPGRADE_TABLES);
        onCreate(db); // todo - tady by melo byt nejspis neco chytrejsiho
    }
}

