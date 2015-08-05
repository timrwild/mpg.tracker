package timwildauer.final_project;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.io.FileOutputStream;
import java.util.ArrayList;

/*
 * a class that displays a list of cars from a database and allows the user to
 * add new cars or edit existing ones
 */
public class CarList extends ActionBarActivity {

    ArrayList<Long> carsID = new ArrayList<>();
    ArrayList<String> carsNames = new ArrayList<>();
    ArrayList<String> carsInfo = new ArrayList<>();
    // set ArrayList of cars and info to be used in the ListView

    ListAdapter carListAdapter;
    ListView carListView;
    // set views to be changed later

    CarListDatabase carListDatabase;
    // set database for cars

    private final int ADD_CAR_RESULT = 1;
    private final int EDIT_CAR_RESULT = 2;
    // set result codes

    /*
     * a method that begins the database, adds existing cars to ArrayLists,
     * and displays a list of the cars
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openDB();
        setupCarList();

        setView();
    }

    /*
     * a method that is called every time the main activity is displayed
     *      sets the content view to display a list
     *      calls the two ArrayLists carsNames and carsInfo and sets data from entries
     *      and sets them in a list
     *
     * sets an onItemClickListener to track when the user clicks on one of the entries in the list
     * sends the user view the car info
     */
    public void setView(){
        setContentView(R.layout.car_list);
        carListAdapter = new CarListAdapter(this, carsNames, carsInfo);
        carListView = (ListView) findViewById(R.id.carList);
        carListView.setAdapter(carListAdapter);
        // set the view to display the list of cars with info from ArrayLists

        carListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                moveToCarInfo(i);
            }
        });
    }

    /*
     * sends the user to the displayCarInfo screen passing along the name and information
     * for the car that was selected
     */
    private void moveToCarInfo(int i) {
        Intent displayCarInfo = new Intent(this, CarInfo.class);

        displayCarInfo.putExtra("car_name", carsNames.get(i));
        displayCarInfo.putExtra("car_info", carsInfo.get(i));

        startActivity(displayCarInfo);
    }

    /*
     * when this window is destroyed, close the database
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        closeDB();
    }

    /*
     * sends the user to a screen to edit existing cars and expects back a result
     */
    public void editCars(View view) {

        int result = EDIT_CAR_RESULT;
        Intent editData = new Intent(this, CarListEdit.class);

        startActivityForResult(editData, result);
    }

    /*
     * sends the user to a screen to add a car and expects a result
     */
    public void addCars(View view) {
        final int result = ADD_CAR_RESULT;

        Intent addCar = new Intent(this, CarListAdd.class);
        startActivityForResult(addCar, result);
    }

    /*
     * if results are canceled, reset the main view
     * when cars are edited, reset the main view
     * when a car is added, add this car to the ArrayLists and the database,
     * and reset the main view
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED){
            setupCarList();
            setView();
        } else if (requestCode == EDIT_CAR_RESULT) {
            setupCarList();
            setView();
        } else if (requestCode == ADD_CAR_RESULT) {
            String carName = data.getStringExtra("car_name");
            String carMake = data.getStringExtra("car_make");
            String carModel = data.getStringExtra("car_model");
            int carYear = data.getIntExtra("car_year", 0);

            carsNames.add(carName);
            carsInfo.add(carYear + " " + carMake + " " + carModel);

            addCar(carName, carMake, carModel, carYear);

            setupCarList();
            setView();
        }
    }

    /*
     * clear the ArrayLists and repopulate them from the database
     */
    private void setupCarList(){
        carsID.clear();
        carsNames.clear();
        carsInfo.clear();

        Cursor cursor = carListDatabase.getAllRows();

        if (cursor.moveToFirst()){
            do {
                Long id = cursor.getLong(carListDatabase.COL_ROW_ID);
                String name = cursor.getString(carListDatabase.COL_NAME);
                String make = cursor.getString(carListDatabase.COL_MAKE);
                String model = cursor.getString(carListDatabase.COL_MODEL);
                int year = cursor.getInt(carListDatabase.COL_YEAR);

                carsID.add(id);
                carsNames.add(name);
                carsInfo.add(year + " " + make + " " + model);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    /*
     * add a new car to the database
    */
    public void addCar(String name, String make, String model, int year){
        carListDatabase.insertRow(name, make, model, year);
    }

    /*
     * export the list of cars as a ".csv"
     */
    public void exportData() {
        String filename = "car_list.csv";

        getFilesDir();

        Cursor cursor = carListDatabase.getAllRows();

        String message = "";

        if (cursor.moveToFirst()){
            do {
                String name = cursor.getString(CarListDatabase.COL_NAME);
                String year = cursor.getString(CarListDatabase.COL_YEAR);
                String make = cursor.getString(CarListDatabase.COL_MAKE);
                String model = cursor.getString(CarListDatabase.COL_MODEL);

                message += name + ", " + year + ", " + make + ", " + model + "\n";
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
     * open the database
     */
    private void openDB(){
        carListDatabase = new CarListDatabase(this);
        carListDatabase.open();
    }

    /*
     * close the database after exporting all the data
     */
    private void closeDB(){
        exportData();
        carListDatabase.close();
    }
}
