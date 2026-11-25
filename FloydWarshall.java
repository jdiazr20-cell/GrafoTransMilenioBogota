import java.util.ArrayList;
import java.util.List;


public final class FloydWarshall {
    public FloydWarshall() {
    }

    public ResultadoFloydWarshall calcular(Grafo grafo) {
        int cantidad = grafo.getCantidadEstaciones();
        int[][] tiempos = new int[cantidad][cantidad];
        int[][] siguiente = new int[cantidad][cantidad];
        List<ResultadoFloydWarshall.Actualizacion> actualizaciones = new ArrayList<>();

        for (int origen = 0; origen < cantidad; origen++) {
            for (int destino = 0; destino < cantidad; destino++) {
                tiempos[origen][destino] = grafo.getTiempo(origen, destino);
                siguiente[origen][destino] =
                    grafo.getTiempo(origen, destino) == Grafo.INFINITO ? -1 : destino;
            }
        }

        for (int intermedia = 0; intermedia < cantidad; intermedia++) {
            for (int origen = 0; origen < cantidad; origen++) {
                if (tiempos[origen][intermedia] == Grafo.INFINITO) {
                    continue;
                }
                for (int destino = 0; destino < cantidad; destino++) {
                    if (tiempos[intermedia][destino] == Grafo.INFINITO) {
                        continue;
                    }

                    int alternativa =
                        tiempos[origen][intermedia] + tiempos[intermedia][destino];
                    if (alternativa < tiempos[origen][destino]) {
                        actualizaciones.add(new ResultadoFloydWarshall.Actualizacion(
                            intermedia,
                            origen,
                            destino,
                            tiempos[origen][destino],
                            alternativa
                        ));
                        tiempos[origen][destino] = alternativa;
                        siguiente[origen][destino] = siguiente[origen][intermedia];
                    }
                }
            }
        }

        return new ResultadoFloydWarshall(tiempos, siguiente, actualizaciones);
    }
}
