package es.dexusta.ticketcompra.tests;

/**
 * Created by asincrono on 07/05/14.
 */
public class ShopStructure {
    private String address;
    private String townName;
    private String chainName;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    @Override
    public String toString() {
        return "ShopStructure{" +
                "address='" + address + '\'' +
                ", townName='" + townName + '\'' +
                ", chainName='" + chainName + '\'' +
                '}';
    }
}
