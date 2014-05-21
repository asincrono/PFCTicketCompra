package es.dexusta.ticketcompra;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.cloud.backend.android.CloudBackendActivity;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccess;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.model.Total;

public class AddTotalActivity extends CloudBackendActivity {
    private static final String          TAG   = "AddTotalActivity";
    private static final boolean         DEBUG = true;

    private EditText                     mEdtTotal;

    private DataAccessCallbacks<Receipt> mReceiptListener;
    private DataAccessCallbacks<Total>   mTotalListener;
    private DataSource                   mDS;
    private Shop                         mShop;

    private boolean                      mPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShop = getIntent().getParcelableExtra(Keys.KEY_SHOP);
        if (mShop == null) {
            if (DEBUG) Log.d(TAG, "La hemos cagado... no se la tienda.");
        } else {
            setContentView(R.layout.add_total_activity);

            showAcceptCancelActionBar(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Crear receipt y salvar receipt.
                    List<Receipt> listReceipt = new ArrayList<Receipt>();
                    Receipt receipt = new Receipt();
                    receipt.setShopId(mShop.getId());
                    receipt.setTimestamp(System.currentTimeMillis());
                    listReceipt.add(receipt);
                    mDS.insertReceipts(listReceipt);
                }
            }, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            mEdtTotal = (EditText) findViewById(R.id.edtTotal);
            mEdtTotal.setFilters(new InputFilter[] { new InputFilter.LengthFilter(9),
                    new DecimalFilter(2) });

            mShop = (Shop) getIntent().getParcelableExtra(Keys.KEY_SHOP);

            mDS = DataSource.getInstance(getApplicationContext());
            mReceiptListener = new DataAccessCallbacks<Receipt>() {

                @Override
                public void onInfoReceived(Object result, Option option) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onDataReceived(List<Receipt> results) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onDataProcessed(int processed, List<Receipt> dataList,
                        Operation operation, boolean result) {
                    if (result) {
                        Receipt receipt = dataList.get(0);
                        Total total = new Total();
                        total.setReceiptId(receipt.getId());
                        /*
                         * We don't need to check the user input value in order
                         * to prevent it exceeds integer (MIN_VALUE/MAX_VALUE)
                         * limits as we already set the right filters in the
                         * EditText So the max input an user can 999999999
                         * (without decimals) or 9999999.9/999999.99 including
                         * decimals both < Integer.MAX_VALUE (= 2147483647). We
                         * don't allow negative numbers neither. In summary we
                         * don't need to do any programmatic check.
                         */

                        total.setValue(decimalToInt(mEdtTotal.getText()));

                        // List<Total> listTotal = new ArrayList<Total>();
                        // listTotal.add(total);
                        // mDS.insertTotals(listTotal);
                        if (BackendDataAccess.hasConnectivity(AddTotalActivity.this)) {
                            // getCloudBackend().insert(total.getEntity(AddTotalActivity.this),
                            // new InsertTotalHandler(total,
                            // AddTotalActivity.this));
                            // Toast.makeText(getApplicationContext(),
                            // "Total inserted",
                            // Toast.LENGTH_SHORT).show();
                            //BackendDataAccess.uploadTotal(total, getApplicationContext(), getCloudBackend());
                        }

                    }                 
                }
            };

            mTotalListener = new DataAccessCallbacks<Total>() {

                @Override
                public void onDataProcessed(int processed, List<Total> dataList,
                        Operation operation, boolean result) {
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onDataReceived(List<Total> results) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onInfoReceived(Object result, Option option) {
                    // TODO Auto-generated method stub

                }
            };

            mDS.setReceiptCallback(mReceiptListener);
            mDS.setTotalCallback(mTotalListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            mDS.setReceiptCallback(mReceiptListener);
            mDS.setTotalCallback(mTotalListener);
        }
    }

    @Override
    protected void onPause() {
        mDS.setReceiptCallback(null);
        mDS.setTotalCallback(null);
        mPaused = true;
        super.onPause();
    }

    private int decimalToInt(CharSequence decCharSequence) {
        StringBuilder strBuilder = new StringBuilder(decCharSequence);
        int result = -1;
        // We check if there is a decmial separator.

        int dotPos = strBuilder.indexOf(".");

        if (dotPos > 0) {
            strBuilder.deleteCharAt(dotPos);
            result = Integer.parseInt(strBuilder.toString());
        }
        return result;
    }

    private void showAcceptCancelActionBar(OnClickListener onClickAccept,
            OnClickListener onClickCancel) {
        final ActionBar actionBar = getActionBar();

        LayoutInflater inflater = LayoutInflater.from(actionBar.getThemedContext());

        final View actionBarCustomView = inflater.inflate(R.layout.actionbar_cancel_accept, null);

        actionBarCustomView.findViewById(R.id.actionbar_accept).setOnClickListener(onClickAccept);
        actionBarCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(onClickCancel);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        // Previous line is equivalent to:
        // actionBar.setDisplayShowTitleEnabled(false);
        // actionBar.setDisplayShowHomeEnabled(false);
        // actionBar.setDisplayUseLogoEnabled(false);
        // actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setCustomView(actionBarCustomView, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    class DecimalFilter implements InputFilter {
        private int    mDecimals;
        private String mRegex;

        public DecimalFilter(int decimals) {
            mDecimals = decimals;
        }

        /*
         * Regex for "x...x[.]xx" being x=0,1,...9: "\d*(\.\d?\d?)?"
         */

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                int dstart, int dend) {

            String regex = String.format("\\d*(\\.\\d{0,%d})?", mDecimals);

            // if the CharSequene to be inserter does not fit the regex we need
            // to reject it.
            if (source.toString().matches(regex)) {
                // now we check what would be the result.
                StringBuilder result = new StringBuilder(dest);
                result.replace(dstart, dend, source.subSequence(start, end).toString());
                if (result.toString().matches(regex)) return null;
            }

            return "";
        }

    }

    private class InsertTotalHandler extends CloudCallbackHandler<CloudEntity> {
        private Total   mTotal;
        private Context mContext;

        public InsertTotalHandler(Total total, Context context) {
            mTotal = total;
            mContext = context;
        }

        @Override
        public void onComplete(CloudEntity result) {
            List<Total> dataList = new ArrayList<Total>();
            dataList.add(new Total(result));
            mDS.insertTotals(dataList);
            Toast.makeText(mContext, "Total inserted in the cloud", Toast.LENGTH_SHORT).show();
            mContext = null;

        }

        @Override
        public void onError(IOException exception) {
            List<Total> dataList = new ArrayList<Total>();
            dataList.add(mTotal);
            mDS.insertTotals(dataList);
            Toast.makeText(mContext, "Total NOT inserted in the cloud", Toast.LENGTH_SHORT).show();
            mContext = null;
        }
    }
}
