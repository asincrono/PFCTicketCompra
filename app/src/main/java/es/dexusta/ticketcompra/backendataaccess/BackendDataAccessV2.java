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
import es.dexusta.ticketcompra.localdataaccess.DataAccessCallback;
import es.dexusta.ticketcompra.localdataaccess.LocalDataSource;
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
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Update data started: we proceed to download new data.");
        downloadData(context, backend);
    }

    public static void uploadShops(final List<Shop> shops, final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {

            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final DataAccessCallback<Shop> updateShopsCallback = new DataAccessCallback<Shop>() {
                @Override
                public void onComplete(List<Shop> results, boolean result) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, results.size() + " shops updated in the database.");
                }
            };

            CloudCallbackHandler<List<CloudEntity>> cloudCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, results.size() + " shops uploaded to the DataStore.");
                    for (Shop shop : shops) {
                        shop.setUpdated(true);
                    }

                    localDataSource.updateShops(shops, updateShopsCallback);
                }
            };

            List<CloudEntity> entities = new ArrayList<CloudEntity>(shops.size());

            for (Shop shop : shops) {
                entities.add(shop.getEntity(context));
            }

            backend.insertAll(entities, cloudCallbackHandler);
        }
    }

//    public static void uploadShops(final List<Shop> shops, final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            dataSource.setShopCallback(new DataAccessCallbacks<Shop>() {
//                @Override
//                public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
//                    if (BuildConfig.DEBUG)
//                        Log.d(TAG, dataList.size() + " shops updated in the database.");
//                }
//
//                @Override
//                public void onDataReceived(List<Shop> results) {
//
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            CloudCallbackHandler<List<CloudEntity>> cloudCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
//                @Override
//                public void onComplete(List<CloudEntity> results) {
//                    if (BuildConfig.DEBUG)
//                        Log.d(TAG, results.size() + " shops uploaded to the DataStore.");
//                    for (Shop shop : shops) {
//                        shop.setUpdated(true);
//                    }
//
//                    dataSource.updateShops(shops);
//                }
//            };
//
//            List<CloudEntity> entities = new ArrayList<CloudEntity>(shops.size());
//
//            for (Shop shop : shops) {
//                entities.add(shop.getEntity(context));
//            }
//
//            backend.insertAll(entities, cloudCallbackHandler);
//        }
//    }

    public static void uploadProducts(final List<Product> products, final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {

//            final DataSource dataSource = DataSource.getInstance(context);

            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final DataAccessCallback<Product> updateProductsCallback = new DataAccessCallback<Product>() {
                @Override
                public void onComplete(List<Product> results, boolean result) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, results.size() + " products updated in the database.");
                }
            };

//            dataSource.setProductCallback(new DataAccessCallbacks<Product>() {
//                @Override
//                public void onDataProcessed(int processed, List<Product> dataList, Types.Operation operation, boolean result) {
//                    if (BuildConfig.DEBUG)
//                        Log.d(TAG, dataList.size() + " products updated in the database.");
//                }
//
//                @Override
//                public void onDataReceived(List<Product> results) {
//
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });

//            CloudCallbackHandler<List<CloudEntity>> cloudCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
//                @Override
//                public void onComplete(List<CloudEntity> results) {
//                    if (BuildConfig.DEBUG)
//                        Log.d(TAG, results.size() + " products uploaded to the DataStore.");
//                    for (Product product : products) {
//                        product.setUpdated(true);
//                    }
//
//                    dataSource.updateProducts(products);
//                }
//            };

            CloudCallbackHandler<List<CloudEntity>> cloudCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, results.size() + " products uploaded to the DataStore.");
                    for (Product product : products) {
                        product.setUpdated(true);
                    }

                    localDataSource.updateProducts(products, updateProductsCallback);
                }
            };

            List<CloudEntity> entities = new ArrayList<CloudEntity>(products.size());

            for (Product product : products) {
                entities.add(product.getEntity(context));
            }

            backend.insertAll(entities, cloudCallbackHandler);
        }
    }

    public static void uploadReceiptAndDetails(final Receipt receipt, final List<Detail> details,
                                               final Context context,
                                               final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final long shopId = receipt.getShopId();

            if (localDataSource.isShopUpdated(shopId)) {
                simpleUploadReceiptAndDetails(receipt, details, context, backend);
            } else {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "The shop wasn't updated, we proceed to do it.");
                // Upload the shop, then call to simpleUploadReceiptAndDetails.

                final DataAccessCallback<Shop> updateShopsCallback = new DataAccessCallback<Shop>() {
                    @Override
                    public void onComplete(List<Shop> results, boolean result) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Shop updated, we proceed to simpleUploadReceiptAndDetails.");
                        simpleUploadReceiptAndDetails(receipt, details, context, backend);
                    }
                };

                DataAccessCallback<Shop> readShopsCallback = new DataAccessCallback<Shop>() {
                    @Override
                    public void onComplete(List<Shop> results, boolean result) {
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

                                localDataSource.updateShops(shopList, updateShopsCallback);
                            }
                        };

                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "We proceed to upload the shop.");
                        backend.insert(shop.getEntity(context), shopCallbackHandler);
                    }
                };

                localDataSource.getShop(shopId, readShopsCallback);
            }
        }
    }

