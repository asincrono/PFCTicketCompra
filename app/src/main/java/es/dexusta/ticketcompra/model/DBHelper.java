package es.dexusta.ticketcompra.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import es.dexusta.ticketcompra.R;
import es.dexusta.ticketcompra.dataaccess.CategoryDataAccess;
import es.dexusta.ticketcompra.dataaccess.ChainDataAccess;
import es.dexusta.ticketcompra.dataaccess.RegionDataAccess;
import es.dexusta.ticketcompra.dataaccess.SubcategoryDataAccess;
import es.dexusta.ticketcompra.dataaccess.SubregionDataAccess;
import es.dexusta.ticketcompra.dataaccess.TownDataAccess;
import es.dexusta.ticketcompra.util.CatSubcatStructure;
import es.dexusta.ticketcompra.util.CatSubcatXMLParser;
import es.dexusta.ticketcompra.util.ChainsXmlParser;
import es.dexusta.ticketcompra.util.CountryList;
import es.dexusta.ticketcompra.util.RegionList;
import es.dexusta.ticketcompra.util.RegionXMLParser;
import es.dexusta.ticketcompra.util.SubregionList;

public class DBHelper extends SQLiteOpenHelper {
    // Tabla de regiones (comunidades autónomas).
    public static final String  TBL_REGION             = "region";
    // Campos:
    public static final String  T_REGION_ID            = "_id";
    public static final String  T_REGION_NAME          = "name";

