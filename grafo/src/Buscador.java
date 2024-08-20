import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Buscador<T> {
    
    private Grafo<T> grafo;

    public Buscador(Grafo<T> grafo) {
        this.grafo = grafo;
    }

    public Stack<Integer> sequencial(int start, T valor) {
        Stack<Integer> caminho = new Stack<>();
        ArrayList<Integer> visitados = new ArrayList<>();

        caminho.add(start);

        while (!caminho.isEmpty()) {
            Integer vertice = caminho.peek();

            visitados.add(vertice);

            if (grafo.getValorVertice(vertice).equals(valor)) {
                return caminho;
            }

            Integer verticeVisitar = null;

            for (Integer verticeAdjacente : grafo.getVerticesAdjacentes(vertice)) {
                if (!visitados.contains(verticeAdjacente) && !caminho.contains(verticeAdjacente)) {
                    verticeVisitar = verticeAdjacente;
                    break;
                }
            }

            if (verticeVisitar != null) {
                caminho.push(verticeVisitar);
            } else {
                caminho.pop();
            }
        }

        return null; // Retorna null se o valor não for encontrado
    }

    public Stack<Integer> paralela(int start, T valor, int threads) throws InterruptedException, ExecutionException {
        AtomicIntegerArray visitados = new AtomicIntegerArray(grafo.size());
        for (int i = 0; i < visitados.length(); i++) {
            visitados.set(i, 0);
        }
        AtomicBoolean resultadoEncontrado = new AtomicBoolean(false);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        return paralelaRamo(start, valor, executorService, visitados, resultadoEncontrado, new Stack<>());
    }

    public Stack<Integer> paralelaRamo(int start, T valor, ExecutorService executorService, AtomicIntegerArray visitados, AtomicBoolean resultadoEncontrado, Stack<Integer> caminho) throws InterruptedException, ExecutionException {
        final int VISITADO = 1;
        ArrayList<Future<Stack<Integer>>> caminhosRamos = new ArrayList<Future<Stack<Integer>>>();
        caminho.add(start);
        while (!caminho.isEmpty() && !resultadoEncontrado.get()) {
            Integer vertice = caminho.peek();
            visitados.set(vertice, VISITADO);
            if (grafo.getValorVertice(vertice).equals(valor)) {
                resultadoEncontrado.set(true);
                return caminho;
            }
            Integer verticeVisitar = null;
            for (Integer verticeAdjacente : grafo.getVerticesAdjacentes(vertice)) {
                if (verticeVisitar == null && visitados.get(verticeAdjacente) != VISITADO && !caminho.contains(verticeAdjacente)) {
                    verticeVisitar = verticeAdjacente;
                    continue;
                }
                @SuppressWarnings("unchecked")
                Stack<Integer> caminhoClone = (Stack<Integer>) caminho.clone();
                caminhosRamos.add(executorService.submit(() -> {
                    return this.paralelaRamo(verticeAdjacente, valor, executorService, visitados, resultadoEncontrado, caminhoClone);
                }));
            }
            if (verticeVisitar != null) {
                caminho.push(verticeVisitar);
            } else {
                caminho.pop();
            }
        }
        for (Future<Stack<Integer>> future : caminhosRamos) {
            caminho = future.get();
            if(caminho != null) {
                return caminho;
            }
        }
        return null;
    }
}
