#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <string>
#include <filesystem>
#include <nlohmann/json.hpp>
#include "Grafo.h"
#include "GrafoBusca.h"
#include "Teste.h"
#include "Buscador.h"
#include "EstatisticaGrafo.h"

namespace fs = std::filesystem;
using json = nlohmann::json;

void saveTestes(const std::vector<Teste>& testes) {
    std::ofstream file("testes.csv");

    if (!file.is_open()) {
        std::cerr << "Erro ao abrir o arquivo 'testes.csv' para escrita." << std::endl;
        return;
    }

    file << "Vértices;Inicio;Busca;Tipo;Threads;Tempo(ms)\n";

    for (const auto& teste : testes) {
        file << teste.getVertices() << ";"
             << teste.getVerticeInicial() << ";"
             << teste.getValorBusca() << ";"
             << (teste.isConcorrente() ? "Concorrente" : "Serial") << ";"
             << teste.getThreads() << ";"
             << teste.getTempo() << "\n";
    }

    file.close();

    // Reabre o arquivo para leitura
    std::ifstream inFile("testes.csv");
    std::string content((std::istreambuf_iterator<char>(inFile)), std::istreambuf_iterator<char>());
    inFile.close();

    // Substitui '.' por ','
    std::replace(content.begin(), content.end(), '.', ',');

    // Reabre o arquivo para escrita
    std::ofstream outFile("testes.csv");
    outFile << content;
    outFile.close();
}

void saveEstatisticas(const std::vector<EstatisticaGrafo>& estatisticaGrafos) {
    std::ofstream file("estatisticas.csv");

    if (!file.is_open()) {
        std::cerr << "Erro ao abrir o arquivo 'estatisticas.csv' para escrita." << std::endl;
        return;
    }

    for (const auto& estatisticaGrafo : estatisticaGrafos) {
        file << estatisticaGrafo.toString();
    }

    file.close();

    // Reabre o arquivo para leitura
    std::ifstream inFile("estatisticas.csv");
    std::string content((std::istreambuf_iterator<char>(inFile)), std::istreambuf_iterator<char>());
    inFile.close();

    // Substitui '.' por ','
    std::replace(content.begin(), content.end(), '.', ',');

    // Reabre o arquivo para escrita
    std::ofstream outFile("estatisticas.csv");
    outFile << content;
    outFile.close();
}

int main() {
    const int testesVariacao = 10;
    const std::vector<int> nThreads = {1, 2, 4, 8, 12, 16, 24, 32};

    std::vector<Teste> testes;
    std::vector<EstatisticaGrafo> estatisticaGrafos;

    std::string grafosDir = "./grafos"; // Substitua pelo caminho real

    for (const auto& entry : fs::directory_iterator(grafosDir)) {
        std::ifstream file(entry.path());
        if (!file.is_open()) {
            std::cerr << "Erro ao abrir o arquivo " << entry.path() << std::endl;
            continue;
        }

        json j;
        file >> j;
        file.close();

        GrafoBusca grafoBusca = j.get<GrafoBusca>();

        EstatisticaGrafo estatisticaGrafo;
        estatisticaGrafo.setVertices(grafoBusca.getGrafo().size());
        estatisticaGrafo.setInicio(grafoBusca.getInicio());
        estatisticaGrafo.setBusca(grafoBusca.getValorBusca());

        for (int nVariacao = 1; nVariacao <= testesVariacao; ++nVariacao) {
            Teste teste = Teste::run(grafoBusca, false, 0);
            testes.push_back(teste);
            estatisticaGrafo.getTemposSeriais().push_back(teste.getTempo());
        }

        for (int nThreadsVariacao : nThreads) {
            estatisticaGrafo.getTemposConcorrentes()[nThreadsVariacao] = std::vector<double>();

            for (int nVariacao = 1; nVariacao <= testesVariacao; ++nVariacao) {
                Teste teste = Teste::run(grafoBusca, true, nThreadsVariacao);
                testes.push_back(teste);
                estatisticaGrafo.getTemposConcorrentes()[nThreadsVariacao].push_back(teste.getTempo());
            }
        }

        estatisticaGrafos.push_back(estatisticaGrafo);
    }

    saveTestes(testes);
    saveEstatisticas(estatisticaGrafos);

    return 0;
}