    /*
     * Comienzo del apartado de cadenas de creación de tablas: Prefijos de las
     * constantes: TBL, T = tabla. F = campo. SQL_CREATE = sentencia SQL de
     * creación de la tabla.
     */
    // Tabla de sub-regiones (provicias).
    public static final String  TBL_SUBREGION          = "subregion";
    // Campos:
    public static final String  T_SUBREGION_ID         = "_id";
    public static final String  T_SUBREGION_REGION_ID  = "region_id";
    public static final String  T_SUBREGION_NAME       = "name";
    // Tabla de puelos (municipios)
    public static final String  TBL_TOWN               = "town";
    public static final String  T_TOWN_ID              = "_id";
    public static final String  T_TOWN_SUBREGION_ID    = "subregion_id";
    public static final String  T_TOWN_NAME            = "name";
    // Tabla de usuarios.
    public static final String  TBL_USER               = "user";
    // Campos:
    public static final String  T_USER_ID              = "_id";
    public static final String  T_USER_NAME            = "name";
    public static final String  T_USER_EMAIL           = "email";
    // Tabla de cadenas.
    public static final String  TBL_CHAIN              = "chain";
    // Campos:
    public static final String  T_CHAIN_ID             = "_id";
    public static final String  T_CHAIN_NAME           = "name";
    public static final String  T_CHAIN_CODE           = "code";
    // Tabla de tiendas.
    public static final String  TBL_SHOP               = "shop";
    // Campos:
    public static final String  T_SHOP_ID              = "_id";
    public static final String  T_SHOP_UNIVERSAL_ID    = "universal_id";
    public static final String  T_SHOP_CHAIN_ID        = "chain_id";
    public static final String  T_SHOP_TOWN_ID         = "town_id";
    public static final String  T_SHOP_TOWN_NAME       = "town_name";
    // public static final String T_SHOP_NAME = "name";
    public static final String  T_SHOP_LATIT           = "latitude";
    public static final String  T_SHOP_LONGT           = "longitude";
    public static final String  T_SHOP_ADDR            = "address";
    public static final String  T_SHOP_UPDATED         = "updated";
    // Tabla de direcciones.
    public static final String  TBL_ADDRESS            = "address";
    // Campos:
    public static final String  T_ADDR_ID              = "_id";
    public static final String  T_ADDR_LATIT           = "latitude";
    public static final String  T_ADDR_LONGT           = "longitude";
    public static final String  T_ADDR_TYPE            = "type";
    public static final String  T_ADDR_NAME            = "name";
    public static final String  T_ADDR_NUMBER          = "number";
    // Tabla de categorías.
    public static final String  TBL_CATEGORY           = "category";
    // Campos:
    public static final String  T_CAT_ID               = "_id";
    public static final String  T_CAT_NAME             = "name";
    public static final String  T_CAT_DESCR            = "description";
    // Tabla de subcategorías.
    public static final String  TBL_SUBCATEGORY        = "subcategory";
    // Campos:
    public static final String  T_SUBCAT_ID            = "_id";
    public static final String  T_SUBCAT_CAT_ID        = "category_id";
    public static final String  T_SUBCAT_NAME          = "name";
    public static final String  T_SUBCAT_DESCR         = "description";
    // Product table.
    public static final String  TBL_PRODUCT            = "product";
    // Columns:
    public static final String  T_PROD_ID              = "_id";
    public static final String  T_PROD_UNIVERSAL_ID    = "universal_id";
    public static final String  T_PROD_SUBCAT_ID       = "subcategory_id";
    public static final String  T_PROD_NAME            = "name";
    public static final String  T_PROD_DESCR           = "description";
    public static final String  T_PROD_IS_FAVOURITE    = "is_favourite";
    public static final String  T_PROD_ARTNUMBER       = "article_number";
    // Alternative names to avoid possible collision in joins.
    public static final String  T_PROD_ID_ALT          = "alt_product_id";
    public static final String  T_PROD_SUBCAT_ID_ALT   = "alt_product_subcategory_id";
    public static final String  T_PROD_NAME_ALT        = "alt_product_name";
    public static final String  T_PROD_UPDATED         = "updated";
    // Tabla de recivos.
    public static final String  TBL_RECEIPT            = "receipt";
    // Campos:
    public static final String  T_RECPT_ID             = "_id";
    public static final String  T_RECPT_UNIVERSAL_ID   = "universal_id";
    public static final String  T_RECPT_USER_ID        = "user_id";
    public static final String  T_RECPT_SHOP_ID        = "shop_id";
    public static final String  T_RECPT_SHOP_UNIV_ID   = "shop_universal_id";
    public static final String  T_RECPT_TOTAL          = "total";
    public static final String  T_RECPT_TIMESTAMP      = "timestamp";
    public static final String  T_RECPT_UPDATED        = "updated";
    // Detail table.
    public static final String  TBL_DETAIL             = "detail";
    // Columns:
    public static final String  T_DETAIL_ID            = "_id";
    public static final String  T_DETAIL_UNIVERSAL_ID  = "universal_id";
    public static final String  T_DETAIL_RECPT_ID      = "receipt_id";
    public static final String  T_DETAIL_RECPT_UNIV_ID = "receipt_universal_id";
    public static final String  T_DETAIL_PROD_ID       = "product_id";
    public static final String  T_DETAIL_PROD_NAME     = "product_name";
    public static final String  T_DETAIL_PROD_UNIV_ID  = "product_universal_id";
    public static final String  T_DETAIL_PRICE         = "price";
    public static final String  T_DETAIL_UNITS         = "units";
    // weight will be stored in grams.
    public static final String  T_DETAIL_WEIGHT        = "weight";
    // volume will be stored in ml.
    public static final String  T_DETAIL_VOLUME        = "volume";
    // Alternative names to avoid possible collision in joins.
    public static final String  T_DETAIL_ID_ALT        = "alt_detail_id";
    public static final String  T_DETAIL_RECPT_ID_ALT  = "alt_detail_receipt_id";
    public static final String  T_DETAIL_PROD_ID_ALT   = "alt_detail_product_id";
    public static final String  T_DETAIL_UPDATED       = "updated";
    // Tabla de totales.
    public static final String  TBL_TOTAL              = "total";
    // Campos:
    public static final String  T_TOTAL_ID             = "_id";
    public static final String  T_TOTAL_UNIVERSAL_ID   = "universal_id";
    public static final String  T_TOTAL_RECPT_ID       = "receipt_id";
    public static final String  T_TOTAL_RECPT_UNIV_ID  = "receipt_universal_id";
    public static final String  T_TOTAL_VALUE          = "value";
    public static final String  T_TOTAL_UPDATED        = "updated";
    private static final String DB_NAME                = "ticketcompra.db";
    private static final int    DB_VERSION             = 1;
    private static final String INTEGER_PRIMARY_KEY    = "INTEGER PRIMARY KEY";
    private static final String INTEGER_NOT_NULL       = "INTEGER NOT NULL";
    private static final String INTEGER_DEFAULT_NULL   = "INTEGER DEFAULT NULL";
    private static final String INTEGER_DEFAULT_ZERO   = "INTEGER DEFAULT 0";
    private static final String TEXT_DEFAULT_NULL      = "TEXT DEFAULT NULL";
    private static final String TEXT_NOT_NULL          = "TEXT NOT NULL";
    private static final String REAL_DEFAULT_NULL      = "REAL DEFAULT NULL";
    private static final String REAL_NOT_NULL          = "REAL NOT NULL";
    private Context             mContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         * CREATE TABLE IF NOT EXISTS region (_id INTEGER PRIMARY KEY, name TEXT
         * NOT NULL)
         */
        String sqlCreateRegion = buildCreateString(TBL_REGION, new String[] { T_REGION_ID,
                T_REGION_NAME }, new String[] { INTEGER_PRIMARY_KEY, TEXT_NOT_NULL });

