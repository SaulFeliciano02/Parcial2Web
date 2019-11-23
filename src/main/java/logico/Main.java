package logico;

import services.BootStrapServices;
import services.DataBaseServices;
import services.GestionDB;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        BootStrapServices.startDb();
        DataBaseServices.getInstancia().testConexion();
        new Rutas().manejoRutas();
        new GestionDB<>();


        Codec codec = new Codec();

        codec.encode("https://www.youtube.com/watch?v=5bBfEt5W1Nc");
        codec.encode("https://www.youtube.com/watch?v=5bBfEt5W1ac");
    }

}
