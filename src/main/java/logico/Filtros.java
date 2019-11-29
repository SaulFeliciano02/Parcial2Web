package logico;

import services.UrlServices;
import services.UsuarioServices;

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
            if(loggedUser == null){
                halt("No tiene las credenciales para ingresar aqui");
            }
            else if(loggedUser.getId() != url.getCreador().getId() && !loggedUser.isAdministrador()){
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
