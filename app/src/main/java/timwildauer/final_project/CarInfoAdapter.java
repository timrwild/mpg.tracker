package timwildauer.final_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/*
 * a class that sets each entry in a list that displays data
 */
public class CarInfoAdapter extends ArrayAdapter<String> {

    ArrayList<String> dateList;
    ArrayList<String> mileageList;
    ArrayList<String> gallonsList;
    ArrayList<String> fullList;
    ArrayList<String> priceList;
    ArrayList<String> mpgList;
    // set ArrayLists for all the data that will be displayed

    /*
     * add data to the ArrayLists
     */
    public CarInfoAdapter(Context context, ArrayList<String> date, ArrayList<String> mileage, ArrayList<String> gallons, ArrayList<String> full, ArrayList<String> price, ArrayList<String> mpg){
        super(context, R.layout.car_info_adapter, date);

        dateList = date;
        mileageList = mileage;
        gallonsList = gallons;
        fullList = full;
        priceList = price;
        mpgList = mpg;
    }

    /*
     * for each entry in the ArrayLists, set a TableRow in the ListView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater carInfoInflater = LayoutInflater.from(getContext());

        View carInfoView = carInfoInflater.inflate(R.layout.car_info_adapter, parent, false);

        String Date = dateList.get(position);
        String Mileage = mileageList.get(position);
        String Gallons = String.format("%.3f", Float.valueOf(gallonsList.get(position)));
        String Full;
        if (fullList.get(position).equals("0")){
            Full = "\u2610";
        } else {
            Full = "\u2611";
        }
        String Price = "$" + priceList.get(position);
        String MPG = String.format("%.1f", Float.valueOf(mpgList.get(position)));

        TextView dateText = (TextView) carInfoView.findViewById(R.id.data_table_date);
        TextView mileageText = (TextView) carInfoView.findViewById(R.id.data_table_mileage);
        TextView gallonsText = (TextView) carInfoView.findViewById(R.id.data_table_gallons);
        TextView fullText = (TextView) carInfoView.findViewById(R.id.data_table_full);
        TextView priceText = (TextView) carInfoView.findViewById(R.id.data_table_price);
        TextView mpgText = (TextView) carInfoView.findViewById(R.id.data_table_mpg);

        dateText.setText(Date);
        mileageText.setText(Mileage);
        gallonsText.setText(Gallons);
        fullText.setText(Full);
        priceText.setText(Price);
        mpgText.setText(MPG);

        return carInfoView;
    }
}
