import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EstatisticaGrafo {
    private int vertices;
    private int inicio;
    private int busca;
    private ArrayList<Double> temposSeriais = new ArrayList<>();
    private HashMap<Integer, ArrayList<Double>> temposConcorrentes = new HashMap<Integer, ArrayList<Double>>();

    public int getVertices() {
        return vertices;
    }

    public void setVertices(int vertices) {
        this.vertices = vertices;
    }

    public int getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public int getBusca() {
        return busca;
    }

    public void setBusca(int busca) {
        this.busca = busca;
    }

    public ArrayList<Double> getTemposSeriais() {
        return temposSeriais;
    }

    public void setTemposSeriais(ArrayList<Double> temposSeriais) {
        this.temposSeriais = temposSeriais;
    }

    public HashMap<Integer, ArrayList<Double>> getTemposConcorrentes() {
        return temposConcorrentes;
    }

    public void setTemposConcorrentes(HashMap<Integer, ArrayList<Double>> temposConcorrentes) {
        this.temposConcorrentes = temposConcorrentes;
    }

    private static double calcularMedia(ArrayList<Double> tempos) {
        double soma = 0;
        for (double tempo : tempos) {
            soma += tempo;
        }
        return tempos.isEmpty() ? 0 : soma / tempos.size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Cabeçalho da primeira parte
        sb.append("Vértices;Inicio;Busca;Tempo Serial (ms)\n");
        
        // Conteúdo da primeira parte
        double mediaTempoSerial = calcularMedia(temposSeriais);
        sb.append(vertices)
          .append(";")
          .append(inicio)
          .append(";")
          .append(busca)
          .append(";")
          .append(mediaTempoSerial)
          .append("\n");

        // Cabeçalho da segunda parte
        sb.append("Número Threads;Tempo (ms);Speedup;Eficiência\n");

        double mediaTempoConcorrenteUnicaThread = calcularMedia(getTemposConcorrentes().get(1));
        // Usando TreeMap para garantir a ordem crescente das threads
        Map<Integer, ArrayList<Double>> sortedMap = new TreeMap<>(temposConcorrentes);

        // Conteúdo da segunda parte
        for (Map.Entry<Integer, ArrayList<Double>> entry : sortedMap.entrySet()) {
            int numeroThreads = entry.getKey();
            double mediaTempoConcorrente = calcularMedia(entry.getValue());
            double speedup = mediaTempoConcorrenteUnicaThread / mediaTempoConcorrente;
            double eficiencia = speedup / numeroThreads;
            
            sb.append(numeroThreads)
              .append(";")
              .append(mediaTempoConcorrente)
              .append(";")
              .append(speedup)
              .append(";")
              .append(eficiencia)
              .append("\n");
        }

        return sb.toString();
    }
}
