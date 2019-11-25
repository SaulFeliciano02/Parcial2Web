package services;

import logico.Url;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class UrlServices extends GestionDB {
    public List<Url> getUrls(){
        return getEntityManager().createQuery("Select u from Url u").getResultList();
    }

    public Url findUrlByShort(String urlShort){
        Query query = getEntityManager().createQuery("Select u from Url u where u.urlBase62 =:urlShort");
        query.setParameter("urlShort", urlShort);
        try{
            return (Url) query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public Url findUrlByOriginal(String urlOriginal){
        Query query = getEntityManager().createQuery("Select u from Url u where u.urlOriginal =:urlOriginal");
        query.setParameter("urlOriginal", urlOriginal);
        try{
            return (Url) query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public Url findUrlById(String id){
        Query query = getEntityManager().createQuery("Select u from Url u where u.urlIndexada =:id");
        query.setParameter("id", id);
        try{
            return (Url) query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public long getSizeUrl(){
        return (Long) getEntityManager().createQuery("Select count(u) from Url u").getSingleResult();
    }
}
