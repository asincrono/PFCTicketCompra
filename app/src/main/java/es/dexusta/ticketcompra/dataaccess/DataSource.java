package es.dexusta.ticketcompra.dataaccess;

import static es.dexusta.ticketcompra.model.DBHelper.TBL_CATEGORY;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_CHAIN;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_DETAIL;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_PRODUCT;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_RECEIPT;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_REGION;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_SHOP;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_SUBCATEGORY;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_SUBREGION;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_TOTAL;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_TOWN;
import static es.dexusta.ticketcompra.model.DBHelper.T_CAT_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_CHAIN_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_DETAIL_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_DETAIL_PRICE;
import static es.dexusta.ticketcompra.model.DBHelper.T_DETAIL_PROD_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_DETAIL_RECPT_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_DETAIL_UNITS;
import static es.dexusta.ticketcompra.model.DBHelper.T_DETAIL_UPDATED;
import static es.dexusta.ticketcompra.model.DBHelper.T_DETAIL_WEIGHT;
import static es.dexusta.ticketcompra.model.DBHelper.T_PROD_ARTNUMBER;
import static es.dexusta.ticketcompra.model.DBHelper.T_PROD_DESCR;
import static es.dexusta.ticketcompra.model.DBHelper.T_PROD_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_PROD_ID_ALT;
import static es.dexusta.ticketcompra.model.DBHelper.T_PROD_NAME;
import static es.dexusta.ticketcompra.model.DBHelper.T_PROD_NAME_ALT;
import static es.dexusta.ticketcompra.model.DBHelper.T_PROD_SUBCAT_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_PROD_SUBCAT_ID_ALT;
import static es.dexusta.ticketcompra.model.DBHelper.T_PROD_UPDATED;
import static es.dexusta.ticketcompra.model.DBHelper.T_RECPT_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_RECPT_SHOP_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_RECPT_TIMESTAMP;
import static es.dexusta.ticketcompra.model.DBHelper.T_RECPT_UPDATED;
import static es.dexusta.ticketcompra.model.DBHelper.T_REGION_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_SHOP_CHAIN_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_SHOP_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_SHOP_UPDATED;
import static es.dexusta.ticketcompra.model.DBHelper.T_SUBCAT_CAT_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_SUBCAT_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_SUBREGION_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_SUBREGION_REGION_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_TOTAL_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_TOTAL_RECPT_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_TOTAL_UPDATED;
import static es.dexusta.ticketcompra.model.DBHelper.T_TOWN_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_TOWN_SUBREGION_ID;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.R.raw;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.cloud.backend.android.CloudBackendMessaging;

import es.dexusta.ticketcompra.backendataaccess.BackendDataAccess;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.InitializeDBTask.InitializerCallback;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.DBHelper;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Region;
import es.dexusta.ticketcompra.model.ReplicatedDBObject;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.model.Subcategory;
import es.dexusta.ticketcompra.model.Subregion;
import es.dexusta.ticketcompra.model.Total;
import es.dexusta.ticketcompra.model.Town;
import es.dexusta.ticketcompra.util.Installation;
import es.dexusta.ticketcompra.util.Interval;

/**
 * Singleton que agrupa todo el sistema de acceso a los datos.
 * 
 * @author asincrono
 * 
 */
// TODO: Realizar como fragmento en lugar de singleton?
public class DataSource {
    private static final String                     TAG                       = "DataSource";
    private static final boolean                    DEBUG                     = true;

    // SQL QUERY CONSTANTS:
    public static final String                      BASIC_QUERY               = "SELECT * FROM ";
    // do not remove first space (not an error).
    public static final String                      BASIC_WHERE               = " WHERE ";
    // do not remove first space (not an error).
    public static final String                      IS_NULL                   = " IS NULL";

    private static DataSource                       mDataSource;

    private DBHelper                                mHelper;
    private Context                                 mContext;

    private HashMap<Long, String>                   mCategoryIdNameMap        = new HashMap<Long, String>();
    private HashMap<Long, String>                   mSubcategoryIdNameMap     = new HashMap<Long, String>();
    private HashMap<Long, Long>                     mSubcategoryCategoryIdMap = new HashMap<Long, Long>();

    private HashMap<String, Long>                   mShopUnivIdLocIdMap       = new HashMap<String, Long>();
    private HashMap<String, Long>                   mProductUnivIdLocIdMap    = new HashMap<String, Long>();
    private HashMap<Long, Long>                     mProductSubcategoryId     = new HashMap<Long, Long>();
    private HashMap<String, Long>                   mReceiptUnivIdLocIdMap    = new HashMap<String, Long>();

    private HashMap<Long, String>                   mChainIdNameMap           = new HashMap<Long, String>();
    private HashMap<Long, Long>                     mShopIdChainIdMap         = new HashMap<Long, Long>();

    // private DataAccessCallbacks<Category> mCategoryListener;
    // private DataAccessCallbacks<Subcategory> mSubcategoryListener;
    // private DataAccessCallbacks<Product> mProductListener;
    //
    // private DataAccessCallbacks<Chain> mChainListener;
    // private DataAccessCallbacks<Shop> mShopListener;
    //
    // private DataAccessCallbacks<Receipt> mReceiptListener;
    // private DataAccessCallbacks<Detail> mDetailListener;
    // private DataAccessCallbacks<Total> mTotalListener;

    // private DataAccessCallbacks<Region> mRegionDataListener;
    // private DataAccessCallbacks<Subregion> mSubregionDataListener;
    // private DataAccessCallbacks<Town> mTownDataListener;

    private DataAccessCallbacks<ReplicatedDBObject> mSimpleInsertListener;

    private DataCursorListener                      mCursorListener;

    private ChainDataAccess                         mChainData;
    private ShopDataAccess                          mShopData;

    private CategoryDataAccess                      mCategoryData;
    private SubcategoryDataAccess                   mSubcategoryData;

    private ProductDataAccess                       mProductData;

    private ReceiptDataAccess                       mReceiptData;
    private DetailDataAccess                        mDetailData;
    private TotalDataAccess                         mTotalData;

    private RegionDataAccess                        mRegionData;
    private SubregionDataAccess                     mSubregionData;
    private TownDataAccess                          mTownData;

    private DataSource(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        // Crear todos los objetos necesarios para el acceso a los datos:
        // SQLiteOpenHelper, etc.
        mHelper = new DBHelper(mContext);

        // Inicializar las fuentes de datos:
        mChainData = new ChainDataAccess(mHelper);
        mShopData = new ShopDataAccess(mHelper);

        mReceiptData = new ReceiptDataAccess(mHelper);
        mDetailData = new DetailDataAccess(mHelper);
        mTotalData = new TotalDataAccess(mHelper);

        mCategoryData = new CategoryDataAccess(mHelper);
        mSubcategoryData = new SubcategoryDataAccess(mHelper);
        mProductData = new ProductDataAccess(mHelper);

        mRegionData = new RegionDataAccess(mHelper);
        mSubregionData = new SubregionDataAccess(mHelper);
        mTownData = new TownDataAccess(mHelper);

        buildCategoryIdNameMap();
        buildSubcategoriesMaps();
        buildShopUnivIdLocIdMap();
        buildProductMaps();
        buildShopUnivIdLocIdMap();
        buildChainIdNameMap();
        buildShopIdChainIdMap();
    }

