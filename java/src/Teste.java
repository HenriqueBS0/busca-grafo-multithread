import java.util.concurrent.ExecutionException;

public class Teste {
    private int vertices;
    private int verticeIncial;
    private int valorBusca;
    private boolean concorrente;
    private int threads;
    private double tempo;

    public int getVertices() {
        return vertices;
    }

    public void setVertices(int vertices) {
        this.vertices = vertices;
    }

    public int getVerticeIncial() {
        return verticeIncial;
    }

    public void setVerticeIncial(int verticeIncial) {
        this.verticeIncial = verticeIncial;
    }

    public int getValorBusca() {
        return valorBusca;
    }

    public void setValorBusca(int valorBusca) {
        this.valorBusca = valorBusca;
    }

    public boolean isConcorrente() {
        return concorrente;
    }

    public void setConcorrente(boolean concorrente) {
        this.concorrente = concorrente;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public static Teste run(GrafoBusca grafoBusca, boolean concorrente, int threads) {
        Teste teste = new Teste();
        teste.setVertices(grafoBusca.getGrafo().size());
        teste.setVerticeIncial(grafoBusca.getInicio());
        teste.setValorBusca(grafoBusca.getValorBusca());
        teste.setConcorrente(concorrente);
        teste.setThreads(concorrente ? threads : 1);

        long tempoInicio;
        if (!concorrente) {
            tempoInicio = System.nanoTime();
            Buscador.sequencial(grafoBusca);
        } else {
            tempoInicio = System.nanoTime();
            try {
                Buscador.concorrente(grafoBusca, threads);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        long tempoFim = System.nanoTime();

        teste.setTempo((tempoFim - tempoInicio) / 1000000.0);

        return teste;
    }

    @Override
    public String toString() {
        return "Teste {" +
                "vertices=" + vertices +
                ", verticeInicial=" + verticeIncial +
                ", valorBusca=" + valorBusca +
                ", concorrente=" + concorrente +
                ", threads=" + threads +
                ", tempo(ms)=" + tempo +
                '}';
    }
}
