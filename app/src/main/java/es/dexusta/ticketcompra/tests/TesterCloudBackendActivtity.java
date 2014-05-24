package es.dexusta.ticketcompra.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.google.cloud.backend.android.CloudBackendActivity;
import com.google.cloud.backend.android.CloudCallbackHandler;
import com.google.cloud.backend.android.CloudEntity;
import com.google.cloud.backend.android.CloudQuery;
import com.google.cloud.backend.android.F;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import es.dexusta.ticketcompra.Consts;
import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.backendataaccess.BackendDataAccessV2;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.ReplicatedDBObject;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.model.Total;
import es.dexusta.ticketcompra.util.Installation;

/*
 * La finalidad de esta actividad es probar el ev�o y actualizaci�n de datos.
 * Probar� a crear una tienda, subirla y luego comprobar si abriendo la aplicaci�n desde otro dispositivo lee la nueva tienda.
 *  
 *  1.- Arranca la aplicaci�n.
 *  2.- Comprueba si hay conexi�n.
 *  2.1.- Si hay conexi�n intenta cargar nuevos datos (desde �ltima acutalizaci�n)
 *  2.1.1.- 
 *  2.1.2.-
 *  2.1.3.-  
 *  2.2.- Si no hay conexi�n avisa y pasa.
 *  
 */
public class TesterCloudBackendActivtity extends CloudBackendActivity {
    private static final String  TAG   = "TesterCloudBackend";
    private static final boolean DEBUG = true;

    private DateFormat           mDF   = DateFormat.getDateTimeInstance();

    private DataSource           mDS;
    private List<Shop>           mShops;
    private List<Product>        mProducts;
    private List<Receipt>        mReceipts;

    private Button               mBttInsertDB;
    private Button               mBttUpload;
    private Button               mBttDownload;
    private TextView             mTVInfo;

    private StringBuilder        mText;

    private boolean              mFlag = false;

