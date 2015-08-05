package timwildauer.final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.Serializable;


public class CarInfoDatabase implements Serializable {

    private static final String TAG = "CarInfoDatabase";

    public static final String KEY_ROW_ID = "_id";
    public static final int COL_ROW_ID = 0;

    public static final String KEY_NAME = "carName";
    public static final int COL_NAME = 1;

    public static final String KEY_INFO = "carInfo";
    public static final int COL_INFO = 2;

    public static final String KEY_DATE_DAY = "transactionDateDay";
    public static final int COL_DATE_DAY = 3;

    public static final String KEY_DATE_MONTH = "transactionDateMonth";
    public static final int COL_DATE_MONTH = 4;

    public static final String KEY_DATE_YEAR = "transactionDateYear";
    public static final int COL_DATE_YEAR = 5;

    public static final String KEY_MILEAGE = "carMileage";
    public static final int COL_MILEAGE = 6;

    public static final String KEY_GALLONS = "gallonsPurchased";
    public static final int COL_GALLONS = 7;

    public static final String KEY_FULL = "fullTank";
    public static final int COL_FULL = 8;

    public static final String KEY_PRICE = "gasCost";
    public static final int COL_PRICE = 9;

    public static final String KEY_MPG = "carMPG";
    public static final int COL_MPG = 10;

    public static final String[] ALL_KEYS = new String[] {KEY_ROW_ID, KEY_NAME, KEY_INFO,
            KEY_DATE_DAY, KEY_DATE_MONTH, KEY_DATE_YEAR, KEY_MILEAGE, KEY_GALLONS, KEY_FULL, KEY_PRICE, KEY_MPG};

    public static final String DATABASE_NAME = "CarInfoDatabase";
    public static final String DATABASE_TABLE = "CarInfoTable";

    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROW_ID + " integer primary key autoincrement, "
                    + KEY_NAME + " text not null, "
                    + KEY_INFO + " text not null, "
                    + KEY_DATE_DAY + " integer not null, "
                    + KEY_DATE_MONTH + " integer not null, "
                    + KEY_DATE_YEAR + " integer not null, "
                    + KEY_MILEAGE + " real not null, "
                    + KEY_GALLONS + " real not null, "
                    + KEY_FULL + " numeric not null, "
                    + KEY_PRICE + " real not null, "
                    + KEY_MPG + " real not null"
                    + ");";

    private final Context context;

    private DatabaseHelper myDBHelper;
    public SQLiteDatabase db;

    public CarInfoDatabase(Context ctx){
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    public CarInfoDatabase open(){
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        myDBHelper.close();
    }

    public long insertRow(String name, String info, int dateDay, int dateMonth, int dateYear, float mileage, float gallons, int full, float price, float mpg){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_INFO, info);
        initialValues.put(KEY_DATE_DAY, dateDay);
        initialValues.put(KEY_DATE_MONTH, dateMonth);
        initialValues.put(KEY_DATE_YEAR, dateYear);
        initialValues.put(KEY_MILEAGE, mileage);
        initialValues.put(KEY_GALLONS, gallons);
        initialValues.put(KEY_FULL, full);
        initialValues.put(KEY_PRICE, price);
        initialValues.put(KEY_MPG, mpg);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteRow(long rowID){
        String where = KEY_ROW_ID + "=" + rowID;
        return db.delete(DATABASE_TABLE, where, null) != 0;
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

    public boolean updateRow(long rowID, Float mpg){
        String where = KEY_ROW_ID + "=" + rowID;

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_MPG, mpg);

        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    public boolean updateNameInfo(long rowID, String name, String info){
        String where = KEY_ROW_ID + "=" + rowID;

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, name);
        newValues.put(KEY_INFO, info);

        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    public boolean updateFullRow(long rowID, int dateDay, int dateMonth, int dateYear, int mileage, float gallons, int full, float price){
        String where = KEY_ROW_ID + "=" + rowID;

        ContentValues newRow = new ContentValues();
        newRow.put(KEY_DATE_DAY, dateDay);
        newRow.put(KEY_DATE_MONTH, dateMonth);
        newRow.put(KEY_DATE_YEAR, dateYear);
        newRow.put(KEY_MILEAGE, mileage);
        newRow.put(KEY_GALLONS, gallons);
        newRow.put(KEY_FULL, full);
        newRow.put(KEY_PRICE, price);

        return db.update(DATABASE_TABLE, newRow, where, null) != 0;
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
