package timwildauer.final_project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

/*
 * a class that allows the user to edit data associated with a car
 */
public class CarInfoEdit extends ActionBarActivity {

    CarInfoDatabase carInfoDatabase;
    // set database for car info

    String carName;
    String carInfo;
    // set car name and info

    Cursor cycleCursor;
    // set cursor to gather car data

    ArrayList<Long> selectedRows = new ArrayList<>();
    // set array for data entries

    int rowNumber = 0;
    // start listing data and index 0

    /*
     * set car name and information to gather existing data
     * if no data exists, cancel the result
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent getIntent = getIntent();
        carName = getIntent.getStringExtra("car_name");
        carInfo = getIntent.getStringExtra("car_info");

        openDB();

        setTitle("Edit " + carName + "'s Data");
        setContentView(R.layout.car_info_edit);

        cycleCursor = showData();

        if (cycleCursor.moveToFirst()) {
            do {
                getCarInfoID(cycleCursor);
            } while (cycleCursor.moveToNext());

            getRow(carInfoDatabase.getRow(selectedRows.get(rowNumber)));
        } else {
            setResult(RESULT_CANCELED);
            closeDB();
            finish();
        }
    }

    /*
     * if the menu back button is pressed, save the current row and cancel the result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);

            saveRow(rowNumber);
            setResult(RESULT_CANCELED);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * when the window is destroyed, close the database
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        closeDB();
    }

    /*
     * return a cursor for data that matches the given car name and info
     */
    public Cursor showData(){
        SQLiteDatabase db = carInfoDatabase.db;
        String[] args = {carName, carInfo};
        String[] returnColumn =  {carInfoDatabase.KEY_ROW_ID};

        return db.query(carInfoDatabase.DATABASE_TABLE, returnColumn, "carName=? and carInfo=?", args, null, null, carInfoDatabase.KEY_MILEAGE, null);
    }

    /*
     * add id numbers for the data to the ArrayList
     */
    public void getCarInfoID(Cursor cursor) {
        Long id = cursor.getLong(carInfoDatabase.COL_ROW_ID);

        selectedRows.add(id);
    }

    /*
     * set entries with existing data
     */
    public void getRow(Cursor cursor){
        String dateDay = cursor.getString(carInfoDatabase.COL_DATE_DAY);
        String dateMonth = cursor.getString(carInfoDatabase.COL_DATE_MONTH);
        String dateYear = cursor.getString(carInfoDatabase.COL_DATE_YEAR);
        String mileage = cursor.getString(carInfoDatabase.COL_MILEAGE);
        String gallons = cursor.getString(carInfoDatabase.COL_GALLONS);
        String full = cursor.getString(carInfoDatabase.COL_FULL);
        String price = cursor.getString(carInfoDatabase.COL_PRICE);

        EditText originalDateDay = (EditText) findViewById(R.id.edit_transaction_date_day);
        EditText originalDateMonth = (EditText) findViewById(R.id.edit_transaction_date_month);
        EditText originalDateYear = (EditText) findViewById(R.id.edit_transaction_date_year);
        EditText originalMileage = (EditText) findViewById(R.id.edit_transaction_mileage);
        EditText originalGallons = (EditText) findViewById(R.id.edit_transaction_gallons);
        CheckBox originalFull = (CheckBox) findViewById(R.id.edit_transaction_tank_full);
        EditText originalPrice = (EditText) findViewById(R.id.edit_transaction_price);

        originalDateDay.setText(dateDay);
        originalDateMonth.setText(dateMonth);
        originalDateYear.setText(dateYear);

        originalMileage.setText(mileage);
        originalGallons.setText(String.valueOf(gallons));
        if (full.equals("0")) {
            originalFull.setChecked(false);
        } else if (full.equals("1")) {
            originalFull.setChecked(true);
        }
        originalPrice.setText(String.valueOf(price));
    }

    /*
     * save changed data
     * prompt for more data if entries are blank
     */
    public boolean saveRow(int i){
        EditText DateDay = (EditText) findViewById(R.id.edit_transaction_date_day);
        EditText DateMonth = (EditText) findViewById(R.id.edit_transaction_date_month);
        EditText DateYear = (EditText) findViewById(R.id.edit_transaction_date_year);
        EditText Mileage = (EditText) findViewById(R.id.edit_transaction_mileage);
        EditText Gallons = (EditText) findViewById(R.id.edit_transaction_gallons);
        CheckBox Full = (CheckBox) findViewById(R.id.edit_transaction_tank_full);
        EditText Price = (EditText) findViewById(R.id.edit_transaction_price);

        if (DateDay.getText().toString().equals("") || DateMonth.getText().toString().equals("") ||
                DateYear.getText().toString().equals("") || Mileage.getText().toString().equals("") ||
                Gallons.getText().toString().equals("") || Price.getText().toString().equals("")) {
            Toast.makeText(CarInfoEdit.this, "Enter all information", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            long row_id = selectedRows.get(i);
            int dateDay = Integer.valueOf(DateDay.getText().toString());
            int dateMonth = Integer.valueOf(DateMonth.getText().toString());
            int dateYear = Integer.valueOf(DateYear.getText().toString());
            int mileage = Integer.valueOf(Mileage.getText().toString());
            float gallons = Float.valueOf(Gallons.getText().toString());
            int full = 0;
            if (Full.isChecked()) {
                full = 1;
            }
            float price = Float.valueOf(Price.getText().toString());

            carInfoDatabase.updateFullRow(row_id, dateDay, dateMonth, dateYear, mileage, gallons, full, price);
            return true;
        }
    }

    /*
     * delete a row from the database
     */
    public void deleteEntry(View view) {
        carInfoDatabase.deleteRow(selectedRows.get(rowNumber));
        selectedRows.remove(rowNumber);
        if (checkIndex()) {
            getRow(carInfoDatabase.getRow(selectedRows.get(rowNumber)));
        }
    }

    /*
     * if all data is entered, move to the next entry
     */
    public void indexForward(View view){
        if (saveRow(rowNumber)) {
            rowNumber += 1;
            checkIndex();
            if (checkIndex()) {
                getRow(carInfoDatabase.getRow(selectedRows.get(rowNumber)));
            }
        }
    }

    /*
     * if all data is entered, move to the previous entry
     */
    public void indexBackwards(View view){
        if (saveRow(rowNumber)) {
            rowNumber -= 1;
            if (checkIndex()) {
                getRow(carInfoDatabase.getRow(selectedRows.get(rowNumber)));
            }
        }
    }

    /*
     * if the index is still not in range, set the send a result
     */
    private boolean checkIndex(){
        if (rowNumber < 0 || rowNumber >= selectedRows.size()){
            setResult(RESULT_OK);
            finish();
            return false;
        }
        return true;
    }

    /*
     * when the view is initiated, open the database
     */
    private void openDB(){
        carInfoDatabase = new CarInfoDatabase(this);
        carInfoDatabase.open();
    }

    /*
     * when the view is destroyed, close the database
     */
    private void closeDB(){
        carInfoDatabase.close();
    }
}
