package es.dexusta.ticketcompra.localdataaccess;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.util.LongSparseArray;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.InitializeDBTask;
import es.dexusta.ticketcompra.dataaccess.SimpleAsyncInsert;
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
import es.dexusta.ticketcompra.model.Town;
import es.dexusta.ticketcompra.util.Installation;
import es.dexusta.ticketcompra.util.Intervall;

import static es.dexusta.ticketcompra.model.DBHelper.TBL_CATEGORY;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_CHAIN;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_DETAIL;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_PRODUCT;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_RECEIPT;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_REGION;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_SHOP;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_SUBCATEGORY;
import static es.dexusta.ticketcompra.model.DBHelper.TBL_SUBREGION;
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
import static es.dexusta.ticketcompra.model.DBHelper.T_TOWN_ID;
import static es.dexusta.ticketcompra.model.DBHelper.T_TOWN_SUBREGION_ID;

/**
 * Created by asincrono on 22/05/14.
 */
public class LocalDataSource {


    /**
     * Singleton que agrupa todo el sistema de acceso a los datos.
     *
     * @author asincrono
     */
// TODO: Realizar como fragmento en lugar de singleton?

    // SQL QUERY CONSTANTS:
    public static final  String  BASIC_QUERY = "SELECT * FROM ";
    // do not remove first space (not an error).
    public static final  String  BASIC_WHERE = " WHERE ";
    // do not remove first space (not an error).
    public static final  String  IS_NULL     = " IS NULL";
    private static final String  TAG         = "DataSource";
    private static final boolean DEBUG       = true;

    private static LocalDataSource mDataSource;

    private DBHelper mHelper;
    private Context  mContext;

    private HashMap<Long, String>   mCategoryIdNameMap     = new HashMap<Long, String>();
    private LongSparseArray<String> mCategoryIdSparseArray = new LongSparseArray<String>();// ???

    private LongSparseArray<Boolean> mShopUpdated = new LongSparseArray<Boolean>();

    private HashMap<Long, String> mSubcategoryIdNameMap     = new HashMap<Long, String>();
    private HashMap<Long, Long>   mSubcategoryCategoryIdMap = new HashMap<Long, Long>();

    private HashMap<String, Long> mShopUnivIdLocIdMap    = new HashMap<String, Long>();
    private HashMap<String, Long> mProductUnivIdLocIdMap = new HashMap<String, Long>();
    private HashMap<Long, Long>   mProductSubcategoryId  = new HashMap<Long, Long>();
    private HashMap<String, Long> mReceiptUnivIdLocIdMap = new HashMap<String, Long>();

    private HashMap<Long, String> mChainIdNameMap   = new HashMap<Long, String>();
    private HashMap<Long, Long>   mShopIdChainIdMap = new HashMap<Long, Long>();

    private DataAccessCallbacks<ReplicatedDBObject> mSimpleInsertListener;

    private ChainData mChainData;
    private ShopData  mShopData;

    private CategoryData    mCategoryData;
    private SubcategoryData mSubcategoryData;
    private ProductData     mProductData;

    private ReceiptData mReceiptData;
    private DetailData  mDetailData;


    private RegionData    mRegionData;
    private SubregionData mSubregionData;
    private TownData      mTownData;

    private LocalDataSource(Context context) {
        mContext = context;
        init();
    }

