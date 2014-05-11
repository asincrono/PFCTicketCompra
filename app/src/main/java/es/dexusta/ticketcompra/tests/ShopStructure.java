package es.dexusta.ticketcompra.tests;

import java.util.Arrays;

import es.dexusta.ticketcompra.model.Shop;

/**
 * Created by asincrono on 07/05/14.
 */
public class ShopStructure {
    private long               shopId;
    private long               townId;
    private long               chainId;
    private String             address;
    private String             townName;
    private String             chainName;
    private ReceiptStructure[] receipts;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTownId() {
        return townId;
    }

    public void setTownId(long townId) {
        this.townId = townId;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public long getChainId() {
        return chainId;
    }

    public void setChainId(long chainId) {
        this.chainId = chainId;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public ReceiptStructure[] getReceipts() {
        return receipts;
    }

    public void setReceipts(ReceiptStructure[] receipts) {
        this.receipts = receipts;
    }

    public Shop getShop() {
        Shop shop = new Shop();

        shop.setAddress(address);
        shop.setTownId(townId);
        shop.setTownName(townName);
        shop.setChainId(chainId);

        return shop;
    }

    @Override
    public String toString() {
        return "ShopStructure{" +
                "shopId=" + shopId +
                ", address='" + address + '\'' +
                ", townName='" + townName + '\'' +
                ", chainName='" + chainName + '\'' +
                ", receipts=" + Arrays.toString(receipts) +
                '}';
    }
}
