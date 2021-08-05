package co.edu.unab.misiontic.pruebac3.controller;

import co.edu.unab.misiontic.pruebac3.exceptions.NonexistentEntityException;
import co.edu.unab.misiontic.pruebac3.jpacontroller.PartidosJpaController;
import co.edu.unab.misiontic.pruebac3.model.Partido;
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
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Laura Pulido
 */
@RestController
@RequestMapping("/partidos")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class PartidoRestController {

    @PostMapping("/add")
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
    public String addPartido(@RequestBody Partido partido) {
        PartidosJpaController controller = new PartidosJpaController(ConnectionHelper.getEmf());
        controller.create(partido);
        return "Ok";
    }

    @PostMapping("/edit")
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
    public String editPartido(@RequestBody Partido partido) {
        PartidosJpaController controller = new PartidosJpaController(ConnectionHelper.getEmf());
        try {
            controller.edit(partido);
            return "Ok";
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(PartidoRestController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PartidoRestController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error";
    }

    @GetMapping("/find/{id}")
    @ResponseBody
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
    public String findPartidoById(@PathVariable int id) {
        PartidosJpaController controller = new PartidosJpaController(ConnectionHelper.getEmf());
        Partido partido = controller.findPartido(id);
            final String FORMATO_FECHA = "yyyy-MM-dd";
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
            Gson gson = gsonBuilder.setDateFormat(FORMATO_FECHA).create();
        return gson.toJson(partido);
    }

    @GetMapping("/findall")
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
    public String getPartidos(HttpServletRequest request) {
        PartidosJpaController controller = new PartidosJpaController(ConnectionHelper.getEmf());
        List<Partido> partidos = controller.findPartidoEntities();
        if(partidos!=null && partidos.size()>0){
            ObjectMapper mapper = new ObjectMapper();
                try {
                  return mapper.writeValueAsString(partidos);
                } catch (JsonProcessingException exx) {
                   exx.printStackTrace();
                }
        } 
        return "[]";
    }
}
