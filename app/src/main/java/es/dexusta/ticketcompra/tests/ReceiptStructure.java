package es.dexusta.ticketcompra.tests;

import java.util.Arrays;

import es.dexusta.ticketcompra.model.Receipt;

/**
 * Created by asincrono on 02/05/14.
 */
public class ReceiptStructure {
    private long              shopId;
    private String            timestamp;
    private DetailStructure[] details;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public DetailStructure[] getDetails() {
        return details;
    }

    public void setDetails(DetailStructure[] details) {
        this.details = details;
    }

    public Receipt getReceipt() {
        Receipt receipt = new Receipt();

        receipt.setTimestamp(timestamp);
        receipt.setShopId(shopId);

        return receipt;
    }

    @Override
    public String toString() {
        return "ReceiptStructure{" +
                "shopId=" + shopId +
                ", timestamp='" + timestamp + '\'' +
                ", details=" + Arrays.toString(details) +
                '}';
    }
}
