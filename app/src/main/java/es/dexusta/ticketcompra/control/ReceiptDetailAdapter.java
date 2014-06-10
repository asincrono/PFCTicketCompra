package es.dexusta.ticketcompra.control;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.model.Detail;

public class ReceiptDetailAdapter extends DBObjectAdapter<Detail> {
    private static final String TAG = "ReceiptDetailAdapter";

    private static final int TYPE_DETAIL_LINE = 0;
    private static final int TYPE_TOTAL_LINE  = 1;

    // Alt+0164 or \u00A4 = ¤ stands for currency symbol (according to locale).
    private DecimalFormat mPriceFormatter  = new DecimalFormat("###0.00 ¤");
    private DecimalFormat mWeightFormatter = new DecimalFormat("###.### g");
    private DecimalFormat mVolumeFormatter = new DecimalFormat("###.### ml");

    public ReceiptDetailAdapter(Context context) {
        super(context);
    }

    public ReceiptDetailAdapter(Context context, List<Detail> list) {
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

                ((TotalViewHolder) holder).tvTotal.setText(mPriceFormatter.format((double)getTotal() / 100d));

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
                StringBuilder weightVolumeStr = new StringBuilder();
                weightVolumeStr.append('(');

                int volume = detail.getVolume();
                int weight = detail.getWeight();

                if (BuildConfig.DEBUG)
                    Log.d(TAG, "volume = " + volume + ", weight = " + weight);

                if (volume != 0) {
                    weightVolumeStr.append(mVolumeFormatter.format(volume));
                } else if (weight != 0) {
                    weightVolumeStr.append(mWeightFormatter.format(weight));
                } else blank = true;

                weightVolumeStr.append(')');

                if (!blank) {
                    ((DetailViewHolder) holder).tvWeight.setText(weightVolumeStr);
                } else {
                    ((DetailViewHolder) holder).tvWeight.setText("");
                }

                ((DetailViewHolder) holder).tvUnits.setText(detail.getUnits() + "X");

                double price = ((double)detail.getPrice()) / 100d;

                ((DetailViewHolder) holder).tvPrice.setText(mPriceFormatter.format(price));
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
