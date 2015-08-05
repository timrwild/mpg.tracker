package timwildauer.final_project;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/*
 * a class that handles all the data for each car
 * allows the user to add new data, as well as edit or delete existing data
 */
public class CarInfo extends ActionBarActivity {

    CarInfoDatabase carInfoDatabase;
    // set database for car info

    String carName;
    String carInfo;
    // strings for car name and info

    private final int NEW_DATA_RESULT = 3;
    private final int EDIT_DATA_RESULT = 4;
    // set integers for results

    ArrayList<String> dateList = new ArrayList<>();
    ArrayList<String> mileageList = new ArrayList<>();
    ArrayList<String> gallonsList = new ArrayList<>();
    ArrayList<String> fullList = new ArrayList<>();
    ArrayList<String> priceList = new ArrayList<>();
    ArrayList<String> mpgList = new ArrayList<>();
    // set ArrayLists to view data

    /*
     * when the window is created, set the car name and info, open the database,
     * set the screen title, and display all data associated with the car
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent getCar = getIntent();
        carName = getCar.getStringExtra("car_name");
        carInfo = getCar.getStringExtra("car_info");

        openDB();

        setTitle(carName + "'s Data");
        setContentView(R.layout.car_info);

        setCarInfo(getCarData());
    }

    /*
     * when the window is destroyed, close the database
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    /*
     * return a cursor that displays all the data associated with the car ordered by mileage
     */
    public Cursor getCarData() {
        SQLiteDatabase db = carInfoDatabase.db;
        String[] args = {carName, carInfo};

        return db.query(carInfoDatabase.DATABASE_TABLE, null, "carName=? and carInfo=?", args, null, null, carInfoDatabase.KEY_MILEAGE, null);
    }

