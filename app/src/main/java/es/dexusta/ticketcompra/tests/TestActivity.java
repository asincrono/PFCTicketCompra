package es.dexusta.ticketcompra.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Subcategory;
import es.dexusta.ticketcompra.model.Town;

public class TestActivity extends Activity {

    private TextView mTvInfo;
    private DataSource mDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mTvInfo = (TextView) findViewById(R.id.tv_info);

        TestData.TestCallback callback = new TestData.TestCallback() {
            @Override
            public void afterDataRetrieved() {
                List<Chain> chainList = TestData.getChains();
                List<Subcategory> subcategoryList = TestData.getSubcategories();
                List<Town> townList = TestData.getTowns();

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Number of chains retrieved is: " + chainList.size() + ".\n");
                stringBuilder.append("Number of subcategories retrieved is: " + subcategoryList.size() + ".\n");
                stringBuilder.append("Number of towns retrieved is: " + townList.size() + ".\n");

                String townName;

                int countCoincidences = 0;
                for (Town town : townList) {
                    townName = town.getName();


                    for (Town secondTown : townList) {
                        if (town.getName().equals(secondTown.getName())) {
                            countCoincidences += 1;
                        }
                    }


                }

                mTvInfo.setText(stringBuilder);
            }
        };

        mDS = DataSource.getInstance(getApplicationContext());

        TestData.buildDataInfo(mDS, callback);
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
}
