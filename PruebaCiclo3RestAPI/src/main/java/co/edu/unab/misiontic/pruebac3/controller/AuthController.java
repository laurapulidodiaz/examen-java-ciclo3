package co.edu.unab.misiontic.pruebac3.controller;

import co.edu.unab.misiontic.pruebac3.jpacontroller.UsuariosJpaController;
import co.edu.unab.misiontic.pruebac3.model.Usuario;
import co.edu.unab.misiontic.pruebac3.util.ConnectionHelper;
import co.edu.unab.misiontic.pruebac3.util.DateDeserializer;
import co.edu.unab.misiontic.pruebac3.util.LoginObj;
import co.edu.unab.misiontic.pruebac3.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class AuthController {
    

    @PostMapping("/login")
    @CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
    public String login(@RequestBody LoginObj obj) {
        UsuariosJpaController controller = new UsuariosJpaController(ConnectionHelper.getEmf());
        Usuario usuario = controller.loginUsuario(obj.user, obj.pass);
        if(usuario!=null && usuario.getId()>0){
            usuario.setToken(getJWTToken(obj.user));
            usuario.setPartidoList(null);
            return Util.getGson().toJson(usuario);
        }
        return null;
    }

    private String getJWTToken(String username) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("examenc3")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();

        return "Bearer " + token;
    }
}
