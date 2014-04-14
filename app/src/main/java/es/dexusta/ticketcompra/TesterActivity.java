package es.dexusta.ticketcompra;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.model.Region;
import es.dexusta.ticketcompra.model.Subregion;
import es.dexusta.ticketcompra.model.Town;
import es.dexusta.ticketcompra.util.CatSubcatStructure;
import es.dexusta.ticketcompra.util.Country;
import es.dexusta.ticketcompra.util.CountryList;
import es.dexusta.ticketcompra.util.XmlParser;

public class TesterActivity extends Activity {
    private static final String              TAG               = "TextActiviy";
    private static final boolean             DEBUG             = true;
    private static final boolean             DEBUG_TIMES       = true;
    private static final boolean             DEBUG_PASS2       = false;
    private static final boolean             DEBUG_PASS3       = false;

    private static final int                 NANOS_PER_SECOND  = 1000000000;
    private static final int                 MILLIS_PER_SECOND = 1000;
    private static final int                 mNumMunicipios    = 8117;
    private XmlParser                        mXmlParser;
    private XmlResourceParser                mParser;
    private Long                             mEndTime;
    private Long                             mStartTime;
    private DataSource                       mDS;
    private ProgressBar                      mProgressBar;
    private boolean                          mDBAlreadyExists  = false;
    private DataAccessCallbacks<Region>      mRegionListener;
    private DataAccessCallbacks<Subregion>   mSubregionListener;
    private DataAccessCallbacks<Town>        mTownListener;
    private HashMap<Long, Region>            mMapRegions;
    private HashMap<Long, Subregion>         mMapSubregions;
    private HashMap<Region, List<Subregion>> mMapRegionsSubregions;
    private HashMap<Subregion, List<Town>>   mMapSubregionsTowns;
    private int                              mNumRegReaded;
    private int                              mNumSubregReaded;
    private int                              mNumTownsReaded;
    private int                              mNumRegInserted;
    private int                              mNumSubregInserted;
    private int                              mNumTownsInserted;
    private Country                          mCountry;
    private CountryList                      mCountryList;
    private CatSubcatStructure               mStructure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.tester_activity);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        
        // 
        
        startActivity(new Intent(this, ListReceiptsActivity.class));
        finish();
    }
}
