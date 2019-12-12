package logico;

import services.JWTServices;
import services.UrlServices;
import services.UsuarioServices;

import java.util.List;

import static spark.Spark.before;
import static spark.Spark.halt;

public class Filtros {
    public void manejarFiltros(){
        before((request, response) -> {
            Usuario usuario = request.session().attribute("usuario");
            String id = request.cookie("usuario_id");
            if(id != null && usuario == null){
                //String unhashedUsername = DigestUtils.getDigest(username);
                Usuario userLog = new UsuarioServices().searchUserById(id);
                request.session(true).attribute("usuario", userLog);
            }
        });

        before("/stats/:id", (request, response) -> {
            Usuario loggedUser = request.session().attribute("usuario");
            String urlid = request.params("id");
            Url url = new UrlServices().findUrlById(urlid);
            if(url == null){
                halt("No tiene las credenciales para ingresar aqui");
            }
//            if(loggedUser == null){
//                List<Url> anonUrl = request.session().attribute("anonUrl");
//                if(anonUrl != null){
//                    //Url url = new UrlServices().findUrlById(urlid);
//                    boolean found = false;
//                    for (Url u: anonUrl) {
//                        if (u.getUrlIndexada()==url.getUrlIndexada()){
//                            found=true;
//                            break;
//                        }
//                    }
//                    if(!found) {
//                        halt(401, "No tiene las credenciales para ingresar aqui");
//                    }
//                }
//                else{
//                    halt(401,"No tiene las credenciales para ingresar aqui");
//                }
//            }
//            else if(loggedUser.getId() != url.getCreador().getId() && !loggedUser.isAdministrador()){
//                halt("No tiene las credenciales para ingresar aqui");
//            }
        });

        before("/administradores", (request, response) -> {
            Usuario usuario = request.session().attribute("usuario");
            if(usuario == null){
                halt("No tiene las credenciales para ingresar aqui");
            }
            else if(!usuario.isAdministrador()){
                halt("No tiene las credenciales para ingresar aqui");
            }
        });

        /**before("/shorty.com/*", (request, response) -> {
            System.out.println("I made it here");
           String urlShort = request.pathInfo();
           Url url = Controladora.getInstance().findUrlByShort(urlShort);
           if(url != null){
               response.redirect(url.getUrlOriginal());
           }
           else{
               response.redirect("/");
           }
        });**/
    }
}
