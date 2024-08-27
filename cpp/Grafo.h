#ifndef GRAFO_H
#define GRAFO_H

#include <vector>
#include <algorithm>
#include <nlohmann/json.hpp>

class Grafo {
private:
    std::vector<int> vertices;
    std::vector<std::vector<int>> adjacencias;

public:
    // Métodos da classe
    const std::vector<int>& getVertices() const;
    void setVertices(const std::vector<int>& v);
    const std::vector<std::vector<int>>& getAdjacencias() const;
    void setAdjacencias(const std::vector<std::vector<int>>& a);
    int addVertice(int valor);
    int getValorVertice(int indice) const;
    const std::vector<int>& getVerticesAdjacentes(int vertice) const;
    int size() const;
    void addAresta(int origem, int destino);
    bool temAresta(int origem, int destino) const;
    friend void to_json(nlohmann::json& j, const Grafo& g);
    friend void from_json(const nlohmann::json& j, Grafo& g);
};

#endif // GRAFO_H
