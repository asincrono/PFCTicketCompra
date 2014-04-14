package es.dexusta.ticketcompra.control;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import es.dexusta.ticketcompra.model.Region;

public class RegionAdapter extends DBObjectAdapter<Region> {
    private static final String TAG = "RegionAdapter";
    private static final boolean DEBUG = true;
        
    public RegionAdapter(Context context) {
        super(context);
    }
    
    public RegionAdapter(Context context, List<Region> list) {
        super(context, list);
    }
    
    private View getView(int position, View convertView, ViewGroup parent, boolean isDropDown) {
        View view = convertView;        
        int resourceId;
        
        if (view == null) {
            if (isDropDown) {
                resourceId = android.R.layout.simple_spinner_dropdown_item;
            }
            else {
                resourceId = android.R.layout.simple_spinner_item;
            }                        
            view = getInflater().inflate(resourceId, parent, false);
        }
                
        ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position).getName());
        
        return view;
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
