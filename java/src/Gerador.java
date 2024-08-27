import java.util.Random;

public class Gerador {

    private static Random random = new Random(System.nanoTime());

    public static Grafo grafo(int vertices, double changeAresta) {
        Grafo grafo = new Grafo();
    
        // Adiciona os vértices
        for (int i = 0; i < vertices; i++) {
            grafo.addVertice(i);
        }

        for (int origem = 0; origem < vertices; origem++) {
            for (int destino = 0; destino < vertices; destino++) {
                if(origem == destino) {
                    continue;
                }

                if(random.nextDouble() <= changeAresta / 100) {
                    grafo.addAresta(origem, destino);
                }
            }
        }
    
        return grafo;
    }

    public static Grafo caminho(Grafo grafo, int verticeIncio, int verticeFinal) {
        Random random = new Random();

        int verticeAtual = verticeIncio;

        do {
            int verticeAnterior = verticeAtual;
            verticeAtual = random.nextInt(grafo.size());

            if(!grafo.temAresta(verticeAnterior, verticeAtual)) {
                grafo.addAresta(verticeAnterior, verticeAtual);
            }

        } while (verticeAtual != verticeFinal);

        return grafo;
    }

    public static GrafoBusca busca(Grafo grafo, boolean caminhoGarantido) {
        GrafoBusca grafoBusca = new GrafoBusca();
        Random random = new Random();
        grafoBusca.setInicio(random.nextInt(grafo.size()));
        grafoBusca.setValorBusca(random.nextInt(grafo.size()));        
        grafoBusca.setGrafo(caminhoGarantido ? caminho(grafo, grafoBusca.getInicio(), grafoBusca.getValorBusca()) : grafo);
        return grafoBusca;
    }
}