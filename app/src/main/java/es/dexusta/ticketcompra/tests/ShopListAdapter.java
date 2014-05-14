package es.dexusta.ticketcompra.tests;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.control.DBObjectAdapter;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Shop;

/**
 * Created by asincrono on 13/05/14.
 */
public class ShopListAdapter extends DBObjectAdapter<Shop> {
    private static final String TAG = "ShopListAdapter";

    private HashMap<Long, Chain> mChainMap;

    public ShopListAdapter(Context context, List<Shop> list, HashMap<Long, Chain> chainMap) {
        super(context, list);
        mChainMap = chainMap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = getInflater().inflate(R.layout.list_shops_row, parent, false);
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder == null) {
            holder = new ViewHolder(view);
        }

        Shop shop = getItem(position);

        holder.tvChainName.setText(mChainMap.get(shop.getChainId()).getName());
        holder.tvAddress.setText(shop.getAddress());
        holder.tvTownName.setText(shop.getTownName());
        return view;
    }

    class ViewHolder {
        TextView tvChainName;
        TextView tvTownName;
        TextView tvAddress;

        ViewHolder(View v) {
            tvChainName = (TextView) v.findViewById(R.id.tv_chain_name);
            tvTownName = (TextView) v.findViewById(R.id.tv_town_name);
            tvAddress = (TextView) v.findViewById(R.id.tv_address);
        }
    }
}
