package co.edu.unab.misiontic.pruebac3.controller;

import co.edu.unab.misiontic.pruebac3.exceptions.NonexistentEntityException;
import co.edu.unab.misiontic.pruebac3.jpacontroller.EquiposJpaController;
import co.edu.unab.misiontic.pruebac3.model.Equipo;
import co.edu.unab.misiontic.pruebac3.util.ConnectionHelper;
import co.edu.unab.misiontic.pruebac3.util.DateDeserializer;
import co.edu.unab.misiontic.pruebac3.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Laura Pulido
 */
@RestController
@RequestMapping("/equipos")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class EquipoRestController {

    @PostMapping("/add")
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
    public String addEquipo(@RequestBody Equipo equipo) {
        EquiposJpaController controller = new EquiposJpaController(ConnectionHelper.getEmf());
        controller.create(equipo);
        return "Ok";
    }

    @PostMapping("/edit")
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
    public String editEquipo(@RequestBody Equipo equipo) {
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
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
    public String findEquipoById(@PathVariable int id) {
        EquiposJpaController controller = new EquiposJpaController(ConnectionHelper.getEmf());
        Equipo equipo = controller.findEquipos(id);
        return new Gson().toJson(equipo);
    }

    @GetMapping("/findall")
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
    public String getEquipos(HttpServletRequest request) {
        EquiposJpaController controller = new EquiposJpaController(ConnectionHelper.getEmf());
        List<Equipo> equipos = controller.findEquiposEntities();
        if(equipos!=null && equipos.size()>0){
            ObjectMapper mapper = new ObjectMapper();
                try {
                  return mapper.writeValueAsString(equipos);
                } catch (JsonProcessingException exx) {
                   exx.printStackTrace();
                }
        } 
        return "[]";
    }
}
