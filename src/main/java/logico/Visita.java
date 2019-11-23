package logico;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity
public class Visita implements Serializable {
    @Id
    String id;
    @OneToOne
    Url url;
    String sistemaOperativo;
    String navegador;
    String direccionIp;

    public Visita(){

    }

    public Visita(Url url, String sistemaOperativo, String navegador, String direccionIp){
        this.url = url;
        this.sistemaOperativo = sistemaOperativo;
        this.navegador = navegador;
        this.direccionIp = direccionIp;
    }

    public String getId() {
        return id;
    }
    public String getDireccionIp() {
        return direccionIp;
    }
    public String getNavegador() {
        return navegador;
    }
    public String getSistemaOperativo() {
        return sistemaOperativo;
    }
    public Url getUrl() {
        return url;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }
    public void setNavegador(String navegador) {
        this.navegador = navegador;
    }
    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }
    public void setUrl(Url url) {
        this.url = url;
    }
}
