package dessert.chenxi.li.dessert_ui.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by 李天烨 on 2016/9/13.
 */
public class DataBaseUtil {
    private static final String TABLE_NAME = "User";
    //保存到数据库
    public static void insertInSql(Context context, String account, String password){
        DataBase db = new DataBase(context);
        //取得一个可写的数据库对象
        SQLiteDatabase dbS = db.getWritableDatabase();

        //创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        //像ContentValues中存放数据
        values.put("account", account);
        values.put("password", password);
        //数据库执行插入命令
        dbS.insert(TABLE_NAME, null, values);

        Log.i("insert","query-->"+account+":"+password);
    }


    //更新到数据库
    public static void updateInsql(Context context, String account, String password){
        DataBase db = new DataBase(context);
        //取得一个只读的数据库对象
        SQLiteDatabase dbS = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("password", password);
        dbS.update(TABLE_NAME, values, "account", new String[]{account});
        Log.i("update","query-->"+account+":"+password);
    }

    //查询数据库
    public static String searchInSql(Context context, String account){
        String password = "";
        DataBase db = new DataBase(context);
        //取得一个可读的数据库对象
        SQLiteDatabase dbS = db.getReadableDatabase();

        //查询并获得游标
        Cursor c = dbS.query(TABLE_NAME, new String[]{"account","password"}, "account=?", new String[]{account}, null, null, null, null);
        //利用游标遍历所有数据对象
        while(c.moveToNext()){
            password = c.getString(c.getColumnIndex("password"));
            //日志打印输出
            Log.i("search","query-->"+account+":"+password);
        }
        return account+":"+password;
    }

    //查询数据库第一条
    public static String readFirstInSql(Context context){
        String account_out = "";
        String password_out = "";
        DataBase db = new DataBase(context);
        //取得一个可读的数据库对象
        SQLiteDatabase dbS = db.getReadableDatabase();


        Cursor c = dbS.query(TABLE_NAME, null, null, null, null, null, null);//查询并获得游标
        c.moveToFirst();
        account_out = c.getString(c.getColumnIndex("account"));
        password_out = c.getString(c.getColumnIndex("password"));
        //日志打印输出
        Log.i("readFirst", "query-->" + account_out+":"+password_out);
        return account_out + ":" + password_out;
    }

    //删除数据
    public static void deleteInSql(Context context, String account){
        DataBase db = new DataBase(context);
        //取得一个可读的数据库对象
        SQLiteDatabase dbS = db.getWritableDatabase();

        dbS.delete(TABLE_NAME, "account=?", new String[]{account});
        Log.i("delete","query-->"+account);
    }

    //判断空否
    public static boolean isEmpty(Context context){
        DataBase db = new DataBase(context);
        //取得一个可读的数据库对象
        SQLiteDatabase dbS = db.getReadableDatabase();
        Cursor c = dbS.query(TABLE_NAME,null,null,null,null,null,null);//查询并获得游标

        if (c.moveToFirst()){
            Log.i("isEmpty","query-->"+"false");
            return false;
        }else {
            Log.i("isEmpty","query-->"+"true");
            return true;
        }
    }

//    //判断空否
//    public static boolean isNumEmpty(Context context){
//        int i = 0;
//        DataBase db = new DataBase(context);
//        //取得一个可读的数据库对象
//        SQLiteDatabase dbS = db.getReadableDatabase();
//
//        Cursor c = dbS.query(TABLE_NAME,null,null,null,null,null,null);//查询并获得游标
//        //查询并获得游标
//        while(c.moveToNext()){
//            i++;
//            //日志打印输出
//            Log.i("isNum","query-->"+i);
//        }
//        if (i==0){
//            return true;
//        }else {
//            return false;
//        }
//    }
}
