package logico;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.hql.internal.ast.util.NodeTraverser;
import services.*;
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

    UsuarioServices usuarioServices = new UsuarioServices();
    UrlServices urlServices = new UrlServices();
    VisitaServices visitaServices = new VisitaServices();

    public void manejoRutas(){
        final Configuration configuration = new Configuration(new Version(2, 3, 0));
        //configuration.setClassForTemplateLoading(this.getClass(), "/");
        try {
            configuration.setDirectoryForTemplateLoading(new File(
                    "src/main/java/resources/spark/template/freemarker"));
        } catch(IOException e) {
            e.printStackTrace();
        }

        Spark.path("/usuarios", () -> {
            Spark.get("/", (request, response) -> {
                return usuarioServices.getUsuarios();
            }, JsonUtilidades.json());

            Spark.get("/:id", (request, response) -> {
                return usuarioServices.searchUserById(request.params("id"));
            }, JsonUtilidades.json());

            Spark.get("/username/:username", (request, response) -> {
                return usuarioServices.searchUserByUsername(request.params("username"));
            }, JsonUtilidades.json());

            Spark.get("/confirmUsername/:username", (request, response) -> {
                String username = request.params("username");
                if(!usuarioServices.validateUsername(username)){
                    return false;
                }
                return true;
            });

            Spark.get("/confirmPassword/:confirmPassword/:password", (request, response) -> {
                String password = request.params("password");
                String confirmPassword = request.params("confirmPassword");
                if(!password.equals(confirmPassword)){
                    return false;
                }
                return true;
            });

            Spark.get("/validarLogeo/:username/:passwordHash", (request, response) -> {
                String username = request.params("username");
                String passwordHash = request.params("passwordHash");
                if(!usuarioServices.validatePassword(username, passwordHash)){
                    System.out.println("Klk");
                    return false;
                }
                else{
                    return true;
                }
            });

            Spark.post("/registrarUsuario", (request, response) -> {
                Usuario usuario = new Gson().fromJson(request.body(), Usuario.class);
                usuarioServices.crear(usuario);
                List<Url> anonUrl = request.session().attribute("anonUrl");
                if(anonUrl != null){
                    for(Url url : anonUrl){
                        Url databaseUrl = urlServices.findUrlById(url.getUrlIndexada());
                        databaseUrl.setCreador(usuario);
                        urlServices.editar(databaseUrl);
                    }
                }
                System.out.println("Creacion exitosa!");
                return "";
            });

            Spark.put("/activarAdministrador/:username/:checkAdministrador", (request, response) -> {
                String username = request.params("username");
                Usuario usu = usuarioServices.searchUserByUsername(username);
                boolean valor;
                System.out.println(request.params("checkAdministrador"));
                if(request.params("checkAdministrador") != null)
                {
                    System.out.println("El usuario sera convertido en administrador");
                    valor = true;
                }
                else{
                    System.out.println("El usuario no sera convertido en adminsitrador");
                    valor=false;
                }
                usu.setAdministrador(valor);
                usuarioServices.editar(usu);
                return "";
            });
        });

        Spark.path("/Urls", () -> {
            Spark.get("/", (request, response) -> {
                return urlServices.getAllUrls();
            }, JsonUtilidades.json());

            Spark.get("/pagination/:pageNumber", (request, response) -> {
                int pageNumber = Integer.parseInt(request.params("pageNumber"));
                return urlServices.getUrls(pageNumber);
            }, JsonUtilidades.json());

            Spark.get("/paginationID/:pageNumber/:id", (request, response) -> {
                int pageNumber = Integer.parseInt(request.params("pageNumber"));
                String id = request.params("id");
                return urlServices.getUrlByUser(id, pageNumber);
            }, JsonUtilidades.json());

            Spark.get("/size", (request, response) -> {
                return urlServices.getSizeUrl();
            });

            Spark.get("/anonUrls", (request, response) -> {
                List<Url> anonUrl = request.session().attribute("anonUrl");
                if(anonUrl != null){
                    return anonUrl;
                }
                else{
                    return null;
                }
            }, JsonUtilidades.json());

            Spark.post("/createUrl", (request, response) -> {
                Usuario usuario = new Gson().fromJson(request.body(), Usuario.class);
                String originalUrl = request.queryParams("originalUrl");

                if(originalUrl.contains("https://")){
                    originalUrl = originalUrl.substring(8);
                }
                else if (originalUrl.contains("http://")) {
                    originalUrl = originalUrl.substring(7);
                }
                Url url = new Url(originalUrl);
                String shortenedUrl = new Codec().encode(originalUrl);

                url.setUrlBase62(shortenedUrl);
                List<Url> allUrls = urlServices.getAllUrls();
                long id;
                if(allUrls.size() == 0) {
                    id = 1;
                }
                else {
                    id = Long.parseLong(allUrls.get(allUrls.size()-1).getUrlIndexada()) + 1;
                }
                url.setUrlIndexada(Long.toString(id));
                //Añadiendo datos del preview
                String baseUrl = "http://api.linkpreview.net/?key=5de79e90a156b442e87430922b367e4ad6f85640a7bca&q=";
                String previewUrl = baseUrl + originalUrl;
                HttpResponse<JsonNode> preview = Unirest.get(previewUrl)
                        .asJson();

                JSONObject myObj = preview.getBody().getObject();
                String imagenPreview = myObj.getString("image");
                String descripcionPreview = myObj.getString("description");
                url.setImagenPreview(imagenPreview);
                url.setDescripcionPreview(descripcionPreview);
                if(usuario != null){
                    url.setCreador(usuario);
                    urlServices.crear(url);
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
                return "";
            });

            Spark.delete("/delete/:index", (request, response) -> {
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
                return "";
            });
        });

        Spark.path("/Visitas", () -> {
            Spark.get("/shorty/:index/:usuarioID", (request, response) -> {
                String urlShort = request.params("index");
                String idUser = request.params("usuarioID");
                Usuario usuario = usuarioServices.searchUserById(idUser);
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
                }
                return url;
            }, JsonUtilidades.json());

            Spark.get("/statsDaysOfWeek/:id/", (request, response) -> {
                String urlid = request.params("id");
                Url url = urlServices.findUrlById(urlid);
                String urlShort = url.getUrlBase62();
                //Dias de la semana
                long mondayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "MONDAY");
                long tuesdayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "TUESDAY");
                long wednesdayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "WEDNESDAY");
                long thursdayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "THURSDAY");
                long fridayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "FRIDAY");
                long saturdayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "SATURDAY");
                long sundayVisits = visitaServices.getSizeByShortUrlDay(urlShort, "SUNDAY");

                List<Long> daysOfWeek = new ArrayList<>();
                daysOfWeek.add(mondayVisits);
                daysOfWeek.add(tuesdayVisits);
                daysOfWeek.add(wednesdayVisits);
                daysOfWeek.add(thursdayVisits);
                daysOfWeek.add(fridayVisits);
                daysOfWeek.add(saturdayVisits);
                daysOfWeek.add(sundayVisits);

                return daysOfWeek;
            }, JsonUtilidades.json());

            Spark.get("/statsNavegadores/:id/", (request, response) -> {
                String urlid = request.params("id");
                Url url = urlServices.findUrlById(urlid);
                String urlShort = url.getUrlBase62();

                //Navegadores
                long chromeVisits = visitaServices.getSizeVisitaByShortUrlBrowser(urlShort, "Chrome");
                long operaVisits = visitaServices.getSizeVisitaByShortUrlBrowser(urlShort, "Opera");
                long firefoxVisits = visitaServices.getSizeVisitaByShortUrlBrowser(urlShort, "Firefox");
                long edgeVisits = visitaServices.getSizeVisitaByShortUrlBrowser(urlShort, "Edge");
                long safariVisits = visitaServices.getSizeVisitaByShortUrlBrowser(urlShort, "Safari");

                List<Long> navegadores = new ArrayList<>();
                navegadores.add(chromeVisits);
                navegadores.add(operaVisits);
                navegadores.add(firefoxVisits);
                navegadores.add(edgeVisits);
                navegadores.add(safariVisits);

                return navegadores;
            }, JsonUtilidades.json());

            Spark.get("/statsHora/:id/", (request, response) -> {
                String urlid = request.params("id");
                Url url = urlServices.findUrlById(urlid);
                String urlShort = url.getUrlBase62();

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

                List<Long> horas = new ArrayList<>();
                horas.add(zero); horas.add(one); horas.add(two); horas.add(three);
                horas.add(four); horas.add(five); horas.add(six); horas.add(seven);
                horas.add(eight); horas.add(nine); horas.add(ten); horas.add(eleven);
                horas.add(twelve); horas.add(thirteen); horas.add(fourteen); horas.add(fifteen);
                horas.add(sixteen); horas.add(seventeen); horas.add(eightteen); horas.add(nineteen);
                horas.add(twenty); horas.add(twenty_one); horas.add(twenty_two); horas.add(twenty_three);

                return horas;
            }, JsonUtilidades.json());
        });

        Spark.post("/token", (request, response) -> {
            JsonObject json = new JsonObject();
            json.addProperty("token", JWTServices.createJWT(UUID.randomUUID().toString(), "http://shielded-stream-52221.herokuapp.com/", "Access Token", 0));
            return json;
        });

        Spark.get("/validateToken", (request, response) -> {
            System.out.println("Wasaaa");
            String token = request.headers("token") != null ? request.headers("token") : request.headers("TOKEN");
            if (token == null || token.isEmpty() || !JWTServices.decodeJWT(token)) {
                System.out.println("I dont exist!");
                //request.attribute("Denied", "Denied");
                //halt(401);
                return false;
            }
            return true;
        });
    }

    public StringWriter getPlantilla(Configuration configuration, Map<String, Object> model, String templatePath) throws IOException, TemplateException {
        Template plantillaPrincipal = configuration.getTemplate(templatePath);
        StringWriter writer = new StringWriter();
        plantillaPrincipal.process(model, writer);
        return writer;
    }


}
