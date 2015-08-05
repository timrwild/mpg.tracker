package timwildauer.final_project;

import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Objects;

/*
 * a class that allows the user to edit or delete existing cars
 */
public class CarListEdit extends ActionBarActivity {

    CarListDatabase carListDatabase;
    // set the database for the cars

    Cursor cycleCursor;
    // set a cursor to track car entries

    ArrayList<Long> carsID = new ArrayList<>();
    // set an ArrayList of id numbers for existing cars

    int rowNumber = 0;
    // start listing data at index 0

    String startName;
    String startInfo;
    String newName;
    String newInfo;
    // set initial and changed car name and info

    /*
     * set the ArrayList to include all existing cars
     * if there are no existing cars, cancel the result
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openDB();

        setContentView(R.layout.car_list_edit);

        cycleCursor = carListDatabase.getAllRows();

        if (cycleCursor.moveToFirst()) {
            do {
                getCarListID(cycleCursor);
            } while (cycleCursor.moveToNext());

            getRow(carListDatabase.getRow(carsID.get(rowNumber)));
        } else {
            setResult(RESULT_CANCELED);
            closeDB();
            finish();
        }
    }

    /*
     * if the user presses the menu back button, cancel the result after saving data
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            if (saveRow(rowNumber)) {
                NavUtils.navigateUpFromSameTask(this);
                setResult(RESULT_CANCELED);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * add all id numbers for existing cars to the ArrayList
     */
    public void getCarListID(Cursor cursor) {
        Long id = cursor.getLong(carListDatabase.COL_ROW_ID);

        carsID.add(id);
    }

    /*
     * set text boxes for existing car information
     */
    public void getRow(Cursor cursor){

        String name = cursor.getString(carListDatabase.COL_NAME);
        String make = cursor.getString(carListDatabase.COL_MAKE);
        String model = cursor.getString(carListDatabase.COL_MODEL);
        int year = cursor.getInt(carListDatabase.COL_YEAR);

        EditText originalName = (EditText) findViewById(R.id.edit_car_name);
        EditText originalMake = (EditText) findViewById(R.id.edit_car_make);
        EditText originalModel = (EditText) findViewById(R.id.edit_car_model);
        EditText originalYear = (EditText) findViewById(R.id.edit_car_year);

        originalName.setText(name);
        originalMake.setText(make);
        originalModel.setText(model);
        originalYear.setText(String.valueOf(year));

        startName = name;
        startInfo = year + " " + make + " " + model;
    }

    /*
     * if all information is present, update the information in the database
     * if not all information is present, prompt the user to finish entering information
     */
    public boolean saveRow(int i){
        EditText Name = (EditText) findViewById(R.id.edit_car_name);
        EditText Make = (EditText) findViewById(R.id.edit_car_make);
        EditText Model = (EditText) findViewById(R.id.edit_car_model);
        EditText Year = (EditText) findViewById(R.id.edit_car_year);

        if (Name.getText().toString().equals("") || Make.getText().toString().equals("") ||
                Model.getText().toString().equals("") || Year.getText().toString().equals("")) {
            Toast.makeText(CarListEdit.this, "Enter all information", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            long row_id = carsID.get(i);
            String carName = String.valueOf(Name.getText());
            String carMake = String.valueOf(Make.getText());
            String carModel = String.valueOf(Model.getText());
            int carYear = Integer.valueOf(Year.getText().toString());
            carListDatabase.updateRow(row_id, carName, carMake, carModel, carYear);

            newName = carName;
            newInfo = carYear + " " + carMake + " " + carModel;

            return true;
        }
    }

    /*
     * delete a car entry and return to the list of cars
     */
    public void deleteEntry(View view) {
        carListDatabase.deleteRow(carsID.get(rowNumber));
        setResult(RESULT_OK);
        finish();
    }

    /*
     * if information has changed, change the information in CarInfoDatabase
     * move to the next car and update the view
     */
    public void indexForward(View view){
        if (saveRow(rowNumber)) {
            if (!Objects.equals(startName, newName) || !Objects.equals(startInfo, newInfo)) {
                CarInfo.updateCarInfo(startName, startInfo, newName, newInfo, this);
            }
            rowNumber += 1;
            checkIndex();
            if (checkIndex()) {
                getRow(carListDatabase.getRow(carsID.get(rowNumber)));
            }
        }
    }

    /*
     * if information has changed, change the information in CarInfoDatabase
     * move to the previous car and update the view
     */
    public void indexBackwards(View view){
        if (saveRow(rowNumber)) {
            if (!Objects.equals(startName, newName) || !Objects.equals(startInfo, newInfo)) {

                CarInfo.updateCarInfo(startName, startInfo, newName, newInfo, this);
            }
            rowNumber -= 1;
            if (checkIndex()) {
                getRow(carListDatabase.getRow(carsID.get(rowNumber)));
            }
        }
    }

    /*
     * if the index is out of range, send the result back
     */
    private boolean checkIndex(){
        if (rowNumber < 0 || rowNumber >= carsID.size()){
            setResult(RESULT_OK);
            finish();
            return false;
        }
        return true;
    }

    /*
     * open the database
     */
    private void openDB(){
        carListDatabase = new CarListDatabase(this);
        carListDatabase.open();
        // open the database
    }

    /*
     * close the database after exporting all the data
     */
    private void closeDB(){
        // carDatabase.deleteAll();
        carListDatabase.close();
        // close the database
    }
}
