#include "Teste.h"
#include <chrono>
#include <iostream>
#include <sstream>  // Para std::stringstream

int Teste::getVertices() const {
    return vertices;
}

void Teste::setVertices(int vertices) {
    this->vertices = vertices;
}

int Teste::getVerticeInicial() const {
    return verticeInicial;
}

void Teste::setVerticeInicial(int verticeInicial) {
    this->verticeInicial = verticeInicial;
}

int Teste::getValorBusca() const {
    return valorBusca;
}

void Teste::setValorBusca(int valorBusca) {
    this->valorBusca = valorBusca;
}

bool Teste::isConcorrente() const {
    return concorrente;
}

void Teste::setConcorrente(bool concorrente) {
    this->concorrente = concorrente;
}

int Teste::getThreads() const {
    return threads;
}

void Teste::setThreads(int threads) {
    this->threads = threads;
}

double Teste::getTempo() const {
    return tempo;
}

void Teste::setTempo(double tempo) {
    this->tempo = tempo;
}

Teste Teste::run(const GrafoBusca& grafoBusca, bool concorrente, int threads) {
    Teste teste;
    teste.setVertices(grafoBusca.getGrafo().size());
    teste.setVerticeInicial(grafoBusca.getInicio());
    teste.setValorBusca(grafoBusca.getValorBusca());
    teste.setConcorrente(concorrente);
    teste.setThreads(concorrente ? threads : 1);

    auto tempoInicio = std::chrono::high_resolution_clock::now();

    if (!concorrente) {
        Buscador::sequencial(grafoBusca);
    } else {
        try {
            Buscador::paralelo(grafoBusca, threads);
        } catch (const std::exception& e) {
            std::cerr << "Exception: " << e.what() << std::endl;
        }
    }

    auto tempoFim = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double, std::milli> tempoDecorrido = tempoFim - tempoInicio;
    teste.setTempo(tempoDecorrido.count());

    return teste;
}

std::string Teste::toString() const {
    std::stringstream ss;

    ss << "Teste {"
       << "vertices=" << vertices
       << ", verticeInicial=" << verticeInicial
       << ", valorBusca=" << valorBusca
       << ", concorrente=" << (concorrente ? "true" : "false")
       << ", threads=" << threads
       << ", tempo(ms)=" << tempo
       << '}';

    return ss.str();
}