        db.execSQL(sqlCreateRegion);

        /*
         * CREATE TABLE IF NOT EXIST subregion (_id INTEGER PRIMARY KEY, name
         * TEXT NOT NULL, region_id INTEGER NOT NULL REFERENCES region(_id) ON
         * DELETE CASCADE)
         */
        String sqlCreateSubregion = buildCreateString(
                TBL_SUBREGION,
                new String[] { T_SUBREGION_ID, T_SUBREGION_REGION_ID, T_SUBREGION_NAME },
                new String[] {
                        INTEGER_PRIMARY_KEY,
                        buildForeignKeyConstraint(TBL_REGION, new String[] { T_REGION_ID },
                                Action.CASCADE), TEXT_NOT_NULL });

        db.execSQL(sqlCreateSubregion);

        /*
         * CREATE TABLE IF NOT EXIST town (_id INTEGER PRIMARY KEY, name TEXT
         * NOT NULL, subregion_id INTEGER NOT NULL REFERENCES subregion(_id) ON
         * DELETE CASCADE)
         */
        String sqlCreateTown = buildCreateString(
                TBL_TOWN,
                new String[] { T_TOWN_ID, T_TOWN_SUBREGION_ID, T_TOWN_NAME },
                new String[] {
                        INTEGER_PRIMARY_KEY,
                        buildForeignKeyConstraint(TBL_SUBREGION, new String[] { T_SUBREGION_ID },
                                Action.CASCADE), TEXT_NOT_NULL });

        db.execSQL(sqlCreateTown);

        /*
         * CREATE TABLE IF NOT EXISTS user (_id INTEGER PRIMARY KEY, name TEXT
         * NOT NULL, email TEXT NOT NULL)
         */
        String sqlCreateUser = buildCreateString(TBL_USER, new String[] { T_USER_ID, T_USER_NAME,
                T_USER_EMAIL }, new String[] { INTEGER_PRIMARY_KEY, TEXT_NOT_NULL, TEXT_NOT_NULL });

        db.execSQL(sqlCreateUser);

        String sqlCreateChain = buildCreateString(TBL_CHAIN, new String[] { T_CHAIN_ID,
                T_CHAIN_NAME, T_CHAIN_CODE }, new String[] { INTEGER_PRIMARY_KEY, TEXT_NOT_NULL,
                TEXT_DEFAULT_NULL });

        db.execSQL(sqlCreateChain);

        String sqlCreateShop = buildCreateString(
                TBL_SHOP,
                new String[] { T_SHOP_ID, T_SHOP_UNIVERSAL_ID, T_SHOP_CHAIN_ID, T_SHOP_TOWN_ID,
                        T_SHOP_TOWN_NAME, T_SHOP_ADDR, T_SHOP_LATIT, T_SHOP_LONGT, T_SHOP_UPDATED },
                new String[] {
                        INTEGER_PRIMARY_KEY,
                        TEXT_DEFAULT_NULL,
                        buildForeignKeyConstraint(TBL_CHAIN, new String[] { T_CHAIN_ID },
                                Action.CASCADE),
                        buildForeignKeyConstraint(TBL_TOWN, new String[] { T_TOWN_ID },
                                Action.CASCADE), TEXT_DEFAULT_NULL, TEXT_DEFAULT_NULL,
                        REAL_DEFAULT_NULL, REAL_DEFAULT_NULL, INTEGER_DEFAULT_ZERO });

        db.execSQL(sqlCreateShop);

        String sqlCreateCategory = buildCreateString(TBL_CATEGORY, new String[] { T_CAT_ID,
                T_CAT_NAME, T_CAT_DESCR }, new String[] { INTEGER_PRIMARY_KEY, TEXT_NOT_NULL,
                TEXT_DEFAULT_NULL });

        db.execSQL(sqlCreateCategory);

        String sqlCreateSubcategory = buildCreateString(
                TBL_SUBCATEGORY,
                new String[] { T_SUBCAT_ID, T_SUBCAT_CAT_ID, T_SUBCAT_NAME, T_SUBCAT_DESCR },
                new String[] {
                        INTEGER_PRIMARY_KEY,
                        buildForeignKeyConstraint(TBL_CATEGORY, new String[] { T_CAT_ID },
                                Action.CASCADE), TEXT_NOT_NULL, TEXT_DEFAULT_NULL });

        db.execSQL(sqlCreateSubcategory);

