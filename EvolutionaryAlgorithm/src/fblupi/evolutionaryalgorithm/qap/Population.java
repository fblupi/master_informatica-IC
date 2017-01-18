package fblupi.evolutionaryalgorithm.qap;

/**
 * Population in the evolutionary algorithm
 * @author fblupi
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
    public void generate(int size) {
        for (int i = 0; i < population.length; i++) {
            population[i] = new Individual(size);
            population[i].generate();
        }
    }

    /**
     * Calculate improved fitness of each individual in the population
     */
    public void calculateImprovedFitness(EAType type) {
        for (int i = 0; i < population.length; i++) {
            population[i].calculateImprovedFitness(type);
        }
    }

    /**
     * Get fittest individual
     * @return fittest individual
     */
    public Individual getFittest() {
        Individual fittest = new Individual(population[0]);
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
