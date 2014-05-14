package es.dexusta.ticketcompra.tests;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.StateFragment;
import es.dexusta.ticketcompra.dataaccess.DataSource;

public class TestActivity extends Activity {
    private static final java.lang.String TAG = "TestActivity";

    private static final String TAG_STATE_FRAGMENT = "state_fragment";

    private static final String KEY_TEST_DATA = "test_data";

    private TextView   mTvInfo;
    private DataSource mDS;
    private TestData mTestData;

    private StateFragment mStateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        FragmentManager manager = getFragmentManager();
        mStateFragment = (StateFragment) manager.findFragmentByTag(TAG_STATE_FRAGMENT);

        if (mStateFragment == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            mStateFragment = new StateFragment();
            transaction.add(mStateFragment, TAG_STATE_FRAGMENT).commit();
        }

        final StringBuilder stringBuilder = new StringBuilder();

        mTvInfo = (TextView) findViewById(R.id.tv_info);

        mDS = DataSource.getInstance(getApplicationContext());

        mTestData = (TestData) mStateFragment.get(KEY_TEST_DATA);

        if (mTestData == null) {
            mTestData = TestData.getInstance(getApplicationContext(), mDS);
        }


        // Todo: Check if the data structures from the jsons are correctly built.
        mTestData.buildDataInfo(new TestData.ChainedCallback() {
            @Override
            public void after() {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Después de build data info.");
            }
        },
        new TestData.ChainedCallback() {
            @Override
            public void after() {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Después de insertar los datos.");

                mTvInfo.setText(mTestData.getShopInfo().toString());
            }
        },
        true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onStop (cleared data).");
        super.onStop();
        if (mTestData != null) {
            mTestData.clear();
            mTestData = null;
        }
    }
}
