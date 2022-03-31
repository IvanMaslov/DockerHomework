package model;

import org.bson.Document;

public class Stocks {
    private String companyName;
    private int count;
    private int price;

    public Stocks(Document document) {
        this(document.getString("companyName"), document.getInteger("count"), document.getInteger("price"));
    }

    public Stocks(String companyName, int count, int price) {
        this.companyName = companyName;
        this.count = count;
        this.price = price;
    }

    public Document toDocument() {
        return new Document()
                .append("companyName", companyName)
                .append("count", count)
                .append("price", price);
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }

    public void add(int stocksCount) {
        this.count += stocksCount;
    }

    public void minus(int stocksCount) {
        if (count >= stocksCount) {
            this.count -= stocksCount;
        }
    }

    public void changePrice(int newStockPrice) {
        this.price = newStockPrice;
    }

    @Override
    public String toString() {
        return "Stocks {\n" +
                "  companyName : " + companyName + ",\n" +
                "  count : " + count + ",\n" +
                "  price : " + price + "\n" +
                "}";
    }
}
