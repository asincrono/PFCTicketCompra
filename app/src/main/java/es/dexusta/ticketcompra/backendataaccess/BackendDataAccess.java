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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import es.dexusta.ticketcompra.BuildConfig;
import es.dexusta.ticketcompra.Consts;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Shop;

public class BackendDataAccess {
    private static final boolean DEBUG = true;
    private static final String  TAG   = "BackendDataAccess";

//    }

    public static boolean hasConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Deprecated
    public static void downloadShops(Context context, CloudBackendMessaging backend, boolean chain) {
        TimeZone tzUTC = TimeZone.getTimeZone("UTC");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String lastUpdateStr = sp.getString(Consts.PREF_SHOPS_LAST_UPDATE, null);

        DateTime lastUpdate;
        if (lastUpdateStr != null) {
            lastUpdate = DateTime.parseRfc3339(lastUpdateStr);
        } else {
            lastUpdate = new DateTime(new Date(1), tzUTC);
        }

        DateTime thisUpdate = new DateTime(new Date(), tzUTC);

        CloudQuery query = new CloudQuery(Shop.KIND_NAME);

        F filterDate = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdate);

        query.setFilter(filterDate);

        DownloadShopsCallbackHandler handler = new DownloadShopsCallbackHandler(context,
                thisUpdate, backend, chain);

        backend.list(query, handler);
    }

    @Deprecated
    private static void uploadShop(final Shop shop, final Context context,
                                  CloudBackendMessaging backend, final CloudCallbackHandler<CloudEntity> handler) {

        final DataSource ds = DataSource.getInstance(context);
        final DataAccessCallbacks<Shop> callback = null;

        CloudCallbackHandler<CloudEntity> callbackHandler = new CloudCallbackHandler<CloudEntity>() {

            @Override
            public void onComplete(CloudEntity result) {

                if (DEBUG) Log.d(TAG, "(uploadShop) One shop uploaded");

                ds.setShopCallback(new DataAccessCallbacks<Shop>() {

                    @Override
                    public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                                                boolean result) {
                        if (DEBUG) Log.d(TAG, "shop updated");
                        ds.addShopUpdatedInfo(shop);

                        if (handler != null) handler.onComplete(dataList.get(0).getEntity(context));
                    }

                    @Override
                    public void onDataReceived(List<Shop> results) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onInfoReceived(Object result, Option option) {
                        // TODO Auto-generated method stub

                    }
                });

                shop.setUpdated(true);
                List<Shop> shops = new ArrayList<Shop>(1);
                shops.add(shop);

                ds.updateShops(shops);
            }
        };

        backend.insert(shop.getEntity(context), callbackHandler);
    }

    public static void uploadPendingShops(final Context context,
                                          final CloudBackendMessaging backend, final boolean chain) {
        final DataSource ds = DataSource.getInstance(context);

        ds.setShopCallback(new DataAccessCallbacks<Shop>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Shop> results) {
                if (results != null) {
                    List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());
                    for (Shop shop : results) {
                        entities.add(shop.getEntity(context));
                    }
                    UploadShopsCallbackHandler handler = new UploadShopsCallbackHandler(results,
                            context, backend, chain);
                    backend.insertAll(entities, handler);
                } else if (chain) {
                    uploadPendingProducts(context, backend, true);
                }
            }

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                                        boolean result) {
                // TODO Auto-generated method stub
            }
        });

        ds.listPendingShops();
    }

    private static void downloadNewProducts(Context context, CloudBackendMessaging backend,
                                           boolean chain) {
        TimeZone tzUTC = TimeZone.getTimeZone("UTC");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String lastUpdateStr = sp.getString(Consts.PREF_PRODUCTS_LAST_UPDATE, null);

        DateTime lastUpdate;
        if (lastUpdateStr == null) {
            lastUpdate = new DateTime(new Date(1), tzUTC);
        } else {
            lastUpdate = DateTime.parseRfc3339(lastUpdateStr);
        }

        DateTime thisUpdate = new DateTime(new Date(), tzUTC);

        CloudQuery query = new CloudQuery(Product.KIND_NAME);

        F filterDate = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdate);

        query.setFilter(filterDate);

        DownloadProductsCallbackHandler handler = new DownloadProductsCallbackHandler(context,
                thisUpdate, backend, chain);

        backend.list(query, handler);
    }

    private static void uploadProduct(final Product product,
                                     final Context context, CloudBackendMessaging backend,
                                     final CloudCallbackHandler<CloudEntity> handler) {

        final DataSource ds = DataSource.getInstance(context);
        ds.setProductCallback(new DataAccessCallbacks<Product>() {
            @Override
            public void onDataProcessed(int processed, List<Product> dataList, Operation operation, boolean result) {
                if (handler != null) handler.onComplete(dataList.get(0).getEntity(context));
            }

            @Override
            public void onDataReceived(List<Product> results) {

            }

            @Override
            public void onInfoReceived(Object result, Option option) {

            }
        });

        CloudCallbackHandler<CloudEntity> callbackHandler = new CloudCallbackHandler<CloudEntity>() {

            @Override
            public void onComplete(CloudEntity result) {
                if (DEBUG) Log.d(TAG, "One product uploaded");

                product.setUpdated(true);
                List<Product> productList = new ArrayList<Product>(1);
                productList.add(product);
                ds.updateProducts(productList);
            }
        };

        backend.insert(product.getEntity(context), callbackHandler);
    }

    public static void uploadProducts(List<Product> products, Context context,
                                      CloudBackendMessaging backend) {
        List<CloudEntity> entities = new ArrayList<CloudEntity>(products.size());
        for (Product product : products) {
            entities.add(product.getEntity(context));
        }

        UploadPendingProductsCallbackHandler handler = new UploadPendingProductsCallbackHandler(products,
                context, null, false);
        backend.insertAll(entities, handler);
    }

    //    public static void upload(final ReplicatedDBObject repDBObject, final Context context,
