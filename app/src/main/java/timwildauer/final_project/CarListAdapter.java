package timwildauer.final_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/*
 * a class that sets list entries for ArrayLists of cars
 */
public class CarListAdapter extends ArrayAdapter<String> {

    ArrayList<String> carNameList;
    ArrayList<String> carInfoList;

    public CarListAdapter(Context context, ArrayList<String> names, ArrayList<String> info) {
        super(context, R.layout.car_list_adapter, names);
        carNameList = names;
        carInfoList = info;
    }

    /*
     * set the entries for name and info into text boxes to be set in a list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater carListInflater = LayoutInflater.from(getContext());

        View carListView = carListInflater.inflate(R.layout.car_list_adapter, parent, false);

        String carName = carNameList.get(position);
        String carInfo = carInfoList.get(position);

        TextView carNameText = (TextView) carListView.findViewById(R.id.carName);

        TextView carInfoText = (TextView) carListView.findViewById(R.id.carInfo);

        carNameText.setText(carName);

        carInfoText.setText(carInfo);

        return carListView;
    }
}
