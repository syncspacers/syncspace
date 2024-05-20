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
    @RequestMapping(value = {"dashboard", "/dashboard/{seccion}", "/dashboard/folder/{folderID}"})
    public String dashboard(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable(required = false) Long folderID,
            @PathVariable(required = false) String seccion,
            Model model) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Carpeta> carpetaData = carpetaService.findById(folderID);
        Usuario usuario;
        Carpeta carpeta;
        List<Archivo> archivos = null;
        List<Carpeta> carpetas = null;
        String carpetaPadre = "";
        boolean papelera = "papelera".equals(seccion);

        if (usuarioData.isPresent()) {
            usuario = usuarioData.get();
            
            if (papelera) {
                archivos = usuario.getArchivos();
                carpetas = usuario.getCarpetas();

                archivos.removeIf(elem -> !elem.isEnPapelera());
                carpetas.removeIf(elem -> !elem.isEnPapelera());
            } else if (!carpetaData.isPresent()) {
                archivos = usuario.getArchivos();
                carpetas = usuario.getCarpetas();

                archivos.removeIf(elem -> elem.getCarpeta() != null);
                carpetas.removeIf(elem -> elem.getCarpetaPadre() != null);
                model.addAttribute("carpetaActual", "");
            } else {
                carpeta = carpetaData.get();

                if (carpeta.isEnPapelera()) {
                    return "redirect:/dashboard/papelera";
                }

                archivos = carpeta.getArchivos();
                carpetas = carpeta.getCarpetas();
                if (carpeta.getCarpetaPadre() != null) {
                    carpetaPadre = Long.toString(carpeta.getCarpetaPadre().getId());
                }

                model.addAttribute("carpetaActual", carpeta.getId());
            }

            if (!papelera) {
                archivos.removeIf(elem -> elem.isEnPapelera());
                carpetas.removeIf(elem -> elem.isEnPapelera());
            }

            model.addAttribute("usuario", usuario);
            model.addAttribute("archivos", archivos);
            model.addAttribute("carpetas", carpetas);
            model.addAttribute("carpetaPadre", carpetaPadre);
            model.addAttribute("carpetaRaiz", !carpetaData.isPresent());
            model.addAttribute("enPapalera", papelera);
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

    
    @PostMapping(value = {"/users/upload/", "/users/upload/{carpetaID}"})
	public String uploadFile(@RequestParam("file") MultipartFile file,
            @CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable(required = false) Long carpetaID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Carpeta> carpetaData = carpetaService.findById(carpetaID);
        Archivo archivo = generateArchivoFromFile(file);

        if (usuarioData.isPresent() && archivo != null) {
            Usuario usuario = usuarioData.get();

            archivo.setUsuario(usuario);
            if (carpetaData.isPresent()) {
                Carpeta carpeta = carpetaData.get();
                archivo.setCarpeta(carpeta);
            }
            archivoService.save(archivo);
        }

        if (carpetaID == null) {
            return "redirect:/dashboard";
        } else {
            return "redirect:/dashboard/folder/" + carpetaID;
        }
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

    @PostMapping(value = {"/users/newfolder", "/users/newfolder/", "/users/newfolder/{carpetaID}"})
    public String createFolder(@RequestParam("nombreCarpeta") String nombreCarpeta,
            @CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable(required = false) Long carpetaID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Carpeta> carpetaData = carpetaService.findById(carpetaID);
        Carpeta carpeta;

        if (usuarioData.isPresent()) {
            Usuario usuario = usuarioData.get();
            carpeta = generateCarpetaFromName(nombreCarpeta);

            carpeta.setUsuario(usuario);
            if (carpetaData.isPresent()) {
                carpeta.setCarpetaPadre(carpetaData.get());
            }

            carpetaService.save(carpeta);
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/users/renamefile/{fileID}")
    public String renameFile(@PathVariable("fileID") Long fileID,
            @RequestParam("nuevoNombre") String nuevoNombre) {
        Optional<Archivo> archivoData = archivoService.findById(fileID);

        if (archivoData.isPresent()) {
            Archivo archivo = archivoData.get();
            archivo.setNombre(nuevoNombre);
            archivoService.save(archivo);
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/users/renamefolder/{folderID}")
    public String renameFolder(@PathVariable("folderID") Long folderID,
            @RequestParam("nuevoNombre") String nuevoNombre) {
        Optional<Carpeta> carpetaData = carpetaService.findById(folderID);

        if (carpetaData.isPresent()) {
            Carpeta carpeta = carpetaData.get();
            carpeta.setNombre(nuevoNombre);
            carpetaService.save(carpeta);
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/users/deletefile/{fileID}")
    public String deleteFile(@PathVariable("fileID") Long fileID) {
        Optional<Archivo> archivoData = archivoService.findById(fileID);

        if (archivoData.isPresent()) {
            Archivo archivo = archivoData.get();

            if (archivo.isEnPapelera()) {
                archivoService.delete(archivo);
            } else {
                archivo.setEnPapelera(true);
                archivoService.save(archivo);
            }
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/users/deletefolder/{folderID}")
    public String deleteFolder(@PathVariable("folderID") Long folderID) {
        Optional<Carpeta> carpetaData = carpetaService.findById(folderID);

        if (carpetaData.isPresent()) {
            Carpeta carpeta = carpetaData.get();

            if (carpeta.isEnPapelera()) {
                carpetaService.delete(carpeta);
            } else {
                carpeta.setEnPapelera(true);
                carpetaService.save(carpeta);
            }
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
