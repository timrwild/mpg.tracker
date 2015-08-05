package timwildauer.final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Created by timwildauer on 4/24/15.
 */
public class CarListDatabase {

    private static final String TAG = "CarDatabase";

    public static final String KEY_ROW_ID = "_id";
    public static final int COL_ROW_ID = 0;

    public static final String KEY_NAME = "carName";
    public static final int COL_NAME = 1;

    public static final String KEY_MAKE = "carMake";
    public static final int COL_MAKE = 2;

    public static final String KEY_MODEL = "carModel";
    public static final int COL_MODEL = 3;

    public static final String KEY_YEAR = "carYear";
    public static final int COL_YEAR = 4;

    public static final String[] ALL_KEYS = new String[] {KEY_ROW_ID, KEY_NAME, KEY_MAKE, KEY_MODEL, KEY_YEAR};

    public static final String DATABASE_NAME = "CarDatabase";
    public static final String DATABASE_TABLE = "CarList";

    public static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROW_ID + " integer primary key autoincrement, "
                    + KEY_NAME + " text not null, "
                    + KEY_MAKE + " text not null,"
                    + KEY_MODEL + " text not null,"
                    + KEY_YEAR + " integer not null"
                    + ");";

    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    public CarListDatabase(Context ctx){
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    public CarListDatabase open(){
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        myDBHelper.close();
    }

    public long insertRow(String name, String make, String model, int year){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_MAKE, make);
        initialValues.put(KEY_MODEL, model);
        initialValues.put(KEY_YEAR, year);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteRow(long rowID){
        String where = KEY_ROW_ID + "=" + rowID;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public void deleteAll() {
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROW_ID);
        if (c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    public Cursor getAllRows(){
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS, where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getRow(long rowID){
        String where = KEY_ROW_ID + "=" + rowID;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS, where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public boolean updateRow(long rowID, String name, String make, String model, int year){
        String where = KEY_ROW_ID + "=" + rowID;

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, name);
        newValues.put(KEY_MAKE, make);
        newValues.put(KEY_MODEL, model);
        newValues.put(KEY_YEAR, year);

        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
                    newVersion + " which will destroy all old data!");

            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            onCreate(_db);
        }
    }
}
