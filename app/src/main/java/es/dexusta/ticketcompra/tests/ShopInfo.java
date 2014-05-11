package es.dexusta.ticketcompra.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Receipt;
import es.dexusta.ticketcompra.model.Shop;

/**
 * Created by asincrono on 11/05/14.
 */
public class ShopInfo {

    private HashMap<Long, ShopStructure> mData;


    private List<Shop> mShops;

    private HashMap<Shop, List<Receipt>>   mReceiptMap = new HashMap<Shop, List<Receipt>>();
    private HashMap<Receipt, List<Detail>> mDetailMap  = new HashMap<Receipt, List<Detail>>();

    private ChainInfo   mChainInfo;
    private ProductInfo mProductInfo;
    private TownInfo    mTownInfo;

    public ShopInfo(ShopStructure[] data, ChainInfo chainInfo, ProductInfo productInfo, TownInfo townInfo) {
        mChainInfo = chainInfo;
        mProductInfo = productInfo;
        mTownInfo = townInfo;

        mData = new HashMap<Long, ShopStructure>(data.length);
        mShops = new ArrayList<Shop>(data.length);

        Shop shop;
        for (ShopStructure shopStructure : data) {
            mData.put(shopStructure.getShopId(), shopStructure);

            shop = getShop(shopStructure);
            mShops.add(shop);

            ReceiptStructure[] receiptStructures = shopStructure.getReceipts();
            List<Receipt> receipts = new ArrayList<Receipt>(receiptStructures.length);
            Receipt receipt;
            for (ReceiptStructure receiptStructure : receiptStructures) {
                receipt = getReceipt(receiptStructure);
                receipts.add(receipt);

                DetailStructure[] detailStructures = receiptStructure.getDetails();
                List<Detail> details = new ArrayList<Detail>(detailStructures.length);
                for (DetailStructure detailStructure : detailStructures) {
                    details.add(getDetail(detailStructure));
                }

                mDetailMap.put(receipt, details);
            }
            mReceiptMap.put(shop, receipts);
        }

        mChainInfo = null;
        mProductInfo = null;
        mTownInfo = null;
    }

    private Shop getShop(ShopStructure shopStructure) {
        Shop shop = new Shop();

        shop.setAddress(shopStructure.getAddress());
        shop.setChainId(mChainInfo.getId(shopStructure.getChainName()));
        shop.setTownName(shopStructure.getTownName());
        shop.setTownId(mTownInfo.getId(shopStructure.getTownName()));

        return shop;
    }

    private Receipt getReceipt(ReceiptStructure receiptStructure) {
        Receipt receipt = new Receipt();

        receipt.setTimestamp(receiptStructure.getTimestamp());

        return receipt;
    }

    private Detail getDetail(DetailStructure detailStructure) {
        Detail detail = new Detail();

        String productName = detailStructure.getProductName();
        detail.setProductName(productName);
        detail.setProductId(mProductInfo.getId(productName));

        detail.setUnits(detailStructure.getUnits());
        detail.setPrice(detailStructure.getPrice());

        return detail;
    }

    public void updateReceipts(Shop shop) {
        long shopId = shop.getId();
        List<Receipt> receipts = mReceiptMap.get(shop);

        for (Receipt receipt : receipts) {
            receipt.setShopId(shopId);
        }
    }

    private void updateDetails(Receipt receipt) {
        List<Detail> details = mDetailMap.get(receipt);

        long receiptId = receipt.getId();
        for (Detail detail : details) {
            detail.setReceiptId(receiptId);
        }
    }

    public List<Shop> getShops() {
        return mShops;
    }

    public List<Receipt> getReceipts(Shop shop) {
        updateReceipts(shop);
        return mReceiptMap.get(shop);
    }

    public List<Detail> getDetails(Receipt receipt) {
        updateDetails(receipt);
        return mDetailMap.get(receipt);
    }

}
