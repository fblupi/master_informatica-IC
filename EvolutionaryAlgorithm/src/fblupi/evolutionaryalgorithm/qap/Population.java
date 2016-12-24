package fblupi.evolutionaryalgorithm.qap;

/**
 * Population in the evolutionary algorithm
 */
public class Population {
    /**
     * Population
     */
    private Individual[] population;

    /**
     * Constructor. Create as random population as size parameter
     * @param size size of the population
     */
    public Population(int size) {
        population = new Individual[size];
    }

    /**
     * Generate random population
     * @param size number of genes
     */
    public void initialize(int size) {
        for (int i = 0; i < population.length; i++) {
            population[i] = new Individual(size);
            population[i].initialize();
        }
    }

    /**
     * Calculate fitness of each individual in the population
     * @param matrices input matrices
     */
    public void calculateFitness(Matrices matrices) {
        for (int i = 0; i < population.length; i++) {
            population[i].calculateFitness(matrices);
        }
    }

    /**
     * Get fittest individual
     * @param type evolutionary algorithm type
     * @return fittest individual
     */
    public Individual getFittest(EAType type) {
        Individual fittest = new Individual(population[0]);
        switch (type) {
            case STANDARD:
                for (int i = 1; i < population.length; i++) {
                    if (population[i].getFitness() < fittest.getFitness()) {
                        fittest = population[i];
                    }
                }
                break;

            case BALDWINIAN:
                for (int k = 0; k < population.length; k++) {
                    Individual best;
                    Individual S = new Individual(population[k]);
                    do {
                        best = S;
                        for (int i = 0; i < population[k].getSolution().length; i++) {
                            for (int j = i + 1; j < population[k].getSolution().length; j++) {
                                Individual T = new Individual(S);
                                T.getSolution()[i] = S.getSolution()[j];
                                T.getSolution()[j] = S.getSolution()[i];
                                if (T.getFitness() < S.getFitness()) {
                                    S = T;
                                }
                            }
                        }
                    } while (S != best);
                    if (S.getFitness() < fittest.getFitness()) {
                        fittest = population[k];
                    }
                }
                break;

            case LAMARCKIAN:
                for (int k = 0; k < population.length; k++) {
                    Individual best;
                    Individual S = new Individual(population[k]);
                    do {
                        best = S;
                        for (int i = 0; i < population[k].getSolution().length; i++) {
                            for (int j = i + 1; j < population[k].getSolution().length; j++) {
                                Individual T = new Individual(S);
                                T.getSolution()[i] = S.getSolution()[j];
                                T.getSolution()[j] = S.getSolution()[i];
                                if (T.getFitness() < S.getFitness()) {
                                    S = T;
                                }
                            }
                        }
                    } while (S != best);
                    if (S.getFitness() < fittest.getFitness()) {
                        if (S.getFitness() < population[k].getFitness()) {
                            population[k] = S;
                        }
                        fittest = population[k];
                    }
                }
                break;
        }
        return fittest;
    }

    /**
     * Get population
     * @return population
     */
    public Individual[] getPopulation() {
        return population;
    }
}
