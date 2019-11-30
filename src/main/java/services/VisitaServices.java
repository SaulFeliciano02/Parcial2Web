package services;

import logico.Url;
import logico.Usuario;
import logico.Visita;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class VisitaServices extends GestionDB{

    public List<Visita> getUrlsByVisit(String urlid){
        Query query = getEntityManager().createQuery("Select v from Visita v where v.url.urlIndexada =:urlid");
        query.setParameter("urlid", urlid);
        try{
            return (List<Visita>) query.getResultList();
        }catch(NoResultException e){
            return new ArrayList<>();
        }
    }

    public long getSizeVisitaByBrowser(String browser){
        Query query = getEntityManager().createQuery("Select count(v.id) from Visita v where v.navegador =:browser");
        query.setParameter("browser", browser);
        return (long) query.getSingleResult();
    }

    public long getSizeVisitaByOs(String os){
        Query query = getEntityManager().createQuery("Select count(v.id) from Visita v where v.sistemaOperativo =:os");
        query.setParameter("os", os);
        return (long) query.getSingleResult();
    }

    public long getSizeVisitaByShortUrl(String urlShort){
        Query query = getEntityManager().createQuery("Select count(v.id) from Visita v where v.url.urlBase62 =:urlShort");
        query.setParameter("urlShort", urlShort);
        return (long) query.getSingleResult();
    }

    public long getSizeVisitaByShortUrlBrowser(String urlShort, String browser){
        Query query = getEntityManager().createQuery("Select count(v.id) from Visita v where v.url.urlBase62 =:urlShort and" +
                " v.navegador =: browser");
        query.setParameter("urlShort", urlShort);
        query.setParameter("browser", browser);
        return (long) query.getSingleResult();
    }

    public long getSizeVisitaByShortUrlOs(String urlShort, String os){
        Query query = getEntityManager().createQuery("Select count(v.id) from Visita v where v.url.urlBase62 =:urlShort and" +
                " v.sistemaOperativo =: os");
        query.setParameter("urlShort", urlShort);
        query.setParameter("os", os);
        return (long) query.getSingleResult();
    }

    public long getSizeByDay(String dia){
        Query query = getEntityManager().createQuery("Select count(v.id) from Visita v where v.dia =:dia");
        query.setParameter("dia", dia);
        return (long) query.getSingleResult();
    }

    public long getSizeByShortUrlDay(String urlShort, String dia){
        Query query = getEntityManager().createQuery("Select count(v.id) from Visita v where v.dia =:dia and " +
                "v.url.urlBase62 =:urlShort");
        query.setParameter("urlShort", urlShort);
        query.setParameter("dia", dia);
        return (long) query.getSingleResult();
    }

    public long getSizeByHour(long hora){
        Query query = getEntityManager().createQuery("Select count(v.id) from Visita v where v.hora =:hora");
        query.setParameter("hora", hora);
        return (long) query.getSingleResult();
    }

    public long getSizeByShortUrlHour(String urlShort, long hora){
        Query query = getEntityManager().createQuery("Select count(v.id) from Visita v where v.hora =:hora and " +
                "v.url.urlBase62 =:urlShort");
        query.setParameter("urlShort", urlShort);
        query.setParameter("hora", hora);
        return (long) query.getSingleResult();
    }
}
