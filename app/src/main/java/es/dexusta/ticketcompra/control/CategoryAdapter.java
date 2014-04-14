package es.dexusta.ticketcompra.control;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.dexusta.ticketcompra.R;

import es.dexusta.ticketcompra.model.Category;

public class CategoryAdapter extends DBObjectAdapter<Category> {

    public CategoryAdapter(Context context) {
        super(context);
    }
    
    public CategoryAdapter(Context context, List<Category> list) {
        super(context, list);        
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = getInflater().inflate(R.layout.category_row, parent, false);
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.tvCategory.setText(getItem(position).getName());

        return view;
    }

    class ViewHolder {
        TextView tvCategory;

        public ViewHolder(View v) {
            tvCategory = (TextView) v.findViewById(R.id.tv_category);
        }
    }
}