    public static LocalDataSource getInstance(Context context) {

        if (mDataSource == null) {
            mDataSource = new LocalDataSource(context);
        }
        return mDataSource;
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

    private void init() {
        // Crear todos los objetos necesarios para el acceso a los datos:
        // SQLiteOpenHelper, etc.
        mHelper = new DBHelper(mContext);

        // Inicializar las fuentes de datos:
        mChainData = new ChainData(mHelper);
        mShopData = new ShopData(mHelper);

        mReceiptData = new ReceiptData(mHelper);
        mDetailData = new DetailData(mHelper);

        mCategoryData = new CategoryData(mHelper);
        mSubcategoryData = new SubcategoryData(mHelper);
        mProductData = new ProductData(mHelper);

        mRegionData = new RegionData(mHelper);
        mSubregionData = new SubregionData(mHelper);
        mTownData = new TownData(mHelper);

        buildCategoryIdNameMap();
        buildSubcategoriesMaps();
        buildShopUnivIdLocIdMap();
        buildShopUpdated();
        buildProductMaps();
        buildShopUnivIdLocIdMap();
        buildChainIdNameMap();
        buildShopIdChainIdMap();
    }


    public void initDatabase(InitializeDBTask.InitializerCallback callback) {
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

//    public void downloadData(CloudBackendMessaging backend) {
//        //BackendDataAccess.downloadShops(mContext, backend, true);
//        BackendDataAccessV2.downloadData(mContext, backend);
//    }
//
//    public void uploadData(final CloudBackendMessaging backend) {
//        //BackendDataAccess.uploadPendingShops(mContext, backend, true);
//        BackendDataAccessV2.uploadPendingData(mContext, backend);
//    }


    private void buildShopUpdated() {
        listShops(new DataAccessCallback<Shop>() {
            @Override
            public void onComplete(List<Shop> results, boolean result) {
                if (results != null && results.size() > 0) {
                    for (Shop shop : results) {
                        if (shop.isUpdated()) {
                            mShopUpdated.put(shop.getId(), Boolean.TRUE);
                        } else {
                            mShopUpdated.put(shop.getId(), Boolean.FALSE);
                        }
                    }
                }
            }
        });
    }

    private void buildCategoryIdNameMap() {
        listCategories(new DataAccessCallback<Category>() {
            @Override
            public void onComplete(List<Category> results, boolean result) {
                if (results != null && results.size() > 0) {
                    for (Category category : results) {
                        mCategoryIdNameMap.put(category.getId(), category.getName());
                    }
                }
            }
        });
    }

    private void buildSubcategoriesMaps() {
        listSubcategories(new DataAccessCallback<Subcategory>() {
            @Override
            public void onComplete(List<Subcategory> results, boolean result) {
                if (results != null && results.size() > 0) {
                    for (Subcategory subcategory : results) {
                        mSubcategoryIdNameMap.put(subcategory.getId(), subcategory.getName());
                        mSubcategoryCategoryIdMap.put(subcategory.getId(), subcategory.getCategoryId());
                    }
                }
            }
        });
    }

    private void buildShopUnivIdLocIdMap() {
        listShops(new DataAccessCallback<Shop>() {
            @Override
            public void onComplete(List<Shop> results, boolean result) {
                if (results != null && results.size() > 0) {
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
            }
        });
    }

    private void buildProductMaps() {
        listProducts(new DataAccessCallback<Product>() {
            @Override
            public void onComplete(List<Product> results, boolean result) {
                if (results != null && results.size() > 0) {
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
            }
        });
    }

    private void buildReceiptUnivIdLocIdMap() {
        listReceipts(new DataAccessCallback<Receipt>() {
            @Override
            public void onComplete(List<Receipt> results, boolean result) {
                if (results != null && results.size() > 0) {
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
            }
        });
    }

    private void buildChainIdNameMap() {
        listChains(new DataAccessCallback<Chain>() {
            @Override
            public void onComplete(List<Chain> results, boolean result) {
                if (results != null && results.size() > 0) {
                    for (Chain chain : results) {
                        mChainIdNameMap.put(chain.getId(), chain.getName());
                    }
                }
            }
        });
    }

    private void buildShopIdChainIdMap() {
        listShops(new DataAccessCallback<Shop>() {
            @Override
            public void onComplete(List<Shop> results, boolean result) {
                if (results != null && results.size() > 0) {
                    for (Shop shop : results) {
                        mShopIdChainIdMap.put(shop.getId(), shop.getChainId());
                    }
                }
            }
        });
    }

    public boolean isShopUpdated(long shopId) {
        Boolean updated = mShopUpdated.get(shopId);
        return updated != null ? updated : false;
    }

    public void addShopUpdatedInfo(Shop shop) {
        mShopUpdated.put(shop.getId(), shop.isUpdated());
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
        Long localId = mShopUnivIdLocIdMap.get(univId);

        return localId == null ? -1 : localId;
    }

    public long getProductLocIdFromUnivId(String univId) {
        Long localId = mProductUnivIdLocIdMap.get(univId);
        return localId == null ? -1 : localId;
    }

    public long getReceiptLocIdFromUnivId(String univId) {
        Long localId = mReceiptUnivIdLocIdMap.get(univId);

        return localId == null ? -1 : localId;
    }

    public void setSimpleInsertCallback(DataAccessCallbacks<ReplicatedDBObject> listener) {
        mSimpleInsertListener = listener;
    }

    public void simpleInsert(ReplicatedDBObject rdbo) {
        new SimpleAsyncInsert(mHelper, rdbo, mSimpleInsertListener).execute();
    }

    // Operaciones de consulta de datos:
    // Para Region.
    public void listRegions(DataAccessCallback<Region> callback) {
        if (callback != null) {
            mRegionData.list(callback);
        }
    }

    public void getRegion(long id, DataAccessCallback<Region> callback) {
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_REGION + BASIC_WHERE + T_REGION_ID + " = " + id;
            mRegionData.read(rawQuery, null, callback);
        }
    }

    // Para Subregion:
    public void listSubregions(DataAccessCallback<Subregion> callback) {
        if (callback != null) {
            mSubregionData.list(callback);
        }
    }

    public void getSubregion(long id, DataAccessCallback<Subregion> callback) {
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_SUBREGION + BASIC_WHERE + T_SUBREGION_ID + " = "
                    + id;
            mSubregionData.read(rawQuery, null, callback);
        }
    }

    public void getSubregionBy(Region region, DataAccessCallback<Subregion> callback) {
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_SUBREGION + BASIC_WHERE + T_SUBREGION_REGION_ID
                    + " = " + region.getId();
            mSubregionData.read(rawQuery, null, callback);
        }
    }

    // Para Town.
    public void listTwons(DataAccessCallback<Town> callback) {
        if (callback != null) {
            mTownData.list(callback);
        }
    }

    public void getTwon(long id, DataAccessCallback<Town> callback) {
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_TOWN + BASIC_WHERE + T_TOWN_ID + " = " + id;
            mTownData.read(rawQuery, null, callback);
        }
    }

    public void getTownsBy(Subregion subregion, DataAccessCallback<Town> callback) {
            /*
             * SELECT * FROM town WHERE subregion_id = subregion.getId();
             */
        String rawQuery = BASIC_QUERY + TBL_TOWN + BASIC_WHERE + T_TOWN_SUBREGION_ID + " = "
                + subregion.getId();
        mTownData.read(rawQuery, null, callback);
    }

    public void getTownsBy(Region region, DataAccessCallback<Town> callback) {
        /*
         * SELECT * FROM town WHERE subregion_id IN ( SELECT _id FROM subregion
         * WHERE region_id = region.getId());
         */

        String rawQuery = "SELECT * FROM " + TBL_TOWN + " WHERE " + T_TOWN_SUBREGION_ID + " IN "
                + "(SELECT " + T_SUBREGION_ID + " FROM " + TBL_SUBREGION + " WHERE "
                + T_SUBREGION_REGION_ID + " = " + region.getId() + ")";
        mTownData.read(rawQuery, null, callback);
    }


    public void listChains(DataAccessCallback<Chain> callback) {
        if (callback != null) {
            mChainData.list(callback);
        }
    }

    public void getChain(long id, DataAccessCallback<Chain> callback) {
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_CHAIN + BASIC_WHERE + T_CHAIN_ID + " = " + id;
            mChainData.read(rawQuery, null, callback);
        }
    }

