package services;

import logico.Visita;

import javax.persistence.Query;

public class VisitaServices extends GestionDB{
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
}
