package es.dexusta.ticketcompra.control;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.model.Subcategory;

public class SubcategoryAdapter extends DBObjectAdapter<Subcategory> {
    private static final String  TAG   = "SubcategoryAdapter";
    private static final boolean DEBUG = true;

    public SubcategoryAdapter(Context context) {
        super(context);
    }

    public SubcategoryAdapter(Context context, List<Subcategory> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = getInflater().inflate(R.layout.subcategory_row, parent, false);
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.tvSubcategory.setText(getItem(position).getName());

        return view;
    }

    class ViewHolder {
        TextView tvSubcategory;

        public ViewHolder(View v) {
            tvSubcategory = (TextView) v.findViewById(R.id.tv_subcategory);
        }
    }
}
