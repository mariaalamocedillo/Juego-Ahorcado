package es.mariaa.ahorcadospring.servicios;


import es.mariaa.ahorcadospring.modelo.Partida;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PartidaService {
    private List<Partida> repositorio = new ArrayList<>();

    public Partida add (Partida p){
        repositorio.add(p);
        return p;
    }

    public List<Partida> findAll(){
        return repositorio;
    }



    @PostConstruct //se ejecuta tras ejecutar el constructor
    public void init(){
        repositorio.addAll(
                Arrays.asList(new Partida(1, "pato"),
                        new Partida(2, "ornitorrinco"),
                        new Partida(3, "mamut"),
                        new Partida(4, "antílope"),
                        new Partida(5, "ñú")
                )
        );
    }

    public int getRandom(){
        //int n = (int) (Math.random() * (<número_máximo + 1> - <número_mínimo>)) + <numero_mínimo>;
        int longitud = repositorio.size() ;
        return (int) (Math.random() * (longitud + 1 - 1) + 1);
    }

    public Partida obtenerPartida(int id){
        return repositorio.get(id-1);
    }

    //comprueba si la letra es acertada o no
    public boolean compruebaLetra(String palabra, char letra){
        for (int i = 0; i < palabra.length(); i++) {
            //comparamos las letras sin tildes
            if (Objects.equals(quitaTildes(palabra.charAt(i)), quitaTildes(letra)))
                return true;
        }
        return false;
    }

    public Partida manejarPartida(Partida partida){
        char nuevaLetra = quitaTildes(partida.getNuevaLetra().toLowerCase().charAt(0));
        String palabra = partida.getPalabraOculta();
        //si ha adivinado la letra, actualizamos el estado. En caso contrario, la añadimos a falladas y restamos un intento (solo si no se habia intentado antes esa letra)
        if (compruebaLetra(palabra, nuevaLetra)){
            String estado = actualizarEstado(palabra, partida.getEstado(), nuevaLetra) ;
            partida.setEstado(estado);
        } else {
            String falladas = partida.getLetrasFalladas();
            //si la lista de falladas no contiene la letra introducida con o sin tilde
            if (!falladas.contains(nuevaLetra + "-")){
                partida.setLetrasFalladas(falladas + nuevaLetra + "-");
                partida.setIntentos(partida.getIntentos() - 1);
            }
        }
        partida.setNuevaLetra(""); //vaciamos el valor de la letra
        return partida;
    }


    //crea un String que forma la palabra con las letras acertadas y con guiones en los caracteres no adivinados
    public String actualizarEstado(String palabra, String estado, char nuevaLetra){
        String nuevoEstado = "";
        for (int i = 0; i < palabra.length(); i++) {
            char letraActual = palabra.charAt(i);
            //comprobamos si ambas letras son iguales sin tildes
            if (Objects.equals(quitaTildes(letraActual), nuevaLetra)) {
                nuevoEstado += letraActual;
            }else if(Objects.equals(quitaTildes(estado.charAt(i)), quitaTildes(letraActual))) {
                nuevoEstado += letraActual;
            }else {
                nuevoEstado += '-';
            }
        }
        return nuevoEstado;
    }

    //creamos los huecos visualmente
    public String crearHuecos(String estado){
        String huecos = "";
        for (int i = 0; i < estado.length(); i++) {
            if (estado.charAt(i) == '-')
                huecos += " ___ ";
            else
                huecos += " " + estado.charAt(i) + " ";
        }
        return huecos;
    }
    //asigna la clase de css que se dará a la imagen (representa los intentos)
    public String asignaImg(int intentos){
        String clase = "";
        switch (intentos){
            case 1:
                clase = "uno";
                break;
            case 2:
                clase = "dos";
                break;
            case 3:
                clase = "tres";
                break;
            case 4:
                clase = "cuatro";
                break;
            case 5:
                clase = "cinco";
                break;
            case 6:
                clase = "seis";
                break;
            default:
                clase = "cero";
        }
        return clase;
    }
    //elimina las tildes
    public char quitaTildes(char s) {
        Pattern regex = Pattern.compile("[áéíúó]");
        Matcher mat = regex.matcher(s + "");
        if (!mat.matches()) //si no es una vocal con tilde, la devolvemos directamente(para no cambiar la ñ)
            return s;
        String string = Normalizer.normalize(s+ "", Normalizer.Form.NFD);
        string = string.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return string.charAt(0);
    }

}