    /*
     * with the cursor passed in, add data to ArrayLists and set this data to be displayed
     */
    public void setCarInfo(Cursor cursor) {

        int mileagePrevious = 0;
        float totalGallons = 0;

        dateList.clear();
        mileageList.clear();
        gallonsList.clear();
        fullList.clear();
        priceList.clear();
        mpgList.clear();

        if (cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(carInfoDatabase.COL_ROW_ID);
                int dateDay = cursor.getInt(carInfoDatabase.COL_DATE_DAY);
                int dateMonth = cursor.getInt(carInfoDatabase.COL_DATE_MONTH);
                int dateYear = cursor.getInt(carInfoDatabase.COL_DATE_YEAR);
                int mileage = cursor.getInt(carInfoDatabase.COL_MILEAGE);
                float gallons = cursor.getFloat(carInfoDatabase.COL_GALLONS);
                int full = cursor.getInt(carInfoDatabase.COL_FULL);
                float price = cursor.getFloat(carInfoDatabase.COL_PRICE);
                float mpg = 0;

                String date = dateMonth + "/" + dateDay + "/" + dateYear;

                totalGallons += gallons;
                if (full == 1) {
                    if (mileagePrevious == 0) {
                        mpg = 0;
                    } else {
                        float newMPG = (mileage - mileagePrevious) / totalGallons;
                        carInfoDatabase.updateRow(id, newMPG);
                        mpg = newMPG;
                        totalGallons = 0;
                    }
                    mileagePrevious = mileage;
                }

                dateList.add(date);
                mileageList.add(NumberFormat.getNumberInstance(Locale.US).format(mileage));
                gallonsList.add(String.valueOf(gallons));
                fullList.add(String.valueOf(full));
                priceList.add(String.valueOf(price));
                mpgList.add(String.valueOf(mpg));
            } while (cursor.moveToNext());
        }
        cursor.close();
        setDataList();
    }

    /*
     * take data from ArrayLists and display it in a table
     */
    public void setDataList() {
        setContentView(R.layout.car_info);
        ListAdapter carInfoAdapter = new CarInfoAdapter(this, dateList, mileageList, gallonsList, fullList, priceList, mpgList);
        ListView carInfoView = (ListView) findViewById(R.id.data_view);
        carInfoView.setAdapter(carInfoAdapter);
    }

    /*
     * create intent to edit data for the car
     */
    public void editData(View view) {
        final int result = EDIT_DATA_RESULT;
        Intent editData = new Intent(this, CarInfoEdit.class);
        editData.putExtra("car_name", carName);
        editData.putExtra("car_info", carInfo);

        startActivityForResult(editData, result);
    }

    /*
     * create an intent to add data fir the car
     */
    public void newData(View view) {
        final int result = NEW_DATA_RESULT;
        Intent getNewData = new Intent(this, CarInfoAdd.class);
        getNewData.putExtra("car_name", carName);

        startActivityForResult(getNewData, result);
    }

    /*
     * if activities are canceled, reset the screen to view data
     * when new data is entered, add this data to the database and reset the screen to view the data
     * when data is done being edited, reset the screen
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            setCarInfo(getCarData());
        } else if (requestCode == NEW_DATA_RESULT) {
            int dateDay = data.getIntExtra("transaction_date_day", 0);
            int dateMonth = data.getIntExtra("transaction_date_month", 0);
            int dateYear = data.getIntExtra("transaction_date_year", 0);
            int mileage = data.getIntExtra("transaction_mileage", 0);
            float gallons = data.getFloatExtra("transaction_gallons", 0);
            int full = data.getIntExtra("transaction_full", 0);
            float price = data.getFloatExtra("transaction_price", 0);

            carInfoDatabase.insertRow(carName, carInfo, dateDay, dateMonth, dateYear, mileage, gallons, full, price, 0);

            setCarInfo(getCarData());
        } else if (requestCode == EDIT_DATA_RESULT) {
            setCarInfo(getCarData());
        }
    }

    /*
     * when a car name or data is edited, rename this cars information in the info database
     */
    public static void updateCarInfo(String startName, String startInfo, String newName, String newInfo, Context context) {
        CarInfoDatabase carInfoDatabase = new CarInfoDatabase(context);
        carInfoDatabase.open();

        SQLiteDatabase db = carInfoDatabase.db;
        String[] args = {startName, startInfo};

        Cursor search = db.query(carInfoDatabase.DATABASE_TABLE, null, "carName=? and carInfo=?", args, null, null, carInfoDatabase.KEY_MILEAGE, null);

        if (search.moveToFirst()) {
            do {
                Long id = search.getLong(carInfoDatabase.COL_ROW_ID);
                carInfoDatabase.updateNameInfo(id, newName, newInfo);
            } while (search.moveToNext());
        }
        search.close();
        carInfoDatabase.close();
    }

    /*
     * export data for the car in a ".csv"
     */
    public void exportData() {
        String filename = carName + " " + carInfo + ".csv";

        getFilesDir();

        Cursor cursor = carInfoDatabase.getAllRows();

        String message = "";

        if (cursor.moveToFirst()){
            do {
                String name = cursor.getString(CarInfoDatabase.COL_NAME);
                String info = cursor.getString(CarInfoDatabase.COL_INFO);
                String dateDay = cursor.getString(CarInfoDatabase.COL_DATE_DAY);
                String dateMonth = cursor.getString(CarInfoDatabase.COL_DATE_MONTH);
                String dateYear = cursor.getString(CarInfoDatabase.COL_DATE_YEAR);
                String mileage = cursor.getString(CarInfoDatabase.COL_MILEAGE);
                String gallons = cursor.getString(CarInfoDatabase.COL_GALLONS);
                String full = cursor.getString(CarInfoDatabase.COL_FULL);
                String price = cursor.getString(CarInfoDatabase.COL_PRICE);
                String mpg = cursor.getString(CarInfoDatabase.COL_MPG);

                message += name + ", " + info + ", " + dateMonth + "/" + dateDay + "/" + dateYear + ", "
                        + mileage + ", " + gallons + ", " + full + ", " + price + ", " + mpg + "\n";
            } while (cursor.moveToNext());
        }
        cursor.close();

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(message.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * when the view is initiated, open the database
     */
    private void openDB() {
        carInfoDatabase = new CarInfoDatabase(this);
        carInfoDatabase.open();
        // open the database
    }

    /*
     * when the view is destroyed, close the database
     */
    private void closeDB() {
        exportData();
        carInfoDatabase.close();
    }
}