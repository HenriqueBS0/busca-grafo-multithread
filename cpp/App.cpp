#include <iostream>  // Inclusão da biblioteca para entrada e saída padrão
#include <vector>    // Inclusão da biblioteca para utilizar vetores
#include <algorithm> // Inclusão da biblioteca para algoritmos como sort e unique
#include <omp.h>     // Inclusão da biblioteca para OpenMP, que fornece suporte para paralelismo
#include <fstream>
#include <chrono>
#include "nlohmann/json.hpp"

using json = nlohmann::json;

// Declaração da classe Grafo
class Grafo
{
private:
    // Estrutura interna para representar um vértice
    struct Vertice
    {
        int valor;                           // Valor associado ao vértice
        Vertice(int valor) : valor(valor) {} // Construtor que inicializa o valor do vértice
    };

    std::vector<Vertice> vertices;                    // Vetor de vértices
    std::vector<std::vector<bool>> matrizAdjacencias; // Matriz de adjacências representando as arestas

public:
    // Adiciona um novo vértice ao grafo e retorna o índice do novo vértice
    int addVertice(int valor)
    {
        vertices.push_back(Vertice(valor)); // Adiciona o novo vértice ao vetor

        // Atualiza a matriz de adjacências para incluir o novo vértice
        for (auto &linha : matrizAdjacencias)
        {
            linha.push_back(false); // Adiciona uma nova coluna com valor 'false'
        }

        // Adiciona uma nova linha na matriz de adjacências
        std::vector<bool> novaLinha(vertices.size(), false);
        matrizAdjacencias.push_back(novaLinha);

        return vertices.size() - 1; // Retorna o índice do novo vértice
    }

    // Retorna o valor do vértice dado o índice
    int getValorVertice(int indice)
    {
        return vertices[indice].valor;
    }

    // Retorna os vértices adjacentes a um vértice dado
    std::vector<int> getVerticesAdjacentes(int vertice)
    {
        std::vector<int> adjacentes;

        const std::vector<bool> &vetorAdjacencias = matrizAdjacencias[vertice];

        // Verifica quais vértices são adjacentes
        for (int i = 0; i < vetorAdjacencias.size(); ++i)
        {
            if (vetorAdjacencias[i])
            {
                adjacentes.push_back(i);
            }
        }

        return adjacentes;
    }

    // Retorna o número de vértices no grafo
    int size() const
    {
        return vertices.size();
    }

    // Adiciona uma aresta entre dois vértices
    void addAresta(int origem, int destino)
    {
        matrizAdjacencias[origem][destino] = true;
    }

    // Verifica se existe uma aresta entre dois vértices
    bool temAresta(int origem, int destino) const
    {
        return matrizAdjacencias[origem][destino];
    }

    // Realiza uma busca em largura para encontrar um valor no grafo
    bool busca(int inicial, int valorAlvo)
    {
        std::vector<int> visitar{inicial};   // Vetor de vértices a serem visitados
        std::vector<int> visitados{inicial}; // Vetor de vértices já visitados

        while (!visitar.empty())
        {
            std::vector<int> proximosVisitar;

            for (int i = 0; i < visitar.size(); ++i)
            {
                int verticeAtual = visitar[i];

                visitados.push_back(verticeAtual);

                if (vertices[verticeAtual].valor == valorAlvo)
                {
                    return true; // Valor encontrado
                }

                for (int verticeAdjacente : getVerticesAdjacentes(verticeAtual))
                {
                    if (std::find(visitados.begin(), visitados.end(), verticeAdjacente) == visitados.end())
                    {
                        proximosVisitar.push_back(verticeAdjacente);
                    }
                }
            }

            visitar = proximosVisitar;
        }

        return false; // Valor não encontrado
    }

