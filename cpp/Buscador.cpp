#include "Buscador.h"
#include <omp.h>
#include <algorithm>
#include <queue>

bool Buscador::sequencial(const GrafoBusca& grafoBusca) {
    std::vector<bool> visitados(grafoBusca.getGrafo().size(), false);
    std::vector<int> visitar = {grafoBusca.getInicio()};

    while (!visitar.empty()) {
        std::vector<int> proximosVisitar;

        for (int verticeVisitando : visitar) {
            if (grafoBusca.getGrafo().getValorVertice(verticeVisitando) == grafoBusca.getValorBusca()) {
                return true;
            }

            visitados[verticeVisitando] = true;

            for (int verticeAdjacente : grafoBusca.getGrafo().getVerticesAdjacentes(verticeVisitando)) {
                if (!visitados[verticeAdjacente]) {
                    proximosVisitar.push_back(verticeAdjacente);
                }
            }
        }

        visitar = proximosVisitar;
    }

    return false;
}

bool Buscador::paralelo(const GrafoBusca& grafoBusca, int nThreads) {
    std::vector<bool> visitados(grafoBusca.getGrafo().size(), false);
    std::vector<int> visitar = {grafoBusca.getInicio()};
    bool encontrado = false;

    while (!visitar.empty() && !encontrado) {
        std::vector<int> proximosVisitar;

        #pragma omp parallel num_threads(nThreads)
        {
            std::vector<int> locaisProximosVisitar;

            #pragma omp for nowait
            for (int i = 0; i < visitar.size(); ++i) {
                
                int verticeVisitando = visitar[i];

                if (grafoBusca.getGrafo().getValorVertice(verticeVisitando) == grafoBusca.getValorBusca()) {
                    #pragma omp critical 
                    {
                        encontrado = true;
                    }
                }

                #pragma omp critical 
                {
                    visitados[verticeVisitando] = true;
                }

                for (int verticeAdjacente : grafoBusca.getGrafo().getVerticesAdjacentes(verticeVisitando)) {
                    if (!visitados[verticeAdjacente]) {
                        locaisProximosVisitar.push_back(verticeAdjacente);
                    }
                }

            }

            #pragma omp critical 
            {
                proximosVisitar.insert(proximosVisitar.end(), locaisProximosVisitar.begin(), locaisProximosVisitar.end());
            }
        }

        #pragma omp flush(encontrado)
        if (encontrado) break;

        // Remove duplicatas dos próximos vértices a visitar
        std::sort(proximosVisitar.begin(), proximosVisitar.end());
        proximosVisitar.erase(std::unique(proximosVisitar.begin(), proximosVisitar.end()), proximosVisitar.end());

        visitar = proximosVisitar;
    }

    return encontrado;
}
