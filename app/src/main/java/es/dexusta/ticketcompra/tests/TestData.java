package es.dexusta.ticketcompra.tests;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
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
public class TestData {
    private static final String TAG = "TestData";

    private static TestData mInstance = null;

    private Context    mContext;
    private DataSource mDataSource;

    private ChainInfo       mChainInfo;
    private SubcategoryInfo mSubcategoryInfo;
    private ProductInfo     mProductInfo;
    private ShopInfo        mShopInfo;
    private TownInfo        mTownInfo;

    private boolean mInsertTestData = false;

    private int mNumDetails;

    private ChainedCallback mAfterBuildDataInfoCallback;
    private ChainedCallback mAfterInsertTestDataCallback;

    private TestData(Context context, DataSource dataSource) {
        mContext = context;
        mDataSource = dataSource;
    }

    public static TestData getInstance(Context context, DataSource dataSource) {
        if (mInstance == null) {
            mInstance = new TestData(context, dataSource);
        } else {
            mInstance.mContext = context;
            mInstance.mDataSource = dataSource;
        }

        return mInstance;
    }

    public void buildDataInfo(ChainedCallback afterBuildDataInfoCallback,
            ChainedCallback afterInsertTestDataCallback, boolean insertTestData) {
        mInsertTestData = insertTestData;
        mAfterBuildDataInfoCallback = afterBuildDataInfoCallback;
        mAfterInsertTestDataCallback = afterInsertTestDataCallback;
        buildChainInfo(mDataSource);
    }

    private ProductStructure[] readProducts() {
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.products);
        InputStreamReader jsonReader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        return gson.fromJson(jsonReader, ProductStructure[].class);
    }

    private ShopStructure[] readShops() {
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.shops);
        InputStreamReader jsonReader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        return gson.fromJson(jsonReader, ShopStructure[].class);
    }

    private void insertProducts() {

        mDataSource.setProductCallback(new DataAccessCallbacks<Product>() {
            @Override
            public void onDataProcessed(int processed, List<Product> dataList, Types.Operation operation, boolean result) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Inserted " + dataList.size() + " test products");
                insertShops();
            }

            @Override
            public void onDataReceived(List<Product> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });


        ProductStructure[] productStructures = readProducts();

        mProductInfo = new ProductInfo(productStructures, mSubcategoryInfo);

        List<Product> productList = mProductInfo.getProducts();

        mDataSource.insertProducts(productList);
    }

    private void insertShops() {

        // TODO: insert shops, for each shop: insert receipts, for each receipt: insert details.
        mDataSource.setShopCallback(new DataAccessCallbacks<Shop>() {
            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Inserted " + dataList.size() + " test shops.");
                List<Receipt> receipts;
                for (Shop shop : dataList) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "(Inserted shop with id: " + shop.getId() + ")");
                    receipts = mShopInfo.getReceipts(shop);

                    mDataSource.insertReceipts(receipts);
                }
            }

            @Override
            public void onDataReceived(List<Shop> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        mDataSource.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Types.Operation operation, boolean result) {
                List<Detail> details;

                for (Receipt receipt : dataList) {
                    details = mShopInfo.getDetails(receipt);
                    mNumDetails += details.size();
                }

                for (Receipt receipt : dataList) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "Inserted " + dataList.size() + " test receipts");

                    mDataSource.insertDetails(mShopInfo.getDetails(receipt));
                }
            }

            @Override
            public void onDataReceived(List<Receipt> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        mDataSource.setDetailCallback(new DataAccessCallbacks<Detail>() {
            @Override
            public void onDataProcessed(int processed, List<Detail> dataList, Types.Operation operation, boolean result) {
                for (Detail detail : dataList) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "Detail inserted (receipt id : " + detail.getReceiptId() + ") .");
                }
                mNumDetails -= dataList.size();
                if ((mNumDetails == 0) && (mAfterInsertTestDataCallback != null))
                    mAfterInsertTestDataCallback.after();
            }

            @Override
            public void onDataReceived(List<Detail> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        ShopStructure[] shopStructures = readShops();

        mShopInfo = new ShopInfo(shopStructures, mChainInfo, mProductInfo, mTownInfo);

        List<Shop> shopList = mShopInfo.getShops();

        mDataSource.insertShops(shopList);
    }

    private void buildChainInfo(final DataSource dataSource) {
        dataSource.setChainCallback(new DataAccessCallbacks<Chain>() {
            @Override
            public void onDataProcessed(int processed, List<Chain> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Chain> results) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Building chain info.");
                mChainInfo = new ChainInfo(results);
                buildSubcategoryInfo(dataSource);
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.listChains();
    }

    private void buildSubcategoryInfo(final DataSource dataSource) {
        dataSource.setSubcategoryCallback(new DataAccessCallbacks<Subcategory>() {
            @Override
            public void onDataProcessed(int processed, List<Subcategory> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Subcategory> results) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Building subcategory info.");
                mSubcategoryInfo = new SubcategoryInfo(results);
                buildProductInfo();
                buildTownInfo(dataSource);
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.listSubcategories();
    }

    private void buildProductInfo() {
        mProductInfo = new ProductInfo(readProducts(), mSubcategoryInfo);
    }

    private void buildTownInfo(DataSource dataSource) {
        dataSource.setTownCallback(new DataAccessCallbacks<Town>() {
            @Override
            public void onDataProcessed(int processed, List<Town> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Town> results) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Building town info.");
                mTownInfo = new TownInfo(results);

                if (mAfterBuildDataInfoCallback != null) {
                    mAfterBuildDataInfoCallback.after();
                }

                if (mInsertTestData) {
                    insertProducts();
                }
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.listTwons();
    }


    public ShopInfo getShopInfo() {
        return mShopInfo;
    }

    public HashMap<Long, Chain> getChainMap() {
        return mChainInfo.getIdChainMap();
    }

    public List<Shop> getShops() {
        return mShopInfo.getShops();
    }

    public List<Receipt> getReceipts(Shop shop) {
        return mShopInfo.getReceipts(shop);
    }

    public List<Detail> getDetails(Receipt receipt) {
        return mShopInfo.getDetails(receipt);
    }

    public long getChainId(String name) {
        return mChainInfo.getId(name);
    }

    public long getProductId(String name) {
        return mProductInfo.getId(name);
    }

    public long getSubcategoryId(String name) {
        return mSubcategoryInfo.getId(name);
    }

    public void clear() {
        mContext = null;
        mDataSource = null;

        mChainInfo = null;
        mSubcategoryInfo = null;
        mProductInfo = null;
        mShopInfo = null;
        mTownInfo = null;

        mInstance = null;
    }

    public interface ChainedCallback {
        public void after();
    }
}
