package org.syncspacers.syncspace.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.syncspacers.syncspace.model.Usuario;
import org.syncspacers.syncspace.service.UsuarioService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UsuarioController {

    private static final String TOKEN_COOKIE = "sessionToken";

    @Autowired
    private UsuarioService usuarioService;

    /*
     * Ruta para la página principal
     *
     * Necesita un valor correcto para TOKEN_COOKIE, en otro caso redirecciona a la página de
     * inicio de sesión
     */
    @RequestMapping("/dashboard")
    public String dashboard(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            Model model) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Usuario usuario;

        if (usuarioData.isPresent()) {
            usuario = usuarioData.get();
            model.addAttribute("usuario", usuario);
            return "usuario/dashboard";
        } else {
            return "redirect:/login";
        }
    }

    @RequestMapping("/register")
    public String addUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/register";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/login";
    }

    @PostMapping("/users/save")
    public String saveUsuario(Usuario usuario) {
        usuarioService.save(usuario);
        return "redirect:/login";
    }

    @PostMapping("/users/login")
    public String saveSession(Usuario usuario, HttpServletResponse response) {
        String token = usuarioService.login(usuario.getEmail(), usuario.getPassword());

        if (token.isEmpty()) {
            return "redirect:/login";
        } else {
            Cookie sessionCookie = new Cookie(TOKEN_COOKIE, String.valueOf(token));
            sessionCookie.setMaxAge(86400);
            sessionCookie.setPath("/");

            response.addCookie(sessionCookie);
            return "redirect:/dashboard";
        }
    }
}
