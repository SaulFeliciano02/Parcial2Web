package soap;

import com.sun.net.httpserver.HttpContext;
import org.eclipse.jetty.http.spi.HttpSpiContextHandler;
import org.eclipse.jetty.http.spi.JettyHttpContext;
import org.eclipse.jetty.http.spi.JettyHttpServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import javax.xml.ws.Endpoint;
import java.lang.reflect.Method;

public class SoapService {

    public static void init() throws Exception {

        //inicializando el servidor
        Server server = new Server(7777);
        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
        server.setHandler(contextHandlerCollection);
        server.start();

        //El contexto donde estoy agrupando.
        HttpContext context = build(server);

        //El o los servicios que estoy agrupando en ese contexto
        URLWebServiceImpl urlWebServiceImpl = new URLWebServiceImpl();
        Endpoint endpoint = Endpoint.create(urlWebServiceImpl);
        endpoint.publish(context);
        //Para acceder al wsdl en http://localhost:7777/ws/URLWebServiceImpl?wsdl

    }

    private static HttpContext build(Server server) throws Exception {
        JettyHttpServer jettyHttpServer = new JettyHttpServer(server, true);
        JettyHttpContext ctx = (JettyHttpContext) jettyHttpServer.createContext("/ws");
        Method method = JettyHttpContext.class.getDeclaredMethod("getJettyContextHandler");
        method.setAccessible(true);
        HttpSpiContextHandler contextHandler = (HttpSpiContextHandler) method.invoke(ctx);
        contextHandler.start();
        return ctx;
    }

}
