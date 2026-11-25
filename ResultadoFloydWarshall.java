import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class ResultadoFloydWarshall {
    private final int[][] tiempos;
    private final int[][] siguiente;
    private final List<Actualizacion> actualizaciones;

    ResultadoFloydWarshall(
        int[][] tiempos,
        int[][] siguiente,
        List<Actualizacion> actualizaciones
    ) {
        this.tiempos = copiarMatriz(tiempos);
        this.siguiente = copiarMatriz(siguiente);
        this.actualizaciones = Collections.unmodifiableList(
            new ArrayList<>(actualizaciones)
        );
    }

    public int getCantidadEstaciones() {
        return tiempos.length;
    }

    public int getTiempo(int origen, int destino) {
        validarIndice(origen);
        validarIndice(destino);
        return tiempos[origen][destino];
    }

    public List<Integer> getRuta(int origen, int destino) {
        validarIndice(origen);
        validarIndice(destino);
        if (siguiente[origen][destino] == -1) {
            return Collections.emptyList();
        }

        List<Integer> ruta = new ArrayList<>();
        int actual = origen;
        ruta.add(actual);
        while (actual != destino) {
            actual = siguiente[actual][destino];
            if (actual == -1) {
                return Collections.emptyList();
            }
            ruta.add(actual);
        }
        return Collections.unmodifiableList(ruta);
    }

    public List<Actualizacion> getActualizaciones() {
        return actualizaciones;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tiempos.length) {
            throw new IndexOutOfBoundsException("Indice de estacion fuera del resultado: " + indice);
        }
    }

    private static int[][] copiarMatriz(int[][] matriz) {
        int[][] copia = new int[matriz.length][];
        for (int i = 0; i < matriz.length; i++) {
            copia[i] = matriz[i].clone();
        }
        return copia;
    }

    public static final class Actualizacion {
        private final int estacionIntermedia;
        private final int origen;
        private final int destino;
        private final int tiempoAnterior;
        private final int tiempoNuevo;

        Actualizacion(
            int estacionIntermedia,
            int origen,
            int destino,
            int tiempoAnterior,
            int tiempoNuevo
        ) {
            this.estacionIntermedia = estacionIntermedia;
            this.origen = origen;
            this.destino = destino;
            this.tiempoAnterior = tiempoAnterior;
            this.tiempoNuevo = tiempoNuevo;
        }

        public int getEstacionIntermedia() {
            return estacionIntermedia;
        }

        public int getOrigen() {
            return origen;
        }

        public int getDestino() {
            return destino;
        }

        public int getTiempoAnterior() {
            return tiempoAnterior;
        }

        public int getTiempoNuevo() {
            return tiempoNuevo;
        }
    }
}
