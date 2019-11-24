package logico;

import services.BootStrapServices;
import services.DataBaseServices;
import services.GestionDB;

import javax.naming.ldap.Control;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        BootStrapServices.startDb();
        DataBaseServices.getInstancia().testConexion();
        new Rutas().manejoRutas();
        new Filtros().manejarFiltros();
        new GestionDB<>();

        Usuario usuario = new Usuario("Admin", "Admin", "admin", true);
        Url url = new Url("youtube.com");
        String shortenedUrl = new Codec().encode(url.getUrlOriginal());
        url.setUrlBase62(shortenedUrl);
        url.setUrlIndexada(Integer.toString(Controladora.getInstance().getMisUrls().size()+1));
        url.setCreador(usuario);
        Controladora.getInstance().getMisUrls().add(url);


        //Codec codec = new Codec();

        //codec.encode("https://www.youtube.com/watch?v=5bBfEt5W1Nc");
        //codec.encode("https://www.youtube.com/watch?v=5bBfEt5W1ac");
    }

}
