package logico;

import services.BootStrapServices;
import services.DataBaseServices;
import services.GestionDB;
import services.UsuarioServices;
import spark.Spark;
import org.apache.commons.codec.digest.DigestUtils;
import services.*;

import javax.naming.ldap.Control;
import java.sql.SQLException;
import java.util.UUID;

import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws SQLException {
        port(getHerokuAssignedPort());
        Spark.staticFileLocation("/publico");
        BootStrapServices.startDb();
        DataBaseServices.getInstancia().testConexion();
        new Rutas().manejoRutas();
        new Filtros().manejarFiltros();
        new GestionDB<>();
        UsuarioServices usuarioServices = new UsuarioServices();
        VisitaServices visitaServices = new VisitaServices();

        Usuario usuario = null;
        if(usuarioServices.getSizeUsuario() == 0){
            usuario = new Usuario("Admin", "Admin", DigestUtils.md5Hex("admin"), true);
            usuario.setId(UUID.randomUUID().toString());
            Usuario usuarioAnonimo = new Usuario("Anonimo", "Anonimo", DigestUtils.md5Hex(usuario.getId()), false);
            usuarioAnonimo.setId(UUID.randomUUID().toString());
            usuarioServices.crear(usuario);
            usuarioServices.crear(usuarioAnonimo);
            System.out.println("Creando a un admin...");
        }
        else{
            System.out.println("El admin no ha sido creado");
        }

        long visitsByChrome = visitaServices.getSizeVisitaByBrowser("Chrome");
        long visitsByChromeAndPage = visitaServices.getSizeVisitaByShortUrlBrowser("/shorty.com/c", "Chrome");
        System.out.println("La cantidad de visitas por chrome es: " + visitsByChrome);
        System.out.println("La cantidad de visitas por chrome en la pag /shorty.com/c es: " + visitsByChromeAndPage);
    }

    /**
     * Metodo para setear el puerto en Heroku
     * @return
     */
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //Retorna el puerto por defecto en caso de no estar en Heroku.
    }
}
