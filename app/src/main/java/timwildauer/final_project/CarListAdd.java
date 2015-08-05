package timwildauer.final_project;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/*
 * a class that allows the user to submit a new car
 */
public class CarListAdd extends ActionBarActivity {

    /*
     * set view to add new car
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_list_add);
    }

    /*
     * if the user presses the menu back button, cancel the result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);

            setResult(RESULT_CANCELED);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
     * when the user presses submit, the text is taken from the text boxes
     * and turned into strings for "name" and "info." This information is
     * added to an intent and sent back as the result
     */
    public void submitCar(View view) {
        EditText enteredCarName = (EditText) findViewById(R.id.car_name);
        EditText enteredCarMake = (EditText) findViewById(R.id.car_make);
        EditText enteredCarModel = (EditText) findViewById(R.id.car_model);
        EditText enteredCarYear = (EditText) findViewById(R.id.car_year);

        if (enteredCarName.getText().toString().equals("") || enteredCarMake.getText().toString().equals("") ||
                enteredCarModel.getText().toString().equals("") || enteredCarYear.getText().toString().equals("")){
            Toast.makeText(CarListAdd.this, "Enter all information", Toast.LENGTH_SHORT).show();
        } else {
            String carName = String.valueOf(enteredCarName.getText());
            String carMake = String.valueOf(enteredCarMake.getText());
            String carModel = String.valueOf(enteredCarModel.getText());
            int carYear = Integer.valueOf(enteredCarYear.getText().toString());

            Intent sendCarBack = new Intent();

            sendCarBack.putExtra("car_name", carName);
            sendCarBack.putExtra("car_make", carMake);
            sendCarBack.putExtra("car_model", carModel);
            sendCarBack.putExtra("car_year", carYear);

            setResult(RESULT_OK, sendCarBack);

            finish();
        }
    }
}
