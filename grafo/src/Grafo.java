import java.util.ArrayList;
import java.util.Random;

public class Grafo<T> {

    private class Vertice {
        private T valor;

        public Vertice(T valor) {
            this.valor = valor;
        }

        public T getValor() {
            return valor;
        }
    }

    private ArrayList<Vertice> vertices = new ArrayList<Vertice>();
    private ArrayList<ArrayList<Boolean>> matrizAdjacencias = new ArrayList<ArrayList<Boolean>>();

    public int addVertice(T valor) {
        vertices.add(new Vertice(valor));

        // Adicionar um novo elemento em todas as linhas existentes
        for (ArrayList<Boolean> linha : matrizAdjacencias) {
            linha.add(false);
        }

        // Adicionar uma nova linha na matriz de adjacências
        ArrayList<Boolean> novaLinha = new ArrayList<Boolean>();
        for (int i = 0; i < vertices.size(); i++) {
            novaLinha.add(false);
        }

        matrizAdjacencias.add(novaLinha);

        return vertices.size() - 1;
    }

    public T getValorVertice(int indice) {
        return vertices.get(indice).getValor();
    }

    public ArrayList<Integer> getVerticesAdjacentes(Integer vertice) {
        ArrayList<Integer> adjacentes = new ArrayList<Integer>();

        ArrayList<Boolean> vetorAdjacencias = matrizAdjacencias.get(vertice);

        for (int i = 0; i < vetorAdjacencias.size(); i++) {
            if (vetorAdjacencias.get(i)) {
                adjacentes.add(i);
            }
        }

        return adjacentes;
    }

    public Integer size() {
        return vertices.size();
    }

    public void addAresta(int origem, int destino) {
        matrizAdjacencias.get(origem).set(destino, true);
    }

    public boolean temAresta(int origem, int destino) {
        return matrizAdjacencias.get(origem).get(destino);
    }

    public static Grafo<Integer> gerarGrafoAleatorio(int numNodos, int verticeInicial, int verticeFinal) {
        Grafo<Integer> grafo = new Grafo<>();
        Random random = new Random();
    
        // Adiciona os vértices
        for (int i = 0; i < numNodos; i++) {
            grafo.addVertice(i);
        }
    
        // Cria um caminho aleatório entre o vértice inicial e o vértice final
        int verticeAtual = verticeInicial;
        while (verticeAtual != verticeFinal) {
            int proximoVertice;
            do {
                proximoVertice = random.nextInt(numNodos);
            } while (proximoVertice == verticeAtual || grafo.temAresta(verticeAtual, proximoVertice)); // Evita loop e arestas duplicadas
    
            grafo.addAresta(verticeAtual, proximoVertice);
            verticeAtual = proximoVertice;
        }
    
        // Adiciona arestas aleatórias para outros vértices
        for (int i = 0; i < numNodos; i++) {
            for (int j = 0; j < numNodos; j++) {
                if (i != j && !grafo.temAresta(i, j) && random.nextDouble() <= 0.1) { // Adiciona aresta com 10% de chance
                    grafo.addAresta(i, j);
                }
            }
        }
    
        return grafo;
    }
}