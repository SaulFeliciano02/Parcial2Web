package soap;

import logico.Url;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface URLWebService {

    @WebMethod
    List<Url> getUrlsFromUser(String username);

    @WebMethod
    String shortUrl(String url, String username);

}
