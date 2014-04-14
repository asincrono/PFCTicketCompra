package es.dexusta.ticketcompra.dataaccess;

public class Types {
    public static enum Operation {INSERT, UPDATE, DELETE}

    public static enum ReceiptInfoKeys {
        SHOP ("shop_name"),
        TIMESTAMP ("receipt_timestamp"),
        AMOUNT ("total_value");
        
        private final String key;
        
        private ReceiptInfoKeys(String key) {
            this.key = key; 
        }
        
        public String key() {
            return key;
        }
    }
}
