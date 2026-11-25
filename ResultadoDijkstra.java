import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public final class ResultadoDijkstra {
    private final int origen;
    private final int[] tiempos;
    private final int[] predecesores;
    private final List<Estado> estados;

    ResultadoDijkstra(
        int origen, int[] tiempos, int[] predecesores, List<Estado> estados
    ) {
        this.origen = origen;
        this.tiempos = tiempos.clone();
        this.predecesores = predecesores.clone();
        this.estados = Collections.unmodifiableList(new ArrayList<>(estados));
    }

    public int getTiempoHasta(int destino) {
        validarIndice(destino);
        return tiempos[destino];
    }

    public int getPredecesorDe(int estacion) {
        validarIndice(estacion);
        return predecesores[estacion];
    }

    public List<Integer> getRutaHasta(int destino) {
        validarIndice(destino);
        LinkedList<Integer> ruta = new LinkedList<>();
        for (int actual = destino; actual != -1; actual = predecesores[actual]) {
            ruta.addFirst(actual);
            if (actual == origen) {
                return Collections.unmodifiableList(ruta);
            }
        }
        return Collections.emptyList();
    }

    public List<Estado> getEstados() {
        return estados;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tiempos.length) {
            throw new IndexOutOfBoundsException("Indice de estacion fuera del resultado: " + indice);
        }
    }

    public static final class Estado {
        private final int estacionSeleccionada;
        private final int[] tiempos;
        private final int[] predecesores;

        Estado(int estacionSeleccionada, int[] tiempos, int[] predecesores) {
            this.estacionSeleccionada = estacionSeleccionada;
            this.tiempos = tiempos.clone();
            this.predecesores = predecesores.clone();
        }

        public int getEstacionSeleccionada() {
            return estacionSeleccionada;
        }

        public int getTiempo(int estacion) {
            validarIndice(estacion);
            return tiempos[estacion];
        }

        public int getPredecesor(int estacion) {
            validarIndice(estacion);
            return predecesores[estacion];
        }

        private void validarIndice(int indice) {
            if (indice < 0 || indice >= tiempos.length) {
                throw new IndexOutOfBoundsException("Indice de estacion fuera del estado: " + indice);
            }
        }
    }
}