//    public static void uploadReceiptAndDetails(final Receipt receipt, final List<Detail> details, final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            final long shopId = receipt.getShopId();
//
//            if (dataSource.isShopUpdated(shopId)) {
//                simpleUploadReceiptAndDetails(receipt, details, context, backend);
//            } else {
//                if (BuildConfig.DEBUG)
//                    Log.d(TAG, "The shop wasn't updated, we proceed to do it.");
//                // Upload the shop, then call to simpleUploadReceiptAndDetails.
//
//                dataSource.setShopCallback(new DataAccessCallbacks<Shop>() {
//                    @Override
//                    public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
//                        if (operation == Types.Operation.UPDATE) {
//                            if (BuildConfig.DEBUG)
//                                Log.d(TAG, "Shop updated, we proceed to simpleUploadReceiptAndDetails.");
//                            simpleUploadReceiptAndDetails(receipt, details, context, backend);
//                        }
//                    }
//
//                    @Override
//                    public void onDataReceived(List<Shop> results) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "Shop retrieved.");
//                        final Shop shop = results.get(0);
//
//                        CloudCallbackHandler<CloudEntity> shopCallbackHandler = new CloudCallbackHandler<CloudEntity>() {
//                            @Override
//                            public void onComplete(CloudEntity results) {
//                                if (BuildConfig.DEBUG)
//                                    Log.d(TAG, "Shop uploaded, we proceed to update the shop.");
//                                shop.setUpdated(true);
//
//                                List<Shop> shopList = new ArrayList<Shop>(1);
//                                shopList.add(shop);
//
//                                dataSource.updateShops(shopList);
//                            }
//                        };
//
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "We proceed to upload the shop.");
//                        backend.insert(shop.getEntity(context), shopCallbackHandler);
//                    }
//
//                    @Override
//                    public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                    }
//                });
//
//                dataSource.getShop(shopId);
//            }
//        }
//    }

    private static void simpleUploadReceiptAndDetails(final Receipt receipt,
                                                      final List<Detail> details,
                                                      final Context context,
                                                      final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {

            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final CloudCallbackHandler<List<CloudEntity>> detailsCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "(simpleUploadReceiptAndDetail): details uploaded.");
                    for (Detail detail : details) {
                        detail.setUpdated(true);
                    }

                    localDataSource.updateDetails(details, new DataAccessCallback<Detail>() {
                        @Override
                        public void onComplete(List<Detail> results, boolean result) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "(simpleUploadReceiptAndDetail): Details updated.");
                        }
                    });
                }
            };

            CloudCallbackHandler<CloudEntity> receiptCallbackHandler = new CloudCallbackHandler<CloudEntity>() {
                @Override
                public void onComplete(CloudEntity results) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "(simpleUploadReceiptAndDetail): Receipt uploaded.");
                    receipt.setUpdated(true);

                    List<Receipt> receiptList = new ArrayList<Receipt>(1);
                    receiptList.add(receipt);

                    localDataSource.updateReceipts(receiptList, new DataAccessCallback<Receipt>() {
                        @Override
                        public void onComplete(List<Receipt> results, boolean result) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "(simpleUploadReceiptAndDetail): Receipt updated.");

                            List<CloudEntity> entities = new ArrayList<CloudEntity>(details.size());

                            for (Detail detail : details) {
                                entities.add(detail.getEntity(context));
                            }

                            backend.insertAll(entities, detailsCallbackHandler);
                        }
                    });
                }
            };

            backend.insert(receipt.getEntity(context), receiptCallbackHandler);
        }
    }

//    private static void simpleUploadReceiptAndDetails(final Receipt receipt, final List<Detail> details,
//                                                      final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            CloudCallbackHandler<CloudEntity> receiptCallbackHandler = new CloudCallbackHandler<CloudEntity>() {
//                @Override
//                public void onComplete(CloudEntity results) {
//                    if (BuildConfig.DEBUG)
//                        Log.d(TAG, "(simpleUploadReceiptAndDetail): Receipt uploaded.");
//                    receipt.setUpdated(true);
//
//                    List<Receipt> receiptList = new ArrayList<Receipt>(1);
//                    receiptList.add(receipt);
//
//                    dataSource.updateReceipts(receiptList);
//                }
//            };
//
//            final CloudCallbackHandler<List<CloudEntity>> detailsCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
//                @Override
//                public void onComplete(List<CloudEntity> results) {
//                    if (BuildConfig.DEBUG)
//                        Log.d(TAG, "(simpleUploadReceiptAndDetail): details uploaded.");
//                    for (Detail detail : details) {
//                        detail.setUpdated(true);
//                    }
//
//                    dataSource.updateDetails(details);
//                }
//            };
//
//            dataSource.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
//                @Override
//                public void onDataProcessed(int processed, List<Receipt> dataList,
//                                            Types.Operation operation, boolean result) {
//                    if (operation == Types.Operation.UPDATE) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "(simpleUploadReceiptAndDetail): Receipt updated.");
//
//                        List<CloudEntity> entities = new ArrayList<CloudEntity>(details.size());
//
//                        for (Detail detail : details) {
//                            entities.add(detail.getEntity(context));
//                        }
//
//                        backend.insertAll(entities, detailsCallbackHandler);
//                    }
//                }
//
//                @Override
//                public void onDataReceived(List<Receipt> results) {
//
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            dataSource.setDetailCallback(new DataAccessCallbacks<Detail>() {
//                @Override
//                public void onDataProcessed(int processed, List<Detail> dataList, Types.Operation operation, boolean result) {
//                    if (operation == Types.Operation.UPDATE) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "(simpleUploadReceiptAndDetail): Details updated.");
//                    }
//                }
//
//                @Override
//                public void onDataReceived(List<Detail> results) {
//
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            backend.insert(receipt.getEntity(context), receiptCallbackHandler);
//        }
//    }

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

    private static void simpleUploadReceipt(final Receipt receipt, final Context context,
                                            final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final DataAccessCallback<Detail> readDetailsCallback = new DataAccessCallback<Detail>() {
                @Override
                public void onComplete(List<Detail> results, boolean result) {
                    if (results != null) {
                        simpleUploadDetails(results, context, backend);
                    }
                }
            };

            final DataAccessCallback<Receipt> updateReceiptsCallback = new DataAccessCallback<Receipt>() {
                @Override
                public void onComplete(List<Receipt> results, boolean result) {
                    // once the receipt is marked as updated:
                    localDataSource.getDetailsBy(receipt, readDetailsCallback);
                }
            };

            CloudCallbackHandler<CloudEntity> receiptCloudCallbackHandler = new CloudCallbackHandler<CloudEntity>() {
                @Override
                public void onComplete(CloudEntity results) {
                    // Once a receipt is uploaded, check it as "updated" in the DB.
                    receipt.setUpdated(true);

                    List<Receipt> receiptList = new ArrayList<Receipt>(1);
                    receiptList.add(receipt);

                    localDataSource.updateReceipts(receiptList, updateReceiptsCallback);
                }
            };

            backend.insert(receipt.getEntity(context), receiptCloudCallbackHandler);
        }
    }

//    private static void simpleUploadReceipt(final Receipt receipt, final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            // Define the CloudCallbackHandlers for receipt and detail.
//            CloudCallbackHandler<CloudEntity> receiptCallbackHandler = new CloudCallbackHandler<CloudEntity>() {
//                @Override
//                public void onComplete(CloudEntity results) {
//                    // Once a receipt is uploaded, check it as "updated" in the DB.
//                    receipt.setUpdated(true);
//
//                    List<Receipt> receiptList = new ArrayList<Receipt>(1);
//                    receiptList.add(receipt);
//
//                    dataSource.updateReceipts(receiptList);
//                }
//            };
//
//            dataSource.setDetailCallback(new DataAccessCallbacks<Detail>() {
//                @Override
//                public void onDataProcessed(int processed, List<Detail> dataList, Types.Operation operation, boolean result) {
//
//                }
//
//                @Override
//                public void onDataReceived(List<Detail> results) {
//                    if (results != null) {
//                        simpleUploadDetails(results, context, backend);
//                    }
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            dataSource.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
//                @Override
//                public void onDataProcessed(int processed, List<Receipt> dataList, Types.Operation operation, boolean result) {
//                    // once the receipt is marked as updated:
//                    dataSource.getDetailsBy(receipt);
//                }
//
//                @Override
//                public void onDataReceived(List<Receipt> results) {
//
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            backend.insert(receipt.getEntity(context), receiptCallbackHandler);
//        }
//    }

    private static void simpleUploadDetails(final List<Detail> details, Context context,
                                            CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            CloudCallbackHandler<List<CloudEntity>> detailCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    for (Detail detail : details) {
                        detail.setUpdated(true);
                    }

                    localDataSource.updateDetails(details, null);
                }
            };

            List<CloudEntity> entities = new ArrayList<CloudEntity>(details.size());

            for (Detail detail : details) {
                entities.add(detail.getEntity(context));
            }

            backend.insertAll(entities, detailCallbackHandler);
        }
    }

