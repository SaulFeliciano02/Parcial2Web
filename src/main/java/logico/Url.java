package logico;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Url {
    @Id
    private String urlIndexada;
    @Column (length = 1000)
    private String urlOriginal;
    private String urlBase62;
    @OneToOne
    private Usuario creador;

    public Url(){

    }

    public Url(String urlOriginal, String urlIndexada, String urlBase62){

    }

    public Url(String urlOriginal) {
        this.urlOriginal = urlOriginal;
    }

    public String getUrlOriginal() {
        return urlOriginal;
    }

    public void setUrlOriginal(String urlOriginal) {
        this.urlOriginal = urlOriginal;
    }

    public String getUrlIndexada() {
        return urlIndexada;
    }

    public void setUrlIndexada(String urlIndexada) {
        this.urlIndexada = urlIndexada;
    }

    public String getUrlBase62() {
        return urlBase62;
    }

    public void setUrlBase62(String urlBase62) {
        this.urlBase62 = urlBase62;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }
}
