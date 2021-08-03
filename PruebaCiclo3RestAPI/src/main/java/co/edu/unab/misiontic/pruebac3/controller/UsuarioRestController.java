package co.edu.unab.misiontic.pruebac3.controller;

import co.edu.unab.misiontic.pruebac3.exceptions.NonexistentEntityException;
import co.edu.unab.misiontic.pruebac3.jpacontroller.UsuariosJpaController;
import co.edu.unab.misiontic.pruebac3.model.Usuario;
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
@RequestMapping("/usuarios")
public class UsuarioRestController {
    
    @PostMapping("/add")
    public String addUsuario(@RequestBody Usuario usuario){
        UsuariosJpaController controller = new UsuariosJpaController(ConnectionHelper.getEmf());
        controller.create(usuario);
        return "Ok";
    }
    
    @PostMapping("/edit")
    public String editUsuario(@RequestBody Usuario usuario){
        UsuariosJpaController controller = new UsuariosJpaController(ConnectionHelper.getEmf());
        try {
            controller.edit(usuario);
            return "Ok";
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(UsuarioRestController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(UsuarioRestController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error";
    }
    
    @GetMapping("/find/{id}")
    public String findUsuarioById(@PathVariable int id){
        UsuariosJpaController controller = new UsuariosJpaController(ConnectionHelper.getEmf());
        Usuario usuario = controller.findUsuario(id);
        return new Gson().toJson(usuario);
    }
    
    @GetMapping("/findall")
    public List<Usuario> getUsuarios(HttpServletRequest request){
        UsuariosJpaController controller = new UsuariosJpaController(ConnectionHelper.getEmf());
        List<Usuario> usuarios = controller.findUsuarioEntities();
        return usuarios;
    }
}
