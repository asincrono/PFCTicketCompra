package es.dexusta.ticketcompra.control;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.model.Detail;

public class ReceiptDetailListAdapter extends DBObjectAdapter<Detail> {
    private static final int TYPE_DETAIL_LINE = 0;
    private static final int TYPE_TOTAL_LINE  = 1;
    
    // Alt+0164 ¤ stands for currency symbol (according to locale).
    private NumberFormat     mPriceFormatter  = new DecimalFormat("###0.00¤");
    private NumberFormat     mWeightFormatter = new DecimalFormat("###.###g");
    
    private Context mContext;
    
    public ReceiptDetailListAdapter(Context context) {
        super(context);        
    }
    
    public ReceiptDetailListAdapter(Context context, List<Detail> list) {
        super(context, list);
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        
        if (count > 0) {
            ++count;
        }
        
        return count;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (++position == getCount()) {
            return TYPE_TOTAL_LINE;
        }
        return TYPE_DETAIL_LINE;
    }
    
    private int getTotal() {
        int total = 0;
        int last = getCount() - 1;
        
        for (int position = 0; position < last; ++position) {
            total += getItem(position).getPrice();
        }
        
        return total;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        
        int itemViewType = getItemViewType(position);
        
        switch (itemViewType) {
        case TYPE_TOTAL_LINE:
            if (view == null) {
                view = getInflater().inflate(R.layout.receipt_total_row, parent, false);
            }
            
            holder = (ViewHolder) view.getTag();
            
            if (holder == null || (holder.getItemViewType() != TYPE_TOTAL_LINE)) {
                holder = new TotalViewHolder(view);
            }
            
            ((TotalViewHolder) holder).tvTotal.setText(Integer.toString(getTotal()));
            
            break;

        default: // TYPE_DETAIL_LINE.
            if (view == null) {
                view = getInflater().inflate(R.layout.receipt_detail_row, parent, false);    
            }
            
            holder = (ViewHolder) view.getTag();
            
            if (holder == null || (holder.getItemViewType() != TYPE_DETAIL_LINE)) {
                holder = new DetailViewHolder(view);
            }
            
            Detail detail = getItem(position);
            ((DetailViewHolder) holder).tvProductName.setText(detail.getProductName());
            ((DetailViewHolder) holder).tvUnits.setText(Integer.toString(detail.getUnits()));
            
            
            boolean blank = false;
            StringBuilder weightStr = new StringBuilder();
            weightStr.append('(');
            
            int volume = detail.getVolume();
            int weight = detail.getWeight();
            
            if (volume != 0) {
                weightStr.append(volume);
                weightStr.append(" ml.");
            }            
            else if (weight != 0) {
                weightStr.append(weight);
                weightStr.append(" g.");
            }
            else blank = true;
            
            weightStr.append(')');
            
            if (!blank) {
                ((DetailViewHolder) holder).tvWeight.setText(weightStr);
            }
                        
            ((DetailViewHolder) holder).tvPrice.setText(Integer.toString(detail.getPrice()));           
            break;
        }
        
        return view;
    }
    
    abstract class ViewHolder {
        final int itemViewType;
        
        ViewHolder(int itemViewType) {
            this.itemViewType = itemViewType;
        }
        
        public int getItemViewType() {
            return itemViewType;
        }
    }
    
    class DetailViewHolder extends ViewHolder {        
        TextView tvProductName;
        TextView tvUnits;
        TextView tvWeight;
        TextView tvPrice;
        
        public DetailViewHolder(View view) {
            super(TYPE_DETAIL_LINE);
            tvProductName = (TextView) view.findViewById(R.id.tv_product_name);
            tvUnits = (TextView) view.findViewById(R.id.tv_units);
            tvWeight = (TextView) view.findViewById(R.id.tv_weight);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
        }
    }
    
    class TotalViewHolder extends ViewHolder {
        TextView tvTotal;
        
        public TotalViewHolder(View view) {
            super(TYPE_TOTAL_LINE);
            tvTotal = (TextView) view.findViewById(R.id.tv_total);            
        }
        
    }

}