    bool buscaParalela(int inicial, int valorAlvo, int numThreads)
    {
        std::vector<bool> visitados(vertices.size(), false); // Vetor para marcar os vértices visitados
        std::vector<int> visitar{inicial};                   // Vértices a serem visitados
        visitados[inicial] = true;

        bool encontrado = false; // Flag para indicar se o valor foi encontrado

        while (!visitar.empty())
        {
            std::vector<int> proximosVisitar;

#pragma omp parallel num_threads(numThreads) // Inicia a paralelização
            {
                std::vector<int> locaisVisitar;

#pragma omp for nowait // Paraleliza o loop sem aguardar término das threads
                for (int i = 0; i < visitar.size(); ++i)
                {
                    int verticeAtual = visitar[i];

                    if (vertices[verticeAtual].valor == valorAlvo)
                    {
#pragma omp critical // Se o valor foi encontrado, evita múltiplos threads atualizando a flag
                        {
                            encontrado = true; // Valor encontrado
                        }
                    }

                    if (!encontrado)
                    {
                        for (int verticeAdjacente : getVerticesAdjacentes(verticeAtual))
                        {
                            if (!visitados[verticeAdjacente])
                            {
                                locaisVisitar.push_back(verticeAdjacente);
                            }
                        }
                    }
                }

#pragma omp critical // Atualiza o vetor de visitados de forma segura
                {
                    for (int vertice : locaisVisitar)
                    {
                        if (!visitados[vertice])
                        {
                            visitados[vertice] = true;
                            proximosVisitar.push_back(vertice);
                        }
                    }
                }
            }

            if (encontrado)
            {
                return true; // Valor encontrado
            }

            // Remove duplicatas dos próximos vértices a visitar
            std::sort(proximosVisitar.begin(), proximosVisitar.end());
            proximosVisitar.erase(std::unique(proximosVisitar.begin(), proximosVisitar.end()), proximosVisitar.end());

            visitar = proximosVisitar;
        }

        return false; // Valor não encontrado
    }
};

// Função para carregar o grafo a partir de um arquivo JSON
Grafo carregarGrafoDoArquivo(const std::string &caminho)
{
    std::ifstream arquivo(caminho);
    json j;
    arquivo >> j;

    Grafo grafo;

    // Adiciona os vértices
    for (int valor : j["vertices"])
    {
        grafo.addVertice(valor);
    }

    // Adiciona as arestas
    for (const auto &linha : j["matrizAdjacencias"])
    {
        for (int i = 0; i < linha.size(); ++i)
        {
            if (linha[i])
            {
                grafo.addAresta(&linha - &j["matrizAdjacencias"][0], i);
            }
        }
    }

    return grafo;
}

// Função para realizar testes
void testes()
{
    std::cout << "Número de Vértices;Vértice Inicial;Valor da Busca;Tipo;Threads;Tempo (ms)" << std::endl;

    std::vector<std::string> arquivos = {
        "./grafos/grafo_1000_nodos_1.json",
        "./grafos/grafo_1000_nodos_2.json",
        "./grafos/grafo_1000_nodos_3.json",
    };

    int nThreads[] = {1, 2, 4, 8, 16};

    for (const auto &caminho : arquivos)
    {
        Grafo grafo = carregarGrafoDoArquivo(caminho);

        std::ifstream arquivo(caminho);
        json j;
        arquivo >> j;

        int verticeInicial = j["inicio"]; // Substitua com o valor real de inicial
        int valorBusca = j["valorBusca"]; // Substitua com o valor real de busca

        for (int j = 0; j < 10; ++j)
        {
            auto start = std::chrono::high_resolution_clock::now();
            grafo.busca(verticeInicial, valorBusca);
            auto end = std::chrono::high_resolution_clock::now();
            double tempoExecucao = std::chrono::duration<double, std::milli>(end - start).count();
            std::cout << grafo.size() << ";" << verticeInicial << ";" << valorBusca << ";Serial;-;" << tempoExecucao << std::endl;
        }

        for (int threads : nThreads)
        {
            for (int j = 0; j < 10; ++j)
            {
                auto start = std::chrono::high_resolution_clock::now();
                grafo.buscaParalela(verticeInicial, valorBusca, threads);
                auto end = std::chrono::high_resolution_clock::now();
                double tempoExecucao = std::chrono::duration<double, std::milli>(end - start).count();
                std::cout << grafo.size() << ";" << verticeInicial << ";" << valorBusca << ";Paralelo;" << threads << ";" << tempoExecucao << std::endl;
            }
        }
    }
}

// Função principal
int main()
{
    testes();
    return 0;
}