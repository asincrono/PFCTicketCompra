package es.dexusta.ticketcompra.control;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.dexusta.ticketcompra.R;

import es.dexusta.ticketcompra.model.Shop;

public class ShopAdapter extends DBObjectAdapter<Shop> {
    private static final String  TAG   = "ShopAdapter";
    private static final boolean DEBUG = true;

    public ShopAdapter(Context context) {
        super(context);
    }

    public ShopAdapter(Context context, List<Shop> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = getInflater().inflate(R.layout.shop_row, parent, false);
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.tvShopTown.setText(getItem(position).getTownName());
        holder.tvShopAddress.setText(getItem(position).getAddress());

        return view;
    }

    class ViewHolder {
        TextView tvShopTown;
        TextView tvShopAddress;

        ViewHolder(View v) {
            tvShopTown= (TextView) v.findViewById(R.id.tv_shop_town);
            tvShopAddress = (TextView) v.findViewById(R.id.tv_shop_address);
        }
    }
}
