#include "EstatisticaGrafo.h"
#include <numeric> // Para std::accumulate
#include <sstream> // Para std::ostringstream

double EstatisticaGrafo::calcularMedia(const std::vector<double>& tempos) const {
    if (tempos.empty()) return 0;
    double soma = std::accumulate(tempos.begin(), tempos.end(), 0.0);
    return soma / tempos.size();
}

int EstatisticaGrafo::getVertices() const {
    return vertices;
}

void EstatisticaGrafo::setVertices(int v) {
    vertices = v;
}

int EstatisticaGrafo::getInicio() const {
    return inicio;
}

void EstatisticaGrafo::setInicio(int i) {
    inicio = i;
}

int EstatisticaGrafo::getBusca() const {
    return busca;
}

void EstatisticaGrafo::setBusca(int b) {
    busca = b;
}

std::vector<double>& EstatisticaGrafo::getTemposSeriais() {
    return temposSeriais;
}

void EstatisticaGrafo::setTemposSeriais(const std::vector<double>& t) {
    temposSeriais = t;
}

std::unordered_map<int, std::vector<double>>& EstatisticaGrafo::getTemposConcorrentes() {
    return temposConcorrentes; // Remove const para permitir modificação
}

void EstatisticaGrafo::setTemposConcorrentes(const std::unordered_map<int, std::vector<double>>& t) {
    temposConcorrentes = t;
}

std::string EstatisticaGrafo::toString() const {
    std::ostringstream sb;

    // Cabeçalho da primeira parte
    sb << "Vértices;Inicio;Busca;Tempo Serial (ms)\n";

    // Conteúdo da primeira parte
    double mediaTempoSerial = calcularMedia(temposSeriais);
    sb << vertices << ";" << inicio << ";" << busca << ";" << mediaTempoSerial << "\n";

    // Cabeçalho da segunda parte
    sb << "Número Threads;Tempo (ms);Speedup;Eficiência\n";

    // Calcula a média do tempo concorrente com uma única thread
    auto it = temposConcorrentes.find(1);
    double mediaTempoConcorrenteUnicaThread = (it != temposConcorrentes.end()) ? calcularMedia(it->second) : 0;

    // Usando std::map para garantir a ordem crescente das threads
    std::map<int, std::vector<double>> sortedMap(temposConcorrentes.begin(), temposConcorrentes.end());

    // Conteúdo da segunda parte
    for (const auto& entry : sortedMap) {
        int numeroThreads = entry.first;
        double mediaTempoConcorrente = calcularMedia(entry.second);
        double speedup = mediaTempoConcorrenteUnicaThread / mediaTempoConcorrente;
        double eficiencia = speedup / numeroThreads;

        sb << numeroThreads << ";" << mediaTempoConcorrente << ";" << speedup << ";" << eficiencia << "\n";
    }

    return sb.str();
}
