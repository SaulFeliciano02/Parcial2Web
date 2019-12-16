package soap;

import logico.Codec;
import logico.Url;
import logico.Usuario;
import services.UrlServices;
import services.UsuarioServices;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

@WebService(endpointInterface = "soap.URLWebService")
public class URLWebServiceImpl implements URLWebService {


    @Override
    public List<Url> getUrlsFromUser(String username) {
        List<Url> urls = new UrlServices().getUrlByOnlyUsername(username);//new UsuarioServices().searchUserByUsername(username).getUrlCreadas();
        return urls;
    }

    @Override
    public String shortUrl(String originalUrl, String username) {

        if(originalUrl.isEmpty() || username.isEmpty())
        {
            return null;
        }

        Usuario user =  new UsuarioServices().searchUserByUsername(username);

        if(originalUrl.contains("https://")){
            originalUrl = originalUrl.substring(8);
        }
        else if (originalUrl.contains("http://")) {
            originalUrl = originalUrl.substring(7);
        }
        Url url = new Url(originalUrl);
        String shortenedUrl = new Codec().encode(originalUrl);

        url.setUrlBase62(shortenedUrl);
        List<Url> allUrls = new UrlServices().getAllUrls();
        long id;
        if(allUrls.size() == 0)
        {
            id = 1;
        }
        else
        {
            id = Long.parseLong(allUrls.get(allUrls.size()-1).getUrlIndexada()) + 1;
        }
        url.setUrlIndexada(Long.toString(id));

//        Usuario usuario = request.session().attribute("usuario");
        if(user != null){
            url.setCreador(user);
            //usuario.getUrlCreadas().add(url);
            new UrlServices().crear(url);
            //usuarioServices.editar(usuario);
        }
//        else{
//            List<Url> anonUrl = (List<Url>) request.session().attribute("anonUrl");
//            if(anonUrl==null){
//                anonUrl = new ArrayList<Url>();
//            }
//            anonUrl.add(url);
//            request.session().attribute("anonUrl", anonUrl);
//            new UrlServices().crear(url);
//        }


        return url.getUrlBase62();
    }
}
