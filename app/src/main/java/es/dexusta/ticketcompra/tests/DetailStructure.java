package es.dexusta.ticketcompra.tests;

import es.dexusta.ticketcompra.model.Detail;

/**
 * Created by asincrono on 02/05/14.
 */
public class DetailStructure {
    private long   receiptId;
    private long   productId;
    private int    price;
    private int    units;
    private String productName;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(long receiptId) {
        this.receiptId = receiptId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productNane) {
        this.productName = productName;
    }

    public Detail getDetail() {
        Detail detail = new Detail();

        detail.setReceiptId(receiptId);
        detail.setProductId(productId);
        detail.setProductName(productName);
        detail.setUnits(units);
        detail.setPrice(price);

        return detail;
    }

    @Override
    public String toString() {
        return "DetailStructure{" +
                "price=" + price +
                ", units=" + units +
                ", productName='" + productName + '\'' +
                '}';
    }
}
