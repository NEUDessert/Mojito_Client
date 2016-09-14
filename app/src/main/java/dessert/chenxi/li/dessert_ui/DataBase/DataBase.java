package dessert.chenxi.li.dessert_ui.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 李天烨 on 2016/9/13.
 */
public class DataBase extends SQLiteOpenHelper {

    public DataBase(Context context) {
        super(context, "User", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.i("database","created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("database","creating");
//        db.execSQL("create table "+TABLE_NAME_USER+"(" +
//                COLUMN_NAME_ACCOUNT+" TEXT PRIMARY KEY AUTOINCREMENT," +
//                COLUMN_NAME_PASSWORD+" TEXT NOT NULL DEFAULT \"\"" + ")");
        String sql = "create table if not exists User(account varchar(20) not null , password varchar(60) not null );";
        db.execSQL(sql);
    }


//    private static final String TABLE_NAME_USER = "User";
//    private static final String COLUMN_NAME_ACCOUNT = "Account";
//    private static final String COLUMN_NAME_PASSWORD = "Password";

}