    // Para Shop.
    public void listShops(DataAccessCallback<Shop> callback) {
        if (callback != null) {
            mShopData.list(callback);
        }
    }

    public void getShop(long id, DataAccessCallback<Shop> callback) {
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_SHOP + BASIC_WHERE + T_SHOP_ID + " = " + id;
            mShopData.read(rawQuery, null, callback);
        }
    }

    public void getShopsBy(Chain chain, DataAccessCallback<Shop> callback) {
        if (callback != null) {

            String rawQuery = BASIC_QUERY + TBL_SHOP + BASIC_WHERE + T_SHOP_CHAIN_ID + " = "
                    + chain.getId();
            mShopData.read(rawQuery, null, callback);
        }
    }

    public void listPendingShops(DataAccessCallback<Shop> callback) {
        if (callback != null) {
            /*
             * SELECT * FROM shop WHERE updated = 0
             */
            String rawQuery = BASIC_QUERY + TBL_SHOP + BASIC_WHERE + T_SHOP_UPDATED + " = 0";
            mShopData.read(rawQuery, null, callback);
        }
    }

    // Para Category.
    public void listCategories(DataAccessCallback<Category> callback) {
        // No point on retrieve data if no one will handle it.
        if (callback != null) {
            // Call an Asynctask to get the category list.
            // Llamar a Asyntask para obtener lista de categorías.
            mCategoryData.list(callback);
        }
    }

    public void getCategory(long id, DataAccessCallback<Category> callback) {
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_CATEGORY + BASIC_WHERE + T_CAT_ID + " = " + id;
            mCategoryData.read(rawQuery, null, callback);
        }
    }

    public void listSubcategories(DataAccessCallback<Subcategory> callback) {
        if (callback != null) {
            // String rawQuery = BASIC_QUERY + TBL_SUBCATEGORY;
            // mSubcategoryData.read(rawQuery, null);
            mSubcategoryData.list(callback);
        }
    }

    public void getSubcategory(long id, DataAccessCallback<Subcategory> callback) {
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_SUBCATEGORY + BASIC_WHERE + T_SUBCAT_CAT_ID + " = "
                    + id;
            mSubcategoryData.read(rawQuery, null, callback);
        }
    }

    public void getSubcategoriesBy(Category category, DataAccessCallback<Subcategory> callback) {
        // No point on doing work for no one.
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_SUBCATEGORY + BASIC_WHERE + T_SUBCAT_CAT_ID + " = "
                    + category.getId();
            mSubcategoryData.read(rawQuery, null, callback);
        }
    }

    public void listProducts(DataAccessCallback<Product> callback) {
        if (callback != null) {
            // String rawQuery = BASIC_QUERY + TBL_PRODUCT;
            // mProductData.read(rawQuery, null);
            mProductData.list(callback);
        }
    }

    public void getProduct(long id, DataAccessCallback<Product> callback) {
        if (callback != null) {
            String rawQuer = BASIC_QUERY + TBL_PRODUCT + BASIC_WHERE + T_PROD_ID + " = " + id;
            mProductData.read(rawQuer, null, callback);
        }
    }

    public void getProductsBy(Category category, DataAccessCallback<Product> callback) {
        if (callback != null) {
            /*
             * SELECT * FROM product WHERE subcategory_id IN (SELECT id FROM
             * subcategory WHERE category_id = <category.getId()>)
             */

            String rawQuery = "SELECT * FROM " + TBL_PRODUCT + " WHERE " + T_PROD_SUBCAT_ID
                    + " IN " + "(SELECT " + T_SUBCAT_ID + " FROM " + TBL_SUBCATEGORY + " WHERE "
                    + T_SUBCAT_CAT_ID + " = " + category.getId() + ")";
            mProductData.read(rawQuery, null, callback);
        }
    }

    public void getProductsBy(Subcategory subcategory, DataAccessCallback<Product> callback) {
        if (callback != null) {
            /*
             * SELECT * FROM product WHERE subcategory_id =
             * <subcategory.getId()>
             */
            String rawQuery = BASIC_QUERY + TBL_PRODUCT + BASIC_WHERE + T_PROD_SUBCAT_ID + " = "
                    + subcategory.getId();
            mProductData.read(rawQuery, null, callback);
        }
    }

    public void listPendingProducts(DataAccessCallback<Product> callback) {
        if (callback != null) {
            /*
             * SELECT * FROM product WHERE updated = 0
             */
            String rawQuery = BASIC_QUERY + TBL_PRODUCT + BASIC_WHERE + T_PROD_UPDATED + " = 0";
            mProductData.read(rawQuery, null, callback);
        }
    }

    // Para Receipt.
    public void listReceipts(DataAccessCallback<Receipt> callback) {
        if (callback != null) {
            /*
             * SELECT * FROM receipt
             */
            // String rawQuery = BASIC_QUERY + TBL_RECEIPT;
            // mReceiptData.read(rawQuery, null);
            mReceiptData.list(callback);
        }
    }

    public void getReceipt(long id, DataAccessCallback<Receipt> callback) {
        if (callback != null) {
            /*
             * SELECT * FROM receipt WHERE id = <id>
             */
            String rawQuery = BASIC_QUERY + TBL_RECEIPT + BASIC_WHERE + T_RECPT_ID + " = " + id;
            mReceiptData.read(rawQuery, null, callback);
        }
    }

    public void getReceiptsBy(Chain chain, DataAccessCallback<Receipt> callback) {
        if (callback != null) {
            /*
             * SELECT * FROM receipt WHERE shop_id IN (SELECT id FROM shop WHERE
             * chain_id = <chain.getId()>)
             */
            String rawQuery = "SELECT * FROM " + TBL_RECEIPT + " WHERE " + T_RECPT_SHOP_ID + " IN "
                    + "(SELECT " + T_SHOP_ID + " FROM " + TBL_SHOP + " WHERE " + T_SHOP_CHAIN_ID
                    + " = " + chain.getId() + ")";
            mReceiptData.read(rawQuery, null, callback);
        }
    }

    public void getReceiptsBy(Shop shop, DataAccessCallback<Receipt> callback) {
        if (callback != null) {
            /*
             * SELECT * FROM receipt WHERE user_id = <shop.getId()>
             */
            String rawQuery = BASIC_QUERY + TBL_RECEIPT + BASIC_WHERE + T_RECPT_SHOP_ID + " = "
                    + shop.getId();
            mReceiptData.read(rawQuery, null, callback);
        }
    }

    public void getReceiptsBy(Calendar beginning, Calendar end, DataAccessCallback<Receipt> callback) {

        if (callback != null) {
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
                        + Intervall.toRfc3339ZuluString(beginning) + "' AND " + T_RECPT_TIMESTAMP
                        + " <= '" + Intervall.toRfc3339ZuluString(end) + "'";
                mReceiptData.read(rawQuery, null, callback);
                return;
            }
            if (withBeginning) {
                /*
                 * SELECT * FROM receipt WHERE timestamp >= beginning;
                 */
                rawQuery = BASIC_QUERY + TBL_RECEIPT + BASIC_WHERE + T_RECPT_TIMESTAMP + " >= '"
                        + Intervall.toRfc3339ZuluString(beginning) + "'";
                mReceiptData.read(rawQuery, null, callback);
                return;
            }
            if (withEnd) {
                /*
                 * SELECT * FROM receipt WHERE timestamp <= end;
                 */
                rawQuery = BASIC_QUERY + TBL_RECEIPT + BASIC_WHERE + T_RECPT_TIMESTAMP + " <= '"
                        + Intervall.toRfc3339ZuluString(end) + "'";
                mReceiptData.read(rawQuery, null, callback);
                return;
            }
            mReceiptData.list(callback);
        }
    }

    public void listPendingReceipts(DataAccessCallback<Receipt> callback) {
        if (callback != null) {
            /*
             * SELECT * FROM receipt WHERE updated = 0
             */
            String rawQuery = BASIC_QUERY + TBL_RECEIPT + BASIC_WHERE + T_RECPT_UPDATED + " = 0";
            mReceiptData.read(rawQuery, null, callback);
        }
    }

    // Para Detail.
    public void listDetails(DataAccessCallback<Detail> callback) {
        if (callback != null) {
            // String rawQuery = BASIC_QUERY + TBL_DETAIL;
            // mDetailData.read(rawQuery, null);
            mDetailData.list(callback);
        }
    }

    public void getDetail(long id, DataAccessCallback<Detail> callback) {
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_DETAIL + BASIC_WHERE + T_DETAIL_ID + " = " + id;
            mDetailData.read(rawQuery, null, callback);
        }
    }

    public void getDetailsBy(Receipt receipt, DataAccessCallback<Detail> callback) {
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_DETAIL + BASIC_WHERE + T_DETAIL_RECPT_ID + " = "
                    + receipt.getId();
            mDetailData.read(rawQuery, null, callback);
        }
    }

    public void getDetailsBy(List<Receipt> receipts, DataAccessCallback<Detail> callback) {
        if (callback != null) {
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
            mDetailData.read(rawQueryBldr.toString(), null, callback);
        }
    }

    public void getDetailsBy(Product product, DataAccessCallback<Detail> callback) {
        if (callback != null) {
            String rawQuery = BASIC_QUERY + TBL_DETAIL + BASIC_WHERE + T_DETAIL_PROD_ID + " = "
                    + product.getId();
            mDetailData.read(rawQuery, null, callback);
        }
    }

    public void getDetailsBy(Subcategory subcategory, DataAccessCallback<Detail> callback) {
        /*
         * SELECT * FROM detail WHERE product_id IN (SELECT id FROM product
         * WHERE subcategory_id = <subcategory.getId()>
         */
        if (callback != null) {
            String rawQuery = "SELECT * FROM " + TBL_DETAIL + " WHERE " + T_DETAIL_PROD_ID + " IN "
                    + "(SELECT " + T_PROD_ID + "FROM" + TBL_PRODUCT + " WHERE " + T_PROD_SUBCAT_ID
                    + " = " + subcategory.getId();
            mDetailData.read(rawQuery, null, callback);
        }

    }

    public void getDetailsBy(Category category, DataAccessCallback<Detail> callback) {
        /*
         * SELECT * FROM detail WHERE product_id IN (SELECT id FROM product
         * WHERE subcategory_id IN (SELECT id FROM subcategory WHERE category_id
         * = <category.getId()>) )
         */

        if (callback != null) {
            String rawQuery = "SELECT * FROM " + TBL_DETAIL + " WHERE " + T_DETAIL_PROD_ID + " IN "
                    + "(SELECT " + T_PROD_ID + "FROM" + TBL_PRODUCT + " WHERE " + T_PROD_SUBCAT_ID
                    + " IN " + "(SELECT " + T_SUBCAT_ID + " FROM " + TBL_SUBCATEGORY + " WHERE "
                    + T_SUBCAT_CAT_ID + " = " + category.getId() + ") " + ")";
            mDetailData.read(rawQuery, null, callback);
        }
    }

    public void listPendingDetails(DataAccessCallback<Detail> callback) {
        if (callback != null) {
            /*
             * SELECT FROM detail WHERE updated = 0
             */
            String rawQuery = BASIC_QUERY + TBL_DETAIL + BASIC_WHERE + T_DETAIL_UPDATED + " = 0";
            mDetailData.read(rawQuery, null, callback);
        }
    }

    public void insertRegions(List<Region> dataList, DataAccessCallback<Region> callback) {
        mRegionData.insert(dataList, callback);
    }

    public void insertSubregions(List<Subregion> dataList, DataAccessCallback<Subregion> callback) {
        mSubregionData.insert(dataList, callback);
    }

    public void insertTowns(List<Town> dataList, DataAccessCallback<Town> callback) {
        mTownData.insert(dataList, callback);
    }

    public void updateRegions(List<Region> dataList, DataAccessCallback<Region> callback) {
        mRegionData.update(dataList, callback);
    }

    public void updateSubregions(List<Subregion> dataList, DataAccessCallback<Subregion> callback) {
        mSubregionData.update(dataList, callback);
    }

    public void updateTowns(List<Town> dataList, DataAccessCallback<Town> callback) {
        mTownData.update(dataList, callback);
    }

    public void deleteRegions(List<Region> dataList, DataAccessCallback<Region> callback) {
        mRegionData.delete(dataList, callback);
    }

    public void deleteRegions(DataAccessCallback<Region> callback) {
        mRegionData.deleteAll(callback);
    }

    public void deleteSubregions(List<Subregion> dataList, DataAccessCallback<Subregion> callback) {
        mSubregionData.delete(dataList, callback);
    }

    public void deleteSubregions(DataAccessCallback<Subregion> callback) {
        mSubregionData.deleteAll(callback);
    }

    public void deleteTowns(List<Town> dataList, DataAccessCallback<Town> callback) {
        mTownData.delete(dataList, callback);
    }

    public void deleteTowns(DataAccessCallback<Town> callback) {
        mTownData.deleteAll(callback);
    }

    public void insertChains(List<Chain> dataList, DataAccessCallback<Chain> callback) {
        mChainData.insert(dataList, callback);
    }

    public void insertShops(List<Shop> dataList, DataAccessCallback<Shop> callback) {
        mShopData.insert(dataList, callback);
    }

    public void insertCategories(List<Category> dataList, DataAccessCallback<Category> callback) {
        mCategoryData.insert(dataList, callback);
    }

    public void insertSubcategories(List<Subcategory> dataList, DataAccessCallback<Subcategory> callback) {
        mSubcategoryData.insert(dataList, callback);
    }

    public void insertProducts(List<Product> dataList, DataAccessCallback<Product> callback) {
        mProductData.insert(dataList, callback);
    }

    public void insertReceipts(List<Receipt> dataList, DataAccessCallback<Receipt> callback) {
        mReceiptData.insert(dataList, callback);
    }

    public void insertDetails(List<Detail> dataList, DataAccessCallback<Detail> callback) {
        mDetailData.insert(dataList, callback);
    }

    public void updateChains(List<Chain> dataList, DataAccessCallback<Chain> callback) {
        mChainData.update(dataList, callback);
    }

    public void updateShops(List<Shop> dataList, DataAccessCallback<Shop> callback) {
        mShopData.update(dataList, callback);
    }

    public void updateCategories(List<Category> dataList, DataAccessCallback<Category> callback) {
        mCategoryData.update(dataList, callback);
    }

    public void updateSubcategories(List<Subcategory> dataList, DataAccessCallback<Subcategory> callback) {
        mSubcategoryData.update(dataList, callback);
    }

    public void updateProducts(List<Product> dataList, DataAccessCallback<Product> callback) {
        mProductData.update(dataList, callback);
    }

    public void updateReceipts(List<Receipt> dataList, DataAccessCallback<Receipt> callback) {
        mReceiptData.update(dataList, callback);
    }

    public void updateDetails(List<Detail> dataList, DataAccessCallback<Detail> callback) {
        mDetailData.update(dataList, callback);
    }


    /* Delete functions */
    public void deleteChains(List<Chain> dataList, DataAccessCallback<Chain> callback) {
        mChainData.delete(dataList, callback);
    }

    public void deleteChains(DataAccessCallback<Chain> callback) {
        mChainData.deleteAll(callback);
    }

    public void deleteShops(List<Shop> dataList, DataAccessCallback<Shop> callback) {
        mShopData.delete(dataList, callback);
    }

    public void deleteShops(DataAccessCallback<Shop> callback) {
        mShopData.deleteAll(callback);
    }

    public void deleteCategories(List<Category> dataList, DataAccessCallback<Category> callback) {
        mCategoryData.delete(dataList, callback);
    }

    public void deleteCategories(DataAccessCallback<Category> callback) {
        mCategoryData.deleteAll(callback);
    }

    public void deleteSubcategories(List<Subcategory> dataList, DataAccessCallback<Subcategory> callback) {
        mSubcategoryData.delete(dataList, callback);
    }

    public void deleteSubcategories(DataAccessCallback<Subcategory> callback) {
        mSubcategoryData.deleteAll(callback);
    }

    public void deleteProducts(List<Product> dataList, DataAccessCallback<Product> callback) {
        mProductData.delete(dataList, callback);
    }

    public void deleteProducts(DataAccessCallback<Product> callback) {
        mProductData.deleteAll(callback);
    }

    public void deleteReceipts(List<Receipt> dataList, DataAccessCallback<Receipt> callback) {
        mReceiptData.delete(dataList, callback);
    }

    public void deleteReceipts(DataAccessCallback<Receipt> callback) {
        mReceiptData.deleteAll(callback);
    }

    public void deleteDetails(List<Detail> dataList, DataAccessCallback<Detail> callback) {
        mDetailData.delete(dataList, callback);
    }

    public void deleteDetails(DataAccessCallback<Detail> callback) {
        mDetailData.deleteAll(callback);
    }
}