    private String               mInstallation;
    private String               mFalseInstallation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tester_cloudbackend_activity);

        mBttInsertDB = (Button) findViewById(R.id.btt_insert_db);
        mBttUpload = (Button) findViewById(R.id.btt_upload);
        mBttDownload = (Button) findViewById(R.id.btt_download);
        mTVInfo = (TextView) findViewById(R.id.tv_info);

        mText = new StringBuilder();

        String appName = getString(R.string.app_name);
        mInstallation = Installation.id(getApplicationContext());
        mFalseInstallation = mInstallation + "falsa";

        SharedPreferences preferences = getSharedPreferences(appName, MODE_PRIVATE);
        long lastUpdated = preferences.getLong(Consts.PREF_LAST_UPDATED, 0);

        /*
         * 0.- Check if there is connectivity. 1.- Insert in the datastore all
         * the pending shops. 2.- Get from the datastore all the new shops
         * (timestamp > lastUpdated).
         */

    }

    @Override
    protected void onPostCreate() {
        mDS = DataSource.getInstance(getApplicationContext());
    }

    public void onClickTest(View view) {
        // Crear un objeto de cada tipo (replicated object).
        // Insertarlo con el sistema nuevo.

        DataAccessCallbacks<ReplicatedDBObject> simpleListener = new DataAccessCallbacks<ReplicatedDBObject>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<ReplicatedDBObject> results) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataProcessed(int processed, List<ReplicatedDBObject> dataList,
                    Operation operation, boolean result) {
                Log.d(TAG, dataList.get(0).getKindName() + " inserted.");

            }
        };

        Shop shop = new Shop();
        shop.setAddress("null address");
        shop.setChainId(1);
        shop.setTownId(1);
        shop.setTownName("null town");

        mDS.simpleInsert(shop);

        Product product = new Product();
        product.setSubcategoryId(1);
        product.setName("null product");

        mDS.simpleInsert(product);

        Receipt receipt = new Receipt();
        receipt.setShopId(1);
        receipt.setTimestamp(System.currentTimeMillis());

        mDS.simpleInsert(receipt);

        Total total = new Total();
        total.setReceiptId(1);
        total.setValue(100);

        mDS.simpleInsert(total);

        Detail detail = new Detail();
        detail.setPrice(100);
        detail.setReceiptId(1);
        detail.setProductId(1);
        detail.setProductName("null product");

        mDS.simpleInsert(detail);
    }

    public void onClickInsertDB(View v) {
        final List<Shop> shops = new ArrayList<Shop>();
        final List<Product> products = new ArrayList<Product>();
        final List<Receipt> receipts = new ArrayList<Receipt>();
        final List<Total> totals = new ArrayList<Total>();
        final List<Detail> details = new ArrayList<Detail>();

        Shop shop;
        for (int i = 1; i < 6; ++i) {
            shop = new Shop();
            shop.setId(i);
            shop.setChainId(1);
            shop.setTownId(1);
            shop.setTownName("Mock Town");
            shops.add(shop);
        }

        Product product;
        for (int i = 1; i < 6; ++i) {
            product = new Product();
            product.setId(i);
            product.setSubcategoryId(1);
            product.setName("Mock Product");
            products.add(product);
        }

        Receipt receipt;
        for (int i = 0; i < 5; ++i) {
            receipt = new Receipt();
            receipt.setId(i + 1);
            receipt.setShopId(shops.get(i).getId());
            receipt.setTimestamp(new DateTime(new Date()));
            receipts.add(receipt);
        }

        Total total;
        for (int i = 0; i < 5; ++i) {
            total = new Total();
            total.setId(i + 1);
            total.setReceiptId(receipts.get(i).getId());
            totals.add(total);
        }

        Detail detail;
        int d_id = 0;
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {                
                detail = new Detail();
                detail.setId(++d_id);
                detail.setProduct(products.get(i));
                detail.setReceiptId(receipts.get(i).getId());
                detail.setPrice(10);                
                details.add(detail);
            }            
        }

        // Set the DataAccessCallbacks to make a chain insertion.
        DataAccessCallbacks<Shop> shopCallbacks = new DataAccessCallbacks<Shop>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Shop> results) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                    boolean result) {
                mDS.insertProducts(products);
                Log.d(TAG, dataList.size() + " shops inserted");

            }
        };

        DataAccessCallbacks<Product> productCallbacks = new DataAccessCallbacks<Product>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Product> results) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataProcessed(int processed, List<Product> dataList, Operation operation,
                    boolean result) {
                mDS.insertReceipts(receipts);
                Log.d(TAG, dataList.size() + " products inserted");
            }
        };

        DataAccessCallbacks<Receipt> receiptCallbacks = new DataAccessCallbacks<Receipt>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Receipt> results) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                    boolean result) {
                mDS.insertDetails(details);;
                // mDS.insertDetails(details);
                Log.d(TAG, dataList.size() + " receipts inserted");

            }
        };

        DataAccessCallbacks<Total> totalCallbacks = new DataAccessCallbacks<Total>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Total> results) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataProcessed(int processed, List<Total> dataList, Operation operation,
                    boolean result) {
                Log.d(TAG, dataList.size() + " toals inserted");

            }
        };

        DataAccessCallbacks<Detail> detailCallbacks = new DataAccessCallbacks<Detail>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Detail> results) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataProcessed(int processed, List<Detail> dataList, Operation operation,
                    boolean result) {
                Log.d(TAG, dataList.size() + " details inserted");

            }
        };

        mDS.setShopCallback(shopCallbacks);
        mDS.setProductCallback(productCallbacks);
        mDS.setReceiptCallback(receiptCallbacks);
        mDS.setTotalCallback(totalCallbacks);
        mDS.setDetailCallback(detailCallbacks);

        mDS.insertShops(shops);
    }

    public void onClickUpdate(View v) {
        mTVInfo.setText(mText);
    }

    public void onClickUpload(View v) {
        BackendDataAccessV2.uploadPendingData(getApplicationContext(), getCloudBackend());
    }

    public void onClickDownload(View v) {
        BackendDataAccessV2.uploadPendingData(getApplicationContext(), getCloudBackend());
    }

    public void onClickCheck(View v) {
        if (mFlag) {
            mFlag = false;
            mTVInfo.setText("");

            for (Receipt receipt : mReceipts) {
                mDS.getDetailsBy(receipt);
            }

        } else {

            DataAccessCallbacks<Shop> countShopsCallbacks = new DataAccessCallbacks<Shop>() {

                @Override
                public void onInfoReceived(Object result, Option option) {
                    String text = mTVInfo.getText().toString();
                    if (text != null) {
                        text += "\nShops: " + (Long) result;
                    } else {
                        text = "Shops: " + (Long) result;
                    }

                    mTVInfo.setText(text);
                }

                @Override
                public void onDataReceived(List<Shop> results) {
                    StringBuilder text = new StringBuilder();
                    for (Shop shop : results) {
                        text.append(shop.toString());
                    }
                    mTVInfo.setText(text);

                }

                @Override
                public void onDataProcessed(int processed, List<Shop> dataList,
                        Operation operation, boolean result) {
                    // TODO Auto-generated method stub

                }
            };

            DataAccessCallbacks<Product> productCountCallbacks = new DataAccessCallbacks<Product>() {

                @Override
                public void onInfoReceived(Object result, Option option) {
                    String text = mTVInfo.getText().toString();
                    if (text != null) {
                        text += "\nProducts: " + (Long) result;
                    } else {
                        text = "Products: " + (Long) result;
                    }

                    mTVInfo.setText(text);
                }

                @Override
                public void onDataReceived(List<Product> results) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onDataProcessed(int processed, List<Product> dataList,
                        Operation operation, boolean result) {
                    // TODO Auto-generated method stub

                }
            };

            DataAccessCallbacks<Receipt> receiptCountCallbacks = new DataAccessCallbacks<Receipt>() {

                @Override
                public void onInfoReceived(Object result, Option option) {
                    String text = mTVInfo.getText().toString();
                    if (text != null) {
                        text += "\nReceipts: " + (Long) result;
                    } else {
                        text = "Receipts: " + (Long) result;
                    }

                    mTVInfo.setText(text);

                }

                @Override
                public void onDataReceived(List<Receipt> results) {
                    StringBuilder text = new StringBuilder(mTVInfo.getText());
                    for (Receipt receipt : results) {
                        text.append("\n");
                        text.append(receipt);
                        text.append("\n");
                        mDS.getDetailsBy(receipt);
                        mTVInfo.setText(text);
                    }

                }

                @Override
                public void onDataProcessed(int processed, List<Receipt> dataList,
                        Operation operation, boolean result) {
                    // TODO Auto-generated method stub

                }
            };

            DataAccessCallbacks<Detail> detailsCountCallbacks = new DataAccessCallbacks<Detail>() {

                @Override
                public void onInfoReceived(Object result, Option option) {
                    String text = mTVInfo.getText().toString();
                    if (text != null) {
                        text += "\nDetails: " + (Long) result;
                    } else {
                        text = "Details: " + (Long) result;
                    }

                    mTVInfo.setText(text);
                }

                @Override
                public void onDataReceived(List<Detail> results) {
                    if (results != null) {
                        StringBuilder text = new StringBuilder(mTVInfo.getText());
                        text.append("\n").append("results= ").append(results.size());
                        for (Detail detail : results) {
                            text.append("\n(detail)id: ").append(detail.getId());
                            text.append("\n").append("receiptId: ").append(detail.getReceiptId());
                            text.append("\n").append("receiptUnivId: ")
                                    .append(detail.getReceiptUnivId()).append("\n");
                        }
                        mTVInfo.setText(text);
                    }
                }

                @Override
                public void onDataProcessed(int processed, List<Detail> dataList,
                        Operation operation, boolean result) {
                    // TODO Auto-generated method stub

                }
            };

            DataAccessCallbacks<Total> totalCountCallbacks = new DataAccessCallbacks<Total>() {

                @Override
                public void onInfoReceived(Object result, Option option) {
                    String text = mTVInfo.getText().toString();
                    if (text != null) {
                        text += "\nTotals: " + (Long) result;
                    } else {
                        text = "Totals: " + (Long) result;
                    }

                    mTVInfo.setText(text);

                }

                @Override
                public void onDataReceived(List<Total> results) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onDataProcessed(int processed, List<Total> dataList,
                        Operation operation, boolean result) {
                    // TODO Auto-generated method stub

                }
            };

            mDS.setShopCallback(countShopsCallbacks);
            mDS.getShopCount();

            mDS.setProductCallback(productCountCallbacks);
            mDS.getProductCount();

            mDS.setReceiptCallback(receiptCountCallbacks);
            mDS.getReceiptCount();

            mDS.setDetailCallback(detailsCountCallbacks);
            mDS.getDetailCount();

            mDS.setTotalCallback(totalCountCallbacks);
            mDS.getTotalCount();

        }
    }

    public void onClickDeleteDB(View v) {
        mDS.deleteShops();
        mDS.deleteProducts();
    }

    public void onClickInsert(View v) {
        Shop shop;
        List<Shop> shops = new ArrayList<Shop>();

        for (int i = 1; i < 6; ++i) {
            shop = new Shop();

            shop.setChainId(1);
            shop.setTownId(1);
            shop.setTownName("Invalid town name");

            shops.add(shop);
        }

        DataAccessCallbacks<Shop> shopCallbacks = new DataAccessCallbacks<Shop>() {

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                    boolean result) {

                showInsertedToast(dataList);

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

        mDS.setShopCallback(shopCallbacks);

        insertNew(shops);
    }

    public void onClickInsertPending(View v) {
        Shop shop;
        List<Shop> pendingShops = new ArrayList<Shop>();

        /*
         * Este m�todo insertar 5 shops pendientes.
         */
        for (int i = 1; i < 6; ++i) {
            shop = new Shop();

            shop.setChainId(1);
            shop.setTownId(1);
            shop.setTownName("Invalid town name");
            shop.setAddress("Pending shop " + i + " no valid address");
            shop.setUpdated(false);

            pendingShops.add(shop);
        }

        DataAccessCallbacks<Shop> shopCallbacks = new DataAccessCallbacks<Shop>() {

            @Override
            public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                    boolean result) {

                showToast("");

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

        mDS.setShopCallback(shopCallbacks);

        mDS.insertShops(pendingShops);
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.w(TAG, text);
    }

    // public void showPendingInsertedToast(List<Shop> shops) {
    // Toast.makeText(this, "Pending inserted " + shops.size() + " shops.",
    // Toast.LENGTH_SHORT)
    // .show();
    // }

    public void showInsertedToast(List<Shop> shops) {
        Toast.makeText(this, "Inserted " + shops.size() + " shops.", Toast.LENGTH_SHORT).show();
    }

    public void insertNew(List<Shop> shops) {
        /*
         * 1.- Comprobar si hay conectividad (esto no implica que se pueda
         * insertar) 1.1.- Si no hay conectividad: Inserci�n en local con
         * "updated" = false (0). 1.2.- Si hay conectividad: Intentar insertar
         * con CloudBackend. 1.2.1.- Si �xito: inserci�n en local con "updated"
         * = true (1). 1.2.2.- Si error: inserci�n en local con "updated" =
         * false (0).
         */

        if (!isConnected()) {
            // Toast.makeText(this, "Connection available",
            // Toast.LENGTH_SHORT).show();
            showToast("Connection unavailable");
            mDS.insertShops(shops);
        } else {
            // Toast.makeText(this, "Connection unavailable",
            // Toast.LENGTH_SHORT).show();
            showToast("Connection available");
            NewShopsCallbackHandler newShopsCallbackHandler = new NewShopsCallbackHandler(shops);
            List<CloudEntity> ceList = new ArrayList<CloudEntity>();
            Context appContext = getApplicationContext();

            for (Shop shop : shops) {
                ceList.add(shop.getEntity(appContext));
            }

            getCloudBackend().insertAll(ceList, newShopsCallbackHandler);
        }
    }

    public void insertPending() {

        /*
         * 0.- Comprobar conexi�n (si no la hay nada) (hay conexi�n) 1.- Obtener
         * lista de pendientes. 2.- Si la lista no est� vac�a. (hay pendientes)
         * 3.- Generar lista de entities. 4.- Preparar callback que: 4.1.- En
         * acierto actualizar la lista a update = 1. 4.2.- En error no haga
         * nada.
         */
        if (isConnected()) {
            showToast("Connection available.");

            PendingShopDACallbacks shopDACallbacks = new PendingShopDACallbacks();

            mDS.setShopCallback(shopDACallbacks);
            mDS.listPendingShops();

        } else {
            showToast("Connection unavailable.");
        }

    }

    // Buscar� nuevas tiendas en el datastore y las descargar� e insertar�.
    public void listNew() {
        DateTime dt = null;

        CloudQuery query = new CloudQuery(Shop.KIND_NAME);

        String lastUpdated = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Consts.PREF_LAST_UPDATED, null);
        if (lastUpdated != null) {
            dt = new DateTime(lastUpdated);
        } else {
            dt = new DateTime(new Date(0), TimeZone.getTimeZone("UTC"));
        }

        F filter = F.gt(CloudEntity.PROP_UPDATED_AT, dt);

        query.setFilter(filter);

        CloudCallbackHandler<List<CloudEntity>> handler = new CloudCallbackHandler<List<CloudEntity>>() {

            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {

                }
            }
        };

        getCloudBackend().list(query, handler);
    }

    private boolean isConnected() {
        ConnectivityManager connMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = connMngr.getActiveNetworkInfo();

        if (netInfo == null) return false;
        if (!netInfo.isConnected()) return false;
        if (!netInfo.isAvailable()) return false;

        return true;
    }

    private DateTime getTimestamp(long timeInMillis) {
        DateTime dt = new DateTime(new Date(timeInMillis), TimeZone.getTimeZone("UTC"));
        return dt;
    }

    private void uploadPending() {

    }

    private void uploadNewShops() {
        Context context = getApplicationContext();
        Shop shop;

        List<CloudEntity> entities = new ArrayList<CloudEntity>();
        for (int i = 1; i < 6; ++i) {
            shop = new Shop(this);
            shop.setId(i);
            shop.setAddress("Generic address");
            shop.setChainId(1);
            shop.setTownId(1);
            shop.setTownName("Generic town");
            mShops.add(shop);
            entities.add(shop.getEntity(getApplicationContext()));
        }

        CloudCallbackHandler<List<CloudEntity>> shopCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {

            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    showToast("Inserted " + results.size() + " new shops.");
                    uploadNewProducts();
                } else {
                    showToast("No shops inserted.");
                    mBttUpload.setEnabled(true);
                }
            }

            @Override
            public void onError(IOException exception) {
                handleEndpointException(exception);
            }

        };
        getCloudBackend().insertAll(entities, shopCallbackHandler);
    }

    private void uploadNewProducts() {
        Context context = getApplicationContext();
        Product product;

        List<CloudEntity> entities = new ArrayList<CloudEntity>();
        for (int i = 1; i < 6; ++i) {
            product = new Product(this);
            product.setId(i);
            product.setSubcategoryId(1);
            product.setName("Generic name " + i);
            mProducts.add(product);
            entities.add(product.getEntity(context));
        }

        CloudCallbackHandler<List<CloudEntity>> productCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {

            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    showToast("Inserted " + results.size() + " products");
                    uploadNewReceipts(mShops);
                } else {
                    showToast("No new products inserted");
                    mBttUpload.setEnabled(true);
                }
            }
        };
        getCloudBackend().insertAll(entities, productCallbackHandler);
    }

    private void uploadNewReceipts(List<Shop> shops) {
        Context context = getApplicationContext();
        Receipt receipt;

        List<CloudEntity> entities = new ArrayList<CloudEntity>();
        for (Shop shop : mShops) {
            for (int i = 1; i < 6; ++i) {
                receipt = new Receipt(this);
                receipt.setId(i);
                receipt.setShopId(shop.getId());
                receipt.setShopUnivId(shop.getUniversalId());
                receipt.setTimestamp(new DateTime(new Date(), TimeZone.getTimeZone("UTC")));
                mReceipts.add(receipt);
                entities.add(receipt.getEntity(context));
            }
        }

        CloudCallbackHandler<List<CloudEntity>> receiptCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {

            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    Log.d(TAG, "Inserted " + results.size() + " receipts.");
                    uploadNewDetails(mReceipts, mProducts);
                } else {
                    Log.d(TAG, "No new receipts inserted.");
                    mBttUpload.setEnabled(true);
                }
            }
        };

        getCloudBackend().insertAll(entities, receiptCallbackHandler);
    }

    private void uploadNewTotals(List<Receipt> receipts) {

        Total total;

        List<CloudEntity> entities = new ArrayList<CloudEntity>();
        int id = 1;
        for (Receipt receipt : receipts) {
            total = new Total(this);
            Installation.id(this);
            total.setId(id++);
            total.setReceiptId(receipt.getId());
            total.setReceiptUnivId(receipt.getUniversalId());
            total.setValue(10);
            entities.add(total.getEntity(this));
        }

        CloudCallbackHandler<List<CloudEntity>> totalCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {

            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    showToast("Inserted " + results.size() + " totals");
                    DateTime lastUpdate = new DateTime(new Date(1), TimeZone.getTimeZone("UTC"));
                } else {
                    showToast("No totals inserted");
                    mBttUpload.setEnabled(true);
                }
            }
        };

        getCloudBackend().insertAll(entities, totalCallbackHandler);
    }

    private void uploadNewDetails(List<Receipt> receipts, List<Product> products) {
        Detail detail;

        List<CloudEntity> entities = new ArrayList<CloudEntity>();
        int i = 1;
        for (Receipt receipt : receipts) {
            for (Product product : products) {
                detail = new Detail(this);
                detail.setId(i++);
                detail.setPrice(5);
                detail.setUnits(1);
                detail.setReceiptId(receipt.getId());
                detail.setReceiptUnivId(receipt.getUniversalId());
                detail.setProductId(product.getId());
                detail.setProductName(product.getName());
                detail.setProductUnivId(product.getUniversalId());
                entities.add(detail.getEntity(this));
            }
        }

        CloudCallbackHandler<List<CloudEntity>> detailCallbackHandler = new CloudCallbackHandler<List<CloudEntity>>() {

            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    showToast("Inserted " + results.size() + " details");
                    DateTime lastUpdate = new DateTime(new Date(1), TimeZone.getTimeZone("UTC"));
                    downloadNewShops(lastUpdate);
                } else {
                    showToast("Not details inserted.");

                }
                mBttUpload.setEnabled(true);
                mBttDownload.setEnabled(true);
            }
        };
        getCloudBackend().insertAll(entities, detailCallbackHandler);
    }

    private void downloadNew() {
        /*
         * 1.- Get the Date/DateTime of las update.
         */

        DateTime currentUpdate = new DateTime(new Date(), TimeZone.getTimeZone("UTC"));
        DateTime lastUpdate;
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String last_update_str = sp.getString(Consts.PREF_LAST_UPDATED, null);
        if (last_update_str == null) {
            lastUpdate = new DateTime(new Date(1), TimeZone.getTimeZone("UTC"));
        } else {
            lastUpdate = new DateTime(last_update_str);
        }

        uploadNewShops();

        // TODO: Terminar.
        /*
         * 2.- Get the new shops: 2.1.- Build filter and query for the new
         * shops. 2.2.- Create callback handler. 2.3.- Insert new shops in the
         * DB. (insert the new pairs univ_id/_id)
         */
        // downloadNewShops(lastUpdate);

        /*
         * 3.- Get the new products: 3.1.- Build filter for the new products.
         * 3.2.- Create callback handler. 3.3.- Insert the new products in the
         * DB. (insert the new pairs univ_id/_id)
         */
        // downloadNewProducts(lastUpdate);
        /*
         * * 4.- Get the new receipts. 4.1.- Build filter and query. 4.2.-
         * Create callback handler. 4.3.- Insert new receipts in the DB. (insert
         * pairs univ_id/_id). (check for the shop local id in the univ_id/_id
         * table).
         */
        // downloadNewReceipts(lastUpdate);
        /*
         * 5.- Get the new totals 5.1.- Build filter & query. 5.2.- Insert new
         * totals in the DB. (check for receipt id in the univ_id/_id table).
         * 6.- Get the new details (check for receipt id in the univ_id/_id
         * table). (check for product id in the univ_id/_id table).
         */
        // downloadNewTotals(lastUpdate);
        // downloadNewDetails(lastUpdate);
    }

    private void downloadNewShops(final DateTime lastUpdate) {
        mBttDownload.setEnabled(false);
        CloudQuery query = new CloudQuery(Shop.KIND_NAME);
        F filter = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdate);
        query.setFilter(filter);
        CloudCallbackHandler<List<CloudEntity>> shopCBHandler = new CloudCallbackHandler<List<CloudEntity>>() {

            @Override
            public void onComplete(List<CloudEntity> results) {

                if (results.size() > 0) {
                    showToast(results.size() + " shops donwloaded");
                    List<Shop> shops = new ArrayList<Shop>();
                    Shop shop;
                    String shopUnivId;
                    for (CloudEntity entity : results) {
                        shop = new Shop(entity);

                        shopUnivId = shop.getUniversalId();
                        if (!shopUnivId.startsWith(mInstallation)) {
                            shops.add(shop);
                        }
                    }

                    mDS.setShopCallback(new ShopDownloadDACallbacks());
                    mDS.insertShops(shops);

                    downloadNewProducts(lastUpdate);
                } else {
                    showToast("No new shops downloaded.");
                }
            }
        };
        getCloudBackend().list(query, shopCBHandler);
    }

    private void downloadNewProducts(final DateTime lastUpdate) {

        CloudQuery query = new CloudQuery(Product.KIND_NAME);
        F filter = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdate);
        query.setFilter(filter);

        CloudCallbackHandler<List<CloudEntity>> productCBHandler = new CloudCallbackHandler<List<CloudEntity>>() {

            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    showToast(results.size() + " products downloaded");
                    List<Product> products = new ArrayList<Product>();
                    Product product;
                    String productUnivId;
                    for (CloudEntity entity : results) {
                        product = new Product(entity);
                        productUnivId = product.getUniversalId();
                        if (!productUnivId.startsWith(mInstallation)) {
                            products.add(product);
                        }
                    }

                    mDS.setProductCallback(new ProductDownloadDACallbacks());
                    mDS.insertProducts(products);
                    downloadNewReceipts(lastUpdate);
                } else {
                    showToast("No new products downloaded");
                }
            }
        };
        getCloudBackend().list(query, productCBHandler);
    }

    private void downloadNewReceipts(final DateTime lastUpdate) {
        CloudQuery query = new CloudQuery(Receipt.KIND_NAME);

        F filterGTCreatedAt = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdate);
        F filterEQCreatedBy = F.eq(CloudEntity.PROP_CREATED_BY, getAccountName());

        F filter = F.and(filterEQCreatedBy, filterGTCreatedAt);
        query.setFilter(filter);

        CloudCallbackHandler<List<CloudEntity>> receiptCBHandler = new CloudCallbackHandler<List<CloudEntity>>() {

            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    showToast(results.size() + " receipts downloaded");
                    List<Receipt> receipts = new ArrayList<Receipt>();
                    Receipt receipt;
                    long shop_id;
                    String shopUnivId;
                    String receiptUnivId;
                    for (CloudEntity entity : results) {
                        receipt = new Receipt(entity);
                        receiptUnivId = receipt.getUniversalId();
                        if (!receiptUnivId.startsWith(mInstallation)) {
                            shop_id = mDS.getShopLocIdFromUnivId(receipt.getShopUnivId());
                            receipt.setShopId(shop_id);
                            receipts.add(receipt);
                        }
                    }

                    mDS.setReceiptCallback(new ReceiptDownloadDACallbacks());
                    mDS.insertReceipts(receipts);
                    downloadNewDetails(lastUpdate);
                } else {
                    showToast("No new receipts downloaded");
                }
            }
        };

        getCloudBackend().list(query, receiptCBHandler);
    }

    private void downloadNewTotals(final DateTime lastUpdate) {
        CloudQuery query = new CloudQuery(Total.KIND_NAME);

        F filterEQCreatedBy = F.eq(CloudEntity.PROP_CREATED_BY, getAccountName());
        F filterGTCreatedAt = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdate);

        F filter = F.and(filterEQCreatedBy, filterGTCreatedAt);
        query.setFilter(filter);

        CloudCallbackHandler<List<CloudEntity>> totalCBHandler = new CloudCallbackHandler<List<CloudEntity>>() {

            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    showToast(results.size() + " totals downloaded.");
                    List<Total> totals = new ArrayList<Total>();
                    Total total;
                    long receipt_id;
                    String totalUnivId;
                    for (CloudEntity entity : results) {
                        total = new Total(entity);
                        totalUnivId = total.getUniversalId();
                        if (!totalUnivId.startsWith(mInstallation)) {
                            receipt_id = mDS.getReceiptLocIdFromUnivId(total.getReceiptUnivId());
                            total.setReceiptId(receipt_id);
                            totals.add(total);
                        }
                    }
                    mDS.setTotalCallback(null);
                    mDS.insertTotals(totals);
                    // downloadNewDetails(lastUpdate);
                } else {
                    showToast("No new totals downloaded");
                }
                mBttDownload.setEnabled(true);
            }
        };
        getCloudBackend().list(query, totalCBHandler);
    }

    private void downloadNewDetails(DateTime lastUpdate) {
        CloudQuery query = new CloudQuery(Detail.KIND_NAME);
        F filterEQCreatedBy = F.eq(CloudEntity.PROP_CREATED_BY, getAccountName());
        F filterGTCreatedAt = F.gt(CloudEntity.PROP_CREATED_AT, lastUpdate);

        F filter = F.and(filterEQCreatedBy, filterGTCreatedAt);
        query.setFilter(filter);

        CloudCallbackHandler<List<CloudEntity>> detailCBHandler = new CloudCallbackHandler<List<CloudEntity>>() {

            @Override
            public void onComplete(List<CloudEntity> results) {
                if (results.size() > 0) {
                    showToast(results.size() + " details downloaded");
                    List<Detail> details = new ArrayList<Detail>();
                    Detail detail;
                    String detail_univ_id;
                    String product_univ_id;
                    String receipt_univ_id;
                    long product_id;
                    long receipt_id;
                    for (CloudEntity entity : results) {
                        detail = new Detail(entity);
                        detail_univ_id = detail.getUniversalId();
                        if (!detail_univ_id.startsWith(mInstallation)) {
                            receipt_univ_id = detail.getReceiptUnivId();
                            product_univ_id = detail.getProductUnivId();

                            receipt_id = mDS.getReceiptLocIdFromUnivId(receipt_univ_id);
                            product_id = mDS.getProductLocIdFromUnivId(product_univ_id);
                            detail.setReceiptId(receipt_id);

                            detail.setProductId(product_id);

                            details.add(detail);
                        }
                    }
                    mDS.setDetailCallback(null);
                    mDS.insertDetails(details);
                } else {
                    showToast("No new details downloaded");
                }
                mBttDownload.setEnabled(true);
                mShops.clear();
                mProducts.clear();
                mReceipts.clear();
            }
        };
        getCloudBackend().list(query, detailCBHandler);
    }

    private void handleEndpointException(IOException e) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
    }

    class PendingShopDACallbacks implements DataAccessCallbacks<Shop> {

        @Override
        public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                boolean result) {
            showToast("Processed: " + dataList.size() + " items.");
        }

        @Override
        public void onDataReceived(List<Shop> results) {
            /*
             * Esta ser� la lista de tiendas con updated = false (0).
             */

            // Log.d(TAG, "Number of pending shops = " + results.size());
            if (results != null) {
                showToast("insertPending: n� of pending shops: " + results.size());

                Context context = getApplicationContext();

                List<CloudEntity> ceList = new ArrayList<CloudEntity>();
                for (Shop shop : results) {
                    ceList.add(shop.getEntity(context));
                }

                PendingShopsCallbackHandler pendingShopsCallbackHandler = new PendingShopsCallbackHandler(
                        results);

                getCloudBackend().insertAll(ceList, pendingShopsCallbackHandler);
            } else {
                showToast("Nothing pending.");
            }

        }

        @Override
        public void onInfoReceived(Object result, Option option) {
            // TODO Auto-generated method stub

        }
    }

    class UpdateShopsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {

        @Override
        public void onComplete(List<CloudEntity> results) {
            List<Shop> shops = new ArrayList<Shop>();

            showToast("UpdateShopsCallbackHandler: Readed: " + results.size() + " shops.");

            Shop shop;
            for (CloudEntity entity : results) {
                shop = new Shop(entity);
                shops.add(shop);

                Log.d(TAG, shop.toString());
            }
        }

        @Override
        public void onError(IOException exception) {
            // TODO Auto-generated method stub
            super.onError(exception);
        }

    }

    class NewShopsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {
        List<Shop> mShops;

        public NewShopsCallbackHandler(List<Shop> shops) {
            mShops = shops;
        }

        @Override
        public void onComplete(List<CloudEntity> results) {
            for (Shop shop : mShops) {
                shop.setUpdated(true);
            }

            String str_aux = "";
            for (CloudEntity entity : results) {
                SimpleDateFormat dfRfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                str_aux += dfRfc3339.format(entity.getUpdatedAt()) + '\n';

                dfRfc3339.setTimeZone(TimeZone.getTimeZone("UTC"));

                str_aux += dfRfc3339.format(entity.getUpdatedAt()) + '\n';
            }
            mTVInfo.setText(str_aux);

            // Asegurarme de que no hay listener previo (no hay acci�n).

            // mDS.setShopListener(null);
            mDS.updateShops(mShops);
        }

        @Override
        public void onError(IOException exception) {
            // Asegurarme de que no hay listener previo (no hay acci�n).
            // mDS.setShopListener(null);
            mDS.updateShops(mShops);
        }
    }

    class PendingShopsCallbackHandler extends CloudCallbackHandler<List<CloudEntity>> {

        List<Shop> mShops;

        public PendingShopsCallbackHandler(List<Shop> shops) {
            mShops = shops;
        }

        @Override
        public void onComplete(List<CloudEntity> results) {
            showToast("PendingShopCallbackHandler: onComplete (peding inserted: " + results.size()
                    + ").");
            for (Shop shop : mShops) {
                shop.setUpdated(true);
            }

            mDS.updateShops(mShops);
        }

        @Override
        public void onError(IOException exception) {
            showToast("PendingShopCallbackHandler: onError.");
        }
    }

    class ShopUploadDACallbacks implements DataAccessCallbacks<Shop> {
        private DateTime              mDTUpdatedAt;
        private Context               mContext;
        private HashMap<String, Long> mShopIdTable;

        public ShopUploadDACallbacks(DateTime updatedAt, Context context,
                HashMap<String, Long> shopIdTable) {
            mDTUpdatedAt = updatedAt;
            mContext = context;
            mShopIdTable = shopIdTable;
        }

        @Override
        public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                boolean result) {
            if (result) {

                for (Shop shop : dataList) {
                    mShopIdTable.put(shop.getUniversalId(), shop.getId());
                }

                String updatedAtStr = mDTUpdatedAt.toStringRfc3339();
                Editor prefEdt = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                prefEdt.putString(Consts.PREF_LAST_UPDATED, updatedAtStr).commit();
            }

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

    class ShopDownloadDACallbacks implements DataAccessCallbacks<Shop> {

        @Override
        public void onDataProcessed(int processed, List<Shop> dataList, Operation operation,
                boolean result) {
            if (result) {
                for (Shop shop : dataList) {
                    mDS.addToUnivIdLocIdMap(shop);
                }
            }

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

    class ProductDownloadDACallbacks implements DataAccessCallbacks<Product> {

        @Override
        public void onDataProcessed(int processed, List<Product> dataList, Operation operation,
                boolean result) {
            if (result) {
                for (Product product : dataList) {
                    mDS.addToUnivIdLocIdMap(product);
                }
            }

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

    class ReceiptDownloadDACallbacks implements DataAccessCallbacks<Receipt> {

        @Override
        public void onDataProcessed(int processed, List<Receipt> dataList, Operation operation,
                boolean result) {
            if (result) {
                for (Receipt receipt : dataList) {
                    mDS.addToUnivIdLocIdMap(receipt);
                }
            }
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
}
