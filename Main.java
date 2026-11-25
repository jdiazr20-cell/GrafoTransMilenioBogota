import java.util.List;


public class Main {
    private static final int PORTAL_NORTE = 0;
    private static final int CALLE_100 = 1;
    private static final int HEROES = 2;
    private static final int AV_JIMENEZ = 3;
    private static final int RICAURTE = 4;
    private static final int PORTAL_AMERICAS = 5;

    private Main() {
    }

    public static void main(String[] args) {
        Grafo redBogota = crearRedTransMilenio();
        imprimirEstaciones(redBogota);
        imprimirMatrizInicial(redBogota);
        imprimirServiciosDirectos(redBogota);

        System.out.println("\n=== Dijkstra desde Portal Norte hasta Portal Americas ===");
        ResultadoDijkstra resultadoDijkstra = new Dijkstra().calcular(redBogota, PORTAL_NORTE);
        imprimirEstadosDijkstra(redBogota, resultadoDijkstra);
        imprimirRutaYTiempo(
            "Dijkstra",
            redBogota,
            resultadoDijkstra.getRutaHasta(PORTAL_AMERICAS),
            resultadoDijkstra.getTiempoHasta(PORTAL_AMERICAS)
        );

        System.out.println("\n=== Floyd-Warshall (Matriz global de transbordos en Bogota) ===");
        ResultadoFloydWarshall resultadoFloyd =
            new FloydWarshall().calcular(redBogota);
        imprimirActualizacionesFloyd(redBogota, resultadoFloyd);
        imprimirMatrizFloyd(redBogota, resultadoFloyd);
        imprimirRutaYTiempo(
            "Floyd-Warshall",
            redBogota,
            resultadoFloyd.getRuta(PORTAL_NORTE, PORTAL_AMERICAS),
            resultadoFloyd.getTiempo(PORTAL_NORTE, PORTAL_AMERICAS)
        );

        System.out.println("\nComparacion: "
            + (resultadoDijkstra.getTiempoHasta(PORTAL_AMERICAS)
                == resultadoFloyd.getTiempo(PORTAL_NORTE, PORTAL_AMERICAS)
                ? "ambos algoritmos encuentran la ruta mas rapida en minutos."
                : "los algoritmos encontraron tiempos distintos."));
    }


    private static Grafo crearRedTransMilenio() {
        Grafo red = new Grafo(
            "Portal Norte",      // 0
            "Calle 100",         // 1
            "Heroes",            // 2
            "Av. Jimenez",       // 3
            "Ricaurte",          // 4
            "Portal Americas"    // 5
        );

        red.agregarTramo(PORTAL_NORTE, CALLE_100, 12);
        red.agregarTramo(CALLE_100, HEROES, 8);

        // Servicio expreso B14 / H15 directo Portal Norte -> Heroes
        red.agregarTramo(PORTAL_NORTE, HEROES, 16);

        // Troncal Caracas hacia el centro (Av. Jimenez)
        red.agregarTramo(HEROES, AV_JIMENEZ, 18);

        // Conexion Calle 100 -> Ricaurte via Troncal NQS (Ruta E/F) sin pasar por Jimenez
        red.agregarTramo(CALLE_100, RICAURTE, 24);

        // Tunel peatonal / transbordo troncal Av. Jimenez -> Ricaurte
        red.agregarTramo(AV_JIMENEZ, RICAURTE, 10);

        // Troncal Americas hacia el suroccidente (Ruta F)
        red.agregarTramo(RICAURTE, PORTAL_AMERICAS, 22);

        // Ruta zonal SITP directa pero mas lenta por trafico mixto (Av. Jimenez -> Portal Americas)
        red.agregarTramo(AV_JIMENEZ, PORTAL_AMERICAS, 45);

        return red;
    }

    private static void imprimirEstaciones(Grafo grafo) {
        System.out.println("Estaciones y Portales (Bogota - TM/SITP):");
        for (int i = 0; i < grafo.getCantidadEstaciones(); i++) {
            System.out.printf("%d = %s%n", i, grafo.getNombreEstacion(i));
        }
    }

    private static void imprimirMatrizInicial(Grafo grafo) {
        System.out.println("\nMatriz de tiempos directos (minutos):");
        imprimirEncabezado(grafo);
        for (int origen = 0; origen < grafo.getCantidadEstaciones(); origen++) {
            System.out.printf("%-18s", grafo.getNombreEstacion(origen));
            for (int destino = 0; destino < grafo.getCantidadEstaciones(); destino++) {
                System.out.printf("%-18s", formatearTiempo(grafo.getTiempo(origen, destino)));
            }
            System.out.println();
        }
    }

