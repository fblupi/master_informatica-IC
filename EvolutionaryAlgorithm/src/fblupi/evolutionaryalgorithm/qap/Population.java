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
    public void calculateFitness(QAPMatrices matrices) {
        for (int i = 0; i < population.length; i++) {
            population[i].calculateFitness(matrices);
        }
    }

    /**
     * Get fittest individual
     * @return fittest individual
     */
    public Individual getFittest() {
        Individual fittest = population[0];
        for (int i = 1; i < population.length; i++) {
            if (population[i].getFitness() < fittest.getFitness()) {
                fittest = population[i];
            }
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
