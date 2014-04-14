package es.dexusta.ticketcompra.control;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Total;

public class ReceiptDetailLineAdapter extends CursorAdapter {

    private static final int TYPE_DETAIL_LINE = 0;
    private static final int TYPE_TOTAL_LINE  = 1;

    // Alt+0164 ¤ stands for currency symbol (according to locale).
    private NumberFormat     mPriceFormatter  = new DecimalFormat("###0.00¤");
    private NumberFormat     mWeightFormatter = new DecimalFormat("###.###g");
    private LayoutInflater   mInflater;

    private int              mProductNameIndex;
    private int              mDetailPriceIndex;
    private int              mDetailUnitsIndex;
    private int              mDetailWheightIndex;

    private long             mReceiptId       = 0;

    public ReceiptDetailLineAdapter(Context context, Cursor c) {
        super(context, c, 0);

        mInflater = LayoutInflater.from(context);

        setIndex();

        if (c != null && c.moveToFirst()) {
            mReceiptId = c.getLong(c.getColumnIndexOrThrow(DBHelper.T_DETAIL_RECPT_ID));
        }
    }

    private void setIndex() {
        Cursor c = getCursor();
        if (c != null) {
            mProductNameIndex = c.getColumnIndexOrThrow(DBHelper.T_PROD_NAME_ALT);
            mDetailPriceIndex = c.getColumnIndexOrThrow(DBHelper.T_DETAIL_PRICE);
            mDetailUnitsIndex = c.getColumnIndexOrThrow(DBHelper.T_DETAIL_UNITS);
            mDetailWheightIndex = c.getColumnIndexOrThrow(DBHelper.T_DETAIL_WEIGHT);
        }
    }

    @Override
    public Cursor swapCursor(Cursor c) {

        if (c != null && c.moveToFirst()) {
            mReceiptId = c.getLong(c.getColumnIndexOrThrow(DBHelper.T_DETAIL_RECPT_ID));
            setIndex();
        }

        return super.swapCursor(c);
    }

    @Override
    public int getCount() {
        // If cursor it's empty, do nothing. If not, one more.
        // Si el cursor está vacío, nada. Si no, uno más (el total).
        int count = super.getCount();

        if (count > 0) {
            ++count;
        }

        return count;
    }

    @Override
    public Object getItem(int position) {
        // Devolver objeto total.
        int count = getCount();
        if (count > 0 && position == count) {
            Total total = new Total();
            total.setReceiptId(mReceiptId);
            total.setValue(getTotal());
            return total;
        }

        return super.getItem(position);
    }

    public long getReceiptId() {
        return mReceiptId;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getCount()) {
            return TYPE_TOTAL_LINE;
        }
        return TYPE_DETAIL_LINE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Si es la posición total: devuelve total.
        // Si es la posición normal llama a super.

        View view;
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
        case TYPE_DETAIL_LINE:
            view = super.getView(position, convertView, parent);
            break;
        case TYPE_TOTAL_LINE:
            if (convertView == null) {
                view = mInflater.inflate(R.layout.receipt_total_row, parent, false);
            } else {
                view = convertView;
            }

            ViewHolderTotal holder = (ViewHolderTotal) view.getTag();
            if (holder == null) {
                holder = new ViewHolderTotal(view);
                view.setTag(holder);
            }

            holder.tvTotal.setText(mPriceFormatter.format(getTotal()));
        default:
            view = null;
        }

        return view;
    }

    private float getTotal() {
        float total = 0;
        Cursor cursor = getCursor();
        if (cursor != null && cursor.getCount() > 0) {
            int priceIdx = cursor.getColumnIndexOrThrow(DBHelper.T_DETAIL_PRICE);
            int currentPosition = cursor.getPosition();
            cursor.moveToFirst();
            do {
                total += cursor.getFloat(priceIdx);
            } while (cursor.moveToNext());
            cursor.moveToPosition(currentPosition);
        }

        return total;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.receipt_detail_row, parent, false);
        ViewHolderDetail holder = new ViewHolderDetail(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolderDetail holder = (ViewHolderDetail) view.getTag();

        String productName = cursor.getString(mProductNameIndex);
        float price = cursor.getFloat(mDetailPriceIndex);
        int units = cursor.getInt(mDetailUnitsIndex);
        float weight = cursor.getFloat(mDetailWheightIndex);

        holder.tvProductName.setText(productName);
        holder.tvUnits.setText("X" + units);
        if (weight > 0) {
            holder.tvWeight.setText(mWeightFormatter.format(weight));
        }
        holder.tvPrice.setText(mPriceFormatter.format(price));
    }

    class ViewHolderDetail {
        TextView tvProductName;
        TextView tvUnits;
        TextView tvWeight;
        TextView tvPrice;

        ViewHolderDetail(View view) {
            tvProductName = (TextView) view.findViewById(R.id.tv_product_name);
            tvUnits = (TextView) view.findViewById(R.id.tv_units);
            tvWeight = (TextView) view.findViewById(R.id.tv_weight);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
        }
    }

    class ViewHolderTotal {
        TextView tvTotal;

        ViewHolderTotal(View v) {
            tvTotal = (TextView) v.findViewById(R.id.tv_total);
        }
    }
}
