package es.dexusta.ticketcompra.tests;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.model.Subcategory;
import es.dexusta.ticketcompra.model.Town;

/**
 * Created by asincrono on 11/05/14.
 */
public class TestDataV2 {
    private static final String TAG = "TestDataV2";

    private static ChainInfo       mChainInfo;

    private static SubcategoryInfo mSubcategoryInfo;
    private static ProductInfo mProductInfo;
    private static ShopInfo mShopInfo;
    private static TownInfo mTownInfo;

    public static void buildDataInfo(final Context context, final DataSource dataSource, final ChainedCallback callback) {
        buildChainInfo(dataSource, new ChainedCallback() {
            @Override
            public void after() {
                insertShops(context, dataSource);
            }
        });
    }

    private static ProductStructure[] readProducts(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.products);
        InputStreamReader jsonReader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        return gson.fromJson(jsonReader, ProductStructure[].class);
    }

    private static ShopStructure[] readShops(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.shops);
        InputStreamReader jsonReader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        return gson.fromJson(jsonReader, ShopStructure[].class);
    }

    private static void insertProducts(final Context context, final DataSource dataSource) {

        ProductStructure[] productStructures = readProducts(context);

        mProductInfo = new ProductInfo(productStructures, mSubcategoryInfo);

        List<Product> productList = mProductInfo.getProducts();

        dataSource.setProductCallback(new DataAccessCallbacks<Product>() {
            @Override
            public void onDataProcessed(int processed, List<Product> dataList, Types.Operation operation, boolean result) {
                insertShops(context, dataSource);
            }

            @Override
            public void onDataReceived(List<Product> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.insertProducts(productList);
    }

    private static void insertShops(final Context context, final DataSource dataSource) {
        ShopStructure[] shopStructures = readShops(context);

        mShopInfo = new ShopInfo(shopStructures, mChainInfo, mProductInfo, mTownInfo);

        // TODO: insert shops, for each shop: insert receipts, for each receipt: insert details.
        dataSource.setShopCallback(new DataAccessCallbacks<Shop>() {
            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
                for (Shop shop : dataList) {
                    dataSource.insertReceipts(mShopInfo.getReceipts(shop));
                }
            }

            @Override
            public void onDataReceived(List<Shop> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Types.Operation operation, boolean result) {
                for (Receipt receipt : dataList) {
                    dataSource.insertDetails(mShopInfo.getDetails(receipt));
                }
            }

            @Override
            public void onDataReceived(List<Receipt> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.setDetailCallback(new DataAccessCallbacks<Detail>() {
            @Override
            public void onDataProcessed(int processed, List<Detail> dataList, Types.Operation operation, boolean result) {
                for (Detail detail : dataList) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "Detail inserted (receipt id : " + detail.getReceiptId() + ") .");
                }

            }

            @Override
            public void onDataReceived(List<Detail> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.insertShops(mShopInfo.getShops());
    }

    private static void buildChainInfo(final DataSource dataSource, final ChainedCallback callback) {
        dataSource.setChainCallback(new DataAccessCallbacks<Chain>() {
            @Override
            public void onDataProcessed(int processed, List<Chain> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Chain> results) {
                mChainInfo = new ChainInfo(results);
                buildSubcategoryInfo(dataSource, callback);
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.listChains();
    }

    private static void buildSubcategoryInfo(final DataSource dataSource, final ChainedCallback callback) {
        dataSource.setSubcategoryCallback(new DataAccessCallbacks<Subcategory>() {
            @Override
            public void onDataProcessed(int processed, List<Subcategory> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Subcategory> results) {
                mSubcategoryInfo = new SubcategoryInfo(results);
                buildTownInfo(dataSource, callback);
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.listSubcategories();
    }

    private static void buildTownInfo(DataSource dataSource, final ChainedCallback callback) {
        dataSource.setTownCallback(new DataAccessCallbacks<Town>() {
            @Override
            public void onDataProcessed(int processed, List<Town> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Town> results) {
                mTownInfo = new TownInfo(results);
                callback.after();
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.listTwons();
    }


    public interface ChainedCallback {
        public void after();
    }
}