//    private static void simpleUploadDetails(final List<Detail> details, Context context, CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            CloudCallbackHandler<List<CloudEntity>> detailsCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
//                @Override
//                public void onComplete(List<CloudEntity> results) {
//                    for (Detail detail : details) {
//                        detail.setUpdated(true);
//                    }
//
//                    dataSource.updateDetails(details);
//                }
//            };
//
//            List<CloudEntity> entities = new ArrayList<CloudEntity>(details.size());
//
//            for (Detail detail : details) {
//                entities.add(detail.getEntity(context));
//            }
//
//            backend.insertAll(entities, detailsCallbackHandler);
//        }
//    }

    public static void uploadPendingData(Context context, CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Upload pending data started: we proceed to upload pending products.");
            uploadPendingProducts(context, backend);
        }
    }

    private static void uploadPendingProducts(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final DataAccessCallback<Product> updateProductsCallback = new DataAccessCallback<Product>() {
                @Override
                public void onComplete(List<Product> results, boolean result) {
                    if (results != null && results.size() > 0) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Updated " + results.size() + " products (proceeding to upload pending shops).");
                    }
                    uploadPendingShops(context, backend);
                }
            };

            DataAccessCallback<Product> readProductsCallback = new DataAccessCallback<Product>() {
                @Override
                public void onComplete(List<Product> results, boolean result) {
                    if (results != null && results.size() > 0) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Read " + results.size() + " pending products.");

                        final List<Product> productList = results;

                        CloudCallbackHandler<List<CloudEntity>> pendingProductsCH = new CloudCallbackHandler<List<CloudEntity>>() {
                            @Override
                            public void onComplete(List<CloudEntity> results) {
                                if (BuildConfig.DEBUG)
                                    Log.d(TAG, "Uploaded " + results.size() + " products.");

                                for (Product product : productList) {
                                    product.setUpdated(true);
                                }

                                localDataSource.updateProducts(productList, updateProductsCallback);
                            }
                        };

                        List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());
                        for (Product product : results) {
                            entities.add(product.getEntity(context));
                        }

                        backend.insertAll(entities, pendingProductsCH);
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "There were no pending products, we proceed to upload pending shops.");
                        // There aren't pending products, we proceed to upload pending shops.
                        uploadPendingShops(context, backend);
                    }
                }
            };

            localDataSource.listPendingProducts(readProductsCallback);
        }
    }

//    private static void uploadPendingProducts(final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            if (BuildConfig.DEBUG)
//                Log.d(TAG, "Upload pending products started.");
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            // Save current callback.
//            final DataAccessCallbacks<Product> currentCallback = dataSource.getProductCallback();
//
//            dataSource.setProductCallback(new DataAccessCallbacks<Product>() {
//                @Override
//                public void onDataProcessed(int processed, List<Product> dataList, Types.Operation operation, boolean result) {
//                    // Restore previous callback:
//                    dataSource.setProductCallback(currentCallback);
//                    if (dataList != null && dataList.size() > 0) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "Updated " + dataList.size() + " products (proceeding to upload pending shops).");
//                    }
//                    uploadPendingShops(context, backend);
//
//                }
//
//                @Override
//                public void onDataReceived(List<Product> results) {
//                    if (results != null) {
//
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "Read " + results.size() + " pending products.");
//
//                        final List<Product> productList = results;
//                        CloudCallbackHandler<List<CloudEntity>> pendingProductsCH = new CloudCallbackHandler<List<CloudEntity>>() {
//                            @Override
//                            public void onComplete(List<CloudEntity> results) {
//                                if (BuildConfig.DEBUG)
//                                    Log.d(TAG, "Uploaded " + results.size() + " products.");
//
//                                for (Product product : productList) {
//                                    product.setUpdated(true);
//                                }
//
//                                dataSource.updateProducts(productList);
//                            }
//                        };
//
//                        List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());
//                        for (Product product : results) {
//                            entities.add(product.getEntity(context));
//                        }
//
//                        backend.insertAll(entities, pendingProductsCH);
//                    } else {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "There were no pending products, we proceed to upload pending shops.");
//                        // There aren't pending products, we proceed to upload pending shops.
//                        uploadPendingShops(context, backend);
//                    }
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            dataSource.listPendingProducts();
//        }
//    }


    private static void uploadPendingShops(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Upload pending shops started.");

            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final DataAccessCallback<Shop> updateShopsCallback = new DataAccessCallback<Shop>() {
                @Override
                public void onComplete(List<Shop> results, boolean result) {
                    // Once we update as "updated" the uploaded shops, we proceed to upload the pending receipts.
                    if (results != null && results.size() > 0) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Updated " + results.size() + " shops, proceeding to upload pending receipts.");
                    }
                    uploadPendingReceipts(context, backend);
                }
            };

            DataAccessCallback<Shop> readShopsCallback = new DataAccessCallback<Shop>() {
                @Override
                public void onComplete(List<Shop> results, boolean result) {
                    if (results != null) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Read " + results.size() + " pending shops.");
                        final List<Shop> shopList = results;

                        CloudCallbackHandler<List<CloudEntity>> pendingShopsCH = new CloudCallbackHandler<List<CloudEntity>>() {
                            @Override
                            public void onComplete(List<CloudEntity> results) {
                                if (BuildConfig.DEBUG)
                                    Log.d(TAG, "Uploaded " + results.size() + " shops.");
                                for (Shop shop : shopList) {
                                    shop.setUpdated(true);
                                }

                                localDataSource.updateShops(shopList, updateShopsCallback);
                            }
                        };

                        List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());

                        for (Shop shop : results) {
                            entities.add(shop.getEntity(context));
                        }

                        backend.insertAll(entities, pendingShopsCH);
                    } else {
                        // There weren't pending shops. We proceed to upload pending receipts.
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "There were no pending shops, we proceed to upload pending receipts.");
                        uploadPendingReceipts(context, backend);
                    }
                }
            };

            localDataSource.listPendingShops(readShopsCallback);
        }
    }

