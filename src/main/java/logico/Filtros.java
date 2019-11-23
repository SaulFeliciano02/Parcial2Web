package logico;

import static spark.Spark.before;

public class Filtros {
    public void manejarFiltros(){
        before((request, response) -> {
           String urlShort = request.pathInfo();
           Url url = Controladora.getInstance().findUrlByShort(urlShort);
           response.redirect(url.getUrlOriginal());
        });
    }
}
