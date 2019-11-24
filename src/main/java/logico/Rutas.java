package logico;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.apache.commons.codec.digest.DigestUtils;
import services.GestionDB;
import services.UsuarioServices;
import spark.Session;
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Rutas {
    public void manejoRutas(){
        final Configuration configuration = new Configuration(new Version(2, 3, 0));
        //configuration.setClassForTemplateLoading(this.getClass(), "/");
        try {
            configuration.setDirectoryForTemplateLoading(new File(
                    "src/main/java/resources/spark/template/freemarker"));
        } catch(IOException e) {
            e.printStackTrace();
        }

        UsuarioServices usuarioServices = new UsuarioServices();

        Spark.get("/", (request, response) ->{
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedUser", request.session(true).attribute("usuario"));
            return getPlantilla(configuration, attributes, "index.ftl");
        });

        Spark.get("/shorty.com/:index", (request, response) -> {
            String urlShort = request.pathInfo();
            System.out.println(urlShort);
            Url url = Controladora.getInstance().findUrlByShort(urlShort);
            if(url != null){
                System.out.println("Going to..." + url.getUrlOriginal());
                response.redirect("http//" + url.getUrlOriginal());
            }
            else{
                System.out.println("Going nowhere!");
                response.redirect("/");
            }
            return "";
        });

        /**
         * Metodos Get y Post para logearse.
         */
        Spark.get("/login", (request, response) -> {
            String warningText = "";
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("warningText", warningText);
            return getPlantilla(configuration, attributes, "login.ftl");
        });

        Spark.post("/login", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");
            String passwordHash = DigestUtils.md5Hex(password);
            if(!usuarioServices.validatePassword(username, passwordHash)){
                String warningText = "Usuario o contrasena incorrectos.";
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("warningText", warningText);
                return getPlantilla(configuration, attributes, "login.ftl");
            }

            Session session=request.session(true);
            Usuario usuario = usuarioServices.searchUserByUsername(username);
            session.attribute("usuario", usuario);
            System.out.println("Hola");
            String remember = request.queryParams("remember");

            if(remember != null){
                response.cookie("usuario_id", usuario.getId(), 604800000);
            }
            response.redirect("/");
            return "";
        });

        /**
         *Metodos Get y Post para registrarse.
         */
        Spark.get("/register", (request, response) -> {
            String warningText = "";
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("warningText", warningText);
            return getPlantilla(configuration, attributes, "register.ftl");
        });

        Spark.post("/register", (request, response) -> {
            String nombre = request.queryParams("first_name") + " " + request.queryParams("last_name");
            String username = request.queryParams("username");
            if(!usuarioServices.validateUsername(username)){
                String warningText = "Usuario ya existe";
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("warningText", warningText);
                return getPlantilla(configuration, attributes, "register.ftl");
            }
            String password = request.queryParams("password");
            String confirmPassword = request.queryParams("confirm_password");
            if(!password.equals(confirmPassword)){
                String warningText = "Las contraseñas no coinciden";
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("warningText", warningText);
                return getPlantilla(configuration, attributes, "register.ftl");
            }
            String hashedPassword = DigestUtils.md5Hex(password);
            Usuario usuario = new Usuario(username, nombre, hashedPassword, false);
            usuario.setId(UUID.randomUUID().toString());
            usuarioServices.crear(usuario);
            //Controladora.getInstance().getMisUsuarios().add(usuario);
            Session session=request.session(true);
            session.attribute("usuario", usuario);
            response.redirect("/");
            return "";
        });

        /**
         * Metodo get para terminar la sesion.
         */
        Spark.get("/disconnect", (request, response) -> {
            Session session=request.session(true);
            session.invalidate();
            response.removeCookie("usuario_id");
            response.redirect("/menu/1");
            return "";
        });
    }

    public StringWriter getPlantilla(Configuration configuration, Map<String, Object> model, String templatePath) throws IOException, TemplateException {
        Template plantillaPrincipal = configuration.getTemplate(templatePath);
        StringWriter writer = new StringWriter();
        plantillaPrincipal.process(model, writer);
        return writer;
    }


}