//            CloudBackendMessaging backend) {
//        CloudEntity entity = repDBObject.getEntity(context);
//
//        CloudCallbackHandler<CloudEntity> handler = new CloudCallbackHandler<CloudEntity>() {
//
//            @Override
//            public void onComplete(CloudEntity results) {
//                if (DEBUG) Log.d(TAG, repDBObject.getKindName() + " uploaded");
//                repDBObject.setUpdated(true);
//                DataSource.getInstance(context).simpleInsert(repDBObject);
//            }
//        };
//
//        backend.insert(entity, handler);


    private static void uploadPendingProducts(final Context context, final CloudBackendMessaging backend,
                                             final boolean chain) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Call to uploadPendingProducts.");

        DataAccessCallbacks<Product> productCallbacks = new DataAccessCallbacks<Product>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Product> results) {
                if (results != null) {
                    List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());
                    for (Product product : results) {
                        entities.add(product.getEntity(context));
                    }

                    UploadPendingProductsCallbackHandler handler = new UploadPendingProductsCallbackHandler(
                            results, context, backend, chain);
                    backend.insertAll(entities, handler);
                } else if (chain) {
                    uploadPendingReceipts(context, backend, true);
                }
            }

            @Override
            public void onDataProcessed(int processed, List<Product> dataList, Operation operation,
                                        boolean result) {
                // TODO Auto-generated method stub

            }
        };

        DataSource ds = DataSource.getInstance(context);
        ds.setProductCallback(productCallbacks);
        ds.listPendingProducts();
    }

    private static void downloadReceipts(Context context, CloudBackendMessaging backend,
                                        boolean chain) {
        TimeZone tzUTC = TimeZone.getTimeZone("UTC");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String lastUpdateStr = sp.getString(Consts.PREF_RECEIPTS_LAST_UPDATE, null);

        DateTime lastUpdate;
        if (lastUpdateStr == null) {
            lastUpdate = new DateTime(new Date(1), tzUTC);
        } else {
            lastUpdate = DateTime.parseRfc3339(lastUpdateStr);
        }

        DateTime thisUpdate = new DateTime(new Date(), tzUTC);

        CloudQuery query = new CloudQuery(Receipt.KIND_NAME);

        F filterCreatedBy = F.eq(CloudEntity.PROP_CREATED_BY, backend.getCredential()
                .getSelectedAccountName());
        F filterCreatedAt = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdate);

        F filter = F.and(filterCreatedBy, filterCreatedAt);

        query.setFilter(filter);

        DownloadReceiptsCallbackHandler handler = new DownloadReceiptsCallbackHandler(context,
                thisUpdate, backend, chain);

        backend.list(query, handler);
    }

    private static void uploadReceiptDetails(final Receipt receipt, final List<Detail> details,
                                            final Context context, final CloudBackendMessaging backend) {
        // Se considera que el receipt está completo (tiene id ya que fue insertado en la BD.
        // Se considera que la lista de details es completa (tienen id y receipt_id porque fueron
        // insertados en la BD.
        // Simplemente se encadena la inserción de ambos (receipt y lista de details) en el backend.

        // Comprobar si ya tenemos subida la shop asociada al receipt.
        final DataSource ds = DataSource.getInstance(context);

        if (ds.isShopUpdated(receipt.getShopId())) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "La tienda ya estaba actualizada.");

            CloudCallbackHandler<CloudEntity> callbackHandler = new CloudCallbackHandler<CloudEntity>() {
                @Override
                public void onComplete(CloudEntity results) {
                    // We need to mark the receipt as updated.

                    ds.setReceiptCallback(new DataAccessCallbacks<Receipt>() {
                        @Override
                        public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation, boolean result) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "el receipt está marcado como \"updated\".");
                            // Upload details after upload receipt.
                            uploadDetails(details, context, backend);
                        }

                        @Override
                        public void onDataReceived(List<Receipt> results) {

                        }

                        @Override
                        public void onInfoReceived(Object result, Option option) {

                        }
                    });

                    receipt.setUpdated(true);

                    List<Receipt> list = new ArrayList<Receipt>(1);
                    list.add(receipt);

                    ds.updateReceipts(list);
                }
            };

            backend.insert(receipt.getEntity(context), callbackHandler);
        } else {

            if (BuildConfig.DEBUG)
                Log.d(TAG, "La tienda no estaba actualizada.");
            ds.setShopCallback(new DataAccessCallbacks<Shop>() {
                @Override
                public void onDataProcessed(int processed, List<Shop> dataList, Operation operation, boolean result) {

                }

                @Override
                public void onDataReceived(List<Shop> results) {
                    // We need to upload the shop before be able to upload receipt and details.

                    uploadShopReceiptDetails(results.get(0), receipt, details, context, backend);
                }

                @Override
                public void onInfoReceived(Object result, Option option) {

                }
            });

            ds.getShop(receipt.getShopId());
        }
    }

    private static void uploadShopReceiptDetails(Shop shop, final Receipt receipt, final List<Detail> details,
                                                final Context context, final CloudBackendMessaging backend) {
        CloudCallbackHandler<CloudEntity> handler = new CloudCallbackHandler<CloudEntity>() {
            @Override
            public void onComplete(CloudEntity result) {
                uploadReceiptDetails(receipt, details, context, backend);
            }
        };

        uploadShop(shop, context, backend, handler);
    }


    private static void uploadPendingReceipts(final Context context, final CloudBackendMessaging backend,
                                             final boolean chain) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Call to uploadPendingReceipts.");

        DataAccessCallbacks<Receipt> receiptDataAccessCallbacks = new DataAccessCallbacks<Receipt>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Receipt> results) {
                if (results != null) {
                    List<CloudEntity> entities = new ArrayList<CloudEntity>();
                    for (Receipt receipt : results) {
                        entities.add(receipt.getEntity(context));
                    }

                    UploadPendingReceiptsCallbackHandler handler = new UploadPendingReceiptsCallbackHandler(
                            results, context, backend, chain);
                    backend.insertAll(entities, handler);
                } else if (chain) {
                    uploadPendingDetails(context, backend);
                }
            }

            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                                        boolean result) {
                // TODO Auto-generated method stub

            }
        };

        DataSource ds = DataSource.getInstance(context);
        ds.setReceiptCallback(receiptDataAccessCallbacks);
        ds.listPendingReceipts();
    }

    private static void downloadDetails(Context context, CloudBackendMessaging backend) {
        TimeZone tzUTC = TimeZone.getTimeZone("UTC");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        DateTime thisUpdate = new DateTime(new Date(), tzUTC);
        DateTime lastUpdate;
        String lastUpdateStr = sp.getString(Consts.PREF_DETAILS_LAST_UPDATE, null);
        if (lastUpdateStr == null) {
            lastUpdate = new DateTime(new Date(1), tzUTC);
        } else {
            lastUpdate = DateTime.parseRfc3339(lastUpdateStr);
        }

        CloudQuery query = new CloudQuery(Detail.KIND_NAME);

        F filterCreatedBy = F.eq(CloudEntity.PROP_CREATED_BY, backend.getCredential()
                .getSelectedAccountName());
        F filterCreatedAt = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdate);

        F filter = F.and(filterCreatedBy, filterCreatedAt);

        query.setFilter(filter);

        DownloadDetailsCallbackHandler handler = new DownloadDetailsCallbackHandler(context,
                thisUpdate, backend);

        backend.list(query, handler);
    }

    private static void uploadDetails(final List<Detail> details, Context context,
                                     CloudBackendMessaging backend) {

        if (BuildConfig.DEBUG)
            Log.d(TAG, "Call to uploadDetails.");

        List<CloudEntity> entities = new ArrayList<CloudEntity>();
        for (Detail detail : details) {
            entities.add(detail.getEntity(context));
        }

        final DataSource ds = DataSource.getInstance(context);

        CloudCallbackHandler<List<CloudEntity>> handler = new CloudCallbackHandler<List<CloudEntity>>() {
            @Override
            public void onComplete(List<CloudEntity> results) {
                for (Detail detail : details) {
                    detail.setUpdated(true);
                }

                ds.setDetailCallback(new DataAccessCallbacks<Detail>() {
                    @Override
                    public void onDataProcessed(int processed, List<Detail> dataList, Operation operation, boolean result) {
                        if (operation == Operation.UPDATE) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, dataList.size() + " detail(s) marked as updated.");
                        }
                        ds.listPendingDetails();
                    }

                    @Override
                    public void onDataReceived(List<Detail> results) {
                        if (results != null) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, results.size() + " details still marked as not updated.");
                        } else {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "No pending detials.");
                        }
                    }

                    @Override
                    public void onInfoReceived(Object result, Option option) {

                    }
                });

                ds.updateDetails(details);
            }
        };

        backend.insertAll(entities, handler);
    }

    private static void uploadPendingDetails(final Context context, final CloudBackendMessaging backend) {

        if (BuildConfig.DEBUG)
            Log.d(TAG, "Call to uploadPendingDetails.");

        DataAccessCallbacks<Detail> detailCallbacks = new DataAccessCallbacks<Detail>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Detail> results) {
                if (results != null) {
                    List<CloudEntity> entities = new ArrayList<CloudEntity>(results.size());


                    for (Detail detail : results) {
                        entities.add(detail.getEntity(context));
                    }

                    UploadPendingDetailsCallbackHandler handler = new UploadPendingDetailsCallbackHandler(
                            results, context);
                    backend.insertAll(entities, handler);
                }
            }

            @Override
            public void onDataProcessed(int processed, List<Detail> dataList, Operation operation,
                                        boolean result) {
                // TODO Auto-generated method stub

            }
        };

        DataSource ds = DataSource.getInstance(context);
        ds.setDetailCallback(detailCallbacks);
        ds.listPendingDetails();
    }
}
