package klara.lookbook.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "LookBook";

    private static final BaseDbObject[] tableList = new BaseDbObject[]{
            new Item(),
            new Shop()
    };

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLES = "";
        for(BaseDbObject table : tableList) {
            CREATE_TABLES += table.getCreateTableString() + ";";
        }
        db.execSQL(CREATE_TABLES);
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

