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
import java.time.LocalDate;
import java.time.LocalTime;
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

            response.redirect("/menu/1");

            return "";
        });

        Spark.get("/menu/:pageNumber", (request, response) ->{
            int pageNumber = Integer.parseInt(request.params("pageNumber"));
            Map<String, Object> attributes = new HashMap<>();

            Usuario loggedUser = request.session(true).attribute("usuario");
            attributes.put("loggedUser", loggedUser);
            attributes.put("pageNumber", pageNumber);
            attributes.put("sizeAllLinks", urlServices.getSizeUrl());
            if(loggedUser == null){
                List<Url> anonUrl = request.session().attribute("anonUrl");
                if(anonUrl != null){
                    attributes.put("links", anonUrl);
                }
                else{
                    attributes.put("links", new ArrayList<>());
                }
            }
            else if(loggedUser.isAdministrador() == true){
                attributes.put("links", urlServices.getUrls(pageNumber));
            }
            else if(loggedUser != null){
                attributes.put("links", urlServices.getUrlByUser(loggedUser.getId(), pageNumber));
            }

            return getPlantilla(configuration, attributes, "index.ftl");
        });

        Spark.get("/administradores", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("listaUsuarios", usuarioServices.getUsuarios());
            return getPlantilla(configuration, attributes, "administrador.ftl");
        });

        Spark.post("/makeAdministrador/:username", (request, response) -> {
            String username = request.params("username");
            Usuario usu = usuarioServices.searchUserByUsername(username);
            boolean valor;
            if(request.queryParams("checkAdministrador") != null)
            {
                valor = true;
            }
            else{
                valor=false;
            }
            usu.setAdministrador(valor);
            usuarioServices.editar(usu);
            //new UserServices().actualizarUsuario(usu);
            response.redirect("/administradores");
            return "";
        });

        Spark.get("shorty/:index", (request, response) -> {
            String urlShort = request.params("index");
            System.out.println(urlShort);
            Url url = urlServices.findUrlByShort(urlShort);
            if(url != null){
                System.out.println("Going to..." + url.getUrlOriginal());

                Parser uaParser = new Parser();
                Client c = uaParser.parse(request.userAgent());
                String sistemaOperativo = c.os.family + " " + c.os.major;
                String browser = c.userAgent.family;
                String ip = request.ip();
                String id = UUID.randomUUID().toString();
                long hora = LocalTime.now().getHour();
                String dia = LocalDate.now().getDayOfWeek().toString();
                Visita visita = new Visita(url, sistemaOperativo, browser, ip, hora, dia);
                visita.setId(id);
                visitaServices.crear(visita);

                response.redirect("http://" + url.getUrlOriginal());
            }
            else{
                //List<Url> anonUrl = request.session().attribute();
                System.out.println("Going nowhere!");
                response.redirect("/");
            }
            return "";
        });

        Spark.post("/createUrl", (request, response) -> {
            String originalUrl = request.queryParams("originalUrl");
            originalUrl = originalUrl.substring(8);
            Url url = new Url(originalUrl);
            String shortenedUrl = new Codec().encode(originalUrl);

            url.setUrlBase62(shortenedUrl);
            List<Url> allUrls = urlServices.getAllUrls();
            long id = Long.parseLong(allUrls.get(allUrls.size()-1).getUrlIndexada()) + 1;
            url.setUrlIndexada(Long.toString(id));

            Usuario usuario = request.session().attribute("usuario");
            if(usuario != null){
                url.setCreador(usuario);
                //usuario.getUrlCreadas().add(url);
                urlServices.crear(url);
                //usuarioServices.editar(usuario);
            }
            else{
                List<Url> anonUrl = (List<Url>) request.session().attribute("anonUrl");
                if(anonUrl==null){
                    anonUrl = new ArrayList<Url>();
                }
                anonUrl.add(url);
                request.session().attribute("anonUrl", anonUrl);
                urlServices.crear(url);
            }


            response.redirect("/");
            //System.out.println(request.)
            return "";
        });

        Spark.get("/stats/:id", (request, response) -> {
            String urlid = request.params("id");
            Url url = urlServices.findUrlById(urlid);
            String urlShort = url.getUrlBase62();

            //Navegadores
            long chromeVisits = visitaServices.getSizeVisitaByShortUrlBrowser(urlShort, "Chrome");
            long operaVisits = visitaServices.getSizeVisitaByShortUrlBrowser(urlShort, "Opera");
            long firefoxVisits = visitaServices.getSizeVisitaByShortUrlBrowser(urlShort, "Firefox");
            long edgeVisits = visitaServices.getSizeVisitaByShortUrlBrowser(urlShort, "Edge");
            long safariVisits = visitaServices.getSizeVisitaByShortUrlBrowser(urlShort, "Safari");

            //Dias de la semana
            long mondayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "MONDAY");
            long tuesdayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "TUESDAY");
            long wednesdayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "WEDNESDAY");
            long thursdayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "THURSDAY");
            long fridayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "FRIDAY");
            long saturdayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "SATURDAY");
            long sundayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "SUNDAY");

            //Hora del dia
            long zero = visitaServices.getSizeByShortUrlHour(urlShort, 0);
            long one = visitaServices.getSizeByShortUrlHour(urlShort, 1);
            long two = visitaServices.getSizeByShortUrlHour(urlShort, 2);
            long three = visitaServices.getSizeByShortUrlHour(urlShort, 3);
            long four = visitaServices.getSizeByShortUrlHour(urlShort, 4);
            long five = visitaServices.getSizeByShortUrlHour(urlShort, 5);
            long six = visitaServices.getSizeByShortUrlHour(urlShort, 6);
            long seven = visitaServices.getSizeByShortUrlHour(urlShort, 7);
            long eight = visitaServices.getSizeByShortUrlHour(urlShort, 8);
            long nine = visitaServices.getSizeByShortUrlHour(urlShort, 9);
            long ten = visitaServices.getSizeByShortUrlHour(urlShort, 10);
            long eleven = visitaServices.getSizeByShortUrlHour(urlShort, 11);
            long twelve = visitaServices.getSizeByShortUrlHour(urlShort, 12);
            long thirteen = visitaServices.getSizeByShortUrlHour(urlShort, 13);
            long fourteen = visitaServices.getSizeByShortUrlHour(urlShort, 14);
            long fifteen = visitaServices.getSizeByShortUrlHour(urlShort, 15);
            long sixteen = visitaServices.getSizeByShortUrlHour(urlShort, 16);
            long seventeen = visitaServices.getSizeByShortUrlHour(urlShort, 17);
            long eightteen = visitaServices.getSizeByShortUrlHour(urlShort, 18);
            long nineteen = visitaServices.getSizeByShortUrlHour(urlShort, 19);
            long twenty = visitaServices.getSizeByShortUrlHour(urlShort, 20);
            long twenty_one = visitaServices.getSizeByShortUrlHour(urlShort, 21);
            long twenty_two = visitaServices.getSizeByShortUrlHour(urlShort, 22);
            long twenty_three = visitaServices.getSizeByShortUrlHour(urlShort, 23);

            Map<String, Object> attributes = new HashMap<>();
            Usuario loggedUser = request.session().attribute("usuario");
            attributes.put("loggedUser", loggedUser);

            attributes.put("chromeVisits", chromeVisits);
            attributes.put("operaVisits", operaVisits);
            attributes.put("firefoxVisits", firefoxVisits);
            attributes.put("edgeVisits", edgeVisits);
            attributes.put("safariVisits", safariVisits);

            attributes.put("mondayVisits", mondayVisits);
            attributes.put("tuesdayVisits", tuesdayVisits);
            attributes.put("wednesdayVisits", wednesdayVisits);
            attributes.put("thursdayVisits", thursdayVisits);
            attributes.put("fridayVisits", fridayVisits);
            attributes.put("saturdayVisits", saturdayVisits);
            attributes.put("sundayVisits", sundayVisits);

            attributes.put("zero", zero);
            attributes.put("one", one);
            attributes.put("two", two);
            attributes.put("three", three);
            attributes.put("four", four);
            attributes.put("five", five);
            attributes.put("six", six);
            attributes.put("seven", seven);
            attributes.put("eight", eight);
            attributes.put("nine", nine);
            attributes.put("ten", ten);
            attributes.put("eleven", eleven);
            attributes.put("twelve", twelve);
            attributes.put("thirteen", thirteen);
            attributes.put("fourteen", fourteen);
            attributes.put("fifteen", fifteen);
            attributes.put("sixteen", sixteen);
            attributes.put("seventeen", seventeen);
            attributes.put("eighteen", eightteen);
            attributes.put("nineteen", nineteen);
            attributes.put("twenty", twenty);
            attributes.put("twenty_one", twenty_one);
            attributes.put("twenty_two", twenty_two);
            attributes.put("twenty_three", twenty_three);


            return getPlantilla(configuration, attributes, "stats.ftl");
        });

        Spark.get("eliminarUrl/:index", (request, response) -> {
            String urlid = request.params("index");
            Url url = urlServices.findUrlById(urlid);
            List<Visita> visitas = visitaServices.getUrlsByVisit(urlid);
            System.out.println("El tamaño de las visitas es: " + visitas.size());
            for(Visita visita : visitas){
                if(visita != null){
                    System.out.println(visita.getId());
                    new GestionDB<Visita>(Visita.class).eliminar(visita.getId());
                }
            }
            new GestionDB<Url>(Url.class).eliminar(urlid);
            response.redirect("/");
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
            List<Url> anonUrl = request.session().attribute("anonUrl");
            if(anonUrl != null){
                for(Url url : anonUrl){
                    Url databaseUrl = urlServices.findUrlById(url.getUrlIndexada());
                    databaseUrl.setCreador(usuario);
                    urlServices.editar(databaseUrl);
                }
            }
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
            response.redirect("/");
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
