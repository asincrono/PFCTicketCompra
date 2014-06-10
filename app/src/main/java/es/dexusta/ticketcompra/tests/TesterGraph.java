package es.dexusta.ticketcompra.tests;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import es.dexusta.ticketcompra.CumulativeSpendingGraphActivity;
import es.dexusta.ticketcompra.EndingDatePickerFragment;
import es.dexusta.ticketcompra.EndingDatePickerFragment.SetEndingDateCallbacks;
import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.SpendingByCategoryGraphActivity;
import es.dexusta.ticketcompra.SpendingInTimeGraphActivity;
import es.dexusta.ticketcompra.StartDatePickerFragment;
import es.dexusta.ticketcompra.StartDatePickerFragment.SetStartDateCallbacks;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.util.Intervall.Periodicity;

//import android.app.DatePickerDialog;
//import android.widget.DatePicker;

public class TesterGraph extends Activity implements SetStartDateCallbacks, SetEndingDateCallbacks,
        OnItemSelectedListener {
    private static final boolean  DEBUG                   = true;
    private static final String   TAG                     = "TesterGraph";

    private static final String   START_TIME              = "start_time";
    private static final String   ENDING_TIME             = "ending_time";
    private static final String   START_DATE_PICKER       = "start_date_picker";
    private static final String   ENDING_DATE_PICKER      = "endind_date_picker";

    private static final String[] TEST_RECEIPT_TIMESTAMPS = {
            "01/01/2014", // (Jueves)
            "02/01/2014",
            // <salto de un d�a>
            "04/01/2014",
            "05/01/2014",
            // <salto de una semana>
            "13/01/2014", // (Lunes)
            "14/01/2014",
            "15/01/2014",
            // <salto de un mes>
            "01/03/2014", // (S�bado)
            "02/03/2014",
            // <salto una semana>
            "10/03/2014", "11/03/2014", "12/03/2014", "13/03/2014", "14/03/2014", "15/03/2014",
            "16/03/2014", "17/03/2014", "18/03/2014", "19/03/2014", "20/03/2014",
            // <salto un a�o>
            "01/01/2015", "02/01/2015"                   };
    private static final int      SIZE                    = 10;
    private SimpleDateFormat      mServiceDF              = new SimpleDateFormat("dd/MM/yyyy",
                                                                  Locale.US);
    private double                mMaxValue;

    private TextView              mTvStartDate;
    private TextView              mTvEndingDate;
    private Spinner               mSpnPeriodicity;
    // private Button mBttShowGraph;

    private Calendar              mStartDate;
    private Calendar              mEndingDate;
    private Periodicity           mPeriodicity;

    private DataSource            mDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_graph_activity);

        mTvStartDate = (TextView) findViewById(R.id.tv_start_date);
        mTvEndingDate = (TextView) findViewById(R.id.tv_ending_date);
        mSpnPeriodicity = (Spinner) findViewById(R.id.spn_periocidity);

        ArrayAdapter<CharSequence> spnAdapter = ArrayAdapter.createFromResource(this,
                R.array.periodicity, android.R.layout.simple_spinner_item);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnPeriodicity.setAdapter(spnAdapter);
        mSpnPeriodicity.setOnItemSelectedListener(this);

        if (savedInstanceState != null) {
            long startTimeMillis = savedInstanceState.getLong(START_TIME, 0);

            if (startTimeMillis > 0) {
                mStartDate = Calendar.getInstance();
                mStartDate.setTimeInMillis(startTimeMillis);
            }

            long endTimeMillis = savedInstanceState.getLong(ENDING_TIME, 0);
            if (endTimeMillis > 0) {
                mEndingDate = Calendar.getInstance();
                mEndingDate.setTimeInMillis(endTimeMillis);
            }
        }

        updateStart();
        updateEnding();

        // Intent intent = ChartFactory.getLineChartIntent(this, getDataset(),
        // getLineRenderer());
        // Intent intent = ChartFactory.getPieChartIntent(this, getPieDataset(),
        // getPieRenderer(),
        // "Categories with random values");
        // Intent intent = ChartFactory.getBarChartIntent(this, getDataset(),
        // getBarRenderer(),
        // Type.DEFAULT);
        // startActivity(intent);

        DataAccessCallbacks<Receipt> receiptDACallback = new DataAccessCallbacks<Receipt>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Receipt> results) {
                int numReceipts = results.size();
                mDS.getDetailsBy(results);                
            }

            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                    boolean result) {
                SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(TesterGraph.this);
                sp.edit().putBoolean("INSERTED", true).apply();
                // showSpendingInTime();
                // showCumulativeSpending();
                showSpendingByCategory();
            }
        };
        
        DataAccessCallbacks<Detail> detailDACallback = new DataAccessCallbacks<Detail>() {
            
            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onDataReceived(List<Detail> results) {
                int num_details = results.size();
                for (int i = 0; i < num_details; ++i) {
                    Log.d(TAG, "Detail (" + i + "): " + results.get(i));
                }                
            }
            
            @Override
            public void onDataProcessed(int processed, List<Detail> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub
                
            }
        };

        mDS = DataSource.getInstance(getApplicationContext());
        mDS.setDetailCallback(detailDACallback);
        mDS.setReceiptCallback(receiptDACallback);

        // SharedPreferences sp =
        // PreferenceManager.getDefaultSharedPreferences(this);
        // boolean inserted = sp.getBoolean("INSERTED", false);
        // if (!inserted) {
        // try {
        // insertReceipts(TEST_RECEIPT_TIMESTAMPS, callback);
        // } catch (ParseException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // } else {
        // // showSpendingInTime();
        // // showCumulativeSpending();
        // showSpendingByCategory();
        // }
    }

    private void showSpendingInTime() {
        Intent intent = new Intent(this, SpendingInTimeGraphActivity.class);
        startActivity(intent);
    }

    private void showCumulativeSpending() {
        Intent intent = new Intent(this, CumulativeSpendingGraphActivity.class);
        startActivity(intent);
    }

    private void showSpendingByCategory() {
        Intent intent = new Intent(this, SpendingByCategoryGraphActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mStartDate != null) outState.putLong(START_TIME, mStartDate.getTimeInMillis());
        if (mEndingDate != null) outState.putLong(ENDING_TIME, mEndingDate.getTimeInMillis());
    }

    private void updateStart() {
        if (mStartDate == null) {
            mTvStartDate.setText(R.string.first_receipt);
        } else {
            mTvStartDate.setText(DateUtils.formatDateTime(this, mStartDate.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE));
        }
    }

    private void updateEnding() {
        if (mEndingDate == null) {
            mTvEndingDate.setText(R.string.last_receipt);
        } else {
            mTvEndingDate.setText(DateUtils.formatDateTime(this, mEndingDate.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE));
        }
    }

    public void onClickShowGraph(View v) {
        mDS.getReceiptsBy(mStartDate, mEndingDate);
    }

    public void onClickSetStartDate(View v) {
        Toast.makeText(this, "Set start date clicked", Toast.LENGTH_SHORT).show();

        // DatePickerDialog.OnDateSetListener startDateCallback = new
        // DatePickerDialog.OnDateSetListener() {
        //
        // @Override
        // public void onDateSet(DatePicker view, int year, int monthOfYear, int
        // dayOfMonth) {
        // mStartDate = Calendar.getInstance();
        // mStartDate.set(Calendar.YEAR, year);
        // mStartDate.set(Calendar.MONTH, monthOfYear);
        // mStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // updateStart();
        // }
        // };
        // new DatePickerDialog(this, startDateCallback,
        // mStartDate.get(Calendar.YEAR),
        // mStartDate.get(Calendar.MONTH),
        // mStartDate.get(Calendar.DAY_OF_MONTH)).show();
        FragmentManager fm = getFragmentManager();
        DialogFragment startDatePickerFragment = (DialogFragment) fm
                .findFragmentByTag(START_DATE_PICKER);
        if (startDatePickerFragment == null) {
            startDatePickerFragment = new StartDatePickerFragment();
        }
        startDatePickerFragment.show(fm, START_DATE_PICKER);
    }

    public void onClickSetEndingDate(View v) {
        Toast.makeText(this, "Set ending date clicked", Toast.LENGTH_SHORT).show();

        // DatePickerDialog.OnDateSetListener endDateCallback = new
        // DatePickerDialog.OnDateSetListener() {
        //
        // @Override
        // public void onDateSet(DatePicker view, int year, int monthOfYear, int
        // dayOfMonth) {
        // mEndingDate = Calendar.getInstance();
        // mEndingDate.set(Calendar.YEAR, year);
        // mEndingDate.set(Calendar.MONTH, monthOfYear);
        // mEndingDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // updateEnding();
        // }
        // };
        //
        // new DatePickerDialog(this, endDateCallback,
        // mEndingDate.get(Calendar.YEAR),
        // mEndingDate.get(Calendar.MONTH),
        // mEndingDate.get(Calendar.DAY_OF_MONTH)).show();
        FragmentManager fm = getFragmentManager();
        DialogFragment endingDatePickerFragment = (DialogFragment) fm
                .findFragmentByTag(ENDING_DATE_PICKER);
        if (endingDatePickerFragment == null) {
            endingDatePickerFragment = new EndingDatePickerFragment();
        }
        endingDatePickerFragment.show(fm, ENDING_DATE_PICKER);
    }

    private XYMultipleSeriesDataset getDataset() {
        Random rnd = new Random(System.currentTimeMillis());

        XYMultipleSeriesDataset series = new XYMultipleSeriesDataset();

        CategorySeries catSeries = new CategorySeries("category series");
        double acumulador = 0;
        for (int i = 0; i < SIZE; ++i) {
            acumulador += rnd.nextDouble() * 100;
            catSeries.add(acumulador);
        }

        mMaxValue = ((int) (acumulador / 10) + 1) * 10;

        Log.d(TAG, "Max value = " + acumulador);
        Log.d(TAG, "Max value (rounded to 10) = " + mMaxValue);

        series.addSeries(catSeries.toXYSeries());
        return series;
    }

    private CategorySeries getPieDataset() {
        CategorySeries series = new CategorySeries("Products");
        Random rnd = new Random();
        for (int i = 0; i < 10; ++i) {
            series.add("Category " + i, rnd.nextDouble());
        }
        return series;
    }

    private float[] colorToHSV(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv;
    }

    private int[] getColors(int number) {
        int[] colors = new int[number];

        float[][] hsv_colors = { colorToHSV(0xFFCC0000), colorToHSV(0xFFFF8800),
                colorToHSV(0xFF669900), colorToHSV(0XFF9833CC), colorToHSV(0XFF0099CC) };

        // I will have 10 base colors.
        // I check if it's divisible by the highest number (from 10 to 1);
        int hsv_pos;
        int hsv_colors_size = hsv_colors.length;
        for (int i = 0; i < number; ++i) {
            hsv_pos = i % 5;
            colors[i] = Color.HSVToColor(hsv_colors[hsv_pos]);
            hsv_colors[hsv_pos][1] = hsv_colors[hsv_pos][1] * 0.75f;
        }

        return colors;
    }

    private DefaultRenderer getPieRenderer() {
        DefaultRenderer renderer = new DefaultRenderer();
        SimpleSeriesRenderer r;

        int[] colors = getColors(10);
        for (int i = 0; i < 10; ++i) {
            r = new SimpleSeriesRenderer();
            r.setColor(colors[i]);
            NumberFormat nf = NumberFormat.getPercentInstance();
            r.setChartValuesFormat(nf);
            renderer.addSeriesRenderer(r);
        }

        renderer.setBackgroundColor(Color.BLACK);
        renderer.setApplyBackgroundColor(true);
        renderer.setLabelsTextSize(15);
        renderer.setDisplayValues(true);
        renderer.setShowLabels(false);
        renderer.setLegendTextSize(20);
        renderer.setFitLegend(false);

        return renderer;
    }

    private XYMultipleSeriesRenderer getBarRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setLabelsColor(Color.parseColor("#0099CC"));
        renderer.setLabelsTextSize(20);
        renderer.setLegendTextSize(20);

        renderer.setBackgroundColor(Color.BLACK);
        renderer.setApplyBackgroundColor(true);
        renderer.setPanEnabled(true, false);
        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setXLabelsAngle(45);
        renderer.setYLabelsAlign(Align.RIGHT);

        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.parseColor("#0099CC"));
        renderer.setBarSpacing(0.15d);

        renderer.setXLabels(0);
        for (int i = 0; i < 10; ++i) {
            renderer.addXTextLabel(i + 1, "Label " + (i + 1));
        }

        renderer.addSeriesRenderer(r);

        return renderer;
    }

    private XYMultipleSeriesRenderer getLineRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        // renderer.setDisplayValues(true);

        renderer.setMargins(new int[] { 16, 100, 100, 8 });
        renderer.setLabelsColor(Color.parseColor("#0099CC"));
        renderer.setLabelsTextSize(20);

        renderer.setBackgroundColor(Color.BLACK);
        renderer.setApplyBackgroundColor(true);
        renderer.setPanEnabled(true, false);
        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setYLabelsAlign(Align.RIGHT);

        renderer.setLegendHeight(0);
        renderer.setLegendTextSize(20f);
        renderer.setPointSize(10f);
        renderer.setXLabelsAngle(60f);

        renderer.setShowCustomTextGridY(true);

        // renderer.setGridColor(Color.WHITE);

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

        for (int i = 0; i < SIZE; ++i) {
            renderer.addXTextLabel(i + 1, "Position " + i);
        }

        renderer.setYAxisMax(mMaxValue);
        renderer.setYAxisMin(0);
        renderer.setYLabels(0);
        double step = mMaxValue / 10;
        double yLabelValue = 0;
        for (int i = 0; i < 10; ++i) {
            renderer.addYTextLabel(yLabelValue, yLabelValue + " �  ");
            yLabelValue += step;
        }

        return renderer;
    }

    @Override
    public Calendar getEndingDate() {
        return mEndingDate;
    }

    @Override
    public void onSetStartDate(Calendar startDate) {
        mStartDate = startDate;
        updateStart();
    }

    @Override
    public void onSetEndingDate(Calendar endingDate) {
        mEndingDate = endingDate;
        updateEnding();
    }

    @Override
    public Calendar getStartDate() {
        return mStartDate;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        Log.d(TAG, "onItemSelected");
        switch (position) {
        case 0:
            mPeriodicity = Periodicity.DAILY;
            Toast.makeText(this, "Daily selected", Toast.LENGTH_SHORT).show();
            break;
        case 1:
            mPeriodicity = Periodicity.WEEKLY;
            Toast.makeText(this, "Weekly selected", Toast.LENGTH_SHORT).show();
            break;
        case 2:
            mPeriodicity = Periodicity.MONTHLY;
            Toast.makeText(this, "Monthly selected", Toast.LENGTH_SHORT).show();
            break;
        case 3:
            mPeriodicity = Periodicity.ANNUAL;
            Toast.makeText(this, "Annual selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Log.d(TAG, "onNothingSelected");
        mPeriodicity = Periodicity.WEEKLY;
        Toast.makeText(this, "NOTHING selected", Toast.LENGTH_SHORT).show();
    }

    private void insertReceipts(String[] date_list, DataAccessCallbacks<Receipt> callback)
            throws ParseException {
        final List<Receipt> receipts = new ArrayList<Receipt>();
        Receipt receipt;
        int total;
        Random rnd = new Random();
        for (int i = 0; i < date_list.length; ++i) {
            receipt = new Receipt();

            total = rnd.nextInt(100) + 1;
            receipt.setTotal(total);

            // receipt.setTotal(rnd.nextInt(100) + 1);
            Log.d(TAG, "fecha: " + date_list[i] + ", total: " + total);

            mServiceDF.setTimeZone(TimeZone.getTimeZone("UTC"));

            receipt.setShopId(1);
            receipt.setTimestamp(mServiceDF.parse(date_list[i]).getTime());
            receipts.add(receipt);
        }
        mDS.setReceiptCallback(callback);

        DataAccessCallbacks<Shop> shopCallbacks = new DataAccessCallbacks<Shop>() {

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                    boolean result) {
                long shop_id = dataList.get(0).getId();

                for (Receipt receipt : receipts) {
                    receipt.setShopId(shop_id);
                }

                mDS.insertReceipts(receipts);
            }

            @Override
            public void onDataReceived(List<Shop> results) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }
        };

        mDS.setShopCallback(shopCallbacks);
        Shop shop = new Shop();
        shop.setAddress("Generic address");
        shop.setChainId(1l);
        shop.setTownId(1l);
        shop.setTownName("Generic twon name");

        List<Shop> shops = new ArrayList<Shop>();
        shops.add(shop);
        mDS.insertShops(shops);
    }

}
