package services;

import logico.Usuario;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class UsuarioServices extends GestionDB {

    public UsuarioServices(){

    }

    public List<Usuario> getUsuarios(){
        return getEntityManager().createQuery("Select u from Usuario u").getResultList();
    }

    public boolean validateUsername(String username){
        Usuario usuario = null;
        Query query = getEntityManager().createQuery("Select u from Usuario u where u.username =:username");
        query.setParameter("username", username);
        try{
            usuario = (Usuario) query.getSingleResult();
        }catch(NoResultException e){
            return true;
        }
        return false;
    }

    public boolean validatePassword(String username, String password){
        Usuario usuario = null;
        System.out.println("El usuario es: " + username);
        System.out.println("El password es: " + password);
        Query query = getEntityManager().createQuery("Select u from Usuario u where u.username =:username and " +
                "u.password =:password ");
        query.setParameter("username", username);
        query.setParameter("password", password);
        try{
            usuario = (Usuario) query.getSingleResult();
        }catch(NoResultException e){
            return false;
        }
        return true;
    }

    public Usuario searchUserByUsername(String username){
        Query query = getEntityManager().createQuery("Select u from Usuario u where u.username =:username");
        query.setParameter("username", username);
        try{
            return (Usuario) query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public Usuario searchUserById(String id){
        Query query = getEntityManager().createQuery("Select u from Usuario u where u.id =:id");
        query.setParameter("id", id);
        try{
            return (Usuario) query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public long getSizeUsuario(){
        return (long) getEntityManager().createQuery("Select count(u.id) from Usuario u").getSingleResult();
    }
}
