package es.dexusta.ticketcompra;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.api.client.util.DateTime;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dexusta.ticketcompra.EndingDatePickerFragment.SetEndingDateCallbacks;
import es.dexusta.ticketcompra.StartDatePickerFragment.SetStartDateCallbacks;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.util.Intervall;
import es.dexusta.ticketcompra.util.Intervall.Periodicity;

public class SpendingInTimeGraphActivity extends Activity implements SetStartDateCallbacks,
        SetEndingDateCallbacks, OnItemSelectedListener {
    private static final boolean DEBUG = true;
    private static final String  TAG   = "SpendingInTimeGraphActivity";

    private static final String TAG_STATE_FRAGMENT = "state_fragment";
    private static final String RECEIPTS_LIST      = "receipts";
    // private static final String INTERVALS_LIST = "intervals";
    private static final String DATASET            = "dataset";
    private static final String RENDERER           = "renderer";

    private static final String START_DATE  = "start_date";
    private static final String ENDING_DATE = "ending_date";

    private static final String START_DATE_PICKER  = "start_date_picker";
    private static final String ENDING_DATE_PICKER = "ending_date_picker";

    private StateFragment mStateFragment;

    private boolean mChanged = true;

    private DataSource      mDS;
    // private List<Receipt> mReceipts;
    private List<Intervall> mIntervals;

    private Calendar mStartDate;
    private Calendar mEndingDate;

    private TextView mTvStartDate;
    private TextView mTvEndingDate;
    private Spinner  mSpnPeriodicity;

    private Periodicity mPeriodicity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            long startTimeMillis = savedInstanceState.getLong(START_DATE, 0);

            if (startTimeMillis > 0) {
                mStartDate = Calendar.getInstance();
                mStartDate.setTimeInMillis(startTimeMillis);
            }

            long endTimeMillis = savedInstanceState.getLong(ENDING_DATE, 0);
            if (endTimeMillis > 0) {
                mEndingDate = Calendar.getInstance();
                mEndingDate.setTimeInMillis(endTimeMillis);
            }
        }

        setContentView(R.layout.show_graph_activity);

        mTvStartDate = (TextView) findViewById(R.id.tv_start_date);
        mTvEndingDate = (TextView) findViewById(R.id.tv_ending_date);
        mSpnPeriodicity = (Spinner) findViewById(R.id.spn_periocidity);

        ArrayAdapter spnAdapter = ArrayAdapter.createFromResource(this, R.array.periodicity,
                android.R.layout.simple_spinner_item);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnPeriodicity.setAdapter(spnAdapter);
        mSpnPeriodicity.setOnItemSelectedListener(this);

        FragmentManager fm = getFragmentManager();

        mStateFragment = (StateFragment) fm.findFragmentByTag(TAG_STATE_FRAGMENT);
        if (mStateFragment == null) {
            mStateFragment = new StateFragment();
            fm.beginTransaction().add(mStateFragment, TAG_STATE_FRAGMENT).commit();
        }

        DataAccessCallbacks<Receipt> callback = new DataAccessCallbacks<Receipt>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Receipt> results) {
                if ((results != null) && (results.size() > 0)) {
                    String title = getString(R.string.spending_in_time_title);

                    XYMultipleSeriesDataset dataset = getDataset(results);
                    XYSeries series = dataset.getSeriesAt(0);
                    double maxY = series.getMaxY();
                    if (DEBUG) Log.d(TAG, "max Y = " + maxY);
                    

                    XYMultipleSeriesRenderer renderer = getRenderer(getXLabels(), maxY);

                    Intent intent = ChartFactory.getBarChartIntent(
                            SpendingInTimeGraphActivity.this, dataset, renderer, Type.DEFAULT,
                            title);
                    startActivity(intent);
                }
            }

            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        };

        mDS = DataSource.getInstance(getApplicationContext());
        mDS.setReceiptCallback(callback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mStartDate != null) outState.putLong(START_DATE, mStartDate.getTimeInMillis());
        if (mEndingDate != null) outState.putLong(ENDING_DATE, mEndingDate.getTimeInMillis());
    }

    public void onClickShowGraph(View v) {
        mDS.getReceiptsBy(mStartDate, mEndingDate);
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

    private XYMultipleSeriesDataset getDataset(List<Receipt> receipts) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        mIntervals = new ArrayList<Intervall>();

        int num_receipts = receipts.size();
        if (num_receipts > 0) {
            DateTime dtStart = receipts.get(0).getTimestamp();
            Intervall interval = new Intervall(dtStart, mPeriodicity, true);

            String title = getString(R.string.spending_in_time_title);
            CategorySeries catSeries = new CategorySeries(title);

            // Receipts in mReceipts are sorted by time stamp.
            Receipt receipt;
            double value = 0;
            for (int i = 0; i < num_receipts; ++i) {
                receipt = receipts.get(i);
                value += receipt.getTotal();
                while (!interval.contains(receipt.getTimestamp())) {
                    mIntervals.add(interval);
                    catSeries.add(0d);
                    interval = interval.getNext();
                }

                if ((i == num_receipts - 1) // Last one implies that is in the
                                            // interval.
                        || (!interval.contains(receipts.get(i + 1).getTimestamp()))) {
                    catSeries.add(value);
                    mIntervals.add(interval);
                    // Useless (but not harmless) if we are at last receipt in
                    // list.
                    value = 0;
                    interval = interval.getNext();
                }
            }
            dataset.addSeries(catSeries.toXYSeries());
        }

        Log.d(TAG, "Intervals: " + mIntervals.size());
        Log.d(TAG, "catSeries: " + dataset.getSeriesCount());

        return dataset;
    }

    private String[] getXLabels() {
        int num_intervals = mIntervals.size();
        String[] xLabels = new String[num_intervals];
        for (int i = 0; i < num_intervals; ++i) {
            xLabels[i] = mIntervals.get(i).getLabel();
        }
        return xLabels;
    }

    private XYMultipleSeriesRenderer getRenderer(String[] xLabels, double yMax) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        // Setting scroll (pan) limits and zoom.
        double xMax = xLabels.length + 2;
        renderer.setPanLimits(new double[] {0d, xMax, 0d, 0d});
        renderer.setZoomEnabled(true, false);

        renderer.setMargins(new int[] { 16, 100, 150, 8 });
        renderer.setLegendHeight(75);


        renderer.setLabelsColor(0xFF0099CC);
        renderer.setLabelsTextSize(20f);
        renderer.setLegendTextSize(30f);

        renderer.setBackgroundColor(Color.BLACK);
        renderer.setApplyBackgroundColor(true);

        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setXLabelsAngle(60f);
        renderer.setYLabelsAlign(Align.RIGHT);

        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(0xFF0099CC);

        renderer.setBarSpacing(0.15d);

        renderer.setYAxisMin(0d);
        renderer.setYAxisMax(yMax + (yMax * 0.05));
        
        renderer.setYLabels(0);

        double auxValue = yMax;
        String yLabel;
        //DecimalFormat df = new DecimalFormat("##.00");
        for (int i = 0; i < 10; ++i) {
            yLabel =  Long.toString(Math.round(auxValue/100));
            //yLabel = df.format(auxValue / 100);
            renderer.addYTextLabel(auxValue, yLabel + " €  ");
            auxValue -= yMax * 0.1;
        }

        renderer.setXAxisMin(0d);
        renderer.setXAxisMax(30d);

        renderer.setXLabels(0);        

        // for (int i = 0; i < xLabels.length; ++i) {
        // renderer.addXTextLabel(i, xLabels[i]);
        // }
        for (int i = 1; i <= xLabels.length; ++i) {
            renderer.addXTextLabel(i, xLabels[i - 1]);
        }

        renderer.addSeriesRenderer(r);

        return renderer;
    }

    @Override
    public Calendar getEndingDate() {
        return mEndingDate;
    }

    @Override
    public void onSetStartDate(Calendar startDate) {
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

    private void updateStartDate() {
        if (mStartDate == null) {
            mTvStartDate.setText(R.string.first_receipt);
        } else {
            mTvStartDate.setText(DateUtils.formatDateTime(this, mStartDate.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE));
        }
    }

    private void updateEndingDate() {
        if (mEndingDate == null) {
            mTvEndingDate.setText(R.string.last_receipt);
        } else {
            mTvEndingDate.setText(DateUtils.formatDateTime(this, mEndingDate.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        Log.d(TAG, "onItemSelected");
        switch (position) {
        case 0:
            if ((mPeriodicity != null) && (mPeriodicity != Periodicity.DAILY)) {
                mStateFragment.put(DATASET, null);
                mStateFragment.put(RENDERER, null);
            }
            mPeriodicity = Periodicity.DAILY;
            // Toast.makeText(this, "Daily selected",
            // Toast.LENGTH_SHORT).show();
            break;
        case 1:
            if ((mPeriodicity != null) && (mPeriodicity != Periodicity.WEEKLY)) {
                mStateFragment.put(DATASET, null);
                mStateFragment.put(RENDERER, null);
            }
            mPeriodicity = Periodicity.WEEKLY;
            // Toast.makeText(this, "Weekly selected",
            // Toast.LENGTH_SHORT).show();
            break;
        case 2:
            if ((mPeriodicity != null) && (mPeriodicity != Periodicity.MONTHLY)) {
                mStateFragment.put(DATASET, null);
                mStateFragment.put(RENDERER, null);
            }
            mPeriodicity = Periodicity.MONTHLY;
            // Toast.makeText(this, "Monthly selected",
            // Toast.LENGTH_SHORT).show();
            break;
        case 3:
            if ((mPeriodicity != null) && (mPeriodicity != Periodicity.ANNUAL)) {
                mStateFragment.put(DATASET, null);
                mStateFragment.put(RENDERER, null);
            }
            mPeriodicity = Periodicity.ANNUAL;
            // Toast.makeText(this, "Annual selected",
            // Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        mPeriodicity = Periodicity.DAILY;
    }
}
