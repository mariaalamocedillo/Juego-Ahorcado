package es.mariaa.ahorcadospring.modelo;

import lombok.Data;

@Data
public class Partida {
    private long id;
    private String palabraOculta;
    private String estado;
    private String letrasFalladas;
    private String nuevaLetra;
    private int intentos;

    public long getId() {
        return id;
    }

    public String getPalabraOculta() {
        return palabraOculta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLetrasFalladas() {
        return letrasFalladas;
    }

    public void setLetrasFalladas(String letrasFalladas) {
        this.letrasFalladas = letrasFalladas;
    }

    public String getNuevaLetra() {
        return nuevaLetra;
    }

    public void setNuevaLetra(String nuevaLetra) {
        this.nuevaLetra = nuevaLetra;
    }

    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }



    public Partida(long id, String palabraOculta) {
        this.id = id;
        this.palabraOculta = palabraOculta;
        this.letrasFalladas = "";
        this.estado = creaEstado();
        this.intentos = 6;
    }

    private String creaEstado(){
        String estado = "";
        for (int i = 0; i < palabraOculta.length(); i++) {
            estado += "_";
        }
        return estado;
    }

}
