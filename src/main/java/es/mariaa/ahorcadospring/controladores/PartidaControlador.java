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


@Controller
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
        model.addAttribute("huecos", servicio.crearHuecos(p.getEstado()));

        model.addAttribute( "partida", p);
        return "partida";
    }

    @GetMapping("/partida/jugar/random")
    public String partidaRandom (){
        int partida = servicio.getRandom();
        return "redirect:/partida/jugar/" + partida;
    }

    @PostMapping("/partida/nuevaLetra")
    public String jugada(@ModelAttribute("partida") Partida partida, Model model){
        //si el carcter introducido es una letra, procedemos a realizar la partida
        Pattern regex = Pattern.compile("[a-zA-ZÀ-ÿ\u00f1\u00d1]");
        Matcher mat = regex.matcher(partida.getNuevaLetra());
        if (mat.matches())
            partida = servicio.manejarPartida(partida);

        if (partida.getIntentos() == 0 || Objects.equals(partida.getEstado(), partida.getPalabraOculta()))
            return "final";

        String claseImagen = servicio.asignaImg(partida.getIntentos());
        model.addAttribute("claseImagen", claseImagen);
        model.addAttribute("huecos", servicio.crearHuecos(partida.getEstado()));
        return "partida";
    }

/*    @GetMapping("/finPartida")
    public String finPartida(@ModelAttribute("partida") Partida partida, Model model){
        String mensajeFinal = "";

        if (partida.getIntentos() == 0)
            mensajeFinal = "¡Enhorabuena!, has adivinado la palaba";
        else
            mensajeFinal = "¡Oh nooo!, has perdido";

        model.addAttribute("mensaje", mensajeFinal);
        return "final";
    }*/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(Exception e) {
        Logger log = null;
        log.warn("Returning HTTP 400 Bad Request", e);
    }

}
