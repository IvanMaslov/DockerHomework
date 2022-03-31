package http;

import com.mongodb.rx.client.Success;
import dao.AccountDao;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;
import rx.functions.Func4;

import java.util.*;

public class AccountHttpServer {
    private final AccountDao dao;

    public AccountHttpServer(AccountDao dao) {
        this.dao = dao;
    }

    public <T> Observable<String> getResponse(HttpServerRequest<T> request) {
        String path = request.getDecodedPath().substring(1);
        if (path.equals("add_user")) {
            return addUser(request);
        }
        if (path.equals("add_money")) {
            return addMoney(request);
        }
        if (path.equals("get_all_money")) {
            return getAllMoney(request);
        }
        if (path.equals("get_user_stocks_info")) {
            return getUserStocksInfo(request);
        }
        if (path.equals("buy_stocks")) {
            return buyStocks(request);
        }
        if (path.equals("sell_stocks")) {
            return sellStocks(request);
        }
        return Observable.just("Unsupported request : " + path);
    }

    private <T> Observable<String> addUser(HttpServerRequest<T> request) {
        Optional<String> error = checkRequestParameters(request, Collections.singletonList("id"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        long id = getLongParam(request, "id");
        return dao.addUser(id).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> addMoney(HttpServerRequest<T> request) {
        Optional<String> error = checkRequestParameters(request, Arrays.asList("id", "count"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        long id = getLongParam(request, "id");
        int count = getIntParam(request, "count");
        return dao.addMoney(id, count).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> getAllMoney(HttpServerRequest<T> request) {
        Optional<String> error = checkRequestParameters(request, Collections.singletonList("id"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        long id = getLongParam(request, "id");
        return dao.getAllMoney(id).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    private <T> Observable<String> getUserStocksInfo(HttpServerRequest<T> request) {
        Optional<String> error = checkRequestParameters(request, Collections.singletonList("id"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        long id = getLongParam(request, "id");
        return dao.getUserStocksInfo(id).map(Objects::toString).reduce("", (s1, s2) -> s1 + ",\n" + s2);
    }

    private <T> Observable<String> buyStocks(HttpServerRequest<T> request) {
        return stocksOperations(request, AccountDao::buyStocks);
    }

    private <T> Observable<String> sellStocks(HttpServerRequest<T> request) {
        return stocksOperations(request, AccountDao::sellStocks);
    }

    private <T> Observable<String> stocksOperations(HttpServerRequest<T> request, Func4<AccountDao, Long, String, Integer, Observable<Success>> action) {
        Optional<String> error = checkRequestParameters(request, Arrays.asList("id", "company_name", "count"));
        if (error.isPresent()) {
            return Observable.just(error.get());
        }

        long id = getLongParam(request, "id");
        String companyName = getQueryParam(request, "company_name");
        int count = getIntParam(request, "count");

        return action.call(dao, id, companyName, count).map(Objects::toString).onErrorReturn(Throwable::getMessage);
    }

    public static <T> String getQueryParam(HttpServerRequest<T> request, String param) {
        return request.getQueryParameters().get(param).get(0);
    }

    public static <T> int getIntParam(HttpServerRequest<T> request, String param) {
        return Integer.parseInt(getQueryParam(request, param));
    }

    public static <T> long getLongParam(HttpServerRequest<T> request, String param) {
        return Long.parseLong(getQueryParam(request, param));
    }

    public static <T> Optional<String> checkRequestParameters(HttpServerRequest<T> request, List<String> requiredParameters) {
        return requiredParameters.stream()
                .filter(key -> !request.getQueryParameters().containsKey(key))
                .reduce((s1, s2) -> s1 + ", " + s2)
                .map(s -> s + " parameters not found");
    }
}
