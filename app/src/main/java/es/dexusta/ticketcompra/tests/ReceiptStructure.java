package es.dexusta.ticketcompra.tests;

import java.util.List;

/**
 * Created by asincrono on 02/05/14.
 */
public class ReceiptStructure {
    private long shopId;
    private String timestamp;
    private List<DetailStructure> details;

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

    public List<DetailStructure> getDetails() {
        return details;
    }

    public void setDetails(List<DetailStructure> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ReceiptStructure{" +
                "shopId=" + shopId +
                ", timestamp='" + timestamp + '\'' +
                ", details=" + details +
                '}';
    }
}
