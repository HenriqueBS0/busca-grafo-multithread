#ifndef GRAFOBUSCA_H
#define GRAFOBUSCA_H

#include "Grafo.h"  // Inclua o cabeçalho da classe Grafo
#include <nlohmann/json.hpp>

class GrafoBusca {
private:
    int inicio;
    int valorBusca;
    Grafo grafo;

public:
    // Getters e Setters
    int getInicio() const;
    void setInicio(int i);
    
    int getValorBusca() const;
    void setValorBusca(int v);
    
    const Grafo& getGrafo() const;
    void setGrafo(const Grafo& g);

    friend void to_json(nlohmann::json& j, const GrafoBusca& gb);
    friend void from_json(const nlohmann::json& j, GrafoBusca& gb);
};

#endif // GRAFOBUSCA_H