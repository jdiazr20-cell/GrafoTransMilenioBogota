import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class Dijkstra {

    public Dijkstra() {
    }

    /**
     * @param grafo red de transporte que se analiza
     * @param origen indice de la estacion inicial
     * @return tiempos minimos, estaciones predecesoras y estados de seleccion
     * @throws IndexOutOfBoundsException si el origen no pertenece a la red
     */
    public ResultadoDijkstra calcular(Grafo grafo, int origen) {
        int cantidad = grafo.getCantidadEstaciones();
        validarIndice(origen, cantidad);

        int[] tiempos = new int[cantidad];
        int[] predecesores = new int[cantidad];
        boolean[] seleccionados = new boolean[cantidad];
        Arrays.fill(tiempos, Grafo.INFINITO);
        Arrays.fill(predecesores, -1);
        tiempos[origen] = 0;

        PriorityQueue<NodoTiempo> cola = new PriorityQueue<>(
            Comparator.comparingInt(nodo -> nodo.tiempo)
        );
        List<ResultadoDijkstra.Estado> estados = new ArrayList<>();
        cola.add(new NodoTiempo(origen, 0));

        while (!cola.isEmpty()) {
            NodoTiempo actual = cola.poll();
            int estacion = actual.estacion;
            if (actual.tiempo != tiempos[estacion] || seleccionados[estacion]) {
                continue;
            }
            seleccionados[estacion] = true;

            for (int destino = 0; destino < cantidad; destino++) {
                int tiempoTramo = grafo.getTiempo(estacion, destino);
                if (tiempoTramo == Grafo.INFINITO || seleccionados[destino]) {
                    continue;
                }

                int nuevoTiempo = tiempos[estacion] + tiempoTramo;
                if (nuevoTiempo < tiempos[destino]) {
                    tiempos[destino] = nuevoTiempo;
                    predecesores[destino] = estacion;
                    cola.add(new NodoTiempo(destino, nuevoTiempo));
                }
            }

            estados.add(new ResultadoDijkstra.Estado(estacion, tiempos, predecesores));
        }

        return new ResultadoDijkstra(origen, tiempos, predecesores, estados);
    }

    private void validarIndice(int indice, int cantidad) {
        if (indice < 0 || indice >= cantidad) {
            throw new IndexOutOfBoundsException("Indice de origen fuera de la red: " + indice);
        }
    }


    private static final class NodoTiempo {
        private final int estacion;
        private final int tiempo;

        private NodoTiempo(int estacion, int tiempo) {
            this.estacion = estacion;
            this.tiempo = tiempo;
        }
    }
}
