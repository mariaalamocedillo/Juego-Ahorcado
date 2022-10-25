package es.mariaa.ahorcadospring.controladores;

import ch.qos.logback.classic.Logger;
import es.mariaa.ahorcadospring.modelo.Partida;
import es.mariaa.ahorcadospring.servicios.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
public class PartidaControlador {

    @Autowired
    public PartidaService servicio;

    @GetMapping("/")
    public String index (Model model){
        model.addAttribute("listaPartidas", servicio.findAll());
        return "index";
    }

    @GetMapping("/partida/jugar/{id}")
    public String comienzaPartida (@PathVariable("id") int id, Model model){
        Partida p = servicio.obtenerPartida(id);

        String claseImagen = servicio.asignaImg(p.getIntentos());
        model.addAttribute("claseImagen", claseImagen);

        model.addAttribute( "partida", p);
        return "partida";
    }

    @GetMapping("/partida/borrar/{id}")
    public String borrarPartida(@PathVariable("id") int id){
        servicio.delete(id);
        return "redirect:/";
    }

    @GetMapping("/partida/jugar/random")
    public String partidaRandom (){
        int idPartida = servicio.getRandom();
        return "redirect:/partida/jugar/" + idPartida;
    }

    @PostMapping("/partida/nuevaLetra")
    public String jugada(@ModelAttribute("partida") Partida partida, Model model){
        //si el caracter introducido es una letra, procedemos a realizar la partida
        Pattern regex = Pattern.compile("[a-zA-ZÀ-ÿ\u00f1\u00d1]");
        Matcher mat = regex.matcher(partida.getNuevaLetra());
        if (mat.matches()){
            partida = servicio.manejarPartida(partida);
            model.addAttribute("noLetra", "");
        }else{
            model.addAttribute("noLetra", "Solo se aceptan letras");
            partida.setNuevaLetra("");
        }
        if (partida.getIntentos() == 0 || Objects.equals(partida.getEstado(), partida.getPalabraOculta()))
            return "final";

        String claseImagen = servicio.asignaImg(partida.getIntentos());
        model.addAttribute("claseImagen", claseImagen);
        return "partida";
    }


    @PostMapping("/addPartida")
    public String anadePalabra(@RequestParam String palabra, Model model){
        //si el carcter introducido es una letra, procedemos a realizar la partida
        Pattern regex = Pattern.compile("[a-zA-ZÀ-ÿ\u00f1\u00d1]*");
        Matcher mat = regex.matcher(palabra);
        if (mat.matches() && !palabra.equals("")) {
            servicio.add(new Partida(servicio.last(), palabra));
            model.addAttribute("msg", "");
        }else{
            model.addAttribute("msg", "La palabra solo puede contener letras");
        }
        model.addAttribute("listaPartidas", servicio.findAll());
        return "index";
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void handle(Exception e) {
        Logger log = null;
        log.warn("Returning HTTP 405 Bad Request", e);
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handles(Exception e) {
        Logger log = null;
        log.warn("Returning HTTP 403 Bad Request", e);
    }

}
