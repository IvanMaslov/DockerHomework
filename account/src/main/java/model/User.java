package model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class User {
    private int money;
    private final Map<String, Stocks> stocks = new HashMap<>();

    public User(int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public Collection<Stocks> getStocks() {
        return stocks.values();
    }

    public void addMoney(int addition) {
        money += addition;
    }

    public void buyStocks(String companyName, int price, int count) {
        if (price * count > money) {
            throw new IllegalArgumentException("Not enough money");
        }
        Stocks current = stocks.getOrDefault(companyName, new Stocks(companyName, 0, price));
        stocks.put(companyName, current.add(count));
        money -= price * count;
    }

    public void sellStocks(String companyName, int price, int count) {
        if (!stocks.containsKey(companyName) || stocks.get(companyName).getCount() < count) {
            throw new IllegalArgumentException("Not enough stocks");
        }
        Stocks current = stocks.get(companyName);
        stocks.put(companyName, current.minus(count));
        money += price * count;
    }
}
