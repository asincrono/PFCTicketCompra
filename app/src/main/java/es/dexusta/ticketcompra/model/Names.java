package es.dexusta.ticketcompra.model;

public class Names {
    public static final String DATA_KIND_NAME           = "data";
    public static final String FIELD_DATA_ID            = "d_id";
    public static final String FIELD_DATA_NAME          = "d_name";
    public static final String FIELD_DATA_VALUE         = "d_value";

    public static final String USER_KIND_NAME           = "user";
    public static final String FIELD_USER_ID            = "u_id";
    public static final String FIELD_USER_NAME          = "u_name";
    public static final String FIELD_USER_EMAIL         = "u_email";

    public static final String CAT_KIND_NAME            = "category";
    public static final String FIELD_CAT_ID             = "c_id";
    public static final String FIELD_CAT_NAME           = "c_name";
    public static final String FIELD_CAT_DESCRIPTION    = "c_description";

    public static final String SUBCAT_KIND_NAME         = "subcategory";
    public static final String FIELD_SUBCAT_ID          = "s_id";
    public static final String FIELD_SUBCAT_CAT_ID      = "s_category_id";
    public static final String FIELD_SUBCAT_NAME        = "s_name";
    public static final String FIELD_SUBCAT_DESCRIPTION = "s_description";

    public static final String CHAIN_KIND_NAME          = "chain";
    public static final String FIELD_CHAIN_ID           = "ch_id";
    public static final String FIELD_CHAIN_NAME         = "ch_name";
    public static final String FIELD_CHAIN_CODE         = "ch_code";

    public static final String SHOP_KIND_NAME           = "shop";
    public static final String FIELD_SHOP_ID            = "sh_id";
    public static final String FIELD_SHOP_NAME          = "sh_name";
    public static final String FIELD_SHOP_CHAIN_ID      = "sh_chain_id";
    public static final String FIELD_SHOP_LATITUDE      = "sh_latitude";
    public static final String FIELD_SHOP_LONGITUDE     = "sh_longitude";
    public static final String FIELD_SHOP_ADDRESS       = "sh_address";

    public static final String PROD_KIND_NAME           = "product";
    public static final String FIELD_PROD_ID            = "p_id";
    public static final String FIELD_PROD_NAME          = "p_name";
    public static final String FIELD_PROD_SUBCAT_ID     = "p_subcat_id";
    public static final String FIELD_PROD_DESCRIPTION   = "p_desciption";
    public static final String FIELD_PROD_ARTNUMBER     = "p_artnumber";

    public static final String PRICE_KIND_NAME          = "price";
    public static final String FIELD_PRICE_ID           = "pr_id";
    public static final String FIELD_PRICE_PROD_ID      = "pr_prod_id";
    public static final String FIELD_PRICE_VALUE        = "pr_value";

    public static final String RECPT_KIND_NAME          = "receipt";
    public static final String FIELD_RECPT_ID           = "r_id";
    public static final String FIELD_RECPT_USER_ID      = "r_user_id";
    public static final String FIELD_RECPT_SHOP_ID      = "r_shop_id";
    public static final String FIELD_RECPT_TIMESTAMP    = "r_timestamp";

    public static final String DETAIL_KIND_NAME         = "detail";
    public static final String FIELD_DETAIL_ID          = "d_id";
    public static final String FIELD_DETAIL_RECPT_ID    = "d_receipt_id";
    public static final String FIELD_DETAIL_PROD_ID     = "d_product_id";
    public static final String FIELD_DETAIL_PRICE       = "d_price";
    public static final String FIELD_DETAIL_UNITS       = "d_units";
    public static final String FIELD_DETAIL_WEIGHT      = "d_weight";
    public static final String FIELD_DETAIL_VOLUME      = "d_volume";

    public static final String TOTAL_KIND_NAME          = "total";
    public static final String FIELD_TOTAL_ID           = "t_id";
    public static final String FIELD_TOTAL_RECPT_ID     = "t_receipt_id";
    public static final String FIELD_TOTAL_VALUE        = "t_value";
}
