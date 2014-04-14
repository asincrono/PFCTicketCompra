package es.dexusta.ticketcompra.control;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import es.dexusta.ticketcompra.model.Category;

public   
class CategorySpinnerAdapter extends DBObjectAdapter<Category> {



    public CategorySpinnerAdapter(Context context) {
        super(context);
    }

    public CategorySpinnerAdapter(Context context, List<Category> list) {
        super(context, list);
    }

    private View getView(int position, View convertView, ViewGroup parent, boolean isDropdown) {
        View v = convertView;

        if (v == null) {
            int resourceId; 
            if (isDropdown) {
                resourceId = android.R.layout.simple_spinner_dropdown_item;
            }
            else {
                resourceId = android.R.layout.simple_spinner_item;
            }

            v = getInflater().inflate(resourceId, parent, false);        
        }
        
        ((TextView) v.findViewById(android.R.id.text1)).setText(getItem(position).getName());
        
        return v;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, true);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, false);
    }

}