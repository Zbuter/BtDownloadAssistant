package cn.zbuter.btdownloadassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String  CREATE_TABLE_MAGNET = "create table MagnetUris(" +
            "id integer primary key autoincrement," +
            "name text ," +
            "magnet text ," +
            "tips text," +
            "length long," +
            "createTime text" +
            ")";
    public MyDatabaseHelper( Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MAGNET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
