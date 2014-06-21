package es.dexusta.ticketcompra;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dexusta.ticketcompra.EndingDatePickerFragment.SetEndingDateCallbacks;
import es.dexusta.ticketcompra.StartDatePickerFragment.SetStartDateCallbacks;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Receipt;

public class SpendingByCategoryGraphActivity extends Activity implements SetStartDateCallbacks,
        SetEndingDateCallbacks {
    private static final boolean DEBUG = true;
    private static final String  TAG   = "SpendingByCategoryGraph";

    private static final String STATE_FRAGMENT     = "state_fragment";
    private static final String START_DATE_PICKER  = "start_date_picker";
    private static final String ENDING_DATE_PICKER = "ending_date_picker";
    private static final String START_DATE_MILLIS  = "start_date_millis";
    private static final String ENDING_DATE_MILLIS = "ending_date_millis";

    private static final String DETAILS_LIST  = "details";
    private static final String RECEIPTS_LIST = "receipts";
    private static final String DATASET       = "dataset";
    private static final String RENDERER      = "renderer";

    private boolean mChanged = true;

    private StateFragment mStateFragment;

    private Calendar mStartDate;
    private Calendar mEndingDate;

    private TextView mTvStartDate;
    private TextView mTvEndingDate;
    private TextView mLblPeriodicity;
    private View     mDivPeriodicity;
    private Spinner  mSpnPeriodicity;

    private DataSource mDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            long startDateMillis = savedInstanceState.getLong(START_DATE_MILLIS);
            if (startDateMillis > 0) {
                mStartDate = Calendar.getInstance();
                mStartDate.setTimeInMillis(startDateMillis);
            }
            long endingDateMillis = savedInstanceState.getLong(ENDING_DATE_MILLIS);
            if (endingDateMillis > 0) {
                mEndingDate = Calendar.getInstance();
                mEndingDate.setTimeInMillis(endingDateMillis);
            }
        }

        setContentView(R.layout.show_graph_activity);

        mTvStartDate = (TextView) findViewById(R.id.tv_start_date);
        mTvEndingDate = (TextView) findViewById(R.id.tv_ending_date);

        mLblPeriodicity = (TextView) findViewById(R.id.lbl_spn_periodicity);
        mDivPeriodicity = findViewById(R.id.div_spn_periodicity);
        mSpnPeriodicity = (Spinner) findViewById(R.id.spn_periocidity);

        mLblPeriodicity.setVisibility(View.GONE);
        mDivPeriodicity.setVisibility(View.GONE);
        mSpnPeriodicity.setVisibility(View.GONE);

        FragmentManager fm = getFragmentManager();
        mStateFragment = (StateFragment) fm.findFragmentByTag(STATE_FRAGMENT);

        if (mStateFragment == null) {
            mStateFragment = new StateFragment();
            fm.beginTransaction().add(mStateFragment, STATE_FRAGMENT).commit();
        }

        DataAccessCallbacks<Receipt> receiptCallback = new DataAccessCallbacks<Receipt>() {

            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                                        boolean result) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Receipt> results) {
                mStateFragment.put(RECEIPTS_LIST, results);
                mDS.getDetailsBy(results);
            }

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }
        };

        DataAccessCallbacks<Detail> detailCallback = new DataAccessCallbacks<Detail>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Detail> results) {
                String title = getString(R.string.spending_by_category_title);
                CategorySeries dataset = getDataset(results);
                mStateFragment.put(DATASET, dataset);

                int num_categories = dataset.getItemCount();
                DefaultRenderer renderer = getRenderer(num_categories);
                mStateFragment.put(RENDERER, renderer);

                mChanged = false;

                Intent intent = ChartFactory.getPieChartIntent(
                        SpendingByCategoryGraphActivity.this, dataset, renderer, title);
                startActivity(intent);

            }

            @Override
            public void onDataProcessed(int processed, List<Detail> dataList, Operation operation,
                                        boolean result) {
                // TODO Auto-generated method stub

            }
        };

        mDS = DataSource.getInstance(getApplicationContext());
        mDS.setReceiptCallback(receiptCallback);
        mDS.setDetailCallback(detailCallback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mStartDate != null) {
            outState.putLong(START_DATE_PICKER, mStartDate.getTimeInMillis());
        }
        if (mEndingDate != null) {
            outState.putLong(ENDING_DATE_PICKER, mEndingDate.getTimeInMillis());
        }

    }

    public void onClickShowGraph(View v) {
        String title = getString(R.string.spending_by_category_title);

        if (mChanged) {
            mDS.getReceiptsBy(mStartDate, mEndingDate);
        } else {
            CategorySeries dataset = (CategorySeries) mStateFragment.get(DATASET);
            DefaultRenderer renderer = (DefaultRenderer) mStateFragment.get(RENDERER);
            Intent intent = ChartFactory.getPieChartIntent(this, dataset, renderer, title);
            startActivity(intent);
        }
    }

    public void onClickSetStartDate(View v) {
        FragmentManager fm = getFragmentManager();
        DialogFragment startDatePickerFragment = (DialogFragment) fm
                .findFragmentByTag(START_DATE_PICKER);
        if (startDatePickerFragment == null) {
            startDatePickerFragment = new StartDatePickerFragment();
        }
        startDatePickerFragment.show(fm, START_DATE_PICKER);

    }

    public void onClickSetEndingDate(View v) {
        FragmentManager fm = getFragmentManager();
        DialogFragment endingDatePickerFragment = (DialogFragment) fm
                .findFragmentByTag(ENDING_DATE_PICKER);
        if (endingDatePickerFragment == null) {
            endingDatePickerFragment = new EndingDatePickerFragment();
        }
        endingDatePickerFragment.show(fm, ENDING_DATE_PICKER);
    }

    private DefaultRenderer getRenderer(int num_categories) {
        DefaultRenderer renderer = new DefaultRenderer();
        SimpleSeriesRenderer r;

        int[] colors = getColors(num_categories);
        for (int i = 0; i < num_categories; ++i) {
            r = new SimpleSeriesRenderer();
            r.setColor(colors[i]);
            NumberFormat nf = NumberFormat.getPercentInstance();
            r.setChartValuesFormat(nf);
            renderer.addSeriesRenderer(r);
        }

        //renderer.setMargins(new int[] {0, 0, 0, 0});
        //renderer.setLegendHeight(35);

        renderer.setBackgroundColor(Color.BLACK);
        renderer.setApplyBackgroundColor(true);
        renderer.setLabelsTextSize(25f);
        renderer.setLegendTextSize(25f);
        renderer.setDisplayValues(true);
        renderer.setShowLabels(false);
        //renderer.setLegendTextSize(30f);

        renderer.setShowLegend(true);
        renderer.setFitLegend(true);
        //renderer.setFitLegend(false);

        return renderer;
    }

    private CategorySeries getDataset(List<Detail> details) {
        CategorySeries category_series = new CategorySeries("Categories");
        HashMap<String, Integer> seriesMap = new HashMap<String, Integer>();

        String category_name;
        Integer value;
        double total = 0d;
        for (Detail detail : details) {
            category_name = mDS.getProductCategoryName(detail.getProductId());
            value = seriesMap.get(category_name);

            // value += value == null ? 0 : detail.getPrice();
            if (value == null) {
                value = detail.getPrice();
            } else {
                value += detail.getPrice();
            }

            total += detail.getPrice();

            if (BuildConfig.DEBUG)
                Log.d(TAG, category_name + " value: " + value);
            seriesMap.put(category_name, value);
        }


        for (Map.Entry<String, Integer> entry : seriesMap.entrySet()) {
            category_series.add(entry.getKey(), entry.getValue() / total);
        }

        return category_series;
    }

    private float[] colorToHSV(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv;
    }

    private int[] getColors(int number) {
        int[] colors = new int[number];

        float[][] hsv_colors = {colorToHSV(0xFFCC0000), colorToHSV(0xFFFF8800),
                colorToHSV(0xFF669900), colorToHSV(0XFF9833CC), colorToHSV(0XFF0099CC)};

        // I will have 5 base colors.
        // I check if it's divisible by the highest number (from 5 to 1);
        int hsv_pos;
        for (int i = 0; i < number; ++i) {
            hsv_pos = i % 5;
            colors[i] = Color.HSVToColor(hsv_colors[hsv_pos]);
            hsv_colors[hsv_pos][1] = hsv_colors[hsv_pos][1] * 0.75f;
        }

        return colors;
    }

    private void updateStartDate() {
        mTvStartDate.setText(DateUtils.formatDateTime(this, mStartDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE));
    }

    private void updateEndingDate() {
        mTvEndingDate.setText(DateUtils.formatDateTime(this, mEndingDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE));
    }

    @Override
    public Calendar getEndingDate() {
        return mEndingDate;
    }

    @Override
    public void onSetStartDate(Calendar startDate) {
        /*
         * mStartDate == null -> first execution. if mStartDate (current start
         * date) is equal to startDate (new selected) do nothing. We don't need
         * to update the graphic.
         */
        if ((mStartDate == null) || (!mStartDate.equals(startDate))) {
            mChanged = true;
            mStartDate = startDate;
            updateStartDate();
        }
    }

    @Override
    public void onSetEndingDate(Calendar endingDate) {
        if ((mEndingDate == null) || (!mEndingDate.equals(endingDate))) {
            mChanged = true;
            mEndingDate = endingDate;
            updateEndingDate();
        }
    }

    @Override
    public Calendar getStartDate() {
        return mStartDate;
    }
}
