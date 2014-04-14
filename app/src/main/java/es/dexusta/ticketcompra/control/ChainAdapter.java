package es.dexusta.ticketcompra.control;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.dexusta.ticketcompra.R;

import es.dexusta.ticketcompra.model.Chain;

/**
 * Created by asincrono on 16/05/13.
 */
public class ChainAdapter extends DBObjectAdapter<Chain> {

    public ChainAdapter(Context context) {
        super(context);
    }
    
    public ChainAdapter(Context context, List<Chain> list) {
        super(context, list);
    }    

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = getInflater().inflate(R.layout.chain_row, parent, false);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        
        holder.tvChain.setText(getItem(position).getName());
        
        return view;
    }

    class ViewHolder {
        TextView tvChain;

        ViewHolder(View view) {
            tvChain = (TextView) view.findViewById(R.id.tv_chain);
        }
    }
}
