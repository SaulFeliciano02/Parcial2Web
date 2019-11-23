package logico;

import java.util.ArrayList;

public class Controladora {

    private ArrayList<Url> misUrls;
    private ArrayList<Usuario> misUsuarios;
    private ArrayList<Visita> misVisitas;
    private static Controladora controladora;

    public Controladora() {
        this.misUrls = new ArrayList<>();
        this.misUsuarios = new ArrayList<>();
        this.misVisitas = new ArrayList<>();
    }

    public static Controladora getInstance()
    {
        if(controladora == null)
        {
            controladora = new Controladora();
        }

        return controladora;
    }

    public Url findUrlByShort(String urlShort){
        for(Url url : Controladora.getInstance().getMisUrls()){
            if(url.getUrlBase62().equalsIgnoreCase(urlShort)){
                return url;
            }
        }
        return null;
    }

    public ArrayList<Url> getMisUrls() {
        return misUrls;
    }
}
