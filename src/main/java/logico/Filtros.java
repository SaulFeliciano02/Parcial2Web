package logico;

import services.UsuarioServices;

import static spark.Spark.before;

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
