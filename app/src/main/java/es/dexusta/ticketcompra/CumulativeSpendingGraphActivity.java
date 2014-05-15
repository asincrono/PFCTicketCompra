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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

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
import es.dexusta.ticketcompra.util.Interval;
import es.dexusta.ticketcompra.util.Interval.Periodicity;

public class CumulativeSpendingGraphActivity extends Activity implements SetStartDateCallbacks,
        SetEndingDateCallbacks, OnItemSelectedListener {
    private static final String          TAG                = "CumulativeSpendingGraphActivity";
    private static final boolean         DEBUG              = true;

    private static final String          STATE_FRAGMENT     = "state_fragment";
    private static final String          START_DATE_PICKER  = "start_date_picker";
    private static final String          ENDING_DATE_PICKER = "ending_date_picker";
    private static final String          RENDERER           = "renderer";
    private static final String          DATASET            = "dataset";

    private static final String          START_DATE         = "start_date";
    private static final String          ENDING_DATE        = "ending_date";

    private Button                       mBttShowGraph;

    private Interval.Periodicity         mPeriodicity;
    private List<String>                 mXLabels;

    private StateFragment                mStateFragment;
    private boolean                      mChanged           = true;

    private DataSource                   mDS;
    private DataAccessCallbacks<Receipt> mReceiptListener;

    // private List<Receipt> mReceipts;
    // mirar de eliminar esta lista (intervals).

    private Calendar                     mStartDate;
    private Calendar                     mEndingDate;

    private TextView                     mTvStartDate;
    private TextView                     mTvEndingDate;

    // private LinkedHashMap<Receipt, Integer> mReceiptSpendingMap;
    // private LinkedHashMap<Interval, Integer> mIntervalSpendingMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            long startDate = savedInstanceState.getLong(START_DATE, 0l);
            if (startDate > 0) {
                mStartDate = Calendar.getInstance();
                mStartDate.setTimeInMillis(startDate);
            }

            long endingDate = savedInstanceState.getLong(ENDING_DATE);
            if (endingDate > 0) {
                mEndingDate = Calendar.getInstance();
                mEndingDate.setTimeInMillis(endingDate);
            }
        }

        setContentView(R.layout.show_graph_activity);

        mTvStartDate = (TextView) findViewById(R.id.tv_start_date);
        mTvEndingDate = (TextView) findViewById(R.id.tv_ending_date);
        Spinner spnPeriodicity = (Spinner) findViewById(R.id.spn_periocidity);

        ArrayAdapter<CharSequence> spnAdapter = ArrayAdapter.createFromResource(this,
                R.array.periodicity, android.R.layout.simple_spinner_item);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPeriodicity.setAdapter(spnAdapter);

        spnPeriodicity.setOnItemSelectedListener(this);

        FragmentManager fm = getFragmentManager();
        mStateFragment = (StateFragment) fm.findFragmentByTag(STATE_FRAGMENT);

        if (mStateFragment == null) {
            mStateFragment = new StateFragment();
            fm.beginTransaction().add(mStateFragment, STATE_FRAGMENT).commit();
        }

        DataAccessCallbacks<Receipt> callback = new DataAccessCallbacks<Receipt>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Receipt> results) {
                String title = getString(R.string.cumulative_spending_title);
                XYMultipleSeriesDataset dataset = getDataset(results);

                XYSeries series = dataset.getSeriesAt(0);
                double maxY = series.getMaxY();
                if (DEBUG) Log.d(TAG, "max Y = " + maxY);

                mStateFragment.put(DATASET, dataset);
                XYMultipleSeriesRenderer renderer = getRenderer(mXLabels, maxY);
                mStateFragment.put(RENDERER, renderer);

                Intent intent = ChartFactory.getLineChartIntent(
                        CumulativeSpendingGraphActivity.this, dataset, renderer, title);
                startActivity(intent);
            }

            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        };

        mDS = DataSource.getInstance(getApplicationContext());
        mDS.setReceiptCallback(callback);
        // Comprobar si se ha seleccionado un periodo.
        // Si se ha hecho, obtener los receipts dentro del mismo.
        // Si no, obtener todos los receipts.
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Salvar las fechas elegidas.
        if (mStartDate != null) {
            outState.putLong(START_DATE, mStartDate.getTimeInMillis());
        }

        if (mEndingDate != null) {
            outState.putLong(ENDING_DATE, mEndingDate.getTimeInMillis());
        }
    }

    public void onClickShowGraph(View v) {
        if (mChanged) {
            mDS.getReceiptsBy(mStartDate, mEndingDate);
        } else {
            XYMultipleSeriesDataset dataset = (XYMultipleSeriesDataset) mStateFragment.get(DATASET);
            XYMultipleSeriesRenderer renderer = (XYMultipleSeriesRenderer) mStateFragment
                    .get(RENDERER);
            Intent intent = ChartFactory.getLineChartIntent(CumulativeSpendingGraphActivity.this,
                    dataset, renderer);
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

    private XYMultipleSeriesRenderer getRenderer(List<String> xLabels, double yMax) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        // Setting scroll (pan) limits and zoom.
        double xMax = xLabels.size() + 2;
        renderer.setPanLimits(new double[] {0d, xMax, 0d, 0d});
        renderer.setZoomEnabled(true, false);

        renderer.setMargins(new int[]{16, 100, 150, 8});
        renderer.setLegendHeight(75);

        renderer.setPointSize(10f);

        renderer.setLabelsColor(0xFF0099CC);
        renderer.setLabelsTextSize(20f);
        renderer.setLegendTextSize(30f);

        renderer.setBackgroundColor(Color.BLACK);
        renderer.setApplyBackgroundColor(true);

        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setXLabelsAngle(60f);

        renderer.setYLabelsAlign(Align.RIGHT);

        renderer.setXAxisMin(0d);
        renderer.setXAxisMax(30d);

        renderer.setYAxisMin(0d);
        renderer.setYAxisMax(yMax + (yMax * 0.05));

        renderer.setYLabels(0);
        double auxValue = yMax;
        for (int i = 0; i < 10; ++i) {
            renderer.addYTextLabel(auxValue, auxValue / 100 + "  €  ");
            auxValue -= yMax * 0.1;
        }

        XYSeriesRenderer r = new XYSeriesRenderer();

        r.setLineWidth(5);
        r.setColor(Color.parseColor("#0099CC"));
        r.setPointStyle(PointStyle.CIRCLE);
        r.setFillPoints(true);

        FillOutsideLine fol = new FillOutsideLine(FillOutsideLine.Type.BELOW);
        fol.setColor(Color.parseColor("#33B5E5"));
        r.addFillOutsideLine(fol);

        renderer.addSeriesRenderer(r);

        renderer.setXLabels(0);

        int num_labels = xLabels.size();
        for (int i = 1; i <= num_labels; ++i) {
            renderer.addXTextLabel(i, xLabels.get(i - 1));
        }

        return renderer;
    }

    private XYMultipleSeriesDataset getDataset(List<Receipt> receipts) {
        XYMultipleSeriesDataset dataset = null;

        // List<Interval> intervals = new ArrayList<Interval>();

        int num_receipts = receipts.size();

        if (num_receipts > 0) {

            dataset = new XYMultipleSeriesDataset();
            String title = getString(R.string.cumulative_spending_title);
            CategorySeries catSeries = new CategorySeries(title);

            Interval interval = new Interval(receipts.get(0).getTimestamp(), mPeriodicity, true);

            // TESTING ADDING A START OF 0 (PREVIOUS INTERVAL LABEL.

            Receipt receipt;
            mXLabels = new ArrayList<String>();

            // TESTING.
            // mXLabels.add(interval.getPrevious().getLabel());

            // TESTING
            // catSeries.add(0d);

            int accumulator = 0;
            for (int i = 0; i < num_receipts; ++i) {
                receipt = receipts.get(i);
                while (!interval.contains(receipt.getTimestamp())) {
                    catSeries.add(accumulator);
                    mXLabels.add(interval.getLabel());
                    interval = interval.getNext();
                }

                accumulator += receipt.getTotal();

                if ((i == num_receipts - 1)
                        || (!interval.contains(receipts.get(i + 1).getTimestamp()))) {
                    catSeries.add(accumulator);
                    mXLabels.add(interval.getLabel());
                    interval = interval.getNext();
                }
            }
            dataset.addSeries(catSeries.toXYSeries());
        }

        return dataset;
    }

    // private String[] getXLabels(List<Interval> intervals) {
    // String[] xLabels = new String[intervals.size()];
    //
    // int pos = 0;
    //
    // for (Interval interval : intervals) {
    // xLabels[pos] = interval.getLabel();
    // ++pos;
    // }
    //
    // return xLabels;
    // }

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
    public Calendar getStartDate() {
        return mStartDate;
    }

    @Override
    public void onSetEndingDate(Calendar endingDate) {
        mEndingDate = endingDate;
        updateEndingDate();
    }

    @Override
    public void onSetStartDate(Calendar startDate) {
        mStartDate = startDate;
        updateStartDate();
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
            break;
        case 1:
            if ((mPeriodicity != null) && (mPeriodicity != Periodicity.WEEKLY)) {
                mStateFragment.put(DATASET, null);
                mStateFragment.put(RENDERER, null);
            }
            mPeriodicity = Periodicity.WEEKLY;
            break;
        case 2:
            if ((mPeriodicity != null) && (mPeriodicity != Periodicity.MONTHLY)) {
                mStateFragment.put(DATASET, null);
                mStateFragment.put(RENDERER, null);
            }
            mPeriodicity = Periodicity.MONTHLY;
            break;
        case 3:
            if ((mPeriodicity != null) && (mPeriodicity != Periodicity.ANNUAL)) {
                mStateFragment.put(DATASET, null);
                mStateFragment.put(RENDERER, null);
            }
            mPeriodicity = Periodicity.ANNUAL;
            Toast.makeText(this, "Annual selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

}