        String sqlCreateProduct = buildCreateString(
                TBL_PRODUCT,
                new String[] { T_PROD_ID, T_PROD_UNIVERSAL_ID, T_PROD_SUBCAT_ID, T_PROD_NAME,
                        T_PROD_DESCR, T_PROD_ARTNUMBER, T_PROD_IS_FAVOURITE, T_PROD_UPDATED },
                new String[] {
                        INTEGER_PRIMARY_KEY,
                        TEXT_DEFAULT_NULL,
                        buildForeignKeyConstraint(TBL_SUBCATEGORY, new String[] { T_SUBCAT_ID },
                                Action.CASCADE), TEXT_NOT_NULL, TEXT_DEFAULT_NULL,
                        TEXT_DEFAULT_NULL, INTEGER_DEFAULT_ZERO, INTEGER_DEFAULT_ZERO });

        db.execSQL(sqlCreateProduct);

        /*
         * CREATE TABLE IF NOT EXISTS receipt (_id INTEGER PRIMARY KEY, shop_id
         * INTEGER REFERENCES shop(_id), user_id REFERENCES user(_id), timestamp
         * INTEGER NOT NULL)
         */
        String sqlCreateReceipt = buildCreateString(
                TBL_RECEIPT,
                new String[] { T_RECPT_ID, T_RECPT_UNIVERSAL_ID, T_RECPT_SHOP_ID,
                        T_RECPT_SHOP_UNIV_ID, T_RECPT_USER_ID, T_RECPT_TOTAL, T_RECPT_TIMESTAMP,
                        T_RECPT_UPDATED },
                new String[] {
                        INTEGER_PRIMARY_KEY,
                        TEXT_DEFAULT_NULL,
                        buildForeignKeyConstraint(TBL_SHOP, new String[] { T_SHOP_ID },
                                Action.SET_NULL),
                        TEXT_DEFAULT_NULL,
                        buildForeignKeyConstraint(TBL_USER, new String[] { T_USER_ID },
                                Action.SET_NULL), INTEGER_NOT_NULL, TEXT_NOT_NULL,
                        INTEGER_DEFAULT_ZERO });

        db.execSQL(sqlCreateReceipt);

        /*
         * CREATE TABLE IF NOT EXISTS detail (_id INTEGER PRIMARY KEY,
         * receipt_id INTEGER REFERENCES receipt(_id) ON DELETE CASCADE,
         * product_id INTEGER REFERENCES product(_id) ON DELETE CASCADE, price
         * REAL NOT NULL, units INTEGER NOT NULL, weight INTEGER DEFAULT NULL,
         * volume INTEGER DEFAULT NULL)
         */
        String sqlCreateDetail = buildCreateString(
                TBL_DETAIL,
                new String[] { T_DETAIL_ID, T_DETAIL_UNIVERSAL_ID,

                        T_DETAIL_RECPT_ID,
                        T_DETAIL_RECPT_UNIV_ID,

                        T_DETAIL_PROD_ID,
                        T_DETAIL_PROD_UNIV_ID,
                        T_DETAIL_PROD_NAME,

                        T_DETAIL_PRICE,
                        T_DETAIL_UNITS,
                        T_DETAIL_WEIGHT,
                        T_DETAIL_VOLUME,
                        T_DETAIL_UPDATED },
                new String[] {
                        INTEGER_PRIMARY_KEY, TEXT_DEFAULT_NULL,

                        buildForeignKeyConstraint(TBL_RECEIPT, new String[] { T_RECPT_ID },
                                Action.CASCADE),
                        TEXT_DEFAULT_NULL,

                        buildForeignKeyConstraint(TBL_PRODUCT, new String[] { T_PROD_ID },
                                Action.CASCADE),
                        TEXT_DEFAULT_NULL,
                        TEXT_DEFAULT_NULL,

                        INTEGER_NOT_NULL,
                        INTEGER_NOT_NULL,
                        INTEGER_DEFAULT_NULL,
                        INTEGER_DEFAULT_NULL,
                        INTEGER_DEFAULT_ZERO });

        db.execSQL(sqlCreateDetail);

        String sqlCreateTotal = buildCreateString(
                TBL_TOTAL,
                new String[] { T_TOTAL_ID, T_TOTAL_UNIVERSAL_ID, T_TOTAL_RECPT_ID,
                        T_TOTAL_RECPT_UNIV_ID, T_TOTAL_VALUE, T_TOTAL_UPDATED },
                new String[] {
                        INTEGER_PRIMARY_KEY,
                        TEXT_DEFAULT_NULL,
                        buildForeignKeyConstraint(TBL_RECEIPT, new String[] { T_RECPT_ID },
                                Action.CASCADE), TEXT_DEFAULT_NULL, INTEGER_NOT_NULL,
                        INTEGER_DEFAULT_ZERO });

