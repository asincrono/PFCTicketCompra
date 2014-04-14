package es.dexusta.ticketcompra.control;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.model.Receipt;

public class ReceiptAdapter extends DBObjectAdapter<Receipt>{
    
    private DataSource mDS;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public ReceiptAdapter(Context context) {
        super(context);    
        mDS = DataSource.getInstance(context);
    }
    
    public ReceiptAdapter(Context context, List<Receipt> list) {
        this(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        
        if (view == null) {
            view = getInflater().inflate(R.layout.receipt_row, parent, false);
        }
        
        ViewHolder holder = (ViewHolder) view.getTag();
        
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        
        Receipt receipt = getItem(position);
        if (mDS != null) {
            
        }
        holder.tvChainName.setText(mDS.getChainNameByShopId(receipt.getShopId()));
        holder.tvDate.setText(df.format(receipt.getDateTimestamp()));
        holder.tvAmount.setText(Integer.toString(receipt.getTotal()));
        
        return view;
    }

    class ViewHolder {
        TextView tvChainName;
        TextView tvDate;
        TextView tvAmount;
        
        ViewHolder(View v) {
            tvChainName = (TextView) v.findViewById(R.id.tv_chain_name);
            tvDate = (TextView) v.findViewById(R.id.tv_date);
            tvAmount = (TextView) v.findViewById(R.id.tv_amount);
        }
    }
}
