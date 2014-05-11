package es.dexusta.ticketcompra.tests;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Region;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.model.Subcategory;
import es.dexusta.ticketcompra.model.Subregion;
import es.dexusta.ticketcompra.model.Town;

/**
 * Created by asincrono on 02/05/14.
 */

public class TestData {
    private static final java.lang.String TAG = "TestData";


    private static List<Product> mProducts;
    private static List<Shop>    mShops;
    private static int mNumRegions    = 0;
    private static int mNumSubregions = 0;
    private static int mNumTowns      = 0;

    private static RegionInfo      mRegionInfo      = new RegionInfo();
    private static ChainInfo       mChainInfo       = new ChainInfo();
    private static TownInfo        mTownInfo        = new TownInfo();
    private static SubcategoryInfo mSubcategoryInfo = new SubcategoryInfo();


    public static void buildRegionStructure(final DataSource dataSource) {
        dataSource.setRegionCallback(new DataAccessCallbacks<Region>() {
            @Override
            public void onDataProcessed(int processed, List<Region> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Region> results) {
                Region[] regions = results.toArray(new Region[results.size()]);
                mRegionInfo.add(regions);
                mNumRegions += results.size();
                for (Region region : regions) {
                    dataSource.getSubregionBy(region);
                }
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.setSubregionCallback(new DataAccessCallbacks<Subregion>() {
            @Override
            public void onDataProcessed(int processed, List<Subregion> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Subregion> results) {
                mNumRegions -= 1;
                mNumSubregions += results.size();
                Subregion[] subregions = results.toArray(new Subregion[results.size()]);
                mRegionInfo.add(subregions);

                for (Subregion subregion : subregions)
                    dataSource.getTownsBy(subregion);

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.setTownCallback(new DataAccessCallbacks<Town>() {
            @Override
            public void onDataProcessed(int processed, List<Town> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Town> results) {
                mNumSubregions -= 1;

                Town[] towns = results.toArray(new Town[results.size()]);
                mRegionInfo.add(towns);
                if (mNumSubregions == 0) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "Last bunch of towns added to the structure.");

                } else {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, results.size() + " towns added, " + mNumSubregions +
                                " more subregions to go.");
                }
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.listRegions();
    }

    private static void buildChainInfo(final DataSource dataSource, final TestCallback callback) {
        dataSource.setChainCallback(new DataAccessCallbacks<Chain>() {
            @Override
            public void onDataProcessed(int processed, List<Chain> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Chain> results) {
                if (results.size() > 0)
                    mChainInfo.add(results);

                if (BuildConfig.DEBUG)
                    Log.d(TAG, "buildChainInfo done.");

                buildSubcategoryInfo(dataSource, callback);
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.listChains();
    }

    private static void buildSubcategoryInfo(final DataSource dataSource, final TestCallback callback) {
        dataSource.setSubcategoryCallback(new DataAccessCallbacks<Subcategory>() {
            @Override
            public void onDataProcessed(int processed, List<Subcategory> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Subcategory> results) {
                if (results.size() > 0)
                    mSubcategoryInfo.add(results);
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "buildSubcategoryInfo done.");
                buildTownInfo(dataSource, callback);
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        dataSource.listSubcategories();
    }

    private static void buildTownInfo(DataSource dataSource, final TestCallback callback) {
        dataSource.setTownCallback(new DataAccessCallbacks<Town>() {
            @Override
            public void onDataProcessed(int processed, List<Town> dataList, Types.Operation operation, boolean result) {

            }

            @Override
            public void onDataReceived(List<Town> results) {
                if (results.size() > 0)
                    mTownInfo.add(results);

                if (BuildConfig.DEBUG)
                    Log.d(TAG, "buildTownInfo done and about to call callback method.");

                callback.afterDataRetrieved();
            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });
        dataSource.listTwons();
    }

    public static void buildDataInfo(DataSource dataSource, TestCallback callback) {
        // buildChainInfo is chained to buildSubcategoryInfo. And this one to buildTownInfo. So all
        // three methods would be called in sequence. All the work will be done in an async task.
        buildChainInfo(dataSource, callback);
    }

    public static List<Chain> getChains() {
        return mChainInfo.getChains();
    }

    public static List<Town> getTowns() {
        return mTownInfo.getTowns();
    }


    public static List<Subcategory> getSubcategories() {
        return mSubcategoryInfo.getSubcategories();
    }

    public static List<Product> getProductList(ProductStructure[] products) {
        List<Product> productList = new ArrayList<Product>(products.length);

        Product product;
        long subcategoryId;
        for (ProductStructure productStructure : products) {
            product = new Product();

            product.setName(productStructure.getName());
            product.setDescription(productStructure.getDescription());

            subcategoryId = mSubcategoryInfo.getId(productStructure.getSubcategoryName());
            if (subcategoryId < 0) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "subcategory: " + productStructure.getSubcategoryName() + " not found.");
            }
            product.setSubcategoryId(subcategoryId);
            productList.add(product);
        }

        return productList;
    }

    public static List<Shop> getShopList(ShopStructure[] shops) {
        List<Shop> shopList = new ArrayList<Shop>(shops.length);

        Shop shop;
        long townId, chainId;
        for (ShopStructure shopStructure : shops) {
            shop = new Shop();

            shop.setAddress(shopStructure.getAddress());
            shop.setTownName(shopStructure.getTownName());

            townId = mTownInfo.getId(shopStructure.getTownName());
            shop.setTownId(townId);

            chainId = mChainInfo.getId(shopStructure.getChainName());
            shop.setChainId(chainId);

            shopList.add(shop);
        }

        return shopList;
    }



    public static List<Receipt> getReceiptList(ReceiptStructure[] receipts) {
        List<Receipt> receiptList = new ArrayList<Receipt>(receipts.length);

        return receiptList;
    }


    public static ProductStructure[] getProducts(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.products);
        InputStreamReader jsonReader = new InputStreamReader(inputStream);

        Gson gson = new Gson();

        return gson.fromJson(jsonReader, ProductStructure[].class);
    }

    public static ShopStructure[] getShops(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.shops);
        InputStreamReader jsonReader = new InputStreamReader(inputStream);

        Gson gson = new Gson();

        return gson.fromJson(jsonReader, ShopStructure[].class);
    }

    public static ReceiptStructure[] getReceipts(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.receipts);
        InputStreamReader jsonReader = new InputStreamReader(inputStream);

        Gson gson = new Gson();

        return gson.fromJson(jsonReader, ReceiptStructure[].class);
    }


    public static void InsertProducs(Context context, final DataSource dataSource) {
        final HashMap<java.lang.String, Long> subcategoryIdMap = new HashMap<java.lang.String, Long>();

        // Build product structure from json.
        InputStream inputStream = context.getResources().openRawResource(R.raw.products);
        InputStreamReader jsonReader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        ProductStructure[] productStructures = gson.fromJson(jsonReader, ProductStructure[].class);

        List<Product> productList = getProductList(productStructures);
        dataSource.insertProducts(productList);
    }

    public static void insertShops(Context context, DataSource dataSource) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.shops);
        InputStreamReader jsonReader = new InputStreamReader(inputStream);

        Gson gson = new Gson();

        ShopStructure[] shopStructures = gson.fromJson(jsonReader, ShopStructure[].class);

        List<Shop> shopList = getShopList(shopStructures);

        dataSource.insertShops(shopList);
    }

    private static ReceiptStructure[] buildReceiptStructure(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.receipts);

        InputStreamReader jsonReader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        return gson.fromJson(jsonReader, ReceiptStructure[].class);
    }

    interface TestCallback {
        public void afterDataRetrieved();
    }
}