//    private static void uploadPendingShops(final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            if (BuildConfig.DEBUG)
//                Log.d(TAG, "Upload pending shops started.");
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            dataSource.setShopCallback(new DataAccessCallbacks<Shop>() {
//                @Override
//                public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
//                    // Once we update as "updated" the uploaded shops, we proceed to upload the pending receipts.
//                    if (dataList != null && dataList.size() > 0) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "Updated " + dataList.size() + " shops, proceeding to upload pending receipts.");
//                    }
//                    uploadPendingReceipts(context, backend);
//                }
//
//                @Override
//                public void onDataReceived(List<Shop> results) {
//                    if (results != null) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "Read " + results.size() + " pending shops.");
//                        final List<Shop> shopList = results;
//                        CloudCallbackHandler<List<CloudEntity>> pendingShopsCH = new CloudCallbackHandler<List<CloudEntity>>() {
//                            @Override
//                            public void onComplete(List<CloudEntity> results) {
//                                if (BuildConfig.DEBUG)
//                                    Log.d(TAG, "Uploaded " + results.size() + " shops.");
//                                for (Shop shop : shopList) {
//                                    shop.setUpdated(true);
//                                }
//
//                                dataSource.updateShops(shopList);
//                            }
//                        };
//
//                        List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());
//
//                        for (Shop shop : results) {
//                            entities.add(shop.getEntity(context));
//                        }
//
//                        backend.insertAll(entities, pendingShopsCH);
//                    } else {
//                        // There weren't pending shops. We proceed to upload pending receipts.
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "There were no pending shops, we proceed to upload pending receipts.");
//                        uploadPendingReceipts(context, backend);
//                    }
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            dataSource.listPendingShops();
//        }
//    }


    private static void uploadPendingReceipts(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Upload pending receipts started.");

            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final DataAccessCallback<Receipt> updateReceiptsCallback = new DataAccessCallback<Receipt>() {
                @Override
                public void onComplete(List<Receipt> results, boolean result) {
                    // Once updated the receipts, we proceed to upload pending details.
                    if (results != null && results.size() > 0) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Updated " + results.size() + " receipts, proceeding to upload pending details.");
                    }

                    uploadPendingDetails(context, backend);
                }
            };

            DataAccessCallback<Receipt> readReceiptsCallback = new DataAccessCallback<Receipt>() {
                @Override
                public void onComplete(List<Receipt> results, boolean result) {
                    if (results != null && results.size() > 0) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Read " + results.size() + " pending receipts.");
                        final List<Receipt> receiptList = results;

                        CloudCallbackHandler<List<CloudEntity>> pendingReceiptsCH = new CloudCallbackHandler<List<CloudEntity>>() {
                            @Override
                            public void onComplete(List<CloudEntity> results) {
                                if (BuildConfig.DEBUG)
                                    Log.d(TAG, "Uploaded " + results.size() + " receipts.");
                                for (Receipt receipt : receiptList) {
                                    receipt.setUpdated(true);
                                }

                                localDataSource.updateReceipts(receiptList, updateReceiptsCallback);
                            }
                        };

                        List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());

                        for (Receipt receipt : results) {
                            entities.add(receipt.getEntity(context));
                        }

                        backend.insertAll(entities, pendingReceiptsCH);
                    } else {
                        // There weren't any new receipts, we proceed to upload the pending details,
                        // if any.
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "No pending receipts, we proceed to upload pending details.");
                        uploadPendingDetails(context, backend);
                    }
                }
            };

            localDataSource.listPendingReceipts(readReceiptsCallback);
        }
    }

