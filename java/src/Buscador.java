import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Buscador {
    public static boolean sequencial(GrafoBusca grafoBusca) {
        ArrayList<Integer> visitados = new ArrayList<>();
        ArrayList<Integer> visitar = new ArrayList<>();

        visitar.add(grafoBusca.getInicio());

        while(!visitar.isEmpty()) {
            ArrayList<Integer> proximosVisitar = new ArrayList<>();

            for (Integer verticeVisitando : visitar) {
                if(grafoBusca.getGrafo().getValorVertice(verticeVisitando).equals(grafoBusca.getValorBusca())) {
                    return true;
                }

                visitados.add(verticeVisitando);

                for (Integer verticeAdjacente : grafoBusca.getGrafo().getVerticesAdjacentes(verticeVisitando)) {
                    proximosVisitar.add(verticeAdjacente);
                }
            }

            visitar = proximosVisitar;
        }

        return false;
    }

    public static boolean concorrente(GrafoBusca grafoBusca, int nThreads) throws InterruptedException, ExecutionException {
        AtomicIntegerArray visitados = new AtomicIntegerArray(grafoBusca.getGrafo().size());
        ArrayList<Integer> visitar = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        AtomicBoolean encontrou = new AtomicBoolean();

        visitar.add(grafoBusca.getInicio());

        while(!visitar.isEmpty() && !encontrou.get()) {
            ArrayList<Future<ArrayList<Integer>>> futures = new ArrayList<>();

            for (Integer verticeVisitando : visitar) {
                futures.add(executorService.submit(() -> {
                    ArrayList<Integer> proximosVisitar = new ArrayList<>();
                    
                    if(visitados.get(verticeVisitando) == 1 || encontrou.get()) {
                        return proximosVisitar;
                    }

                    if(grafoBusca.getGrafo().getValorVertice(verticeVisitando).equals(grafoBusca.getValorBusca())) {
                        encontrou.set(true);
                        return proximosVisitar;
                    }

                    for (Integer verticeAdjacente : grafoBusca.getGrafo().getVerticesAdjacentes(verticeVisitando)) {
                        if(visitados.get(verticeAdjacente) == 1) {
                            continue;
                        }
                        proximosVisitar.add(verticeAdjacente);
                    }

                    return proximosVisitar;
                }));
            }

            visitar = new ArrayList<>();

            for (Future<ArrayList<Integer>> future : futures) {
                visitar.addAll(future.get());
            }
        }

        executorService.shutdownNow();

        return encontrou.get();
    }
}
