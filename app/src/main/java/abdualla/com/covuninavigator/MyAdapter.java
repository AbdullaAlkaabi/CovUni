package abdualla.com.covuninavigator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

class MyAdapter extends ArrayAdapter {
    private ArrayList<Place> mOriginalValues;
    private final ArrayList<Place> mDisplayedValues;


    private final Context context;
    private ArrayList<Place> data;
    private static LayoutInflater inflater = null;

    public MyAdapter(Context context, ArrayList<Place> list) {
        super(context, R.layout.place_list_item,list);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = list;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mDisplayedValues=list;
        this.mOriginalValues=list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.place_list_item,null);
        }
        TextView nameTV = (TextView) vi.findViewById(R.id.placename);
        TextView addressTV = (TextView) vi.findViewById(R.id.address);

        nameTV.setText(data.get(position).getName());
        addressTV.setText(data.get(position).getAddress());
        return vi;
    }
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                data = (ArrayList<Place>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Place> FilteredArrList = new ArrayList<Place>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Place>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).name;
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new Place(mOriginalValues.get(i).getName(),mOriginalValues.get(i).getAddress(),mOriginalValues.get(i).getLat(),mOriginalValues.get(i).getLng()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

}