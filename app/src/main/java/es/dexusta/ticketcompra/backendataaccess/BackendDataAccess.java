package es.dexusta.ticketcompra.backendataaccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

import es.dexusta.ticketcompra.Consts;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.model.Total;

public class BackendDataAccess {
    private static final boolean DEBUG = true;
    private static final String  TAG   = "BackendDataAccess";

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
//    }

    public static boolean hasConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();        
    }
    
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

    public static void uploadShop(final Shop shop, final Context context,
            CloudBackendMessaging backend) {
        final DataSource ds = DataSource.getInstance(context);
        final DataAccessCallbacks<Shop> callback = new DataAccessCallbacks<Shop>() {

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                    boolean result) {
                if (DEBUG) Log.d(TAG, "shop updated");
            }

            @Override
            public void onDataReceived(List<Shop> results) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub
                
            }
        };

        CloudCallbackHandler<CloudEntity> callbackHandler = new CloudCallbackHandler<CloudEntity>() {

            @Override
            public void onComplete(CloudEntity result) {
                if (DEBUG) Log.d(TAG, "one shop uploaded");
                shop.setUpdated(true);
                List<Shop> shops = new ArrayList<Shop>();
                shops.add(shop);
                ds.setShopCallback(callback);
                ds.updateShops(shops);
            }
        };

        backend.insert(shop.getEntity(context), callbackHandler);
    }

    public static void uploadPendingShops(final Context context,
            final CloudBackendMessaging backend, final boolean chain) {
        DataAccessCallbacks<Shop> shopCallbacks = new DataAccessCallbacks<Shop>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Shop> results) {
                if (results != null) {
                    List<CloudEntity> entities = new ArrayList<CloudEntity>();
                    for (Shop shop : results) {
                        entities.add(shop.getEntity(context));
                    }
                    UploadShopsCallbackHandler handler = new UploadShopsCallbackHandler(results,
                            context, backend, chain);
                    backend.insertAll(entities, handler);
                } else if (chain) {
                    uploadPendingProducts(context, backend, chain);
                }
            }

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        };

        DataSource ds = DataSource.getInstance(context);
        ds.setShopCallback(shopCallbacks);
        ds.listPendingShops();
    }

    public static void downloadNewProducts(Context context, CloudBackendMessaging backend,
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

    public static void uploadProduct(Product product, Context context, CloudBackendMessaging backend) {
        CloudCallbackHandler<CloudEntity> callbackHandler = new CloudCallbackHandler<CloudEntity>() {
            
            @Override
            public void onComplete(CloudEntity results) {
                if (DEBUG) Log.d(TAG, "one product uploaded");
            }
        };
        
        backend.insert(product.getEntity(context), callbackHandler);
    }
    
    public static void uploadProducts(List<Product> products, Context context,
            CloudBackendMessaging backend) {
        List<CloudEntity> entities = new ArrayList<CloudEntity>();
        for (Product product : products) {
            entities.add(product.getEntity(context));
        }

        UploadProductsCallbackHandler handler = new UploadProductsCallbackHandler(products,
                context, null, false);
        backend.insertAll(entities, handler);
    }

    public static void uploadPendingProducts(final Context context, final CloudBackendMessaging backend,
            final boolean chain) {
        DataAccessCallbacks<Product> productCallbacks = new DataAccessCallbacks<Product>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Product> results) {
                if (results != null) {
                    List<CloudEntity> entities = new ArrayList<CloudEntity>();
                    for (Product product : results) {
                        entities.add(product.getEntity(context));
                    }

                    UploadProductsCallbackHandler handler = new UploadProductsCallbackHandler(
                            results, context, backend, chain);
                    backend.insertAll(entities, handler);
                } else if (chain) {
                    uploadPendingReceipts(context, backend, chain);
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

    public static void downloadReceipts(Context context, CloudBackendMessaging backend,
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
    
    public static void uploadReceipt(Receipt receipt, Context context, CloudBackendMessaging backend) {
        CloudCallbackHandler<CloudEntity> callbackHandler = new CloudCallbackHandler<CloudEntity>() {

            @Override
            public void onComplete(CloudEntity results) {
                if (DEBUG) Log.d(TAG, "one receipt uploaded");
            }
            
        };
        
        backend.insert(receipt.getEntity(context), callbackHandler);
    }

    public static void uploadReceipts(List<Receipt> receipts, Context context,
            CloudBackendMessaging backend) {
        List<CloudEntity> entities = new ArrayList<CloudEntity>();
        for (Receipt receipt : receipts) {
            entities.add(receipt.getEntity(context));
        }

        UploadReceiptsCallbackHandler handler = new UploadReceiptsCallbackHandler(receipts,
                context, null, false);
        backend.insertAll(entities, handler);
    }

    public static void uploadReceiptDetails(final Receipt receipt, final List<Detail> details,
            final Context context, final CloudBackendMessaging backend) {
        // Se considera que el receipt está completo (tiene id ya que fue insertado en la BD.
        // Se considera que la lista de details es completa (tienen id y receipt_id porque fueron
        // insertados en la BD.
        // Simplemente se encadena la inserción de ambos (receipt y lista de details) en el backend.
        
    }

    public static void uploadPendingReceipts(final Context context, final CloudBackendMessaging backend,
            final boolean chain) {
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

                    UploadReceiptsCallbackHandler handler = new UploadReceiptsCallbackHandler(
                            results, context, backend, chain);
                    backend.insertAll(entities, handler);
                } else if (chain) {
                    uploadTotals(context, backend, chain);
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

    public static void downloadTotals(Context context, CloudBackendMessaging backend, boolean chain) {
        TimeZone tzUTC = TimeZone.getTimeZone("UTC");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String lastUpdateStr = sp.getString(Consts.PREF_TOTALS_LAST_UPDATE, null);

        DateTime lastUpdate;
        if (lastUpdateStr == null) {
            lastUpdate = new DateTime(new Date(1), tzUTC);
        } else {
            lastUpdate = DateTime.parseRfc3339(lastUpdateStr);
        }

        DateTime thisUpdate = new DateTime(new Date(), tzUTC);

        CloudQuery query = new CloudQuery(Total.KIND_NAME);

        F filterCreateAt = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdate);
        F filterCreateBy = F.eq(CloudEntity.PROP_CREATED_BY, backend.getCredential()
                .getSelectedAccountName());

        F filter = F.and(filterCreateBy, filterCreateAt);

        query.setFilter(filter);

        DownloadTotalsCallbackHandler handler = new DownloadTotalsCallbackHandler(context,
                thisUpdate, backend, chain);

        backend.list(query, handler);
    }
    
    public static void uploadTotal(final Total total, final Context context, CloudBackendMessaging backend) {
        final DataSource ds = DataSource.getInstance(context);
        final DataAccessCallbacks<Total> callback = new DataAccessCallbacks<Total>() {

            @Override
            public void onDataProcessed(int processed, List<Total> dataList, Operation operation,
                    boolean result) {
                if (DEBUG) Log.d(TAG, "total updated");                
            }

            @Override
            public void onDataReceived(List<Total> results) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub
                
            }
        };
        CloudCallbackHandler<CloudEntity> callbackHandler = new CloudCallbackHandler<CloudEntity>() {

            @Override
            public void onComplete(CloudEntity results) {
                
                total.setUpdated(true);
                List<Total> totals = new ArrayList<Total>();
                totals.add(total);
                ds.setTotalCallback(callback);        
                ds.updateTotals(totals);
                if (DEBUG) Log.d(TAG, "one total inserted");
            }
        };
        
        backend.insert(total.getEntity(context), callbackHandler);
    }

    public static void uploadTotals(List<Total> totals, Context context,
            CloudBackendMessaging backend) {
        List<CloudEntity> entities = new ArrayList<CloudEntity>();
        for (Total total : totals) {
            entities.add(total.getEntity(context));
        }

        UploadTotalsCallbackHandler handler = new UploadTotalsCallbackHandler(totals, context,
                null, false);

        backend.insertAll(entities, handler);
    }

    public static void uploadTotals(final Context context, final CloudBackendMessaging backend,
            final boolean chain) {
        DataAccessCallbacks<Total> totalCallbacks = new DataAccessCallbacks<Total>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Total> results) {
                if (results != null) {
                    List<CloudEntity> entities = new ArrayList<CloudEntity>();
                    for (Total total : results) {
                        entities.add(total.getEntity(context));
                    }

                    UploadTotalsCallbackHandler handler = new UploadTotalsCallbackHandler(results,
                            context, backend, chain);
                    backend.insertAll(entities, handler);
                } else if (chain) {
                    uploadPendingDetails(context, backend);
                }
            }

            @Override
            public void onDataProcessed(int processed, List<Total> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        };

        DataSource ds = DataSource.getInstance(context);
        ds.setTotalCallback(totalCallbacks);
        ds.listPendingTotals();
    }

    public static void downloadDetails(Context context, CloudBackendMessaging backend, boolean chain) {
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

    public static void uploadDetails(List<Detail> details, Context context,
            CloudBackendMessaging backend) {
        List<CloudEntity> entities = new ArrayList<CloudEntity>();
        for (Detail detail : details) {
            entities.add(detail.getEntity(context));
        }

        UploadDetailsCallbackHandler handler = new UploadDetailsCallbackHandler(details, context);

        backend.insertAll(entities, handler);
    }

    public static void uploadPendingDetails(final Context context, final CloudBackendMessaging backend) {
        DataAccessCallbacks<Detail> detailCallbacks = new DataAccessCallbacks<Detail>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Detail> results) {
                if (results != null) {
                    List<CloudEntity> entities = new ArrayList<CloudEntity>();
                    for (Detail detail : results) {
                        entities.add(detail.getEntity(context));
                    }

                    UploadDetailsCallbackHandler handler = new UploadDetailsCallbackHandler(
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