    public static DataSource getInstance(Context context) {

        if (mDataSource == null) {
            mDataSource = new DataSource(context);
        }
        return mDataSource;
    }

    public void initDatabase(InitializerCallback callback) {
        // I don't need to check if db is already initialized cause:
        // 1.- I already did it in the caller.
        // 2.- Here I only trigger the SqliteOpenHelper.onCreate()/onUpgrade()
        // if needed.
        // (As I populate the DB in the onCreate/onUpgrade).
        new InitializeDBTask(mHelper, callback).execute();
    }

    public void addToUnivIdLocIdMap(Shop shop) {
        String univ_id = shop.getUniversalId();
        if (univ_id != null) {
            mShopUnivIdLocIdMap.put(shop.getUniversalId(), shop.getId());
        }
    }

    public void addToUnivIdLocIdMap(Product product) {
        String univ_id = product.getUniversalId();
        if (univ_id != null) {
            mProductUnivIdLocIdMap.put(product.getUniversalId(), product.getId());
        }
    }

    public void addToUnivIdLocIdMap(Receipt receipt) {
        String univ_id = receipt.getUniversalId();
        if (univ_id != null) {
            mReceiptUnivIdLocIdMap.put(receipt.getUniversalId(), receipt.getId());
        }
    }

    public void downloadData(CloudBackendMessaging backend) {
        BackendDataAccess.downloadShops(mContext, backend, true);
    }

    public void uploadData(final CloudBackendMessaging backend) {
        BackendDataAccess.uploadPendingShops(mContext, backend, true);
    }

    private void buildCategoryIdNameMap() {
        DataAccessCallbacks<Category> categoryCallbacks = new DataAccessCallbacks<Category>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Category> results) {
                for (Category category : results) {
                    mCategoryIdNameMap.put(category.getId(), category.getName());
                }
                setCategoryCallback(null);
            }

