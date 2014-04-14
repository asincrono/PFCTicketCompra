package es.dexusta.ticketcompra.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.text.format.DateFormat;
import android.view.animation.DecelerateInterpolator;

import com.google.api.client.util.DateTime;

public class Interval {
    public static final SimpleDateFormat DF_RFC3339    = new SimpleDateFormat(
                                                               "yyyy-MM-dd'T'HH:mm:ss.SSSz",
                                                               Locale.getDefault());
    public static final SimpleDateFormat DF_DEFAULT    = new SimpleDateFormat(
                                                               "yyyy-MM-dd HH:mm:ss.SSS zz",
                                                               Locale.getDefault());
    public static final SimpleDateFormat DF_DIALY      = new SimpleDateFormat("EE dd MMM",
                                                               Locale.getDefault());
    public static final SimpleDateFormat DF_WEEKLY     = new SimpleDateFormat("W'ª sem.' MMM",
                                                               Locale.getDefault());
    public static final SimpleDateFormat DF_MONTHLY    = new SimpleDateFormat("MMM yyyy",
                                                               Locale.getDefault());
    public static final SimpleDateFormat DF_ANNUAL     = new SimpleDateFormat("y",
                                                               Locale.getDefault());

    public static final long             MILLIS_SECOND = 1000;
    public static final long             MILLIS_MINUTE = 60000;
    public static final long             MILLIS_HOUR   = 3600000;

    public static final long             MILLIS_DAY    = 86400000;

    private Calendar                     mStart;
    private Calendar                     mEnd;

    private Periodicity                  mPeriodicity;

    public Interval(long start, Periodicity periodicity, boolean flat) {
        mStart = Calendar.getInstance();
        mStart.setTimeInMillis(start);
        mPeriodicity = periodicity;

        if (flat) {
            flatten(mStart);
        }

        mEnd = (Calendar) mStart.clone();
        increase(mEnd);
        mEnd.add(Calendar.MILLISECOND, -1);
    }

    public Interval(Calendar start, Periodicity periodicity, boolean flat) {
        mStart = start;
        mPeriodicity = periodicity;

        if (flat) {
            flatten(mStart);
        }

        mEnd = (Calendar) mStart.clone();
        increase(mEnd);
        mEnd.add(Calendar.MILLISECOND, -1);
    }

    public Interval(Date start, Periodicity periodicity, boolean flat) {
        mStart = Calendar.getInstance();
        mStart.setTime(start);
        mPeriodicity = periodicity;

        if (flat) {
            flatten(mStart);
        }

        mEnd = (Calendar) mStart.clone();
        increase(mEnd);
        mEnd.add(Calendar.MILLISECOND, -1);
    }

    public Interval(DateTime start, Periodicity periodicity, boolean flat) {        
        mStart = new GregorianCalendar(TimeZone.getTimeZone("UTC"));        
        mStart.setTimeInMillis(start.getValue());
        mPeriodicity = periodicity;
        
        if (flat) {
            flatten(mStart);
        }
        
        mEnd = (Calendar) mStart.clone();
        increase(mEnd);
        mEnd.add(Calendar.MILLISECOND, -1);
    }
    
    public boolean contains(Calendar date) {
        if (mStart.equals(date)) return true;
        if (mEnd.equals(date)) return true;
        if (mStart.before(date) && mEnd.after(date)) return true;
        return false;
    }

    public boolean contains(String date) {
        DateTime dt = new DateTime(date);
        long date_millis = dt.getValue();
        if ((mStart.getTimeInMillis() <= date_millis) && (date_millis <= mEnd.getTimeInMillis())) {
            return true;
        }
        return false;
    }
    
    public boolean contains(DateTime date) {
        long date_millis = date.getValue();
        
        return (mStart.getTimeInMillis() <= date_millis) && (date_millis <= mEnd.getTimeInMillis());
    }

    private void flatten(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        switch (mPeriodicity) {
        case DAILY:
            break;
        case WEEKLY:
            date.set(Calendar.DAY_OF_WEEK, date.getFirstDayOfWeek());
            break;
        case MONTHLY:
            date.set(Calendar.DAY_OF_MONTH, 1);
            break;
        case ANNUAL:
            date.set(Calendar.DAY_OF_YEAR, 1);

        }
    }

    public void increase() {
        increase(mStart);
        increase(mEnd);
    }

    public void decrease() {
        decrease(mStart);
        decrease(mEnd);
    }
    
    public Interval getNext() {
        Calendar newStart = (Calendar) mStart.clone();
        increase(newStart);
        return new Interval(newStart, mPeriodicity, false);
    }

    public Interval getPrevious() {
        Calendar newStart = (Calendar) mStart.clone();
        decrease(newStart);
        return new Interval(newStart, mPeriodicity, false);
    }
    
    private void increase(Calendar date) {

        switch (mPeriodicity) {
        case DAILY:
            date.add(Calendar.DAY_OF_YEAR, 1);
            break;
        case WEEKLY:
            date.add(Calendar.WEEK_OF_YEAR, 1);
            break;
        case MONTHLY:
            date.add(Calendar.MONTH, 1);
            break;
        case ANNUAL:
            date.add(Calendar.YEAR, 1);
        }
    }
    
    private void decrease(Calendar date) {
        switch (mPeriodicity) {
        case DAILY:
            date.add(Calendar.DAY_OF_YEAR, -1);
            break;
        case WEEKLY:
            date.add(Calendar.WEEK_OF_YEAR, -1);
            break;
        case MONTHLY:
            date.add(Calendar.MONTH, -1);
            break;
        case ANNUAL:
            date.add(Calendar.YEAR, -1);
        }
    }

    public long getStart() {
        return mStart.getTimeInMillis();
    }

    public Date getStartDate() {
        return mStart.getTime();
    }

    public Calendar getStartCal() {
        return (Calendar) mStart.clone();
    }

    public long getEnd() {
        return mEnd.getTimeInMillis();
    }

    public Date getEndDate() {
        return mEnd.getTime();
    }

    public Calendar getEndCal() {
        return (Calendar) mEnd.clone();
    }

    public String getLabel() {
        String result = null;

        switch (mPeriodicity) {
        case DAILY:
            result = DF_DIALY.format(mStart.getTime());
            break;
        case WEEKLY:
            result = DF_WEEKLY.format(mStart.getTime());
            break;
        case MONTHLY:
            result = DF_MONTHLY.format(mStart.getTime());
            break;
        case ANNUAL:
            result = DF_ANNUAL.format(mStart.getTime());
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Start: ").append(DF_RFC3339.format(mStart.getTime())).append("\n");
        sb.append("End: ").append(DF_RFC3339.format(mEnd.getTime())).append("\n");
        return sb.toString();
    }

    public enum Periodicity {
        DAILY, WEEKLY, MONTHLY, ANNUAL
    }
    
    public static String toRfc3339ZuluString(Calendar calendar) {        
        DateTime dt = new DateTime(false, calendar.getTimeInMillis(), 0);
        return dt.toStringRfc3339();
    }

}
