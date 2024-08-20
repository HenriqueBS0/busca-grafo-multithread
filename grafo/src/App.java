import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class App {
    private static final String GRAFOS_DIR = "./grafos/";

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        testes();
    }

    public static void testes() throws InterruptedException, ExecutionException, IOException {
        System.out.println("Número de Vértices;Vértice Inicial;Valor da Busca;Tipo;Threads;Tempo (ms)");

        File diretorioGrafos = new File(GRAFOS_DIR);
        File[] arquivos = diretorioGrafos.listFiles((dir, name) -> name.endsWith(".json"));

        int[] nThreads = {1, 2, 4, 8, 16};

        if (arquivos != null) {
            for (File arquivo : arquivos) {
                GrafoDTO<Integer> grafoDTO = carregarGrafoDoArquivo(arquivo);
                Grafo<Integer> grafo = converterDTOParaGrafo(grafoDTO);

                int verticeInicial = grafoDTO.getInicio();
                int verticeFinal = grafoDTO.getValorBusca();
                Buscador<Integer> buscador = new Buscador<>(grafo);

                // Teste Serial
                for (int j = 0; j < 10; j++) {
                    long tempoInicio = System.nanoTime();
                    buscador.sequencial(verticeInicial, verticeFinal);
                    long tempoFim = System.nanoTime();
                    double tempoExecucao = (tempoFim - tempoInicio) / 1000000.0;
                    System.out.printf("%d;%d;%d;Serial;-;%.10f\n", grafo.size(), verticeInicial, verticeFinal, tempoExecucao);
                }

                for (int threads : nThreads) {
                    for (int j = 0; j < 10; j++) {
                        long tempoInicio = System.nanoTime();
                        long tempoFim = System.nanoTime();
                        double tempoExecucao = (tempoFim - tempoInicio) / 1000000.0;
                        System.out.printf("%d;%d;%d;Paralelo;%d;%.10f\n", grafo.size(), verticeInicial, verticeFinal, threads, tempoExecucao);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static GrafoDTO<Integer> carregarGrafoDoArquivo(File arquivo) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(arquivo, GrafoDTO.class);
    }

    private static Grafo<Integer> converterDTOParaGrafo(GrafoDTO<Integer> grafoDTO) {
        Grafo<Integer> grafo = new Grafo<>();
        for (Integer vertice : grafoDTO.getVertices()) {
            grafo.addVertice(vertice);
        }
        for (int i = 0; i < grafoDTO.getMatrizAdjacencias().size(); i++) {
            for (int j = 0; j < grafoDTO.getMatrizAdjacencias().get(i).size(); j++) {
                if (grafoDTO.getMatrizAdjacencias().get(i).get(j)) {
                    grafo.addAresta(i, j);
                }
            }
        }
        return grafo;
    }

    private static void gerarGrafos() {
        // Configurações para os diferentes tamanhos de grafos
        int[] tamanhos = {1000};
        int numGrafosPorTamanho = 3;

        for (int tamanho : tamanhos) {
            for (int i = 1; i <= numGrafosPorTamanho; i++) {
                // Gera vértices inicial e final aleatórios
                Random random = new Random();
                int verticeInicial = random.nextInt(tamanho);
                int verticeFinal;
                do {
                    verticeFinal = random.nextInt(tamanho);
                } while (verticeFinal == verticeInicial); // Garante que o vértice final seja diferente do inicial

                // Gera o grafo aleatório
                Grafo<Integer> grafo = Grafo.gerarGrafoAleatorio(tamanho, verticeInicial, verticeFinal);

                // Define o caminho do arquivo onde o grafo será salvo
                String caminhoArquivo = String.format("./grafos/grafo_%d_nodos_%d.json", tamanho, i);

                GrafoDTO<Integer> grafoDTO = new GrafoDTO<>(grafo);
                grafoDTO.setInicio(verticeInicial);
                grafoDTO.setValorBusca(verticeFinal);

                // salva o grafo em um arquivo JSON
                salvarGrafoComoJson(grafoDTO, caminhoArquivo);
            }
        }
    }

    private static void salvarGrafoComoJson(GrafoDTO<Integer> grafoDTO, String caminhoArquivo) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File arquivo = new File(caminhoArquivo);
            // Cria os diretórios se não existirem
            if (!arquivo.getParentFile().exists()) {
                arquivo.getParentFile().mkdirs();
            }

            // Salva o grafo no arquivo JSON
            mapper.writeValue(arquivo, grafoDTO);

            System.out.println("Grafo salvo em " + caminhoArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o grafo em JSON: " + e.getMessage());
        }
    }
}
