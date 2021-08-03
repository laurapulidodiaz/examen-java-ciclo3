package co.edu.unab.misiontic.pruebac3.controller;

import co.edu.unab.misiontic.pruebac3.exceptions.NonexistentEntityException;
import co.edu.unab.misiontic.pruebac3.jpacontroller.EquiposJpaController;
import co.edu.unab.misiontic.pruebac3.model.Equipo;
import co.edu.unab.misiontic.pruebac3.util.ConnectionHelper;
import com.google.gson.Gson;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Laura Pulido
 */
@RestController
@RequestMapping("/equipos")
public class EquipoRestController {
    
    @PostMapping("/add")
    public String addEquipo(@RequestBody Equipo equipo){
        EquiposJpaController controller = new EquiposJpaController(ConnectionHelper.getEmf());
        controller.create(equipo);
        return "Ok";
    }
    
    @PostMapping("/edit")
    public String editEquipo(@RequestBody Equipo equipo){
        EquiposJpaController controller = new EquiposJpaController(ConnectionHelper.getEmf());
        try {
            controller.edit(equipo);
            return "Ok";
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(EquipoRestController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(EquipoRestController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error";
    }
    
    @GetMapping("/find/{id}")
    public String findEquipoById(@PathVariable int id){
        EquiposJpaController controller = new EquiposJpaController(ConnectionHelper.getEmf());
        Equipo equipo = controller.findEquipos(id);
        return new Gson().toJson(equipo);
    }
    
    @GetMapping("/findall")
    public List<Equipo> getEquipos(HttpServletRequest request){
        EquiposJpaController controller = new EquiposJpaController(ConnectionHelper.getEmf());
        List<Equipo> equipos = controller.findEquiposEntities();
        return equipos;
    }
}
