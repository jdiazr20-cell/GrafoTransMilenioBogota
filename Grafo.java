import java.util.Arrays;


public final class Grafo {
    public static final int INFINITO = 1_000_000_000;

    private final String[] estaciones;

    private final int[][] tiempos;

    /**
     * 
     *
     * @param estaciones nombres de los vertices (estaciones/portales), en orden de indice
     * @throws IllegalArgumentException si no se proporcionan estaciones
     */
    public Grafo(String... estaciones) {
        if (estaciones == null || estaciones.length == 0) {
            throw new IllegalArgumentException("La red debe tener al menos una estacion.");
        }

        this.estaciones = estaciones.clone();
        this.tiempos = new int[estaciones.length][estaciones.length];
        for (int i = 0; i < tiempos.length; i++) {
            Arrays.fill(tiempos[i], INFINITO);
            tiempos[i][i] = 0;
        }
    }

    /**
     *
     *
     * @param origen indice de la estacion de salida
     * @param destino indice de la estacion de llegada
     * @param minutos tiempo estimado de viaje en minutos (no negativo)
     * @throws IndexOutOfBoundsException si alguno de los indices no pertenece a la red
     * @throws IllegalArgumentException si el tiempo es negativo
     */
    public void agregarTramo(int origen, int destino, int minutos) {
        validarIndice(origen);
        validarIndice(destino);
        if (minutos < 0) {
            throw new IllegalArgumentException("El tiempo de viaje no puede ser negativo.");
        }
        tiempos[origen][destino] = minutos;
    }

    /**
     * @param estacionA primera estacion
     * @param estacionB segunda estacion
     * @param minutos tiempo estimado en cada sentido
     */
    public void agregarTramoBidireccional(int estacionA, int estacionB, int minutos) {
        agregarTramo(estacionA, estacionB, minutos);
        agregarTramo(estacionB, estacionA, minutos);
    }

    /**
     * @return cantidad de estaciones
     */
    public int getCantidadEstaciones() {
        return estaciones.length;
    }

    /**
     * @param indice indice de la estacion
     * @return nombre de la estacion indicada
     * @throws IndexOutOfBoundsException si el indice no pertenece a la red
     */
    public String getNombreEstacion(int indice) {
        validarIndice(indice);
        return estaciones[indice];
    }

    /**
     * @param origen indice de la estacion de salida
     * @param destino indice de la estacion de llegada
     * @return tiempo en minutos o el valor centinela
     * @throws IndexOutOfBoundsException si alguno de los indices no pertenece a la red
     */
    public int getTiempo(int origen, int destino) {
        validarIndice(origen);
        validarIndice(destino);
        return tiempos[origen][destino];
    }

    /**
     * @param origen indice de la estacion de salida
     * @param destino indice de la estacion de llegada
     * @return {@code true} si hay un tramo directo entre las estaciones
     * @throws IndexOutOfBoundsException si alguno de los indices no pertenece a la red
     */
    public boolean existeTramo(int origen, int destino) {
        validarIndice(origen);
        validarIndice(destino);
        return origen != destino && getTiempo(origen, destino) != INFINITO;
    }

    /**
     * @param indice indice que se valida
     * @throws IndexOutOfBoundsException si el indice es invalido
     */
    private void validarIndice(int indice) {
        if (indice < 0 || indice >= estaciones.length) {
            throw new IndexOutOfBoundsException("Indice de estacion fuera de la red: " + indice);
        }
    }
}
