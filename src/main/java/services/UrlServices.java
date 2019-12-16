package services;

import logico.Url;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class UrlServices extends GestionDB {
    public List<Url> getUrls(int pageNum){
        Query query = getEntityManager().createQuery("Select u from Url u");
        int pageNumber = pageNum;
        int pageSize = 5;
        query.setFirstResult((pageNumber-1) * pageSize);
        query.setMaxResults(pageSize);
//        Query queryTotal = getEntityManager().createQuery
//                ("Select count(f.id) from Url f");
//        long countResult = (long)queryTotal.getSingleResult();
        List <Url> urlist = query.getResultList();
        try{
            return urlist;
        }catch(NoResultException e){
            return null;
        }
    }

    public List<Url> getAllUrls(){
        try{
            return getEntityManager().createQuery("Select u from Url u").getResultList();
        }catch(NoResultException e){
            return new ArrayList<>();
        }
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
        return (Long) getEntityManager().createQuery("Select count(u.urlIndexada) from Url u").getSingleResult();
    }

    public List<Url> getUrlByUser(String userid, int pageNum){
        Query query = getEntityManager().createQuery("Select u from Url u where u.creador.id =:userid");
        query.setParameter("userid", userid);
        int pageNumber = pageNum;
        int pageSize = 5;
        query.setFirstResult((pageNumber-1) * pageSize);
        query.setMaxResults(pageSize);
//        Query queryTotal = getEntityManager().createQuery
//                ("Select count(f.id) from Url f");
//        long countResult = (long)queryTotal.getSingleResult();
        List <Url> urlist = query.getResultList();
        try{
            return urlist;
        }catch(NoResultException e){
            return null;
        }
    }

    public List<Url> getUrlByOnlyUsername(String username){
        Query query = getEntityManager().createQuery("Select u from Url u where u.creador.username =:username");
        query.setParameter("username", username);
        try{
            return query.getResultList();
        }catch(NoResultException e){
            return null;
        }
    }
}
