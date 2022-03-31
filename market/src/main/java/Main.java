import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import dao.MongoMarketDao;
import io.reactivex.netty.protocol.http.server.HttpServer;
import http.MarketHttpServer;
import org.bson.Document;
import rx.Observable;

public class Main {
    public static void main(String[] args) {

        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("market");
        MongoCollection<Document> companies = database.getCollection("companies");
        MongoMarketDao marketDao = new MongoMarketDao(companies);
        MarketHttpServer server = new MarketHttpServer(marketDao);

        HttpServer
                .newServer(8080)
                .start((req, resp) -> {
                    Observable<String> response = server.getResponse(req);
                    return resp.writeString(response.map(r -> r + System.lineSeparator()));
                })
                .awaitShutdown();
    }
}
