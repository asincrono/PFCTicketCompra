package es.dexusta.ticketcompra.control;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import es.dexusta.ticketcompra.model.Town;

public class TownAdapter extends DBObjectAdapter<Town> {

    public TownAdapter(Context context) {
        super(context);
    }
    
    public TownAdapter(Context context, List<Town> list) {
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
        
        holder.tvTownName.setText(getItem(position).getName());
        
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

    class ViewHolder {
        TextView tvTownName;
        
        ViewHolder(View v) {
            tvTownName = (TextView) v.findViewById(android.R.id.text1);
        }
    }
}
