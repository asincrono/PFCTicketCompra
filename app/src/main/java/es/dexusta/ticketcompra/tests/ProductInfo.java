package es.dexusta.ticketcompra.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.model.Product;

/**
 * Created by asincrono on 11/05/14.
 */
public class ProductInfo {
    private HashMap<String, Product> mData = new HashMap<String, Product>();
    private List<Product> mProducts;

    private SubcategoryInfo mSubcategoryInfo;


    public ProductInfo(ProductStructure[] productStructures, SubcategoryInfo subcategoryInfo) {
        mSubcategoryInfo = subcategoryInfo;
        mProducts = new ArrayList<Product>(productStructures.length);
        Product product;
        for (ProductStructure productStructure : productStructures) {
            product = getProduct(productStructure);

            mData.put(productStructure.getName(), product);
            mProducts.add(product);
        }

        mSubcategoryInfo = null;
    }

    private Product getProduct(ProductStructure productStructure) {
        Product product = new Product();

        product.setName(productStructure.getName());
        product.setDescription(productStructure.getDescription());
        product.setSubcategoryId(mSubcategoryInfo.getId(productStructure.getSubcategoryName()));

        return product;
    }

    public void add(List<Product> products) {
        for (Product product : products) {
            mData.put(product.getName(), product);
        }
    }

    public long getId(String name) {
        Product product = mData.get(name);
        return (product == null) ? -1 : product.getId();
    }

    public List<Product> getProducts() {
        return mProducts;
    }
}
