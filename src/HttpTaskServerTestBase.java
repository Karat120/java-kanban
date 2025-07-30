import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.IOException;

public abstract class HttpTaskServerTestBase {
    protected InMemoryTaskManager manager;
    protected HttpTaskServer server;

    @BeforeEach
    public void startServer() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }
}
