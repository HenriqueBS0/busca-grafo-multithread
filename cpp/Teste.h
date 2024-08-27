#ifndef TESTE_H
#define TESTE_H

#include "GrafoBusca.h"
#include "Buscador.h"
#include <string>

class Teste {
private:
    int vertices;
    int verticeInicial;
    int valorBusca;
    bool concorrente;
    int threads;
    double tempo;

public:
    // Getters e Setters
    int getVertices() const;
    void setVertices(int vertices);
    int getVerticeInicial() const;
    void setVerticeInicial(int verticeInicial);
    int getValorBusca() const;
    void setValorBusca(int valorBusca);
    bool isConcorrente() const;
    void setConcorrente(bool concorrente);
    int getThreads() const;
    void setThreads(int threads);
    double getTempo() const;
    void setTempo(double tempo);

    // Método estático para execução do teste
    static Teste run(const GrafoBusca& grafoBusca, bool concorrente, int threads);

    // Método para converter a classe em string
    std::string toString() const;
};

#endif // TESTE_H
