#include "GrafoBusca.h"
#include <nlohmann/json.hpp>

// Implementações dos métodos

int GrafoBusca::getInicio() const {
    return inicio;
}

void GrafoBusca::setInicio(int i) {
    inicio = i;
}

int GrafoBusca::getValorBusca() const {
    return valorBusca;
}

void GrafoBusca::setValorBusca(int v) {
    valorBusca = v;
}

const Grafo& GrafoBusca::getGrafo() const {
    return grafo;
}

void GrafoBusca::setGrafo(const Grafo& g) {
    grafo = g;
}

void to_json(nlohmann::json& j, const GrafoBusca& gb) {
    j = nlohmann::json{{"inicio", gb.inicio}, {"valorBusca", gb.valorBusca}, {"grafo", gb.grafo}};
}

void from_json(const nlohmann::json& j, GrafoBusca& gb) {
    j.at("inicio").get_to(gb.inicio);
    j.at("valorBusca").get_to(gb.valorBusca);
    j.at("grafo").get_to(gb.grafo);
}