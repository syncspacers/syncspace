package org.syncspacers.syncspace.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.syncspacers.syncspace.model.Archivo;
import org.syncspacers.syncspace.model.Carpeta;
import org.syncspacers.syncspace.model.Usuario;
import org.syncspacers.syncspace.service.ArchivoService;
import org.syncspacers.syncspace.service.CarpetaService;
import org.syncspacers.syncspace.service.UsuarioService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UsuarioController {

    private static final String TOKEN_COOKIE = "sessionToken";
    private static final String FOLDER_COOKIE = "folderID";

    @Autowired
    private ArchivoService archivoService;

    @Autowired
    private CarpetaService carpetaService;

    @Autowired
    private UsuarioService usuarioService;

    @RequestMapping("/")
    public String homepage() {
        return "index";
    }

    /*
     * Ruta para la página principal
     *
     * Necesita un valor correcto para TOKEN_COOKIE, en otro caso redirecciona a la página de
     * inicio de sesión
     */
    @RequestMapping("/dashboard")
    public String dashboard(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @CookieValue(value = FOLDER_COOKIE, defaultValue =  "") String carpetaID,
            Model model) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Carpeta> carpetaData = carpetaService.findById(carpetaID);
        Usuario usuario;
        Carpeta carpeta;
        List<Archivo> archivos = null;
        List<Carpeta> carpetas = null;

        if (usuarioData.isPresent()) {
            usuario = usuarioData.get();
            
            if (!carpetaData.isPresent()) {
                archivos = usuario.getArchivos();
                carpetas = usuario.getCarpetas();
            } else {
                carpeta = carpetaData.get();

                archivos = carpeta.getArchivos();
                carpetas = carpeta.getCarpetas();
            }

            model.addAttribute("usuario", usuario);
            model.addAttribute("archivos", archivos);
            model.addAttribute("carpetas", carpetas);
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

    @PostMapping("/users/upload")
	public String uploadFile(@RequestParam("file") MultipartFile file,
            @CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @CookieValue(value = FOLDER_COOKIE, defaultValue =  "") String carpetaID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Carpeta> carpetaData = carpetaService.findById(carpetaID);
        Archivo archivo = generateArchivoFromFile(file);

        if (usuarioData.isPresent() && archivo != null) {
            Usuario usuario = usuarioData.get();

            if (!carpetaData.isPresent()) {
                archivo.setUsuario(usuario);
            } else {
                Carpeta carpeta = carpetaData.get();
                archivo.setCarpeta(carpeta);
            }
            archivoService.save(archivo);
        }

        return "redirect:/dashboard";
    }

    @RequestMapping("/preview/{fileId}")
    public String previewFile(@PathVariable Long fileId, Model model) {
        Optional<Archivo> archivoData = archivoService.findById(fileId);

        if (archivoData.isPresent()) {
            Archivo archivo = archivoData.get();
            String extensionArchivo = getFileExtension(archivo.getNombre());

            model.addAttribute("archivo", archivo);
            model.addAttribute("extensionArchivo", extensionArchivo);

            return "usuario/share";
        }

        return "redirect:/dashboard";
    }

    @RequestMapping("/users/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        Optional<Archivo> archivoData = archivoService.findById(fileId);

        System.out.println(String.format("fileId: %d", archivoData.get().getId()));

        if (archivoData.isPresent()) {
            Archivo archivo = archivoData.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", archivo.getNombre());

            return new ResponseEntity<>(archivo.getContenido(), headers, HttpStatus.OK);
        }

        return null;
    }

    @PostMapping("/users/newfolder")
    public String createFolder(@RequestParam("nombreCarpeta") String nombreCarpeta,
            @CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @CookieValue(value = FOLDER_COOKIE, defaultValue =  "") String carpetaID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Carpeta> carpetaData = carpetaService.findById(carpetaID);
        Carpeta carpeta;

        if (usuarioData.isPresent()) {
            Usuario usuario = usuarioData.get();
            carpeta = generateCarpetaFromName(nombreCarpeta);

            if (carpetaData.isPresent()) {
                carpeta.setCarpetaPadre(carpetaData.get());
            } else {
                carpeta.setUsuario(usuario);
            }

            carpetaService.save(carpeta);
        }

        return "redirect:/dashboard";
    }

    private Archivo generateArchivoFromFile(MultipartFile file) {
        Archivo archivo = new Archivo();

        archivo.setNombre(file.getOriginalFilename());
        archivo.setFechaDeSubida(new Date());

        try {
            archivo.setContenido(file.getBytes());
        } catch (IOException e) {
            return null;
        }

        return archivo;
    }

    private Carpeta generateCarpetaFromName(String nombreCarpeta) {
        Carpeta carpeta = new Carpeta();

        carpeta.setNombre(nombreCarpeta);
        carpeta.setFechaDeSubida(new Date());

        return carpeta;
    }

    private String getFileExtension(String filename) {
        int index = filename.lastIndexOf(".");

        if (index > 0) {
            return "Type " + filename.substring(index);
        }

        return "No extension";
    }
}
