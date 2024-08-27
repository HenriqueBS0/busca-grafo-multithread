#ifndef BUSCADOR_H
#define BUSCADOR_H

#include "GrafoBusca.h"
#include <vector>
#include <atomic>

class Buscador {
public:
    // Função para busca sequencial
    static bool sequencial(const GrafoBusca& grafoBusca);

    // Função para busca concorrente utilizando OpenMP
    static bool paralelo(const GrafoBusca& grafoBusca, int nThreads);
};

#endif // BUSCADOR_H
