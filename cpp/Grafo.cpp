#include "Grafo.h"
#include <nlohmann/json.hpp>

// Implementações dos métodos

const std::vector<int>& Grafo::getVertices() const {
    return vertices;
}

void Grafo::setVertices(const std::vector<int>& v) {
    vertices = v;
}

const std::vector<std::vector<int>>& Grafo::getAdjacencias() const {
    return adjacencias;
}

void Grafo::setAdjacencias(const std::vector<std::vector<int>>& a) {
    adjacencias = a;
}

int Grafo::addVertice(int valor) {
    vertices.push_back(valor);
    adjacencias.push_back(std::vector<int>());
    return vertices.size() - 1;
}

int Grafo::getValorVertice(int indice) const {
    return vertices[indice];
}

const std::vector<int>& Grafo::getVerticesAdjacentes(int vertice) const {
    return adjacencias[vertice];
}

int Grafo::size() const {
    return vertices.size();
}

void Grafo::addAresta(int origem, int destino) {
    adjacencias[origem].push_back(destino);
}

bool Grafo::temAresta(int origem, int destino) const {
    const auto& adj = adjacencias[origem];
    return std::find(adj.begin(), adj.end(), destino) != adj.end();
}

void to_json(nlohmann::json& j, const Grafo& g) {
    j = nlohmann::json{{"vertices", g.vertices}, {"adjacencias", g.adjacencias}};
}

void from_json(const nlohmann::json& j, Grafo& g) {
    j.at("vertices").get_to(g.vertices);
    j.at("adjacencias").get_to(g.adjacencias);
}