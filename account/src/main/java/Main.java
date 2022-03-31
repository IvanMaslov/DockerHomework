import dao.AccountDao;
import http.AccountHttpServer;
import http.MarketHttpClient;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

public class Main {
    public static void main(String[] args) {
        AccountHttpServer server = new AccountHttpServer(new AccountDao(new MarketHttpClient()));

        HttpServer
                .newServer(8081)
                .start((req, resp) -> {
                    Observable<String> response = server.getResponse(req);
                    return resp.writeString(response.map(r -> r + System.lineSeparator()));
                })
                .awaitShutdown();
    }
}