//    private static void uploadPendingReceipts(final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            if (BuildConfig.DEBUG)
//                Log.d(TAG, "Upload pending receipts started.");
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            dataSource.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
//                @Override
//                public void onDataProcessed(int processed, List<Receipt> dataList, Types.Operation operation, boolean result) {
//                    // Once updated the receipts, we proceed to upload pending details.
//                    if (dataList != null && dataList.size() > 0) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "Updated " + dataList.size() + " receipts, proceeding to upload pending details.");
//                    }
//
//                    uploadPendingDetails(context, backend);
//                }
//
//                @Override
//                public void onDataReceived(List<Receipt> results) {
//                    if (results != null && results.size() > 0) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "Read " + results.size() + " pending receipts.");
//                        final List<Receipt> receiptList = results;
//
//                        CloudCallbackHandler<List<CloudEntity>> pendingReceiptsCH = new CloudCallbackHandler<List<CloudEntity>>() {
//                            @Override
//                            public void onComplete(List<CloudEntity> results) {
//                                if (BuildConfig.DEBUG)
//                                    Log.d(TAG, "Uploaded " + results.size() + " receipts.");
//                                for (Receipt receipt : receiptList) {
//                                    receipt.setUpdated(true);
//                                }
//
//                                dataSource.updateReceipts(receiptList);
//                            }
//                        };
//
//                        List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());
//
//                        for (Receipt receipt : results) {
//                            entities.add(receipt.getEntity(context));
//                        }
//
//                        backend.insertAll(entities, pendingReceiptsCH);
//                    } else {
//                        // There weren't any new receipts, we proceed to upload the pending details,
//                        // if any.
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "No pending receipts, we proceed to upload pending details.");
//                        uploadPendingDetails(context, backend);
//                    }
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            dataSource.listPendingReceipts();
//        }
//    }


    private static void uploadPendingDetails(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Upload pending details started.");

            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final DataAccessCallback<Detail> updateDetailsCallback = new DataAccessCallback<Detail>() {
                @Override
                public void onComplete(List<Detail> results, boolean result) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "Update data chain finish.");
                }
            };

            DataAccessCallback<Detail> readDetailsCallback = new DataAccessCallback<Detail>() {
                @Override
                public void onComplete(List<Detail> results, boolean result) {
                    if (results != null && results.size() > 0) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Read " + results.size() + "pending details.");

                        final List<Detail> detailList = results;

                        CloudCallbackHandler<List<CloudEntity>> pendingDetailsCH = new CloudCallbackHandler<List<CloudEntity>>() {
                            @Override
                            public void onComplete(List<CloudEntity> results) {
                                if (BuildConfig.DEBUG)
                                    Log.d(TAG, "Uploaded " + results.size() + " details.");
                                for (Detail detail : detailList) {
                                    detail.setUpdated(true);
                                }

                                localDataSource.updateDetails(detailList, updateDetailsCallback);
                            }
                        };

                        List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());

                        for (Detail detail : results) {
                            entities.add(detail.getEntity(context));
                        }

                        backend.insertAll(entities, pendingDetailsCH);
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "No pending details. Update data chain finish.");
                    }
                }
            };

            localDataSource.listPendingDetails(readDetailsCallback);
        }
    }

//    private static void uploadPendingDetails(final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            if (BuildConfig.DEBUG)
//                Log.d(TAG, "Upload pending details started.");
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            dataSource.setDetailCallback(new DataAccessCallbacks<Detail>() {
//                @Override
//                public void onDataProcessed(int processed, List<Detail> dataList, Types.Operation operation, boolean result) {
//                    if (BuildConfig.DEBUG)
//                        Log.d(TAG, "Update data chain finish.");
//                }
//
//                @Override
//                public void onDataReceived(List<Detail> results) {
//                    if (results != null && results.size() > 0) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "Read " + results.size() + "pending details.");
//
//                        final List<Detail> detailList = results;
//
//                        CloudCallbackHandler<List<CloudEntity>> pendingDetailsCH = new CloudCallbackHandler<List<CloudEntity>>() {
//                            @Override
//                            public void onComplete(List<CloudEntity> results) {
//                                if (BuildConfig.DEBUG)
//                                    Log.d(TAG, "Uploaded " + results.size() + " details.");
//                                for (Detail detail : detailList) {
//                                    detail.setUpdated(true);
//                                }
//
//                                dataSource.updateDetails(detailList);
//                            }
//                        };
//
//                        List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());
//
//                        for (Detail detail : results) {
//                            entities.add(detail.getEntity(context));
//                        }
//
//                        backend.insertAll(entities, pendingDetailsCH);
//                    } else {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "No pending details. Update data chain finish.");
//                    }
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            dataSource.listPendingDetails();
//        }
//    }

    private static void downloadData(final Context context, final CloudBackendMessaging backend) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Download new data started: we proceed to download new shops.");
        downloadShops(context, backend);
    }


    private static void downloadShops(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Download new shops started.");

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

            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final DataAccessCallback<Shop> insertShopsCallback = new DataAccessCallback<Shop>() {
                @Override
                public void onComplete(List<Shop> results, boolean result) {
                    if (results != null && results.size() > 0) {
                        for (Shop shop : results) {
                            localDataSource.addShopUpdatedInfo(shop);
                            localDataSource.addToUnivIdLocIdMap(shop);
                        }
                    }
                    // once processed all new shops, we can proceed to download new products.
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "New shops inserted, proceeding to download new products.");
                    downloadProducts(context, backend);
                }
            };

            CloudCallbackHandler<List<CloudEntity>> shopCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    if (results != null && results.size() > 0) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "There are " + results.size() + " new shops.");
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
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "In the end we inserted " + shopList.size() + " new shops.");
                            localDataSource.insertShops(shopList, insertShopsCallback);
                        } else {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "In the end there were no new shops (proceeding to download products).");
                            downloadProducts(context, backend);
                        }
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "There were no new shops (proceeding to download new products).");
                        // if there are no new shops, we proceed to download the new products
                        // right away.
                        downloadProducts(context, backend);
                    }

                    // We update the time for the last update.
                    sharedPreferences.edit()
                            .putString(Consts.PREF_SHOPS_LAST_UPDATE, thisUpdate.toStringRfc3339())
                            .commit();
                }
            };
            backend.list(query, shopCallbackHandler);
        }
    }

