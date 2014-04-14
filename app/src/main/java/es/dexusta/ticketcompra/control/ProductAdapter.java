package es.dexusta.ticketcompra.control;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.dexusta.ticketcompra.R;

import es.dexusta.ticketcompra.model.Product;

public class ProductAdapter extends DBObjectAdapter<Product> {
    
    public ProductAdapter(Context context) {
        super(context);
    }

    public ProductAdapter(Context context, List<Product> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        
        if (view == null) {
            view = getInflater().inflate(R.layout.product_row, parent, false);           
        }
        
        ViewHolder holder = (ViewHolder) view.getTag();
        
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }            
        
        holder.tvProduct.setText(getItem(position).getName());
        
        return view;
    }
    
    class ViewHolder {
        TextView tvProduct;
        
        public ViewHolder(View v) {
            tvProduct = (TextView) v.findViewById(R.id.tv_product);
        }
    }
}
