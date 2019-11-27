package logico;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.hql.internal.ast.util.NodeTraverser;
import services.GestionDB;
import services.UrlServices;
import services.UsuarioServices;
import services.VisitaServices;
import spark.Session;
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import ua_parser.Parser;
import ua_parser.Client;

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
        UrlServices urlServices = new UrlServices();
        VisitaServices visitaServices = new VisitaServices();

        Spark.get("/", (request, response) ->{
            Map<String, Object> attributes = new HashMap<>();

            Usuario loggedUser = request.session(true).attribute("usuario");
            attributes.put("loggedUser", loggedUser);
            if(loggedUser != null){
                attributes.put("links", urlServices.getUrlByUser(loggedUser.getId()));
            }
            else{
                attributes.put("links", new ArrayList<>());
            }

            return getPlantilla(configuration, attributes, "index.ftl");
        });

        Spark.get("/shorty.com/:index", (request, response) -> {
            String urlShort = request.pathInfo();
            System.out.println(urlShort);
            Url url = urlServices.findUrlByShort(urlShort);
            if(url != null){
                System.out.println("Going to..." + url.getUrlOriginal());

//                Parser uaParser = new Parser();
//                Client c = uaParser.parse(request.userAgent());
//                String sistemaOperativo = c.os.family + " " + c.os.major;
//                String browser = c.userAgent.family;
//                String ip = request.ip();
//                String id = UUID.randomUUID().toString();
//                Visita visita = new Visita(url, sistemaOperativo, browser, ip);
//                visita.setId(id);
//                visitaServices.crear(visita);

                response.redirect("http://" + url.getUrlOriginal());
            }
            else{
                System.out.println("Going nowhere!");
                response.redirect("/");
            }
            return "";
        });

        Spark.post("/createUrl", (request, response) -> {
            String originalUrl = request.queryParams("originalUrl");
            Url url = new Url(originalUrl);
            String shortenedUrl = new Codec().encode(originalUrl);

            url.setUrlBase62(shortenedUrl);
            url.setUrlIndexada(Long.toString(urlServices.getSizeUrl()+1));

            Usuario usuario = request.session().attribute("usuario");
            if(usuario != null){
                url.setCreador(usuario);
                usuario.getUrlCreadas().add(url);
            }else{
                /**Usuario usuarioNotLogged = new Usuario("Anonimo", "Anonimo", "", false);
                usuarioServices.crear(usuarioNotLogged);
                url.setCreador(usuarioNotLogged);**/
            }

            urlServices.crear(url);
            usuarioServices.editar(usuario);
            response.redirect("/");
            //System.out.println(request.)
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
