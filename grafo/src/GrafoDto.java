import java.util.ArrayList;

// Classe DTO para o grafo
class GrafoDTO<T> {
    private int inicio;
    private int valorBusca;
    private ArrayList<T> vertices = new ArrayList<>();
    private ArrayList<ArrayList<Boolean>> matrizAdjacencias = new ArrayList<>();

    public GrafoDTO() {
        
    }

    public GrafoDTO(Grafo<T> grafo) {
        // Adiciona os valores dos vértices
        for (int i = 0; i < grafo.size(); i++) {
            vertices.add(grafo.getValorVertice(i));

            matrizAdjacencias.add(new ArrayList<>());

            for (int j = 0; j < grafo.size(); j++) {
                matrizAdjacencias.get(i).add(grafo.temAresta(i, j));
            }
        }
    }

    // Getters e Setters
    public int getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public int getValorBusca() {
        return valorBusca;
    }

    public void setValorBusca(int valorBusca) {
        this.valorBusca = valorBusca;
    }

    public ArrayList<T> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<T> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<ArrayList<Boolean>> getMatrizAdjacencias() {
        return matrizAdjacencias;
    }

    public void setMatrizAdjacencias(ArrayList<ArrayList<Boolean>> matrizAdjacencias) {
        this.matrizAdjacencias = matrizAdjacencias;
    }
}
