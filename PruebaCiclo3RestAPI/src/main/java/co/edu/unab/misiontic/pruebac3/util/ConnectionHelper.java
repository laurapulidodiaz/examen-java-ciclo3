package co.edu.unab.misiontic.pruebac3.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ConnectionHelper {
    private static EntityManagerFactory emf;
    
    public static EntityManagerFactory getEmf(){
        if(emf==null){
           emf = Persistence.createEntityManagerFactory("co.edu.unab.misiontic_pruebac3_jar_0.0.1PU");
        }
        return emf;
    }
}
