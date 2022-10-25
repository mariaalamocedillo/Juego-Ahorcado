package es.mariaa.ahorcadospring.servicios;


import es.mariaa.ahorcadospring.modelo.Partida;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PartidaService {
    private List<Partida> repositorio = new ArrayList<>();

    public void add (Partida p){
        repositorio.add(p);
    }
    public void delete (int id){
        repositorio.removeIf(partida -> partida.getId() == id);
    }

    public List<Partida> findAll(){
        return repositorio;
    }

    @PostConstruct //se ejecuta tras ejecutar el constructor
    public void init() throws IOException, JSONException {
        List<String> palabras = generateRandomWords(14);
        for (int i = 0; i < palabras.size(); i++) {
            repositorio.add(new Partida(i+1, palabras.get(i)));
        }
    }


    public List<String> generateRandomWords(int numberOfWords) throws IOException, JSONException {
        List<String> listado = new ArrayList<>(List.of(new String[0]));
        for (int i = 0; i <= numberOfWords; i++) {
            StringBuilder result = new StringBuilder();
            String nueva;
            URL url = new URL("https://palabras-aleatorias-public-api.herokuapp.com/random");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            try (var reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }
            JSONObject jsonarray = new JSONObject(result.toString());
            JSONObject objeto = jsonarray.getJSONObject("body");
            nueva = objeto.getString("Word"); //obtiene valor 10
            listado.add(nueva.split(" ")[0]);
        }
        return listado;

    }
    public int last(){
        List<Integer> numeros = new ArrayList<Integer>();
        for (int i = 1; i < repositorio.size()+1; i++) {
            numeros.add((int) repositorio.get(i-1).getId());
        }
        for (int i = 1; i < repositorio.size()+1; i++) {
            //si al reccorrer el array, hay una partida que no tiene el mismo id que su posición, significa que no existe ese id; lo devolvemos
            if (i != repositorio.get(i-1).getId() && !numeros.contains(i)){
                return i;
            }
        }
        //si todas las partidas del array son continuas, devolvemos el ultimo
        return repositorio.size()+1;
    }

    //obtiene número aleatorio para seleccionar una partida (en base al tamaño del array de partidas)
    public int getRandom(){
        //int n = (int) (Math.random() * (<número_máximo + 1> - <número_mínimo>)) + <numero_mínimo>;
        int longitud = repositorio.size() ;
        return (int) (Math.random() * (longitud + 1 - 1) + 1);
    }

    public Partida obtenerPartida(int id){
        int i;
        for (i = 0; i <= repositorio.size(); i++) {
            if (repositorio.get(i).getId() == id)
                break;
        }
        return repositorio.get(i);
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
    //comprobamos si ha acertado la letra usando otros métodos, y realizamos los ajustes pertinentes (número de intentos y nuevo estado)
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
            if (!falladas.contains("" + nuevaLetra)){
                if (falladas.length() == 0)  partida.setLetrasFalladas(falladas + nuevaLetra);
                else  partida.setLetrasFalladas(falladas + "-" + nuevaLetra);
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
                nuevoEstado += '_';
            }
        }
        return nuevoEstado;
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
        Pattern regex = Pattern.compile("[áéíúóäëïüö]");
        Matcher mat = regex.matcher(s + "");
        if (!mat.matches()) //si no es una vocal con tilde, la devolvemos directamente(para no cambiar la ñ)
            return s;
        String string = Normalizer.normalize(s+ "", Normalizer.Form.NFD);
        string = string.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return string.charAt(0);
    }

}
