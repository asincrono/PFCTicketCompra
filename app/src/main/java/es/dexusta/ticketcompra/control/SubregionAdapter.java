package es.dexusta.ticketcompra.control;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import es.dexusta.ticketcompra.model.Subregion;

public class SubregionAdapter extends DBObjectAdapter<Subregion> {

    public SubregionAdapter(Context context) {
        super(context);
    }
    
    public SubregionAdapter(Context context, List<Subregion> list) {
        super(context, list);
    }
    
    private View getView(int position, View convertView, ViewGroup parent, boolean isDropDown) {
        View view = convertView;        
        int resourceId;
        
        if (isDropDown) {
            resourceId = android.R.layout.simple_spinner_dropdown_item;
        }
        else {
            resourceId = android.R.layout.simple_spinner_item;
        }        
        
        if (view == null) {
            view = getInflater().inflate(resourceId, parent, false);
        }
        
        ViewHolder holder = (ViewHolder) view.getTag();
        
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        
        holder.tvSubregionName.setText(getItem(position).getName());
        
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {       
        return getView(position, convertView, parent, false);
    }
    
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, true);
    }

    class ViewHolder {
        TextView tvSubregionName;
        
        ViewHolder(View v) {
            tvSubregionName = (TextView) v.findViewById(android.R.id.text1);
        }
        
    }

}
