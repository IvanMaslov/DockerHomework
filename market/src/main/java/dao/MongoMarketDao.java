package dao;

import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.Success;
import model.Stocks;
import org.bson.Document;
import rx.Observable;
import rx.functions.Action2;

public class MongoMarketDao {
    private final MongoCollection<Document> companies;

    public MongoMarketDao(MongoCollection<Document> companies) {
        this.companies = companies;
    }

    public Observable<Success> addCompany(String name, int stocksCount, int stocksPrice) {
        return companies
                .find(Filters.eq("companyName", name))
                .toObservable()
                .isEmpty()
                .flatMap(isEmpty -> {
                    if (isEmpty) {
                        return companies.insertOne(new Stocks(name, stocksCount, stocksPrice).toDocument());
                    } else {
                        return Observable.error(new IllegalArgumentException("Company '" + name + "' already exists"));
                    }
                });
    }

    public Observable<Stocks> getCompanies() {
        return companies.find().toObservable().map(Stocks::new);
    }

    public Observable<Stocks> getStocksInfo(String companyName) {
        return companies
                .find(Filters.eq("companyName", companyName))
                .toObservable()
                .map(Stocks::new);
    }

    public Observable<Success> addStocks(String companyName, int stocksCount) {
        return manageStocks(companyName, stocksCount, Stocks::add);
    }

    public Observable<Success> buyStocks(String companyName, int count) {
        return manageStocks(companyName, count, Stocks::minus);
    }

    public Observable<Success> changeStocksPrice(String companyName, int newStocksPrice) {
        return manageStocks(companyName, newStocksPrice, Stocks::changePrice);
    }

    private Observable<Success> manageStocks(String companyName, int parameter, Action2<Stocks, Integer> modification) {
        return companies
                .find(Filters.eq("companyName", companyName))
                .toObservable()
                .map(Stocks::new)
                .defaultIfEmpty(null)
                .flatMap(company -> {
                    if (company == null) {
                        return Observable.error(new IllegalArgumentException("Company '" + companyName + "' doesn't exists"));
                    } else {
                        modification.call(company, parameter);
                        return Observable.just(Success.SUCCESS);
                    }
                });
    }
}
