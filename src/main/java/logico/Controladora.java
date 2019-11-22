package logico;

import java.util.ArrayList;

public class Controladora {

    private ArrayList<Url> misUrls;
    private static Controladora controladora;

    public Controladora() {
        this.misUrls = new ArrayList<>();
    }

    public static Controladora getInstance()
    {
        if(controladora == null)
        {
            controladora = new Controladora();
        }

        return controladora;
    }

    public ArrayList<Url> getMisUrls() {
        return misUrls;
    }
}