//    private static void downloadShops(final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            if (BuildConfig.DEBUG)
//                Log.d(TAG, "Download new shops started.");
//
//            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//
//            TimeZone tzUTC = TimeZone.getTimeZone("UTC");
//
//            String strLastUpdated = sharedPreferences.getString(Consts.PREF_SHOPS_LAST_UPDATE, null);
//
//            final String localInstallation = Installation.id(context);
//
//            DateTime lastUpdated;
//            if (strLastUpdated != null) {
//                lastUpdated = DateTime.parseRfc3339(strLastUpdated);
//            } else {
//                lastUpdated = new DateTime(new Date(1), tzUTC);
//            }
//
//            final DateTime thisUpdate = new DateTime(new Date(), tzUTC);
//
//            CloudQuery query = new CloudQuery(Shop.KIND_NAME);
//
//            F filterDate = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdated);
//            query.setFilter(filterDate);
//
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            CloudCallbackHandler<List<CloudEntity>> shopsCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
//                @Override
//                public void onComplete(List<CloudEntity> results) {
//                    if (results != null && results.size() > 0) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "There are " + results.size() + " new shops.");
//                        List<Shop> shopList = new ArrayList<Shop>();
//
//                        String shopUnivId;
//                        Shop shop;
//                        for (CloudEntity entity : results) {
//                            shop = new Shop(entity);
//                            shopUnivId = shop.getUniversalId();
//
//                            if (!shopUnivId.startsWith(localInstallation)) {
//                                shopList.add(shop);
//                            }
//                        }
//                        if (shopList.size() > 0) {
//                            if (BuildConfig.DEBUG)
//                                Log.d(TAG, "In the end we inserted " + shopList.size() + " new shops.");
//                            dataSource.insertShops(shopList);
//                        } else {
//                            if (BuildConfig.DEBUG)
//                                Log.d(TAG, "In the end there were no new shops (proceeding to download products).");
//                            downloadProducts(context, backend);
//                        }
//                    } else {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "There were no new shops (proceeding to download new products).");
//                        // if there are no new shops, we proceed to download the new products
//                        // right away.
//                        downloadProducts(context, backend);
//                    }
//
//                    // We update the time for the last update.
//                    sharedPreferences.edit()
//                            .putString(Consts.PREF_SHOPS_LAST_UPDATE, thisUpdate.toStringRfc3339())
//                            .commit();
//
//                }
//            };
//
//            dataSource.setShopCallback(new DataAccessCallbacks<Shop>() {
//                @Override
//                public void onDataProcessed(int processed, List<Shop> dataList, Types.Operation operation, boolean result) {
//                    if (result && dataList.size() > 0) {
//                        for (Shop shop : dataList) {
//                            dataSource.addShopUpdatedInfo(shop);
//                            dataSource.addToUnivIdLocIdMap(shop);
//                        }
//                    }
//                    // once processed all new shops, we can proceed to download new products.
//                    if (BuildConfig.DEBUG)
//                        Log.d(TAG, "New shops inserted, proceeding to download new products.");
//                    downloadProducts(context, backend);
//                }
//
//                @Override
//                public void onDataReceived(List<Shop> results) {
//
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            backend.list(query, shopsCallbackHandler);
//        }
//    }

    private static void downloadProducts(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Download new products started.");

            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final String installation = Installation.id(context);

            TimeZone tzUTC = TimeZone.getTimeZone("UTC");

            final SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);

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

            final DataAccessCallback<Product> insertProductsCallback = new DataAccessCallback<Product>() {
                @Override
                public void onComplete(List<Product> results, boolean result) {
                    if (results != null && results.size() > 0) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Added products to the UnivId/LocalId map:");
                        for (Product product : results) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "Product univId: " + product.getUniversalId() + ", locId: " + product.getId() + ".");
                            localDataSource.addToUnivIdLocIdMap(product);
                        }
                    }
                    // Now we downloaded and updated all new products, we may proceed to download the
                    // new receipts.
                    downloadReceipts(context, backend);
                }
            };

            CloudCallbackHandler<List<CloudEntity>> productsCallbacHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    if (results.size() > 0) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "There are " + results.size() + " new products.");
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
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "In the end we inserted " + productList.size() + " new products.");
                            localDataSource.insertProducts(productList, insertProductsCallback);
                        } else {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "In the end there were no new products.");
                            downloadReceipts(context, backend);
                        }
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "There were no new products (proceeding to download new receipts).");
                        // If there were no new products, we proceed to download new receipts
                        // right away.
                        downloadReceipts(context, backend);
                    }

                    sharedPreferences.edit()
                            .putString(Consts.PREF_PRODUCTS_LAST_UPDATE, thisUpdate.toStringRfc3339())
                            .commit();
                }
            };

            backend.list(query, productsCallbacHandler);
        }
    }

//    private static void downloadProducts(final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            if (BuildConfig.DEBUG)
//                Log.d(TAG, "Download new products started.");
//
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            final String installation = Installation.id(context);
//
//            TimeZone tzUTC = TimeZone.getTimeZone("UTC");
//
//            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//            String strLastUpdated = sharedPreferences.getString(Consts.PREF_PRODUCTS_LAST_UPDATE, null);
//            DateTime lastUpdated;
//
//            if (strLastUpdated == null) {
//                lastUpdated = new DateTime(new Date(1), tzUTC);
//            } else {
//                lastUpdated = DateTime.parseRfc3339(strLastUpdated);
//            }
//
//            final DateTime thisUpdate = new DateTime(new Date(), tzUTC);
//
//            CloudQuery query = new CloudQuery(Product.KIND_NAME);
//            F filterDate = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdated);
//            query.setFilter(filterDate);
//
//            CloudCallbackHandler<List<CloudEntity>> productsCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
//                @Override
//                public void onComplete(List<CloudEntity> results) {
//                    if (results.size() > 0) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "There are " + results.size() + " new products.");
//                        List<Product> productList = new ArrayList<Product>();
//
//                        Product product;
//                        String univId;
//                        for (CloudEntity entity : results) {
//                            product = new Product(entity);
//                            univId = product.getUniversalId();
//
//                            if (!univId.startsWith(installation)) {
//                                productList.add(product);
//                            }
//                        }
//
//                        if (productList.size() > 0) {
//                            if (BuildConfig.DEBUG)
//                                Log.d(TAG, "In the end we inserted " + productList.size() + " new products.");
//                            dataSource.insertProducts(productList);
//                        } else {
//                            if (BuildConfig.DEBUG)
//                                Log.d(TAG, "In the end there were no new products.");
//                            downloadReceipts(context, backend);
//                        }
//                    } else {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "There were no new products (proceeding to download new receipts).");
//                        // If there were no new products, we proceed to download new receipts
//                        // right away.
//                        downloadReceipts(context, backend);
//                    }
//
//                    sharedPreferences.edit()
//                            .putString(Consts.PREF_PRODUCTS_LAST_UPDATE, thisUpdate.toStringRfc3339())
//                            .commit();
//                }
//            };
//
//            dataSource.setProductCallback(new DataAccessCallbacks<Product>() {
//                @Override
//                public void onDataProcessed(int processed, List<Product> dataList, Types.Operation operation, boolean result) {
//                    if (dataList != null && dataList.size() > 0) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "Added products to the UnivId/LocalId map:");
//                        for (Product product : dataList) {
//                            if (BuildConfig.DEBUG)
//                                Log.d(TAG, "Product univId: " + product.getUniversalId() + ", locId: " + product.getId() + ".");
//                            dataSource.addToUnivIdLocIdMap(product);
//                        }
//                    }
//                    // Now we downloaded and updated all new products, we may proceed to download the
//                    // new receipts.
//                    downloadReceipts(context, backend);
//                }
//
//                @Override
//                public void onDataReceived(List<Product> results) {
//
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            backend.list(query, productsCallbackHandler);
//        }
//    }

    private static void downloadReceipts(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Download new receipts started.");

            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

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

            final DataAccessCallback<Receipt> insertReceiptsCallback = new DataAccessCallback<Receipt>() {
                @Override
                public void onComplete(List<Receipt> results, boolean result) {
                    if (results != null) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "(downloadReceipts): " + results.size() + " receipts inserted.");

                        for (Receipt receipt : results) {
                            localDataSource.addToUnivIdLocIdMap(receipt);
                        }
                    }

                    // Once all new receipts were processed, we proceed to download new details.
                    downloadDetails(context, backend);
                }
            };

            CloudCallbackHandler<List<CloudEntity>> receiptCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    if (results.size() > 0) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "There are " + results.size() + " new receipts");
                        List<Receipt> receiptList = new ArrayList<Receipt>();

                        Receipt receipt;
                        String univId;
                        String shopUnivId;
                        long shopLocalId;

                        for (CloudEntity entity : results) {
                            receipt = new Receipt(entity);
                            univId = receipt.getUniversalId();

                            if (!univId.startsWith(installation)) {
                                shopUnivId = receipt.getShopUnivId();
                                shopLocalId = localDataSource.getShopLocIdFromUnivId(shopUnivId);
                                receipt.setShopId(shopLocalId);

                                receiptList.add(receipt);
                            }
                        }

                        if (receiptList.size() > 0) {
                            // We will wait til the receipts are updated and then proceed to
                            // download the details.
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "In the end we inserted " + receiptList.size() + " new receipts.");
                            localDataSource.insertReceipts(receiptList, insertReceiptsCallback);
                        } else {
                            // finally there were no receipts to insert, but we need to check for
                            // new details anyway.
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "In the end there were no new receipts (proceeding to download details).");
                            downloadDetails(context, backend);
                        }
                    } else {
                        // if no new receipts, shouldn't be any new detail. Anyway we follow the chain
                        // and proceed to download new details.
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "There were not new receipts (proceeding to download new details).");
                        downloadDetails(context, backend);
                    }
                    sharedPreferences.edit()
                            .putString(Consts.PREF_RECEIPTS_LAST_UPDATE, thisUpdate.toStringRfc3339())
                            .commit();
                }
            };

            backend.list(query, receiptCallbackHandler);
        }
    }

