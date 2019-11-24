package logico;

import static spark.Spark.before;

public class Filtros {
    public void manejarFiltros(){
        before("/shorty.com/*", (request, response) -> {
            System.out.println("I made it here");
           String urlShort = request.pathInfo();
           Url url = Controladora.getInstance().findUrlByShort(urlShort);
           if(url != null){
               response.redirect(url.getUrlOriginal());
           }
           else{
               response.redirect("/");
           }
        });
    }
}
