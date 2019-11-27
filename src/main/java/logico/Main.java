package logico;

import services.BootStrapServices;
import services.DataBaseServices;
import services.GestionDB;
import services.UsuarioServices;
import spark.Spark;

import javax.naming.ldap.Control;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        Spark.staticFileLocation("/publico");
        BootStrapServices.startDb();
        DataBaseServices.getInstancia().testConexion();
        new Rutas().manejoRutas();
        new Filtros().manejarFiltros();
        new GestionDB<>();
        UsuarioServices usuarioServices = new UsuarioServices();

        Usuario usuario = null;
        if(usuarioServices.getSizeUsuario() == 0){
            //usuario = new Usuario("Admin", "Admin", "admin", true);
            System.out.println("Creando a un admin...");
        }
        else{
            System.out.println("El admin no ha sido creado");
        }

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