    private static void imprimirServiciosDirectos(Grafo grafo) {
        System.out.println("\nServicios directos disponibles (TM / SITP):");
        for (int origen = 0; origen < grafo.getCantidadEstaciones(); origen++) {
            for (int destino = 0; destino < grafo.getCantidadEstaciones(); destino++) {
                if (grafo.existeTramo(origen, destino)) {
                    System.out.printf(
                        "%s -> %s: %d min%n",
                        grafo.getNombreEstacion(origen),
                        grafo.getNombreEstacion(destino),
                        grafo.getTiempo(origen, destino)
                    );
                }
            }
        }
    }

    private static void imprimirEstadosDijkstra(
        Grafo grafo, ResultadoDijkstra resultado
    ) {
        for (ResultadoDijkstra.Estado estado : resultado.getEstados()) {
            System.out.println(
                "\nEstacion fijada: " + grafo.getNombreEstacion(estado.getEstacionSeleccionada())
            );
            System.out.printf("%-18s %-18s %-18s%n", "Estacion", "Tiempo (min)", "Estacion previa");
            for (int estacion = 0; estacion < grafo.getCantidadEstaciones(); estacion++) {
                int tiempo = estado.getTiempo(estacion);
                int predecesor = estado.getPredecesor(estacion);
                System.out.printf(
                    "%-18s %-18s %-18s%n",
                    grafo.getNombreEstacion(estacion),
                    formatearTiempo(tiempo),
                    predecesor == -1 ? "-" : grafo.getNombreEstacion(predecesor)
                );
            }
        }
    }

    private static void imprimirActualizacionesFloyd(
        Grafo grafo, ResultadoFloydWarshall resultado
    ) {
        for (ResultadoFloydWarshall.Actualizacion actualizacion
            : resultado.getActualizaciones()) {
            System.out.printf(
                "Transbordo en %s: %s -> %s mejora de %s a %d min%n",
                grafo.getNombreEstacion(actualizacion.getEstacionIntermedia()),
                grafo.getNombreEstacion(actualizacion.getOrigen()),
                grafo.getNombreEstacion(actualizacion.getDestino()),
                formatearTiempo(actualizacion.getTiempoAnterior()),
                actualizacion.getTiempoNuevo()
            );
        }
    }

    private static void imprimirMatrizFloyd(
        Grafo grafo, ResultadoFloydWarshall resultado
    ) {
        System.out.println("\nMatriz de tiempos minimos entre todas las estaciones (min):");
        imprimirEncabezado(grafo);
        for (int origen = 0; origen < grafo.getCantidadEstaciones(); origen++) {
            System.out.printf("%-18s", grafo.getNombreEstacion(origen));
            for (int destino = 0; destino < grafo.getCantidadEstaciones(); destino++) {
                System.out.printf(
                    "%-18s",
                    formatearTiempo(resultado.getTiempo(origen, destino))
                );
            }
            System.out.println();
        }
    }

    private static void imprimirEncabezado(Grafo grafo) {
        System.out.printf("%-18s", "");
        for (int estacion = 0; estacion < grafo.getCantidadEstaciones(); estacion++) {
            System.out.printf("%-18s", grafo.getNombreEstacion(estacion));
        }
        System.out.println();
    }

    private static void imprimirRutaYTiempo(
        String algoritmo, Grafo grafo, List<Integer> ruta, int tiempo
    ) {
        if (ruta.isEmpty() || tiempo == Grafo.INFINITO) {
            System.out.println(algoritmo + ": no existe una ruta entre las estaciones.");
            return;
        }

        StringBuilder textoRuta = new StringBuilder();
        for (int i = 0; i < ruta.size(); i++) {
            if (i > 0) {
                textoRuta.append(" -> ");
            }
            textoRuta.append(grafo.getNombreEstacion(ruta.get(i)));
        }
        System.out.println(algoritmo + ": " + textoRuta);
        System.out.println("Tiempo total estimado: " + tiempo + " minutos");
    }

    private static String formatearTiempo(int tiempo) {
        return tiempo == Grafo.INFINITO ? "INF" : Integer.toString(tiempo);
    }
}
