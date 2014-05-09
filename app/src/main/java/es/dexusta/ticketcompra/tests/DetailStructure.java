package es.dexusta.ticketcompra.tests;

/**
 * Created by asincrono on 02/05/14.
 */
public class DetailStructure {
    private long price;
    private long units;
    private ProductStructure product;

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getUnits() {
        return units;
    }

    public void setUnits(long units) {
        this.units = units;
    }

    public ProductStructure getProduct() {
        return product;
    }

    public void setProduct(ProductStructure product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "DetailStructure{" +
                "price=" + price +
                ", units=" + units +
                ", product=" + product +
                '}';
    }
}