//    private static void downloadReceipts(final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            if (BuildConfig.DEBUG)
//                Log.d(TAG, "Download new receipts started.");
//            final DataSource dataSource = DataSource.getInstance(context);
//
//            TimeZone tzUTC = TimeZone.getTimeZone("UTC");
//
//            final String installation = Installation.id(context);
//
//            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//            String strLastUpdated = sharedPreferences.getString(Consts.PREF_RECEIPTS_LAST_UPDATE, null);
//            DateTime lastUpdated;
//
//            if (strLastUpdated == null) {
//                lastUpdated = new DateTime(new Date(1), tzUTC);
//            } else {
//                lastUpdated = DateTime.parseRfc3339(strLastUpdated);
//            }
//
//            final DateTime thisUpdate = new DateTime(new Date(), tzUTC);
//
//            CloudQuery query = new CloudQuery(Receipt.KIND_NAME);
//            F filterDate = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdated);
//            query.setFilter(filterDate);
//
//            CloudCallbackHandler<List<CloudEntity>> receiptCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
//                @Override
//                public void onComplete(List<CloudEntity> results) {
//                    if (results.size() > 0) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "There are " + results.size() + " new receipts");
//                        List<Receipt> receiptList = new ArrayList<Receipt>();
//
//                        Receipt receipt;
//                        String univId;
//                        String shopUnivId;
//                        long shopLocalId;
//
//                        for (CloudEntity entity : results) {
//                            receipt = new Receipt(entity);
//                            univId = receipt.getUniversalId();
//
//                            if (!univId.startsWith(installation)) {
//                                shopUnivId = receipt.getShopUnivId();
//                                shopLocalId = dataSource.getShopLocIdFromUnivId(shopUnivId);
//                                receipt.setShopId(shopLocalId);
//
//                                receiptList.add(receipt);
//                            }
//                        }
//
//                        if (receiptList.size() > 0) {
//                            // We will wait til the receipts are updated and then proceed to
//                            // download the details.
//                            if (BuildConfig.DEBUG)
//                                Log.d(TAG, "In the end we inserted " + receiptList.size() + " new receipts.");
//                            dataSource.insertReceipts(receiptList);
//                        } else {
//                            // finally there were no receipts to insert, but we need to check for
//                            // new details anyway.
//                            if (BuildConfig.DEBUG)
//                                Log.d(TAG, "In the end there were no new receipts (proceeding to download details).");
//                            downloadDetails(context, backend);
//                        }
//                    } else {
//                        // if no new receipts, shouldn't be any new detail. Anyway we follow the chain
//                        // and proceed to download new details.
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "There were not new receipts (proceeding to download new details).");
//                        downloadDetails(context, backend);
//                    }
//                    sharedPreferences.edit()
//                            .putString(Consts.PREF_RECEIPTS_LAST_UPDATE, thisUpdate.toStringRfc3339())
//                            .commit();
//                }
//            };
//
//            dataSource.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
//                @Override
//                public void onDataProcessed(int processed, List<Receipt> dataList, Types.Operation operation, boolean result) {
//                    for (Receipt receipt : dataList) {
//                        dataSource.addToUnivIdLocIdMap(receipt);
//                    }
//
//                    // Once all new receipts were processed, we proceed to download new details.
//                    downloadDetails(context, backend);
//                }
//
//                @Override
//                public void onDataReceived(List<Receipt> results) {
//
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            backend.list(query, receiptCallbackHandler);
//        }
//    }

    private static void downloadDetails(final Context context, final CloudBackendMessaging backend) {
        if (hasConnectivity(context)) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Download new details started.");

            final LocalDataSource localDataSource = LocalDataSource.getInstance(context);

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            TimeZone tzUTC = TimeZone.getTimeZone("UTC");
            String strLastUpdated = sharedPreferences.getString(Consts.PREF_DETAILS_LAST_UPDATE, null);
            DateTime lastUpdated;

            if (strLastUpdated == null) {
                lastUpdated = new DateTime(new Date(1), tzUTC);
            } else {
                lastUpdated = DateTime.parseRfc3339(strLastUpdated);
            }

            final DateTime thisUpdate = new DateTime(new Date(), tzUTC);

            CloudQuery query = new CloudQuery(Detail.KIND_NAME);
            F filterDate = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdated);
            query.setFilter(filterDate);

            final DataAccessCallback<Detail> insertDetailsCallback = new DataAccessCallback<Detail>() {
                @Override
                public void onComplete(List<Detail> results, boolean result) {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "Details inserted in the DB, proceeding to upload pending data.");
                    uploadPendingData(context, backend);
                }
            };

            CloudCallbackHandler<List<CloudEntity>> detailCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
                @Override
                public void onComplete(List<CloudEntity> results) {
                    if (results.size() > 0) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "There are " + results.size() + " new details.");

                        String localInstallation = Installation.id(context);
                        String univId;

                        String receiptUnivId;
                        long receiptLocalId;

                        String productUnivId;
                        long productLocalId;

                        Detail detail;
                        List<Detail> detailList = new ArrayList<Detail>();
                        for (CloudEntity entity : results) {
                            detail = new Detail(entity);
                            univId = detail.getUniversalId();


                            if (!univId.startsWith(localInstallation)) {
                                receiptUnivId = detail.getReceiptUnivId();
                                receiptLocalId = localDataSource.getReceiptLocIdFromUnivId(receiptUnivId);
                                detail.setReceiptId(receiptLocalId);

                                productUnivId = detail.getProductUnivId();
                                productLocalId = localDataSource.getProductLocIdFromUnivId(productUnivId);
                                detail.setProductId(productLocalId);

                                detailList.add(detail);
                            }
                        }
                        if (detailList.size() > 0) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "In the end there were " + detailList.size() + " new details.");
                            localDataSource.insertDetails(detailList, insertDetailsCallback);
                        } else {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "In the end there were no new details (proceeding to upload pending data).");
                            uploadPendingData(context, backend);
                        }
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "There were no new details (proceeding to upload pending data).");
                        // in any case, we proceed to upload the pending data.
                        uploadPendingData(context, backend);
                    }
                    sharedPreferences.edit()
                            .putString(Consts.PREF_DETAILS_LAST_UPDATE, thisUpdate.toStringRfc3339())
                            .commit();
                }
            };

            backend.list(query, detailCallbackHandler);
        }
    }

