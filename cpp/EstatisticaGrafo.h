#include <vector>
#include <unordered_map>
#include <map>
#include <string>

class EstatisticaGrafo {
private:
    int vertices;
    int inicio;
    int busca;
    std::vector<double> temposSeriais;
    std::unordered_map<int, std::vector<double>> temposConcorrentes;

    double calcularMedia(const std::vector<double>& tempos) const;

public:
    int getVertices() const;
    void setVertices(int v);

    int getInicio() const;
    void setInicio(int i);

    int getBusca() const;
    void setBusca(int b);

    std::vector<double>& getTemposSeriais();
    void setTemposSeriais(const std::vector<double>& t);

    std::unordered_map<int, std::vector<double>>& getTemposConcorrentes(); // Modificado
    void setTemposConcorrentes(const std::unordered_map<int, std::vector<double>>& t);

    std::string toString() const;
};
