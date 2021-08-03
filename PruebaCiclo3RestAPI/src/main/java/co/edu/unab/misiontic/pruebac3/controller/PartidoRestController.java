package co.edu.unab.misiontic.pruebac3.controller;

import co.edu.unab.misiontic.pruebac3.exceptions.NonexistentEntityException;
import co.edu.unab.misiontic.pruebac3.jpacontroller.PartidosJpaController;
import co.edu.unab.misiontic.pruebac3.model.Partido;
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
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Laura Pulido
 */
@RestController
@RequestMapping("/partidos")
public class PartidoRestController {

    @PostMapping("/add")
    public String addPartido(@RequestBody Partido partido) {
        PartidosJpaController controller = new PartidosJpaController(ConnectionHelper.getEmf());
        controller.create(partido);
        return "Ok";
    }

    @PostMapping("/edit")
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
    public String findPartidoById(@PathVariable int id) {
        PartidosJpaController controller = new PartidosJpaController(ConnectionHelper.getEmf());
        Partido partido = controller.findPartido(id);
        return new Gson().toJson(partido);
    }

    @GetMapping("/findall")
    public List<Partido> getPartidos(HttpServletRequest request) {
        PartidosJpaController controller = new PartidosJpaController(ConnectionHelper.getEmf());
        List<Partido> partidos = controller.findPartidoEntities();
        return partidos;
    }
}