        db.execSQL(sqlCreateTotal);

        // Populted DB:
        // 1.- Populate Region, Subregion and Town.
        XmlResourceParser parser = mContext.getResources().getXml(R.xml.spain);
        RegionXMLParser regionParser = new RegionXMLParser();

        CountryList countryList;

        try {
            countryList = regionParser.parse(parser);

            List<Region> regions = countryList.getRegions();
            List<Subregion> subregions;
            List<Town> towns;

            RegionList regionList;
            SubregionList subregionList;

            db.beginTransaction();
            try {
                long regionId, subregionId, townId;
                for (Region region : regions) {
                    regionId = db.insert(TBL_REGION, null, RegionDataAccess.getValues(region));
                    regionList = countryList.getRegion(region);
                    regionList.setId(regionId);

                    subregions = regionList.getSubregions();

                    for (Subregion subregion : subregions) {
                        subregionId = db.insert(TBL_SUBREGION, null,
                                SubregionDataAccess.getValues(subregion));
                        subregionList = countryList.getSubregion(subregion);
                        subregionList.setId(subregionId);

                        towns = subregionList.getTowns();
                        for (Town town : towns) {
                            db.insert(TBL_TOWN, null, TownDataAccess.getValues(town));
                        }
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 2.- Populate Chain.

        ChainsXmlParser chainsParser = new ChainsXmlParser();
        parser = mContext.getResources().getXml(R.xml.chains);

        try {
            List<Chain> chains = chainsParser.parse(parser);

            db.beginTransaction();
            try {
                for (Chain chain : chains) {
                    db.insert(TBL_CHAIN, null, ChainDataAccess.getValues(chain));
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 3.- Populate Category and Subcategory.
        CatSubcatXMLParser catSubcatParser = new CatSubcatXMLParser();
        parser = mContext.getResources().getXml(R.xml.cat_subcat_structure);

        try {
            CatSubcatStructure structure = catSubcatParser.parse(parser);

            db.beginTransaction();
            try {
                List<Subcategory> subcategories;
                List<Category> categories = structure.getCategories();
                long categoryId;
                for (Category category : categories) {
                    categoryId = db.insert(TBL_CATEGORY, null,
                            CategoryDataAccess.getValues(category));
                    category.setId(categoryId);
                    structure.updateCategoryId(category);

                    subcategories = structure.getSubcategories(category);
                    for (Subcategory subcategory : subcategories) {
                        db.insert(TBL_SUBCATEGORY, null,
                                SubcategoryDataAccess.getValues(subcategory));
                    }
                }

                db.setTransactionSuccessful();

            } finally {
                db.endTransaction();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            if (!db.isReadOnly()) db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public Context getContext() {
        return mContext;
    }

    private String buildCreateString(String tableName, String[] fields, String[] options) {
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("CREATE TABLE IF NOT EXISTS ");
        strBuff.append(tableName);
        strBuff.append(" (");
        int limit = fields.length;
        for (int pos = 0; pos < limit; ++pos) {
            strBuff.append(fields[pos]);
            strBuff.append(" ");
            strBuff.append(options[pos]);
            if (pos < limit - 1) {
                strBuff.append(", ");
            }
        }
        strBuff.append(")");
        return strBuff.toString();
    }

    private String buildForeignKeyConstraint(String foreignTable, String[] foreignFields,
            Action action) {
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("INTEGER REFERENCES ");
        strBuff.append(foreignTable);
        strBuff.append("(");
        int limit = foreignFields.length;
        for (int pos = 0; pos < limit; ++pos) {
            strBuff.append(foreignFields[pos]);
            if (pos < limit - 1) {
                strBuff.append(", ");
            }
        }
        strBuff.append(")");

        switch (action) {
        case CASCADE:
            strBuff.append(" ON DELETE CASCADE");
            break;
        case NO_ACTION:
            strBuff.append(" ON DELETE NO ACTION");
            break;
        case SET_NULL:
            strBuff.append(" ON DELETE SET NULL");
            break;
        case SET_DEFAULT:
            strBuff.append(" ON DELETE SET DEFAULT");
            break;
        case RESTRICT:
            strBuff.append(" ON DELETE RESTRICT");
        }
        return strBuff.toString();
    }

    static enum Action {
        CASCADE, NO_ACTION, SET_NULL, SET_DEFAULT, RESTRICT
    };
}
