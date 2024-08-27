import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GerarBuscasTestes {

    public static final String GRAFOS_DIR = "./grafos/";

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int numeroBuscas = 3;
        int vertices = 10000;
        double chanceAresta = 10.0;

        for (int busca = 1; busca <= numeroBuscas; busca++) {
            GrafoBusca grafoBusca = Gerador.busca(Gerador.grafo(vertices, chanceAresta), true);
            String caminhoArquivo = String.format("%sgrafo_%d_nodos_%d.json", GRAFOS_DIR, vertices, busca);
            salvarGrafoComoJson(grafoBusca, caminhoArquivo);
        }

    }

    private static void salvarGrafoComoJson(GrafoBusca grafoBusca, String caminhoArquivo) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File arquivo = new File(caminhoArquivo);
            // Cria os diretórios se não existirem
            if (!arquivo.getParentFile().exists()) {
                arquivo.getParentFile().mkdirs();
            }

            // Salva o grafo no arquivo JSON
            mapper.writeValue(arquivo, grafoBusca);

            System.out.println("Grafo salvo em " + caminhoArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o grafo em JSON: " + e.getMessage());
        }
    }
}