//    private static void downloadDetails(final Context context, final CloudBackendMessaging backend) {
//        if (hasConnectivity(context)) {
//            if (BuildConfig.DEBUG)
//                Log.d(TAG, "Download new details started.");
//            final DataSource dataSource = DataSource.getInstance(context);
//
//
//            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//
//            TimeZone tzUTC = TimeZone.getTimeZone("UTC");
//            String strLastUpdated = sharedPreferences.getString(Consts.PREF_DETAILS_LAST_UPDATE, null);
//            DateTime lastUpdated;
//
//            if (strLastUpdated == null) {
//                lastUpdated = new DateTime(new Date(1), tzUTC);
//            } else {
//                lastUpdated = DateTime.parseRfc3339(strLastUpdated);
//            }
//
//            final DateTime thisUpdate = new DateTime(new Date(), tzUTC);
//
//            CloudQuery query = new CloudQuery(Detail.KIND_NAME);
//            F filterDate = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdated);
//            query.setFilter(filterDate);
//
//            CloudCallbackHandler<List<CloudEntity>> detailsCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {
//                @Override
//                public void onComplete(List<CloudEntity> results) {
//                    if (results.size() > 0) {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "There are " + results.size() + " new details.");
//
//                        String localInstallation = Installation.id(context);
//                        String univId;
//
//                        String receiptUnivId;
//                        long receiptLocalId;
//
//                        String productUnivId;
//                        long productLocalId;
//
//                        Detail detail;
//                        List<Detail> detailList = new ArrayList<Detail>();
//                        for (CloudEntity entity : results) {
//                            detail = new Detail(entity);
//                            univId = detail.getUniversalId();
//
//
//                            if (!univId.startsWith(localInstallation)) {
//                                receiptUnivId = detail.getReceiptUnivId();
//                                receiptLocalId = dataSource.getReceiptLocIdFromUnivId(receiptUnivId);
//                                detail.setReceiptId(receiptLocalId);
//
//                                productUnivId = detail.getProductUnivId();
//                                productLocalId = dataSource.getProductLocIdFromUnivId(productUnivId);
//                                detail.setProductId(productLocalId);
//
//                                detailList.add(detail);
//                            }
//                        }
//                        if (detailList.size() > 0) {
//                            if (BuildConfig.DEBUG)
//                                Log.d(TAG, "In the end there were " + detailList.size() + " new details.");
//                            dataSource.insertDetails(detailList);
//                        } else {
//                            if (BuildConfig.DEBUG)
//                                Log.d(TAG, "In the end there were no new details (proceeding to upload pending data).");
//                            uploadPendingData(context, backend);
//                        }
//                    } else {
//                        if (BuildConfig.DEBUG)
//                            Log.d(TAG, "There were no new details (proceeding to upload pending data).");
//                        // in any case, we proceed to upload the pending data.
//                        uploadPendingData(context, backend);
//                    }
//                    sharedPreferences.edit()
//                            .putString(Consts.PREF_DETAILS_LAST_UPDATE, thisUpdate.toStringRfc3339())
//                            .commit();
//                }
//            };
//
//            dataSource.setDetailCallback(new DataAccessCallbacks<Detail>() {
//                @Override
//                public void onDataProcessed(int processed, List<Detail> dataList, Types.Operation operation, boolean result) {
//                    if (BuildConfig.DEBUG)
//                        Log.d(TAG, "Details inserted in the DB, proceeding to upload pending data.");
//                    uploadPendingData(context, backend);
//                }
//
//                @Override
//                public void onDataReceived(List<Detail> results) {
//
//                }
//
//                @Override
//                public void onInfoReceived(Object result, AsyncStatement.Option option) {
//
//                }
//            });
//
//            backend.list(query, detailsCallbackHandler);
//        }
//    }
}
