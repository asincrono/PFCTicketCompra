package es.dexusta.ticketcompra.tests;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Subcategory;
import es.dexusta.ticketcompra.model.Town;

public class TestActivity extends Activity {
    private static final java.lang.String TAG = "TestActivity";

    private TextView   mTvInfo;
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

                int repeatedTows = 0;
                int countCoincidences = 0;

                // For every town (name) in the list, I will compare it with all the names.
//                int lenght = townList.size();
//                String strOutTown, strInnTown;
//                for (int posOut = 0; posOut < lenght; posOut += 1) {
//                    strOutTown = townList.get(posOut).getName();
//                    for (int posInn = 0; posInn < lenght; posInn += 1) {
//                        strInnTown = townList.get(posInn).getName();
//                        if ((posOut != posInn) && strInnTown.equals(strOutTown)) {
//
//                                if (BuildConfig.DEBUG)
//                                    Log.d(TAG, strOutTown + " occurrences at "
//                                            + posOut + " and " + posInn + ".");
//                                countCoincidences += 1;
//                        }
//                    }
//                    if (countCoincidences >= 1) {
//                        stringBuilder.append("(Town name: ").append(strOutTown)
//                                .append(" is repeated ").append(countCoincidences)
//                                .append(" times.\n");
//                        countCoincidences = 0;
//                        repeatedTows += 1;
//                    }
//                }
//
//                String[] threeWords = {"hola", "mundo", "cruel"};
//                List<String> wordList = Arrays.asList(threeWords);
//
//
//                int count = 0;
//                int repeated = 0;
//                for(String outStr : wordList) {
//                    for (String innStr : wordList) {
//                        if (outStr.equals(innStr)) {
//                            count += 1;
//                        }
//                    }
//                    if (count > 1) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "repeated!");
//                        count = 0;
//                        repeated += 1;
//                    }
//                }
//
//                if (BuildConfig.DEBUG)
//                    Log.d(TAG, repeated + " repeated words.");
//
//
//                if (BuildConfig.DEBUG)
//                    Log.d(TAG, "repeated towns = " + repeatedTows);

//                for (Town town : townList) {
//                    for (Town secondTown : townList) {
//                        if (town.getName().equals(secondTown.getName())) {
//
//                            countCoincidences += 1;
//                            if (countCoincidences > 1)
//                                Log.d(TAG, "\"" + town.getName() + "\" is equal to \"" + secondTown.getName() + "\"");
//                        }
//                    }
//                    if (countCoincidences > 1) {
//                        stringBuilder.append("(Town name: ").append(town.getName())
//                                .append(" is repeated ").append(countCoincidences - 1)
//                                .append(" times.\n");
//                        countCoincidences = 0;
//                        repeatedTows += 1;
//                    }
//                }
//
//                if (BuildConfig.DEBUG)
//                    Log.d(TAG, "Repeated towns = " + repeatedTows);


                ProductStructure[] products = TestData.getProducts(TestActivity.this);
                List<Product> productList = TestData.getProductList(products);
                ShopStructure[] shops = TestData.getShops(TestActivity.this);
                ReceiptStructure[] receipts = TestData.getReceipts(TestActivity.this);

                if (products == null) {
                    stringBuilder.append("products list is null, we couldn't get it :(\n");
                } else {
                    stringBuilder.append("\nRead ").append(products.length).append(" products.\n");
                    stringBuilder.append(ProductStructure.getString(products)).append("\n");
                    if (productList.isEmpty()) if (BuildConfig.DEBUG)
                        Log.d(TAG, "product list is empty.");

                    for (Product product : productList) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "product: " + product.getName() +
                                    " subcategoryId: " + product.getSubcategoryId());
                    }
                }

                if (shops == null) {
                    stringBuilder.append("shops list is null, we couldn't get it :(\n");
                } else {
                    stringBuilder.append("Read ").append(shops.length).append(" shops.\n");
                    stringBuilder.append(ShopStructure.getString(shops)).append("\n");
                }

                if (receipts == null) {
                    stringBuilder.append("receipts list is null, we couldn't get it :(\n");
                } else {
                    stringBuilder.append("Read ").append(receipts.length).append(" receipts.\n");
                    stringBuilder.append(ReceiptStructure.getString(receipts)).append("\n");
                }



                mTvInfo.setText(stringBuilder);
            }
        };



        mDS = DataSource.getInstance(getApplicationContext());

        TestData.buildDataInfo(mDS, callback);

        // Todo: Check if the data structures from the jsons are correctly built.
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
