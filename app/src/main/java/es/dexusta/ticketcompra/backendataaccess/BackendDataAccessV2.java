package es.dexusta.ticketcompra.backendataaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.cloud.backend.android.CloudBackendMessaging;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;
import com.google.cloud.backend.android.CloudQuery;
import com.google.cloud.backend.android.F;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.Consts;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.util.Installation;

/**
 * Created by asincrono on 20/05/14.
 */
public class BackendDataAccessV2 {
    private static final String TAG = "BackendDataAccessV2";

    public static boolean hasConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void updateData(final Context context, final CloudBackendMessaging backend) {
        downloadData(context, backend);
    }

    public static void uploadShops(final List<Shop> shops, final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {

            final DataSource dataSource = DataSource.getInstance(context);

            dataSource.setShopCallback(new DataAccessCallbacks<Shop>() {
                @Override
                public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, dataList.size() + " shops updated in the database.");
                }

                @Override
                public void onDataReceived(List<Shop> results) {

                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });

            CloudCallbackHandler<List<CloudEntity>> cloudCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, results.size() + " shops uploaded to the DataStore.");
                    for (Shop shop : shops) {
                        shop.setUpdated(true);
                    }

                    dataSource.updateShops(shops);
                }
            };

            List<CloudEntity> entities = new ArrayList<CloudEntity>(shops.size());

            for (Shop shop : shops) {
                entities.add(shop.getEntity(context));
            }

            backend.insertAll(entities, cloudCallbackHandler);
        }
    }

    public static void uploadProducts(final List<Product> products, final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {

            final DataSource dataSource = DataSource.getInstance(context);

            dataSource.setProductCallback(new DataAccessCallbacks<Product>() {
                @Override
                public void onDataProcessed(int processed, List<Product> dataList, Types.Operation operation, boolean result) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, dataList.size() + " products updated in the database.");
                }

                @Override
                public void onDataReceived(List<Product> results) {

                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });

            CloudCallbackHandler<List<CloudEntity>> cloudCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, results.size() + " products uploaded to the DataStore.");
                    for (Product product : products) {
                        product.setUpdated(true);
                    }

                    dataSource.updateProducts(products);
                }
            };

            List<CloudEntity> entities = new ArrayList<CloudEntity>(products.size());

            for (Product product : products) {
                entities.add(product.getEntity(context));
            }

            backend.insertAll(entities, cloudCallbackHandler);
        }
    }

    public static void uploadReceiptAndDetails(final Receipt receipt, final List<Detail> details, final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final DataSource dataSource = DataSource.getInstance(context);

            final long shopId = receipt.getShopId();

            if (dataSource.isShopUpdated(shopId)) {
                simpleUploadReceiptAndDetails(receipt, details, context, backend);
            } else {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "The shop wasn't updated, we proceed to do it.");
                // Upload the shop, then call to simpleUploadReceiptAndDetails.

                dataSource.setShopCallback(new DataAccessCallbacks<Shop>() {
                    @Override
                    public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
                        if (operation == Types.Operation.UPDATE) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "Shop updated, we proceed to simpleUploadReceiptAndDetails.");
                            simpleUploadReceiptAndDetails(receipt, details, context, backend);
                        }
                    }

                    @Override
                    public void onDataReceived(List<Shop> results) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Shop retrieved.");
                        final Shop shop = results.get(0);

                        CloudCallbackHandler<CloudEntity> shopCallbackHandler = new CloudCallbackHandler<CloudEntity>() {
                            @Override
                            public void onComplete(CloudEntity results) {
                                if (BuildConfig.DEBUG)
                                    Log.d(TAG, "Shop uploaded, we proceed to update the shop.");
                                shop.setUpdated(true);

                                List<Shop> shopList = new ArrayList<Shop>(1);
                                shopList.add(shop);

                                dataSource.updateShops(shopList);
                            }
                        };

                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "We proceed to upload the shop.");
                        backend.insert(shop.getEntity(context), shopCallbackHandler);
                    }

                    @Override
                    public void onInfoReceived(Object result, AsyncStatement.Option option) {

                    }
                });

                dataSource.getShop(shopId);
            }
        }
    }

    private static void simpleUploadReceiptAndDetails(final Receipt receipt, final List<Detail> details, final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final DataSource dataSource = DataSource.getInstance(context);

            CloudCallbackHandler<CloudEntity> receiptCallbackHandler = new CloudCallbackHandler<CloudEntity>() {
                @Override
                public void onComplete(CloudEntity results) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "(simpleUploadReceiptAndDetail): Receipt uploaded.");
                    receipt.setUpdated(true);

                    List<Receipt> receiptList = new ArrayList<Receipt>(1);
                    receiptList.add(receipt);

                    dataSource.updateReceipts(receiptList);
                }
            };

            final CloudCallbackHandler<List<CloudEntity>> detailsCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "(simpleUploadReceiptAndDetail): details uploaded.");
                    for (Detail detail : details) {
                        detail.setUpdated(true);
                    }

                    dataSource.updateDetails(details);
                }
            };

            dataSource.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
                @Override
                public void onDataProcessed(int processed, List<Receipt> dataList,
                                            Types.Operation operation, boolean result) {
                    if (operation == Types.Operation.UPDATE) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "(simpleUploadReceiptAndDetail): Receipt updated.");

                        List<CloudEntity> entities = new ArrayList<CloudEntity>(details.size());

                        for (Detail detail : details) {
                            entities.add(detail.getEntity(context));
                        }

                        backend.insertAll(entities, detailsCallbackHandler);
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
                    if (operation == Types.Operation.UPDATE) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "(simpleUploadReceiptAndDetail): Details updated.");
                    }
                }

                @Override
                public void onDataReceived(List<Detail> results) {

                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });

            backend.insert(receipt.getEntity(context), receiptCallbackHandler);
        }
    }

    public static void uploadReceipt(final Receipt receipt, final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final DataSource dataSource = DataSource.getInstance(context);

            long shopId = receipt.getShopId();

            // Check if the receipt's shop is updated or pending.
            if (dataSource.isShopUpdated(shopId)) {
                // if shop already updated, we simply upload the receipt.
                simpleUploadReceipt(receipt, context, backend);
            } else {
                // if shop not uploaded yet:
                // 1.- We read the shop from the DB.
                // 2.- THEN: We upload the shop to the DataStore.
                // 3.- THEN: We mark the shop as updated in the DB.
                // 3.- FINALLY: we simply upload the receipt.
                dataSource.setShopCallback(new DataAccessCallbacks<Shop>() {
                    @Override
                    public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
                        Shop shop = dataList.get(0);
                        dataSource.addShopUpdatedInfo(shop);

                        simpleUploadReceipt(receipt, context, backend);
                    }

                    @Override
                    public void onDataReceived(List<Shop> results) {
                        if (results != null) {
                            final Shop shop = results.get(0);

                            CloudCallbackHandler<CloudEntity> shopCallbackHandler = new CloudCallbackHandler<CloudEntity>() {
                                @Override
                                public void onError(IOException exception) {
                                    super.onError(exception);
                                }

                                @Override
                                public void onComplete(CloudEntity results) {
                                    shop.setUpdated(true);

                                    List<Shop> shopList = new ArrayList<Shop>(1);
                                    shopList.add(shop);

                                    dataSource.updateShops(shopList);
                                }
                            };

                            backend.insert(shop.getEntity(context), shopCallbackHandler);
                        }
                    }

                    @Override
                    public void onInfoReceived(Object result, AsyncStatement.Option option) {

                    }
                });

                dataSource.getShop(shopId);
            }
        }
    }

    private static void simpleUploadReceipt(final Receipt receipt, final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final DataSource dataSource = DataSource.getInstance(context);

            // Define the CloudCallbackHandlers for receipt and detail.
            CloudCallbackHandler<CloudEntity> receiptCallbackHandler = new CloudCallbackHandler<CloudEntity>() {
                @Override
                public void onComplete(CloudEntity results) {
                    // Once a receipt is uploaded, check it as "updated" in the DB.
                    receipt.setUpdated(true);

                    List<Receipt> receiptList = new ArrayList<Receipt>(1);
                    receiptList.add(receipt);

                    dataSource.updateReceipts(receiptList);
                }
            };

            dataSource.setDetailCallback(new DataAccessCallbacks<Detail>() {
                @Override
                public void onDataProcessed(int processed, List<Detail> dataList, Types.Operation operation, boolean result) {

                }

                @Override
                public void onDataReceived(List<Detail> results) {
                    if (results != null) {
                        simpleUploadDetails(results, context, backend);
                    }
                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });

            dataSource.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
                @Override
                public void onDataProcessed(int processed, List<Receipt> dataList, Types.Operation operation, boolean result) {
                    // once the receipt is marked as updated:
                    dataSource.getDetailsBy(receipt);
                }

                @Override
                public void onDataReceived(List<Receipt> results) {

                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });

            backend.insert(receipt.getEntity(context), receiptCallbackHandler);
        }
    }

    private static void simpleUploadDetails(final List<Detail> details, Context context, CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final DataSource dataSource = DataSource.getInstance(context);

            CloudCallbackHandler<List<CloudEntity>> detailsCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    for (Detail detail : details) {
                        detail.setUpdated(true);
                    }

                    dataSource.updateDetails(details);
                }
            };

            List<CloudEntity> entities = new ArrayList<CloudEntity>(details.size());

            for (Detail detail : details) {
                entities.add(detail.getEntity(context));
            }

            backend.insertAll(entities, detailsCallbackHandler);
        }
    }

    public static void uploadPendingData(Context context, CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            uploadPendingProducts(context, backend);
        }
    }

    private static void uploadPendingProducts(final Context context, final CloudBackendMessaging backend) {

        if (hasConnectivity(context)) {
            final DataSource dataSource = DataSource.getInstance(context);

            dataSource.setProductCallback(new DataAccessCallbacks<Product>() {
                @Override
                public void onDataProcessed(int processed, List<Product> dataList, Types.Operation operation, boolean result) {
                    uploadPendingShops(context, backend);
                }

                @Override
                public void onDataReceived(List<Product> results) {
                    final List<Product> productList = results;

                    if (results != null) {
                        CloudCallbackHandler<List<CloudEntity>> pendingProductsCH = new CloudCallbackHandler<List<CloudEntity>>() {
                            @Override
                            public void onComplete(List<CloudEntity> results) {
                                for (Product product : productList) {
                                    product.setUpdated(true);
                                }
                                dataSource.updateProducts(productList);
                            }
                        };

                        List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());
                        for (Product product : results) {
                            entities.add(product.getEntity(context));


                            backend.insertAll(entities, pendingProductsCH);
                        }
                    } else {
                        // There aren't pending products, we proceed to upload pending shops.
                        uploadPendingShops(context, backend);
                    }
                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });

            dataSource.listPendingProducts();
        }
    }

    private static void uploadPendingShops(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final DataSource dataSource = DataSource.getInstance(context);

            dataSource.setShopCallback(new DataAccessCallbacks<Shop>() {
                @Override
                public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
                    // Once we update as "updated" the uploaded shops, we proceed to upload the pending receipts.
                    uploadPendingReceipts(context, backend);
                }

                @Override
                public void onDataReceived(List<Shop> results) {
                    if (results != null) {
                        final List<Shop> shopList = results;
                        CloudCallbackHandler<List<CloudEntity>> pendingShopsCH = new CloudCallbackHandler<List<CloudEntity>>() {
                            @Override
                            public void onComplete(List<CloudEntity> results) {
                                for (Shop shop : shopList) {
                                    shop.setUpdated(true);
                                    dataSource.updateShops(shopList);
                                }
                            }
                        };

                        List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());

                        for (Shop shop : results) {
                            entities.add(shop.getEntity(context));
                        }

                        backend.insertAll(entities, pendingShopsCH);
                    } else {
                        // There are not pending shops. We proceed to upload pending receipts.
                        uploadPendingReceipts(context, backend);
                    }
                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });

            dataSource.listPendingShops();
        }
    }

    private static void uploadPendingReceipts(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final DataSource dataSource = DataSource.getInstance(context);

            dataSource.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
                @Override
                public void onDataProcessed(int processed, List<Receipt> dataList, Types.Operation operation, boolean result) {
                    uploadPendingDetails(context, backend);
                }

                @Override
                public void onDataReceived(List<Receipt> results) {
                    if (results != null) {
                        final List<Receipt> receiptList = results;

                        CloudCallbackHandler<List<CloudEntity>> pendingReceiptsCH = new CloudCallbackHandler<List<CloudEntity>>() {
                            @Override
                            public void onComplete(List<CloudEntity> results) {
                                for (Receipt receipt : receiptList) {
                                    receipt.setUpdated(true);
                                }
                                dataSource.updateReceipts(receiptList);
                            }
                        };

                        List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());

                        for (Receipt receipt : results) {
                            entities.add(receipt.getEntity(context));
                        }

                        backend.insertAll(entities, pendingReceiptsCH);
                    } else {
                        uploadPendingDetails(context, backend);
                    }
                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });

            dataSource.listPendingReceipts();
        }
    }

    private static void uploadPendingDetails(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final DataSource dataSource = DataSource.getInstance(context);

            dataSource.setDetailCallback(new DataAccessCallbacks<Detail>() {
                @Override
                public void onDataProcessed(int processed, List<Detail> dataList, Types.Operation operation, boolean result) {

                }

                @Override
                public void onDataReceived(List<Detail> results) {

                    final List<Detail> detailList = results;

                    CloudCallbackHandler<List<CloudEntity>> pendingDetailsCH = new CloudCallbackHandler<List<CloudEntity>>() {
                        @Override
                        public void onComplete(List<CloudEntity> results) {
                            for (Detail detail : detailList) {
                                detail.setUpdated(true);
                            }
                            dataSource.updateDetails(detailList);
                        }
                    };

                    List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());

                    for (Detail detail : results) {
                        entities.add(detail.getEntity(context));
                    }

                    backend.insertAll(entities, pendingDetailsCH);

                }

                @Override
                public void onInfoReceived(Object result, AsyncStatement.Option option) {

                }
            });

            dataSource.listPendingDetails();
        }
    }

    public static void downloadData(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            downloadShops(context, backend);
        }
    }

    private static void downloadShops(final Context context, final CloudBackendMessaging backend) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        TimeZone tzUTC = TimeZone.getTimeZone("UTC");

        String strLastUpdated = sharedPreferences.getString(Consts.PREF_SHOPS_LAST_UPDATE, null);

        final String localInstallation = Installation.id(context);

        DateTime lastUpdated;
        if (strLastUpdated != null) {
            lastUpdated = DateTime.parseRfc3339(strLastUpdated);
        } else {
            lastUpdated = new DateTime(new Date(1), tzUTC);
        }

        final DateTime thisUpdate = new DateTime(new Date(), tzUTC);

        CloudQuery query = new CloudQuery(Shop.KIND_NAME);

        F filterDate = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdated);
        query.setFilter(filterDate);

        final DataSource dataSource = DataSource.getInstance(context);

        dataSource.setShopCallback(new DataAccessCallbacks<Shop>() {
            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
                if (result && dataList.size() > 0) {
                    for (Shop shop : dataList) {
                        dataSource.addShopUpdatedInfo(shop);
                        dataSource.addToUnivIdLocIdMap(shop);
                    }
                }
                downloadProducts(context, backend);
            }

            @Override
            public void onDataReceived(List<Shop> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        CloudCallbackHandler<List<CloudEntity>> shopsCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results != null && results.size() > 0) {
                    List<Shop> shopList = new ArrayList<Shop>();

                    String shopUnivId;
                    Shop shop;
                    for (CloudEntity entity : results) {
                        shop = new Shop(entity);
                        shopUnivId = shop.getUniversalId();

                        if (!shopUnivId.startsWith(localInstallation)) {
                            shopList.add(shop);
                        }
                    }
                    if (shopList.size() > 0) {
                        dataSource.insertShops(shopList);
                    }
                }

                sharedPreferences.edit()
                        .putString(Consts.PREF_SHOPS_LAST_UPDATE, thisUpdate.toStringRfc3339())
                        .commit();
            }
        };

        backend.list(query, shopsCallbackHandler);
    }

    private static void downloadProducts(final Context context, final CloudBackendMessaging backend) {
        final DataSource dataSource = DataSource.getInstance(context);

        final String installation = Installation.id(context);

        TimeZone tzUTC = TimeZone.getTimeZone("UTC");

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String strLastUpdated = sharedPreferences.getString(Consts.PREF_PRODUCTS_LAST_UPDATE, null);
        DateTime lastUpdated;

        if (strLastUpdated == null) {
            lastUpdated = new DateTime(new Date(1), tzUTC);
        } else {
            lastUpdated = DateTime.parseRfc3339(strLastUpdated);
        }

        final DateTime thisUpdate = new DateTime(new Date(), tzUTC);

        CloudQuery query = new CloudQuery(Product.KIND_NAME);
        F filterDate = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdated);
        query.setFilter(filterDate);

        CloudCallbackHandler<List<CloudEntity>> productsCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    List<Product> productList = new ArrayList<Product>();

                    Product product;
                    String univId;
                    for (CloudEntity entity : results) {
                        product = new Product(entity);
                        univId = product.getUniversalId();

                        if (!univId.startsWith(installation)) {
                            productList.add(product);
                        }
                    }

                    if (productList.size() > 0) {
                        dataSource.insertProducts(productList);
                    }
                }
                sharedPreferences.edit()
                        .putString(Consts.PREF_PRODUCTS_LAST_UPDATE, thisUpdate.toStringRfc3339())
                        .commit();
            }
        };

        dataSource.setProductCallback(new DataAccessCallbacks<Product>() {
            @Override
            public void onDataProcessed(int processed, List<Product> dataList, Types.Operation operation, boolean result) {
                for (Product product : dataList) {
                    dataSource.addToUnivIdLocIdMap(product);
                }
                downloadReceipts(context, backend);
            }

            @Override
            public void onDataReceived(List<Product> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        backend.list(query, productsCallbackHandler);
    }

    private static void downloadReceipts(final Context context, final CloudBackendMessaging backend) {
        final DataSource dataSource = DataSource.getInstance(context);

        TimeZone tzUTC = TimeZone.getTimeZone("UTC");

        final String installation = Installation.id(context);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String strLastUpdated = sharedPreferences.getString(Consts.PREF_RECEIPTS_LAST_UPDATE, null);
        DateTime lastUpdated;

        if (strLastUpdated == null) {
            lastUpdated = new DateTime(new Date(1), tzUTC);
        } else {
            lastUpdated = DateTime.parseRfc3339(strLastUpdated);
        }

        final DateTime thisUpdate = new DateTime(new Date(), tzUTC);

        CloudQuery query = new CloudQuery(Receipt.KIND_NAME);
        F filterDate = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdated);
        query.setFilter(filterDate);

        CloudCallbackHandler<List<CloudEntity>> receiptCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    List<Receipt> receiptList = new ArrayList<Receipt>();

                    Receipt receipt;
                    String univId;

                    for (CloudEntity entity : results) {
                        receipt = new Receipt(entity);
                        univId = receipt.getUniversalId();

                        if (!univId.startsWith(installation)) {
                            receiptList.add(receipt);
                        }
                        dataSource.insertReceipts(receiptList);
                    }
                }
                sharedPreferences.edit()
                        .putString(Consts.PREF_RECEIPTS_LAST_UPDATE, thisUpdate.toStringRfc3339())
                        .commit();
            }
        };

        dataSource.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Types.Operation operation, boolean result) {
                for (Receipt receipt : dataList) {
                    dataSource.addToUnivIdLocIdMap(receipt);
                }
                downloadDetails(context, backend);
            }

            @Override
            public void onDataReceived(List<Receipt> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        backend.list(query, receiptCallbackHandler);
    }

    private static void downloadDetails(final Context contex, final CloudBackendMessaging backend) {
        final DataSource dataSource = DataSource.getInstance(contex);


        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contex);

        TimeZone tzUTC = TimeZone.getTimeZone("UTC");
        String strLastUpdated = sharedPreferences.getString(Consts.PREF_DETAILS_LAST_UPDATE, null);
        DateTime lastUpdated;

        if (sharedPreferences == null) {
            lastUpdated = new DateTime(new Date(1), tzUTC);
        } else {
            lastUpdated = DateTime.parseRfc3339(strLastUpdated);
        }

        final DateTime thisUpdate = new DateTime(new Date(), tzUTC);

        CloudQuery query = new CloudQuery(Detail.KIND_NAME);
        F filterDate = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdated);

        CloudCallbackHandler<List<CloudEntity>> detailsCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    String localInstallation = Installation.id(contex);
                    String univId;

                    Detail detail;
                    List<Detail> detailList = new ArrayList<Detail>();
                    for (CloudEntity entity : results) {
                        detail = new Detail(entity);
                        univId = detail.getUniversalId();
                        if (!univId.startsWith(localInstallation)) {
                            detailList.add(detail);
                        }
                        dataSource.insertDetails(detailList);
                    }
                }
            }
        };

        dataSource.setDetailCallback(new DataAccessCallbacks<Detail>() {
            @Override
            public void onDataProcessed(int processed, List<Detail> dataList, Types.Operation operation, boolean result) {
                uploadPendingData(contex, backend);
            }

            @Override
            public void onDataReceived(List<Detail> results) {

            }

            @Override
            public void onInfoReceived(Object result, AsyncStatement.Option option) {

            }
        });

        backend.list(query, detailsCallbackHandler);
    }
}
