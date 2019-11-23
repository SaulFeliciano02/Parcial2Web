package logico;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

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

        Spark.get("/", (request, response) ->{
            return "Klk";
        });
    }

    public StringWriter getPlantilla(Configuration configuration, Map<String, Object> model, String templatePath) throws IOException, TemplateException {
        Template plantillaPrincipal = configuration.getTemplate(templatePath);
        StringWriter writer = new StringWriter();
        plantillaPrincipal.process(model, writer);
        return writer;
    }


}
