import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Testes {

    public static void main(String[] args) throws InterruptedException, ExecutionException, StreamReadException, DatabindException, IOException {
        int testesVariacao = 10;
        int[] nThreads = {1, 2, 4, 8, 12, 16, 24, 32}; 

        ArrayList<Teste> testes = new ArrayList<>();
        ArrayList<EstatisticaGrafo> estatisticaGrafos = new ArrayList<>();
        
        // Cria um objeto File
        File directory = new File(GerarBuscasTestes.GRAFOS_DIR);
    
        for (String nomeArquivo : directory.list()) {
            File arquivo = new File(GerarBuscasTestes.GRAFOS_DIR, nomeArquivo);
            ObjectMapper mapper = new ObjectMapper();
            GrafoBusca grafoBusca = mapper.readValue(arquivo, GrafoBusca.class);

            EstatisticaGrafo estatisticaGrafo = new EstatisticaGrafo();
            estatisticaGrafo.setVertices(grafoBusca.getGrafo().size());
            estatisticaGrafo.setInicio(grafoBusca.getInicio());
            estatisticaGrafo.setBusca(grafoBusca.getValorBusca());

            int nVariacao;

            for (nVariacao = 1; nVariacao <= testesVariacao; nVariacao++) {
                Teste teste = Teste.run(grafoBusca, false, 0);
                testes.add(teste);
                estatisticaGrafo.getTemposSeriais().add(teste.getTempo());
            }

            for (int nThreadsVariacao : nThreads) {
                estatisticaGrafo.getTemposConcorrentes().put(nThreadsVariacao, new ArrayList<>());

                for (nVariacao = 1; nVariacao <= testesVariacao; nVariacao++) {
                    Teste teste = Teste.run(grafoBusca, true, nThreadsVariacao);
                    testes.add(teste);
                    estatisticaGrafo.getTemposConcorrentes().get(nThreadsVariacao).add(teste.getTempo());
                }
            }

            estatisticaGrafos.add(estatisticaGrafo);
        }

        saveTestes(testes);
        saveEstatisticas(estatisticaGrafos);
    }

    private static void saveTestes(ArrayList<Teste> testes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Vértices;Inicio;Busca;Tipo;Threads;Tempo(ms)\n");

        for (Teste teste : testes) {
            stringBuilder.append(teste.getVertices())
                .append(";")
                .append(teste.getVerticeIncial())
                .append(";")
                .append(teste.getValorBusca())
                .append(";")
                .append(teste.isConcorrente() ? "Concorrente" : "Serial")
                .append(";")
                .append(teste.getThreads())
                .append(";")
                .append(teste.getTempo())
                .append("\n");
        }

        try (FileWriter writer = new FileWriter("testes.csv")) {
            writer.write(stringBuilder.toString().replace('.', ','));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveEstatisticas(ArrayList<EstatisticaGrafo> estatisticaGrafos) {
        StringBuilder stringBuilder = new StringBuilder();
        
        for (EstatisticaGrafo estatisticaGrafo : estatisticaGrafos) {
            stringBuilder.append(estatisticaGrafo.toString());
        }

        try (FileWriter writer = new FileWriter("estatisticas.csv")) {
            writer.write(stringBuilder.toString().replace('.', ','));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
