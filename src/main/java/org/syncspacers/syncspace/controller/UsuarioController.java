package org.syncspacers.syncspace.controller;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.syncspacers.syncspace.model.Archivo;
import org.syncspacers.syncspace.model.Carpeta;
import org.syncspacers.syncspace.model.Usuario;
import org.syncspacers.syncspace.service.ArchivoService;
import org.syncspacers.syncspace.service.CarpetaService;
import org.syncspacers.syncspace.service.UsuarioService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UsuarioController {

    private static final String TOKEN_COOKIE = "sessionToken";

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
            HttpServletRequest request,
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

                // Comprobación de seguridad
                if (!carpeta.getUsuario().getEmail().equals(usuario.getEmail())) {
                    return "redirect:/dashboard";
                }
                //

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
            model.addAttribute("serverURL", ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString());
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
    public String login(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            Model model) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        if (usuarioData.isPresent()) {
            return "redirect:/dashboard";
        }

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

                // Comprobación de seguridad
                if (!carpeta.getUsuario().getEmail().equals(usuario.getEmail())) {
                    return "redirect:/dashboard";
                }
                //

                carpeta.setSize(carpeta.getSize() + archivo.bytesToMB());

                archivo.setCarpeta(carpeta);
            }
            archivoService.save(archivo);
        }

        if (carpetaID != null) {
            return "redirect:/dashboard/folder/" + carpetaID;
        }

        return "redirect:/dashboard";
    }

    @RequestMapping("/preview/{fileId}")
    public String previewFile(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable Long fileId, Model model) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Archivo> archivoData = archivoService.findById(fileId);

        if (archivoData.isPresent() && usuarioData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Archivo archivo = archivoData.get();

            if (archivo.isEnPapelera()) {
                return "redirect:/dashboard/papelera";
            }

            // Comprobación de seguridad
            if (!archivo.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            String extensionArchivo = getFileExtension(archivo.getNombre());
            model.addAttribute("archivo", archivo);
            model.addAttribute("extensionArchivo", extensionArchivo);
            model.addAttribute("archivoSinEnlace", true);

            return "usuario/share";
        }

        return "redirect:/dashboard";
    }

    @RequestMapping("/users/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable Long fileId) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Archivo> archivoData = archivoService.findById(fileId);

        if (archivoData.isPresent() && usuarioData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Archivo archivo = archivoData.get();
            HttpHeaders headers = new HttpHeaders();

            // Comprobación de seguridad
            if (!archivo.getUsuario().getEmail().equals(usuario.getEmail())) {
                headers.setLocation(URI.create("/dashboard"));
                return new ResponseEntity<byte[]>(headers, HttpStatus.FOUND);                
            }
            //

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
        Carpeta carpetaPadre = null;

        if (usuarioData.isPresent()) {
            Usuario usuario = usuarioData.get();
            carpeta = generateCarpetaFromName(nombreCarpeta);

            carpeta.setUsuario(usuario);
            if (carpetaData.isPresent()) {
                carpetaPadre = carpetaData.get();

                // Comprobación de seguridad
                if (!carpetaPadre.getUsuario().getEmail().equals(usuario.getEmail())) {
                    return "redirect:/dashboard";
                }
                
                carpeta.setCarpetaPadre(carpetaPadre);
            }
            System.out.println("CARPETA CREADA CON PESO 0");
            carpeta.setSize(0);
            carpetaService.save(carpeta);
        }

        if (carpetaPadre != null) {
            return "redirect:/dashboard/folder/" + carpetaPadre.getId();
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/users/renamefile/{fileID}")
    public String renameFile(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable("fileID") Long fileID,
            @RequestParam("nuevoNombre") String nuevoNombre) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Archivo> archivoData = archivoService.findById(fileID);
        Carpeta carpetaPadre = null;

        if (archivoData.isPresent() && usuarioData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Archivo archivo = archivoData.get();

            // Comprobación de seguridad
            if (!archivo.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            carpetaPadre = archivo.getCarpeta();
            String extension = archivo.getNombre().split("\\.")[1];
            archivo.setNombre(nuevoNombre + "." + extension);
            archivoService.save(archivo);
        }

        if (carpetaPadre != null) {
            return "redirect:/dashboard/folder/" + carpetaPadre.getId();
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/users/renamefolder/{folderID}")
    public String renameFolder(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable("folderID") Long folderID,
            @RequestParam("nuevoNombre") String nuevoNombre) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Carpeta> carpetaData = carpetaService.findById(folderID);
        Carpeta carpetaPadre = null;

        if (carpetaData.isPresent() && usuarioData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Carpeta carpeta = carpetaData.get();

            // Comprobación de seguridad
            if (!carpeta.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            carpetaPadre = carpeta.getCarpetaPadre();
            carpeta.setNombre(nuevoNombre);
            carpetaService.save(carpeta);
        }

        if (carpetaPadre != null) {
            return "redirect:/dashboard/folder/" + carpetaPadre.getId();
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/users/deletefile/{fileID}")
    public String deleteFile(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable("fileID") Long fileID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Archivo> archivoData = archivoService.findById(fileID);
        Carpeta carpetaPadre = null;

        if (archivoData.isPresent() && usuarioData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Archivo archivo = archivoData.get();

            // Comprobación de seguridad
            if (!archivo.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            carpetaPadre = archivo.getCarpeta();
            if (archivo.isEnPapelera()) {
                archivoService.delete(archivo);
                return "redirect:/dashboard/papelera";
            } else {
                archivo.setEnPapelera(true);
                archivo.setCarpeta(null);
                archivoService.save(archivo);
            }
        }
        
        if (carpetaPadre != null) {
            return "redirect:/dashboard/folder/" + carpetaPadre.getId();
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/users/deletefolder/{folderID}")
    public String deleteFolder(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable("folderID") Long folderID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Carpeta> carpetaData = carpetaService.findById(folderID);
        Carpeta carpetaPadre = null;

        if (carpetaData.isPresent() && usuarioData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Carpeta carpeta = carpetaData.get();

            // Comprobación de seguridad
            if (!carpeta.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            carpetaPadre = carpeta.getCarpetaPadre();
            if (carpeta.isEnPapelera()) {
                carpetaService.delete(carpeta);
                return "redirect:/dashboard/papelera";
            } else {
                carpeta.setEnPapelera(true);
                carpeta.setCarpetaPadre(null);
                carpetaService.save(carpeta);
            }
        }
        
        if (carpetaPadre != null) {
            return "redirect:/dashboard/folder/" + carpetaPadre.getId();
        }

        return "redirect:/dashboard";
    }

    @RequestMapping("/users/restorefile/{fileID}")
    public String restoreFile(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable("fileID") Long fileID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Archivo> archivoData = archivoService.findById(fileID);

        if (archivoData.isPresent() && usuarioData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Archivo archivo = archivoData.get();

            // Comprobación de seguridad
            if (!archivo.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            archivo.setEnPapelera(false);
            archivo.setCarpeta(null);
            archivoService.save(archivo);
        }
        
        return "redirect:/dashboard/papelera";
    }

    @RequestMapping("/users/restorefolder/{folderID}")
    public String restoreFolder(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable("folderID") Long folderID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Carpeta> carpetaData = carpetaService.findById(folderID);

        if (carpetaData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Carpeta carpeta = carpetaData.get();

            // Comprobación de seguridad
            if (!carpeta.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            carpeta.setEnPapelera(false);
            carpetaService.save(carpeta);
        }
        
        return "redirect:/dashboard/papelera";
    }

    @RequestMapping("/share/public/{publicID}")
    public String previewPublicShare(@PathVariable Long publicID, Model model) {
        Optional<Archivo> archivoData = archivoService.findByPublicId(publicID);

        if (archivoData.isPresent()) {
            Archivo archivo = archivoData.get();

            if (archivo.isEnPapelera()) {
                return "redirect:/dashboard/papelera";
            }

            String extensionArchivo = getFileExtension(archivo.getNombre());
            model.addAttribute("archivo", archivo);
            model.addAttribute("extensionArchivo", extensionArchivo);
            model.addAttribute("archivoPublico", true);

            return "usuario/share";
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/users/share/public/{fileID}")
    public String sharePublic(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable Long fileID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Archivo> archivoData = archivoService.findById(fileID);
        
        if (usuarioData.isPresent() && archivoData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Archivo archivo = archivoData.get();

            // Comprobación de seguridad
            if (!archivo.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            archivo.setPublicID(Math.abs(new Random().nextLong()));
            archivoService.save(archivo);

            return "redirect:/share/public/" + archivo.getPublicID();
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/users/unshare/public/{fileID}")
    public String unsharePublic(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable Long fileID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Archivo> archivoData = archivoService.findById(fileID);

        if (usuarioData.isPresent() && archivoData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Archivo archivo = archivoData.get();

            // Comprobación de seguridad
            if (!archivo.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            archivo.setPublicID(null);
            archivoService.save(archivo);
        }

        return "redirect:/dashboard";
    }

    @RequestMapping("/users/download/public/{publicID}")
    public ResponseEntity<byte[]> downloadPublicFile(@PathVariable Long publicID) {
        Optional<Archivo> archivoData = archivoService.findByPublicId(publicID);

        if (archivoData.isPresent()) {
            Archivo archivo = archivoData.get();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", archivo.getNombre());

            return new ResponseEntity<>(archivo.getContenido(), headers, HttpStatus.OK);
        }

        return null;
    }

    @RequestMapping("/share/private/{passwordID}")
    public String previewPasswordShare(@PathVariable Long passwordID, Model model) {
        Optional<Archivo> archivoData = archivoService.findByPasswordId(passwordID);

        if (archivoData.isPresent()) {
            Archivo archivo = archivoData.get();

            if (archivo.isEnPapelera()) {
                return "redirect:/dashboard/papelera";
            }

            String extensionArchivo = getFileExtension(archivo.getNombre());
            model.addAttribute("archivo", archivo);
            model.addAttribute("extensionArchivo", extensionArchivo);
            model.addAttribute("archivoPrivado", true);

            return "usuario/share";
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/users/share/private/{fileID}")
    public String sharePassword(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @RequestParam("filePassword") String filePassword,
            @PathVariable Long fileID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Archivo> archivoData = archivoService.findById(fileID);
        
        if (usuarioData.isPresent() && archivoData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Archivo archivo = archivoData.get();

            // Comprobación de seguridad
            if (!archivo.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            archivo.setPasswordID(Math.abs(new Random().nextLong()));
            archivo.setPassword(filePassword);
            archivoService.save(archivo);

            return "redirect:/share/private/" + archivo.getPasswordID();
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/users/unshare/private/{fileID}")
    public String unsharePassword(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @PathVariable Long fileID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Archivo> archivoData = archivoService.findById(fileID);

        if (usuarioData.isPresent() && archivoData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Archivo archivo = archivoData.get();

            // Comprobación de seguridad
            if (!archivo.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            archivo.setPasswordID(null);
            archivo.setPassword(null);
            archivoService.save(archivo);
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/users/download/private/{passwordID}")
    public ResponseEntity<?> downloadPasswordFile(@RequestParam("filePassword") String filePassword,
            @PathVariable Long passwordID) {
        Optional<Archivo> archivoData = archivoService.findByPasswordId(passwordID);

        if (archivoData.isPresent()) {
            Archivo archivo = archivoData.get();
            HttpHeaders headers = new HttpHeaders();

            // Comprobación de seguridad
            if (!filePassword.equals(archivo.getPassword())) {
                headers.setLocation(URI.create("/share/private/" + passwordID));
                return new ResponseEntity<byte[]>(headers, HttpStatus.FOUND);
            }
            //

            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", archivo.getNombre());

            return new ResponseEntity<byte[]>(archivo.getContenido(), headers, HttpStatus.OK);
        }

        return null;
    }

    @PostMapping("/users/commentfile/{fileID}")
    public String setFileComment(@CookieValue(value = TOKEN_COOKIE, defaultValue = "") String sessionToken,
            @RequestParam String fileComment,
            @PathVariable Long fileID) {
        Optional<Usuario> usuarioData = usuarioService.getByToken(sessionToken);
        Optional<Archivo> archivoData = archivoService.findById(fileID);

        if (usuarioData.isPresent() && archivoData.isPresent()) {
            Usuario usuario = usuarioData.get();
            Archivo archivo = archivoData.get();

            // Comprobación de seguridad
            if (!archivo.getUsuario().getEmail().equals(usuario.getEmail())) {
                return "redirect:/dashboard";
            }
            //

            if (fileComment.isEmpty()) {
                archivo.setComentario(null);
            } else {
                archivo.setComentario(fileComment);
            }
            archivoService.save(archivo);
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
