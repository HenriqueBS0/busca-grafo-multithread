import java.util.ArrayList;

public class Grafo {
    private ArrayList<Integer> vertices = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> adjacencias = new ArrayList<>();

    public ArrayList<Integer> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Integer> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<ArrayList<Integer>> getAdjacencias() {
        return adjacencias;
    }

    public void setAdjacencias(ArrayList<ArrayList<Integer>> adjacencias) {
        this.adjacencias = adjacencias;
    }

    public int addVertice(Integer valor) {
        vertices.add(valor);
        adjacencias.add(new ArrayList<>());
        return vertices.size() - 1;
    }

    public Integer getValorVertice(int indice) {
        return vertices.get(indice);
    }

    public ArrayList<Integer> getVerticesAdjacentes(Integer vertice) {
        return adjacencias.get(vertice);
    }

    public Integer size() {
        return vertices.size();
    }

    public void addAresta(int origem, int destino) {
        adjacencias.get(origem).add(destino);
    }

    public boolean temAresta(int origem, int destino) {
        return adjacencias.get(origem).contains(destino);
    }
}