            @Override
            public void onDataProcessed(int processed, List<Category> dataList,
                    Operation operation, boolean result) {
                // TODO Auto-generated method stub

            }
        };

        setCategoryCallback(categoryCallbacks);
        listCategories();
    }

    private void buildSubcategoriesMaps() {
        DataAccessCallbacks<Subcategory> subcategoryCallbacks = new DataAccessCallbacks<Subcategory>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Subcategory> results) {
                for (Subcategory subcategory : results) {
                    mSubcategoryIdNameMap.put(subcategory.getId(), subcategory.getName());
                    mSubcategoryCategoryIdMap.put(subcategory.getId(), subcategory.getCategoryId());
                }
                setShopCallback(null);
            }

            @Override
            public void onDataProcessed(int processed, List<Subcategory> dataList,
                    Operation operation, boolean result) {
                // TODO Auto-generated method stub

            }
        };

        setSubcategoryCallback(subcategoryCallbacks);
        listSubcategories();
    }

    private void buildShopUnivIdLocIdMap() {

        DataAccessCallbacks<Shop> shopCallbacks = new DataAccessCallbacks<Shop>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Shop> results) {
                if (results != null) {
                    String univ_id;

                    for (Shop shop : results) {
                        univ_id = shop.getUniversalId();
                        if (univ_id != null) {
                            mShopUnivIdLocIdMap.put(univ_id, shop.getId());
                            if (DEBUG) {
                                Log.d(TAG, "Shop inserted in shops universalId/ localId map");
                            }
                        } else {
                            String installation = Installation.id(mContext);
                            long shopId = shop.getId();
                            mShopUnivIdLocIdMap.put(installation + shopId, shopId);
                        }
                    }
                }
                setShopCallback(null);
            }

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        };
        setShopCallback(shopCallbacks);
        listShops();
    }

    private void buildProductMaps() {
        DataAccessCallbacks<Product> productCallbacks = new DataAccessCallbacks<Product>() {

            @Override
            public void onDataProcessed(int processed, List<Product> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Product> results) {
                if (results != null) {
                    String univ_id;

                    for (Product product : results) {
                        mProductSubcategoryId.put(product.getId(), product.getSubcategoryId());
                        univ_id = product.getUniversalId();
                        if (univ_id != null) {
                            mProductUnivIdLocIdMap.put(univ_id, product.getId());
                        } else {
                            long productId = product.getId();
                            String installation = Installation.id(mContext);
                            mProductUnivIdLocIdMap.put(installation + productId, productId);
                        }
                    }
                }
                setProductCallback(null);
            }

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }
        };

        setProductCallback(productCallbacks);
        listProducts();
    }

    private void buildReceiptUnivIdLocIdMap() {
        DataAccessCallbacks<Receipt> receiptCallbacks = new DataAccessCallbacks<Receipt>() {

            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Receipt> results) {
                if (results != null) {
                    String univ_id;
                    for (Receipt receipt : results) {
                        univ_id = receipt.getUniversalId();
                        if (univ_id != null) {
                            mReceiptUnivIdLocIdMap.put(univ_id, receipt.getId());
                        } else {
                            long receiptId = receipt.getId();
                            String installation = Installation.id(mContext);
                            mReceiptUnivIdLocIdMap.put(installation + receiptId, receiptId);
                        }
                    }
                }
                setReceiptCallback(null);
            }

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }
        };
        setReceiptCallback(receiptCallbacks);
        listReceipts();
    }

    private void buildChainIdNameMap() {
        DataAccessCallbacks<Chain> chainCallbacks = new DataAccessCallbacks<Chain>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Chain> results) {
                for (Chain chain : results) {
                    mChainIdNameMap.put(chain.getId(), chain.getName());
                }
                setChainCallback(null);
            }

            @Override
            public void onDataProcessed(int processed, List<Chain> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        };

        setChainCallback(chainCallbacks);
        listChains();
    }

    private void buildShopIdChainIdMap() {
        DataAccessCallbacks<Shop> shopCallbacks = new DataAccessCallbacks<Shop>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Shop> results) {
                for (Shop shop : results) {
                    mShopIdChainIdMap.put(shop.getId(), shop.getChainId());
                }
            }

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        };
    }

    public String getCategoryName(long categoryId) {
        return mCategoryIdNameMap.get(categoryId);
    }

    public String getSubcategoryName(long subcategoryId) {
        return mSubcategoryIdNameMap.get(subcategoryId);
    }

    public long getSubcategoryCategoryId(long subcategoryId) {
        return mSubcategoryCategoryIdMap.get(subcategoryId);
    }

    public String getProductCategory(Product product) {
        long categoryId = mSubcategoryCategoryIdMap.get(product.getSubcategoryId());
        return mCategoryIdNameMap.get(categoryId);
    }

    public long getProductSubcategoryId(Product product) {
        return mProductSubcategoryId.get(product.getId());
    }

    public long getProductSubcategoryId(long product_id) {
        return mProductSubcategoryId.get(product_id);
    }

    public long getProductCategoryId(long product_id) {
        long subcat_id = mProductSubcategoryId.get(product_id);
        return mSubcategoryCategoryIdMap.get(subcat_id);
    }

    public void addToProductSubcategoryIdMap(Product product) {
        mProductSubcategoryId.put(product.getId(), product.getSubcategoryId());
    }
    
    public String getProductCategoryName(long product_id) {
        long subcat_id = mProductSubcategoryId.get(product_id);
        long cat_id = mSubcategoryCategoryIdMap.get(subcat_id);
        return mCategoryIdNameMap.get(cat_id);
    }

    public void addToShopIdChainIdMap(Shop shop) {
        mShopIdChainIdMap.put(shop.getId(), shop.getChainId());
    }

    public String getChainNameByShopId(long shopId) {
        return mChainIdNameMap.get(mShopIdChainIdMap.get(shopId));
    }

    public long getShopLocIdFromUnivId(String univId) {
        String str = univId;
        return mShopUnivIdLocIdMap.get(univId);
    }

    public long getProductLocIdFromUnivId(String univId) {
        return mProductUnivIdLocIdMap.get(univId);
    }

    public long getReceiptLocIdFromUnivId(String univId) {
        return mReceiptUnivIdLocIdMap.get(univId);
    }

    public void setCategoryCallback(DataAccessCallbacks<Category> listener) {
        mCategoryData.setCallback(listener);
    }

    public DataAccessCallbacks<Category> getCategoryCallback() {
        return mCategoryData.getCallback();
    }

    public void setSubcategoryCallback(DataAccessCallbacks<Subcategory> listener) {
        mSubcategoryData.setCallback(listener);
    }

    public DataAccessCallbacks<Subcategory> getSubcategoryCallback() {
        return mSubcategoryData.getCallback();
    }

    public void setProductCallback(DataAccessCallbacks<Product> listener) {
        mProductData.setCallback(listener);
    }

    public DataAccessCallbacks<Product> getProductCallback() {
        return mProductData.getCallback();
    }

    public void setChainCallback(DataAccessCallbacks<Chain> listener) {
        mChainData.setCallback(listener);
    }

    public DataAccessCallbacks<Chain> getChainCallback() {
        return mChainData.getCallback();
    }

    public void setShopCallback(DataAccessCallbacks<Shop> listener) {
        mShopData.setCallback(listener);
    }

    public DataAccessCallbacks<Shop> getShopCallback() {
        return mShopData.getCallback();
    }

    public void setReceiptCallback(DataAccessCallbacks<Receipt> listener) {
        mReceiptData.setCallback(listener);
    }

    public DataAccessCallbacks<Receipt> getReceiptCallback() {
        return mReceiptData.getCallback();
    }

    public void setDetailCallback(DataAccessCallbacks<Detail> listener) {
        mDetailData.setCallback(listener);
    }

    public DataAccessCallbacks<Detail> getDetailCallback() {
        return mDetailData.getCallback();
    }

    public void setTotalCallback(DataAccessCallbacks<Total> listener) {
        mTotalData.setCallback(listener);
    }

    public DataAccessCallbacks<Total> getTotalCallback() {
        return mTotalData.getCallback();
    }

    public void setCursorCallback(DataCursorListener listener) {
        mCursorListener = listener;
    }

    public void removeCursorListener(DataCursorListener listener) {
        mCursorListener = null;
    }

    public void setRegionCallback(DataAccessCallbacks<Region> listener) {
        mRegionData.setCallback(listener);
    }

    public void setSubregionCallback(DataAccessCallbacks<Subregion> listener) {
        mSubregionData.setCallback(listener);
    }

    public void setTownCallback(DataAccessCallbacks<Town> listener) {
        mTownData.setCallback(listener);
    }

    public void setSimpleInsertCallback(DataAccessCallbacks<ReplicatedDBObject> listener) {
        mSimpleInsertListener = listener;
    }

    public void simpleInsert(ReplicatedDBObject rdbo) {
        new SimpleAsyncInsert(mHelper, rdbo, mSimpleInsertListener).execute();
    }

    public void genericQuery(String rawQuery, String[] args) {
        if (mCursorListener != null) {
            AsyncCursorQuery query = new AsyncCursorQuery(mHelper, rawQuery, args, mCursorListener);
            query.execute();
        }
    }

    // Operaciones de consulta de datos:
    // Para Region.
    public void listRegions() {
        if (mRegionData.hasCallback()) {
            mRegionData.list();
        }
    }

    public void getRegion(long id) {

        mHelper.getReadableDatabase();

        if (mRegionData.hasCallback()) {
            String rawQuery = BASIC_QUERY + TBL_REGION + BASIC_WHERE + T_REGION_ID + " = " + id;
            mRegionData.query(rawQuery, null);
        }
    }

    public void getRegionCount() {
        if (mRegionData.hasCallback()) {
            mRegionData.getCount();
        }
    }

    // Para Subregion:
    public void listSubregions() {
        if (mSubregionData.hasCallback()) {
            mSubregionData.list();
        }
    }

    public void getSubregion(long id) {
        if (mSubregionData.hasCallback()) {
            String rawQuery = BASIC_QUERY + TBL_SUBREGION + BASIC_WHERE + T_SUBREGION_ID + " = "
                    + id;
            mSubregionData.query(rawQuery, null);
        }
    }

    public void getSubregionBy(Region region) {
        if (mSubregionData.hasCallback()) {
            String rawQuery = BASIC_QUERY + TBL_SUBREGION + BASIC_WHERE + T_SUBREGION_REGION_ID
                    + " = " + region.getId();
            mSubregionData.query(rawQuery, null);
        }
    }

    public void getSubregionCount() {
        if (mSubregionData.hasCallback()) {
            mSubregionData.getCount();
        }
    }

    // Para Town.
    public void listTwons() {
        if (mTownData.hasCallback()) {

        }
    }

    public void getTwon(long id) {
        if (mTownData.hasCallback()) {
            String rawQuery = BASIC_QUERY + TBL_TOWN + BASIC_WHERE + T_TOWN_ID + " = " + id;
            mTownData.query(BASIC_QUERY, null);
        }
    }

    public void getTownsBy(Subregion subregion) {
        if (mTownData.hasCallback()) {
            /*
             * SELECT * FROM town WHERE subregion_id = subregion.getId();
             */
            String rawQuery = BASIC_QUERY + TBL_TOWN + BASIC_WHERE + T_TOWN_SUBREGION_ID + " = "
                    + subregion.getId();
            mTownData.query(rawQuery, null);
        }
    }

    public void getTownsBy(Region region) {
        /*
         * SELECT * FROM town WHERE subregion_id IN ( SELECT _id FROM subregion
         * WHERE region_id = region.getId());
         */

        String rawQuery = "SELECT * FROM " + TBL_TOWN + " WHERE " + T_TOWN_SUBREGION_ID + " IN "
                + "(SELECT " + T_SUBREGION_ID + " FROM " + TBL_SUBREGION + " WHERE "
                + T_SUBREGION_REGION_ID + " = " + region.getId() + ")";
        mTownData.query(rawQuery, null);
    }

    public void getTwonCount() {
        if (mTownData.hasCallback()) {
            mTownData.getCount();
        }
    }

    // Para User.

    // Para Chain.

    public void listChains() {
        // TODO ??? NO SE PODRÍA USAR SIMPLEMENTE mChainData.list();
        if (DEBUG) Log.d(TAG, "listChains");
        if (mChainData.getCallback() != null) {
            mChainData.list();
        }

    }

    public void getChain(long id) {
        if (mChainData.getCallback() != null) {
            String rawQuery = BASIC_QUERY + TBL_CHAIN + BASIC_WHERE + T_CHAIN_ID + " = " + id;
            mChainData.query(rawQuery, null);
        }
    }

    public void getChainCount() {
        if (mChainData.getCallback() != null) {
            mChainData.getCount();
        }
    }

    // Para Shop.
    public void listShops() {
        if (mShopData.getCallback() != null) {
            // String rawQuery = BASIC_QUERY + TBL_SHOP;
            // mShopData.query(rawQuery, null);
            mShopData.list();
        }
    }

    public void getShop(long id) {
        if (mShopData.getCallback() != null) {
            String rawQuery = BASIC_QUERY + TBL_SHOP + BASIC_WHERE + T_SHOP_ID + " = " + id;
            mShopData.query(rawQuery, null);
        }
    }

    public void getShopsBy(Chain chain) {
        if (mShopData.getCallback() != null) {

            String rawQuery = BASIC_QUERY + TBL_SHOP + BASIC_WHERE + T_SHOP_CHAIN_ID + " = "
                    + chain.getId();
            mShopData.query(rawQuery, null);
        }
    }

    public void getShopCount() {
        if (mShopData.getCallback() != null) {
            mShopData.getCount();
        }
    }

    public void listPendingShops() {
        if (mShopData.getCallback() != null) {
            /*
             * SELECT * FROM shop WHERE updated = 0
             */
            String rawQuery = BASIC_QUERY + TBL_SHOP + BASIC_WHERE + T_SHOP_UPDATED + " = 0";
            mShopData.query(rawQuery, null);
        }
    }

    // Para Category.
    public void listCategories() {
        // No point on retrieve data if no one will handle it.
        if (mCategoryData.getCallback() != null) {
            // Call an Asynctask to get the category list.
            // Llamar a Asyntask para obtener lista de categorÃ­as.
            mCategoryData.list();
        }
    }

    public void getCategory(long id) {
        if (mCategoryData.getCallback() != null) {
            String rawQuery = BASIC_QUERY + TBL_CATEGORY + BASIC_WHERE + T_CAT_ID + " = " + id;
            mCategoryData.query(rawQuery, null);
        }
    }

    public void getCategoryCount() {
        if (mCategoryData.getCallback() != null) {
            mCategoryData.getCount();
        }
    }

    // Para Subcategory.
    public void listSubcategories() {
        if (mSubcategoryData.getCallback() != null) {
            // String rawQuery = BASIC_QUERY + TBL_SUBCATEGORY;
            // mSubcategoryData.query(rawQuery, null);
            mSubcategoryData.list();
        }
    }

    public void getSubcategory(long id) {
        if (mSubcategoryData.getCallback() != null) {
            String rawQuery = BASIC_QUERY + TBL_SUBCATEGORY + BASIC_WHERE + T_SUBCAT_CAT_ID + " = "
                    + id;
            mSubcategoryData.query(rawQuery, null);
        }
    }

    public void getSubcategoriesBy(Category category) {
        // No point on doing work for no one.
        if (mSubcategoryData.getCallback() != null) {
            String rawQuery = BASIC_QUERY + TBL_SUBCATEGORY + BASIC_WHERE + T_SUBCAT_CAT_ID + " = "
                    + category.getId();
            mSubcategoryData.query(rawQuery, null);
        }
    }

    public void getSubcategoryCount() {
        if (mSubcategoryData.getCallback() != null) {
            mSubcategoryData.getCount();
        }
    }

    // Para Producto.

    public void listProducts() {
        if (mProductData.getCallback() != null) {
            // String rawQuery = BASIC_QUERY + TBL_PRODUCT;
            // mProductData.query(rawQuery, null);
            mProductData.list();
        }
    }

    public void getProduct(long id) {
        if (mProductData.getCallback() != null) {
            String rawQuer = BASIC_QUERY + TBL_PRODUCT + BASIC_WHERE + T_PROD_ID + " = " + id;
            mProductData.query(rawQuer, null);
        }
    }

    public void getProductsBy(Category category) {
        if (mProductData.getCallback() != null) {
            /*
             * SELECT * FROM product WHERE subcategory_id IN (SELECT id FROM
             * subcategory WHERE category_id = <category.getId()>)
             */

            String rawQuery = "SELECT * FROM " + TBL_PRODUCT + " WHERE " + T_PROD_SUBCAT_ID
                    + " IN " + "(SELECT " + T_SUBCAT_ID + " FROM " + TBL_SUBCATEGORY + " WHERE "
                    + T_SUBCAT_CAT_ID + " = " + category.getId() + ")";
            mProductData.query(rawQuery, null);
        }
    }

    public void getProductsBy(Subcategory subcategory) {
        if (mProductData.getCallback() != null) {
            /*
             * SELECT * FROM product WHERE subcategory_id =
             * <subcategory.getId()>
             */
            String rawQuery = BASIC_QUERY + TBL_PRODUCT + BASIC_WHERE + T_PROD_SUBCAT_ID + " = "
                    + subcategory.getId();
            mProductData.query(rawQuery, null);
        }
    }

    public void getProductCount() {
        if (mProductData.getCallback() != null) {
            mProductData.getCount();
        }
    }

    public void listPendingProducts() {
        if (mProductData.getCallback() != null) {
            /*
             * SELECT * FROM product WHERE updated = 0
             */
            String rawQuery = BASIC_QUERY + TBL_PRODUCT + BASIC_WHERE + T_PROD_UPDATED + " = 0";
            mProductData.query(rawQuery, null);
        }
    }

    // Para Receipt.
    public void listReceipts() {
        if (mReceiptData.getCallback() != null) {
            /*
             * SELECT * FROM receipt
             */
            // String rawQuery = BASIC_QUERY + TBL_RECEIPT;
            // mReceiptData.query(rawQuery, null);
            mReceiptData.list();
        }
    }

    public void getReceipt(long id) {
        if (mReceiptData.getCallback() != null) {
            /*
             * SELECT * FROM receipt WHERE id = <id>
             */
            String rawQuery = BASIC_QUERY + TBL_RECEIPT + BASIC_WHERE + T_RECPT_ID + " = " + id;
            mReceiptData.query(rawQuery, null);
        }
    }

    public void getReceiptsBy(Chain chain) {
        if (mReceiptData.getCallback() != null) {
            /*
             * SELECT * FROM receipt WHERE shop_id IN (SELECT id FROM shop WHERE
             * chain_id = <chain.getId()>)
             */
            String rawQuery = "SELECT * FROM " + TBL_RECEIPT + " WHERE " + T_RECPT_SHOP_ID + " IN "
                    + "(SELECT " + T_SHOP_ID + " FROM " + TBL_SHOP + " WHERE " + T_SHOP_CHAIN_ID
                    + " = " + chain.getId() + ")";
            mReceiptData.query(rawQuery, null);
        }
    }

    public void getReceiptsBy(Shop shop) {
        if (mReceiptData.getCallback() != null) {
            /*
             * SELECT * FROM receipt WHERE user_id = <shop.getId()>
             */
            String rawQuery = BASIC_QUERY + TBL_RECEIPT + BASIC_WHERE + T_RECPT_SHOP_ID + " = "
                    + shop.getId();
            mReceiptData.query(rawQuery, null);
        }
    }

    public void getReceiptsBy(Calendar beginning, Calendar end) {

        if (mReceiptData.getCallback() != null) {
            String rawQuery = null;
            boolean withBeginning = beginning != null;
            boolean withEnd = end != null;
            if (withBeginning && withEnd) {
                if (end.before(beginning)) {
                    throw new IllegalArgumentException("end can't be before the beggingin");
                }
                /*
                 * SELECT * FROM receipt WHERE timestamp >= beginning AND
                 * timestamp <= end;
                 */
                rawQuery = BASIC_QUERY + TBL_RECEIPT + BASIC_WHERE + T_RECPT_TIMESTAMP + " >= '"
                        + Interval.toRfc3339ZuluString(beginning) + "' AND " + T_RECPT_TIMESTAMP
                        + " <= '" + Interval.toRfc3339ZuluString(end) + "'";
                mReceiptData.query(rawQuery, null);
                return;
            }
            if (withBeginning) {
                /*
                 * SELECT * FROM receipt WHERE timestamp >= beginning;
                 */
                rawQuery = BASIC_QUERY + TBL_RECEIPT + BASIC_WHERE + T_RECPT_TIMESTAMP + " >= '"
                        + Interval.toRfc3339ZuluString(beginning) + "'";
                mReceiptData.query(rawQuery, null);
                return;
            }
            if (withEnd) {
                /*
                 * SELECT * FROM receipt WHERE timestamp <= end;
                 */
                rawQuery = BASIC_QUERY + TBL_RECEIPT + BASIC_WHERE + T_RECPT_TIMESTAMP + " <= '"
                        + Interval.toRfc3339ZuluString(end) + "'";
                mReceiptData.query(rawQuery, null);
                return;
            }
            mReceiptData.list();
        }
    }

    public void getReceiptCount() {
        if (mReceiptData.getCallback() != null) {
            mReceiptData.getCount();
        }
    }

    public void listPendingReceipts() {
        if (mReceiptData.getCallback() != null) {
            /*
             * SELECT * FROM receipt WHERE updated = 0
             */
            String rawQuery = BASIC_QUERY + TBL_RECEIPT + BASIC_WHERE + T_RECPT_UPDATED + " = 0";
            mReceiptData.query(rawQuery, null);
        }
    }

    // Para Detail.
    public void listDetails() {
        if (mDetailData.getCallback() != null) {
            // String rawQuery = BASIC_QUERY + TBL_DETAIL;
            // mDetailData.query(rawQuery, null);
            mDetailData.list();
        }
    }

    public void getDetail(long id) {
        if (mDetailData.getCallback() != null) {
            String rawQuery = BASIC_QUERY + TBL_DETAIL + BASIC_WHERE + T_DETAIL_ID + " = " + id;
            mDetailData.query(rawQuery, null);
        }
    }

    public void getDetailsBy(Receipt receipt) {
        if (mDetailData.getCallback() != null) {
            String rawQuery = BASIC_QUERY + TBL_DETAIL + BASIC_WHERE + T_DETAIL_RECPT_ID + " = "
                    + receipt.getId();
            mDetailData.query(rawQuery, null);
        }
    }

    public void getDetailsBy(List<Receipt> receipts) {
        if (mDetailData.getCallback() != null) {
            // Build receips id list.
            StringBuilder rawQueryBldr = new StringBuilder();
            
            /*
             * SELECT * FROM detail WHERE receipt_id IN (receipt_ids[0..num_receipts]); 
             */
            // SELECT * FROM detail WHERE 
            rawQueryBldr.append(BASIC_QUERY).append(TBL_DETAIL).append(BASIC_WHERE);
            rawQueryBldr.append(T_DETAIL_RECPT_ID).append(" IN (");
            int num_receipts = receipts.size();
            for (int i = 0; i < num_receipts - 1; ++i) {
                rawQueryBldr.append(receipts.get(i).getId()).append(", ");
            }
            rawQueryBldr.append(receipts.get(num_receipts - 1).getId());
            rawQueryBldr.append(")");
            if (DEBUG) Log.d(TAG, "Query issued: \"" + rawQueryBldr + "\"");
            mDetailData.query(rawQueryBldr.toString(), null);            
        }
    }

    public void getDetailsBy(Product product) {
        if (mDetailData.getCallback() != null) {
            String rawQuery = BASIC_QUERY + TBL_DETAIL + BASIC_WHERE + T_DETAIL_PROD_ID + " = "
                    + product.getId();
            mDetailData.query(rawQuery, null);
        }
    }

    public void getDetailsBy(Subcategory subcategory) {
        /*
         * SELECT * FROM detail WHERE product_id IN (SELECT id FROM product
         * WHERE subcategory_id = <subcategory.getId()>
         */
        if (mDetailData.getCallback() != null) {
            String rawQuery = "SELECT * FROM " + TBL_DETAIL + " WHERE " + T_DETAIL_PROD_ID + " IN "
                    + "(SELECT " + T_PROD_ID + "FROM" + TBL_PRODUCT + " WHERE " + T_PROD_SUBCAT_ID
                    + " = " + subcategory.getId();
            mDetailData.query(rawQuery, null);
        }

    }

    public void getDetailsBy(Category category) {
        /*
         * SELECT * FROM detail WHERE product_id IN (SELECT id FROM product
         * WHERE subcategory_id IN (SELECT id FROM subcategory WHERE category_id
         * = <category.getId()>) )
         */

        if (mDetailData.getCallback() != null) {
            String rawQuery = "SELECT * FROM " + TBL_DETAIL + " WHERE " + T_DETAIL_PROD_ID + " IN "
                    + "(SELECT " + T_PROD_ID + "FROM" + TBL_PRODUCT + " WHERE " + T_PROD_SUBCAT_ID
                    + " IN " + "(SELECT " + T_SUBCAT_ID + " FROM " + TBL_SUBCATEGORY + " WHERE "
                    + T_SUBCAT_CAT_ID + " = " + category.getId() + ") " + ")";
            mDetailData.query(rawQuery, null);
        }
    }

    public void listPendingDetails() {
        if (mDetailData.getCallback() != null) {
            /*
             * SELECT FROM detail WHERE updated = 0
             */
            String rawQuery = BASIC_QUERY + TBL_DETAIL + BASIC_WHERE + T_DETAIL_UPDATED + " = 0";
            mDetailData.query(rawQuery, null);
        }
    }

    public void getDetailCount() {
        if (mDetailData.getCallback() != null) {
            mDetailData.getCount();
        }
    }

    // Para Total.
    public void listTotals() {
        if (mTotalData.getCallback() != null) {
            // String rawQuery = BASIC_QUERY + TBL_TOTAL;
            // mTotalData.query(rawQuery, null);
            mTotalData.list();
        }
    }

    public void getTotal(long id) {
        if (mTotalData.getCallback() != null) {
            String rawQuery = BASIC_QUERY + TBL_TOTAL + BASIC_WHERE + T_TOTAL_ID + " = " + id;
            mTotalData.query(rawQuery, null);
        }
    }

    public void getTotalBy(Receipt receipt) {
        if (mTotalData.getCallback() != null) {
            String rawQuery = BASIC_QUERY + TBL_TOTAL + BASIC_WHERE + T_TOTAL_RECPT_ID + " = "
                    + receipt.getId();
            mTotalData.query(rawQuery, null);
        }
    }

    public void getTotalsBy(Chain chain) {
        if (mTotalData.getCallback() != null) {

            /*
             * SELECT * FROM total WHERE receipt_id IN (SELECT id FROM receipt
             * WHERE shop_id IN (SELECT id FROM shop WHERE chain_id =
             * <chain.getId()>) )
             */

            String rawQuery = "SELECT * FROM " + TBL_TOTAL + " WHERE " + T_TOTAL_RECPT_ID + " IN "
                    + "(SELECT " + T_RECPT_ID + " FROM " + TBL_RECEIPT + " WHERE "
                    + T_RECPT_SHOP_ID + " IN " + "(SELECT " + T_SHOP_ID + " FROM " + TBL_SHOP
                    + " WHERE " + T_SHOP_CHAIN_ID + " = " + Long.toString(chain.getId()) + ")"
                    + ")";
            mTotalData.query(rawQuery, null);
        }
    }

    public void getTotalsBy(Shop shop) {
        if (mTotalData.getCallback() != null) {
            /*
             * SELECT * FROM total WHERE receipt_id IN (SELECT id FROM receipt
             * WHERE shop_id = <shop.getId()>)
             */

            String rawQuery = "SELECT * FROM " + TBL_TOTAL + " WHERE " + T_TOTAL_RECPT_ID + " IN "
                    + "(SELECT " + T_RECPT_ID + " FROM " + TBL_RECEIPT + " WHERE "
                    + T_RECPT_SHOP_ID + " = " + Long.toString(shop.getId()) + ")";
            mTotalData.query(rawQuery, null);
        }
    }

    public void listPendingTotals() {
        if (mTotalData.getCallback() != null) {
            /*
             * SELECT FROM total WHERE updated = 0
             */
            String rawQuery = BASIC_QUERY + TBL_TOTAL + BASIC_WHERE + T_TOTAL_UPDATED + " = 0";
            mTotalData.query(rawQuery, null);
        }
    }

    public void getTotalCount() {
        if (mTotalData.getCallback() != null) {
            mTotalData.getCount();
        }
    }

    // Custom queries:

    /*
     * Rececipt/detail query: I will need receipt, product and detail info.
     * SELECT detail._id,..., detail.*, product.name FROM detail, product WHERE
     * detail.product_id = product._id AND detail.receipt_id = <receipt.getId()>
     */
    public void getReceiptDetailLine(Receipt receipt) {
        /*
         * SELECT detail.*, product.id AS alt_product_id, product.subcategory_id
         * AS alt_product_subcategory_id, product.name AS alt_product_name,
         * product.description, product.article_number
         * 
         * FROM detail, product
         * 
         * WHERE detail.product_id = product._id AND detail.receipt_id =
         * <receipt.getId()>
         */

        String select = "SELECT " + TBL_DETAIL + ".*" + TBL_PRODUCT + "." + T_PROD_ID + " AS "
                + T_PROD_ID_ALT + ", " + TBL_PRODUCT + "." + T_PROD_SUBCAT_ID + " AS "
                + T_PROD_SUBCAT_ID_ALT + ", " + TBL_PRODUCT + "." + T_PROD_NAME + " AS "
                + T_PROD_NAME_ALT + ", " + TBL_PRODUCT + "." + T_PROD_DESCR + ", " + TBL_PRODUCT
                + "." + T_PROD_ARTNUMBER +

                " FROM " + TBL_DETAIL + ", " + TBL_PRODUCT;

        String where = " WHERE " + TBL_DETAIL + "." + T_DETAIL_PROD_ID + " = " + TBL_PRODUCT + "."
                + T_PROD_ID + " AND " + TBL_DETAIL + "." + T_DETAIL_RECPT_ID + " = "
                + receipt.getId();

        String rawQuery = select + where;
        genericQuery(rawQuery, null);
    }

    public static Detail getDetailFromDetailLineCursor(Cursor c) {
        Detail detail = null;

        if (c != null && c.getCount() > 0) {

            // Check if we have the right number of columns: 6 (detail) + 5
            // (product) = 11.
            if (c.getColumnCount() == 11) {
                try {
                    int idIdx = c.getColumnIndexOrThrow(T_DETAIL_ID);
                    int prodIdIdx = c.getColumnIndexOrThrow(T_DETAIL_PROD_ID);
                    int recptIdIdx = c.getColumnIndexOrThrow(T_DETAIL_RECPT_ID);
                    int priceIdx = c.getColumnIndexOrThrow(T_DETAIL_PRICE);
                    int unitsIdx = c.getColumnIndexOrThrow(T_DETAIL_UNITS);
                    int weightIdx = c.getColumnIndexOrThrow(T_DETAIL_WEIGHT);

                    detail = new Detail();

                    detail.setId(c.getLong(idIdx));
                    detail.setReceiptId(c.getLong(recptIdIdx));
                    detail.setProductId(c.getLong(prodIdIdx));
                    detail.setPrice(c.getInt(priceIdx));
                    detail.setUnits(c.getInt(unitsIdx));
                    detail.setWeight(c.getInt(weightIdx));
                } catch (IllegalArgumentException e) {
                    // Or simply return null?
                    throw (new IllegalArgumentException("cursor lacks the required columns"));
                }
            }
        }

        return detail;
    }

    public static Product getProductFromDetailLineCursor(Cursor c) {
        Product product = null;

        if (c != null && c.getCount() > 0) {

            // Check if we have the right number of columns: 6 (detail) + 5
            // (product) = 11.
            if (c.getColumnCount() == 11) {
                try {
                    int idIdx = c.getColumnIndexOrThrow(T_PROD_ID_ALT);
                    int subcatIdx = c.getColumnIndexOrThrow(T_PROD_SUBCAT_ID_ALT);
                    int nameIdx = c.getColumnIndexOrThrow(T_PROD_NAME_ALT);
                    int descrIdx = c.getColumnIndexOrThrow(T_PROD_DESCR);
                    int artNumIdx = c.getColumnIndexOrThrow(T_PROD_ARTNUMBER);

                    product = new Product();

                    product.setId(c.getLong(idIdx));
                    product.setSubcategoryId(c.getLong(subcatIdx));
                    product.setName(c.getString(nameIdx));
                    product.setDescription(c.getString(descrIdx));
                    product.setArticleNumber(c.getString(artNumIdx));
                } catch (IllegalArgumentException e) {
                    // Or simply return null?
                    throw (new IllegalArgumentException("cursor lacks the required columns"));
                }
            }
        }

        return product;
    }

    // Operaciones que modifican los datos.

    public void insertRegions(List<Region> dataList) {
        mRegionData.insert(dataList);
    }

    public void insertSubregions(List<Subregion> dataList) {
        mSubregionData.insert(dataList);
    }

    public void insertTowns(List<Town> dataList) {
        mTownData.insert(dataList);
    }

    public void updateRegions(List<Region> dataList) {
        mRegionData.update(dataList);
    }

    public void updateSubregions(List<Subregion> dataList) {
        mSubregionData.update(dataList);
    }

    public void updateTowns(List<Town> dataList) {
        mTownData.update(dataList);
    }

    public void deleteRegions(List<Region> dataList) {
        mRegionData.delete(dataList);
    }

    public void deleteRegions() {
        mRegionData.deleteAll();
    }

    public void deleteSubregions(List<Subregion> dataList) {
        mSubregionData.delete(dataList);
    }

    public void deleteSubregions() {
        mSubregionData.deleteAll();
    }

    public void deleteTowns(List<Town> dataList) {
        mTownData.delete(dataList);
    }

    public void deleteTowns() {
        mTownData.deleteAll();
    }

    // TODO: ACTUALIZAR EL RESTO DE DBOBJECTS A DataAccessNew E INSERCIONES DE
    // LISTAS.

    public void insertChains(List<Chain> dataList) {
        mChainData.insert(dataList);
    }

    public void insertShops(List<Shop> dataList) {
        mShopData.insert(dataList);
    }

    public void insertCategories(List<Category> dataList) {
        mCategoryData.insert(dataList);
    }

    public void insertSubcategories(List<Subcategory> dataList) {
        mSubcategoryData.insert(dataList);
    }

    public void insertProducts(List<Product> dataList) {
        mProductData.insert(dataList);
    }

    public void insertReceipts(List<Receipt> dataList) {
        mReceiptData.insert(dataList);
    }

    public void insertDetails(List<Detail> dataList) {
        mDetailData.insert(dataList);
    }

    public void insertTotals(List<Total> dataList) {
        mTotalData.insert(dataList);
    }

    public void updateChains(List<Chain> dataList) {
        mChainData.update(dataList);
    }

    public void updateShops(List<Shop> dataList) {
        mShopData.update(dataList);
    }

    public void updateCategories(List<Category> dataList) {
        mCategoryData.update(dataList);
    }

    public void updateSubcategories(List<Subcategory> dataList) {
        mSubcategoryData.update(dataList);
    }

    public void updateProducts(List<Product> dataList) {
        mProductData.update(dataList);
    }

    public void updateReceipts(List<Receipt> dataList) {
        mReceiptData.update(dataList);
    }

    public void updateTotals(List<Total> totals) {
        mTotalData.update(totals);

    }

    public void updateDetails(List<Detail> dataList) {
        mDetailData.update(dataList);
    }

    public void deleteChains(List<Chain> dataList) {
        mChainData.delete(dataList);
    }

    public void deleteChains() {
        mChainData.deleteAll();
    }

    public void deleteShops(List<Shop> dataList) {
        mShopData.delete(dataList);
    }

    public void deleteShops() {
        mShopData.deleteAll();
    }

    public void deleteCategories(List<Category> dataList) {
        mCategoryData.delete(dataList);
    }

    public void deleteCategories() {
        mCategoryData.deleteAll();
    }

    public void deleteSubcategories(List<Subcategory> dataList) {
        mSubcategoryData.delete(dataList);
    }

    public void deleteSubcategories() {
        mSubcategoryData.deleteAll();
    }

    public void deleteProducts(List<Product> dataList) {
        mProductData.delete(dataList);
    }

    public void deleteProducts() {
        mProductData.deleteAll();
    }

    public void deleteReceipts(List<Receipt> dataList) {
        mReceiptData.delete(dataList);
    }

    public void deleteReceipts() {
        mReceiptData.deleteAll();
    }

    public void deleteDetails(List<Detail> dataList) {
        mDetailData.delete(dataList);
    }

    public void deleteDetails() {
        mDetailData.deleteAll();
    }

    public void deleteTotals(List<Total> dataList) {
        mTotalData.delete(dataList);
    }

    public void deleteTotals() {
        mTotalData.deleteAll();
    }

    // Static service methods?
    /**
     * Receives a receipt and a bundle and attach the receipt data to the
     * bundle. To be detached with {@link #detachReceipt(Bundle)}
     * 
     * @param receipt
     *            the receipt to attach to the bundle.
     * @param bundle
     *            the bundle to attach the receipt to.
     * @return a bundle with the receipt data attached.
     */
    public static Bundle attachToBundle(Receipt receipt, Bundle bundle) {
        Bundle b;
        if (bundle != null) {
            b = bundle;
        } else {
            b = new Bundle();
        }

        b.putLong(Keys.KEY_RECEIPT_ID, receipt.getId());
        b.putLong(Keys.KEY_RECEIPT_SHOP_ID, receipt.getShopId());
        b.putString(Keys.KEY_RECEIPT_TIMESTAMP, receipt.getTimestampRfc3339());

        return b;
    }

    /**
     * Extracts the info from the provided bundle (previously attached to the
     * bundle with {@link #attachToBundle(Receipt, Bundle)}) and returns a
     * receipt or null if isn't any.
     * 
     * @param bundle
     *            the {@code bundle} from which we will try to extract the
     *            receipt.
     * @return the receipt.
     */
    public static Receipt detachReceipt(Bundle bundle) {
        Receipt receipt = null;

        long id = bundle.getLong(Keys.KEY_RECEIPT_ID);
        if (id > 0) {
            receipt = new Receipt();
            receipt.setId(id);
            receipt.setShopId(bundle.getLong(Keys.KEY_RECEIPT_SHOP_ID));
            receipt.setTimestamp(bundle.getLong(Keys.KEY_RECEIPT_TIMESTAMP));
        }

        return receipt;
    }

    private void populateTestData(int nProducts, int nReceipts, int nTotals, int nDetails,
            int nDetailsPerReceipt) {
        TownDACallbacks townDACallbacks = new TownDACallbacks();
        setTownCallback(townDACallbacks);

        ChainDACallbacks chainDACallbacks = new ChainDACallbacks();
        setChainCallback(chainDACallbacks);

        // TODO: Completar/verificar esta función.

        PopulateSubcategoryDACallbacks subcategoryDACallbacks = new PopulateSubcategoryDACallbacks(
                nProducts);
        setSubcategoryCallback(subcategoryDACallbacks);

        listSubcategories();

        listTwons();
    }

    class TownDACallbacks implements DataAccessCallbacks<Town> {

        @Override
        public void onDataProcessed(int processed, List<Town> dataList, Operation operation,
                boolean result) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDataReceived(List<Town> results) {
            ChainDACallbacks chainDACallbacks = new ChainDACallbacks();
            chainDACallbacks.setTowns(results);
            setChainCallback(chainDACallbacks);
        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }

    }

    class ChainDACallbacks implements DataAccessCallbacks<Chain> {
        private List<Town> mTowns;

        public void setTowns(List<Town> towns) {
            mTowns = towns;
        }

        public List<Town> getTowns() {
            return mTowns;
        }

        @Override
        public void onDataProcessed(int processed, List<Chain> dataList, Operation operation,
                boolean result) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDataReceived(List<Chain> results) {
            Shop shop;
            List<Shop> shops = new ArrayList<Shop>();

            for (Town town : mTowns) {
                for (Chain chain : results) {
                    shop = new Shop();

                    shop.setAddress("Generic test address: " + town.getName());
                    shop.setChainId(chain.getId());
                    shop.setTownId(town.getId());
                    shop.setTownName(town.getName());

                    shops.add(shop);
                }
            }

            PopulateShopDACallbacks populateShopDACallbacks = new PopulateShopDACallbacks();
            setShopCallback(populateShopDACallbacks);

            insertShops(shops);
        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }

    }

    class PopulateShopDACallbacks implements DataAccessCallbacks<Shop> {

        @Override
        public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                boolean result) {
            Receipt receipt;
            List<Receipt> receipts = new ArrayList<Receipt>();

            // generar lista de Receipts por tienda y producto (product list en
            // receipt callbacks)
            // insertar receipts.

            for (Shop shop : dataList) {
                receipt = new Receipt();
                receipt.setShopId(shop.getId());
                receipt.setTimestamp(System.currentTimeMillis());
                receipts.add(receipt);
            }

            insertReceipts(receipts);
        }

        @Override
        public void onDataReceived(List<Shop> results) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }

    }

    class PopulateSubcategoryDACallbacks implements DataAccessCallbacks<Subcategory> {
        private int mNumProducts;

        public PopulateSubcategoryDACallbacks(int numProducts) {
            mNumProducts = numProducts;
        }

        @Override
        public void onDataProcessed(int processed, List<Subcategory> dataList, Operation operation,
                boolean result) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDataReceived(List<Subcategory> results) {
            Product product;
            List<Product> products = new ArrayList<Product>();

            for (Subcategory subcategory : results) {

                for (int i = 0; i < mNumProducts; ++i) {
                    product = new Product();

                    product.setName("Product nº " + i + " (" + subcategory.getName() + ")");
                    product.setSubcategoryId(subcategory.getId());

                    products.add(product);
                }
            }

            PopulateProductDACallbacks productDACallbacks = new PopulateProductDACallbacks();
            setProductCallback(productDACallbacks);

            insertProducts(products);
        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }

    }

    class PopulateProductDACallbacks implements DataAccessCallbacks<Product> {

        @Override
        public void onDataProcessed(int processed, List<Product> dataList, Operation operation,
                boolean result) {
            PopulateReceiptDACallbacks receiptDAC = new PopulateReceiptDACallbacks();
            receiptDAC.setProductList(dataList);
            setReceiptCallback(receiptDAC);
        }

        @Override
        public void onDataReceived(List<Product> results) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }

    }

    class PopulateReceiptDACallbacks implements DataAccessCallbacks<Receipt> {
        private List<Product> mProductList;
        private int           mNumDetails = 5;

        public void setNumDetails(int numDetails) {
            mNumDetails = numDetails;
        }

        public int getNumDetails() {
            return mNumDetails;
        }

        public void setProductList(List<Product> productList) {
            mProductList = productList;
        }

        public List<Product> getProductList() {
            return mProductList;
        }

        @Override
        public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                boolean result) {
            Total total;
            List<Total> totals = new ArrayList<Total>();
            Detail detail;
            List<Detail> details = new ArrayList<Detail>();

            int price;

            int numReceipts = dataList.size();
            Receipt receipt;
            for (int pos = 0; pos < numReceipts; ++pos) {
                receipt = dataList.get(pos);
                if (pos % 2 == 0) {
                    // Total
                    total = new Total();
                    total.setReceiptId(receipt.getId());
                    total.setValue(666);
                    totals.add(total);
                } else {
                    // Detail
                    // Just get 5 products :S
                    price = 0;
                    for (int productPos = 0; productPos < mNumDetails; ++productPos) {
                        price += 5;
                        detail = new Detail();
                        detail.setReceiptId(receipt.getId());
                        detail.setProduct(mProductList.get(productPos));
                        detail.setPrice(price);
                        details.add(detail);
                    }
                }
            }

            insertTotals(totals);
            insertDetails(details);
        }

        @Override
        public void onDataReceived(List<Receipt> results) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }

    }

    class PopulateTotalDACallbacks implements DataAccessCallbacks<Total> {

        @Override
        public void onDataProcessed(int processed, List<Total> dataList, Operation operation,
                boolean result) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDataReceived(List<Total> results) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }

    }

    class PopulateDetailDACallbacks implements DataAccessCallbacks<Detail> {

        @Override
        public void onDataProcessed(int processed, List<Detail> dataList, Operation operation,
                boolean result) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDataReceived(List<Detail> results) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }

    }

}
