package fblupi.evolutionaryalgorithm.qap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Standard evolutionary algorithm
 * @author fblupi
 */
public class EA {
    /**
     * Individual mutation probability after crossover
     */
    private final double INDIVIDUAL_MUTATION_PROBABILITY = 0.75;

    /**
     * Gene mutation probability after crossover
     */
    private final double GENE_MUTATION_PROBABILITY = 0.01;

    /**
     * Number of generations
     */
    private final int NUMBER_OF_GENERATIONS = 300;

    /**
     * Population size
     */
    private final int POPULATION_SIZE = 100;

    /**
     * Number of tournament participants
     */
    private final int TOURNAMENT_SIZE = 8;

    /**
     * Type of the evolutionary algorithm
     */
    private EAType type;

    /**
     * Current population
     */
    private Population population;

    /**
     * Constructor. Copy input matrices and generate population
     * @param type type of evolutionary algorithm
     */
    public EA(EAType type) {
        this.type = type;
        population = new Population(POPULATION_SIZE);
        population.generate(Matrices.getSize());
        population.calculateImprovedFitness(type);
    }

    /**
     * Execute the algorithm during the established number of generations
     */
    public void execute() {
        for (int i = 0; i < NUMBER_OF_GENERATIONS; i++) {
            Population generation = new Population(POPULATION_SIZE);
            for (int j = 0; j < POPULATION_SIZE / 2; j++) {
                // Parents to crossover
                Individual parent1, parent2;
                parent1 = tournamentSelection();
                do {
                    parent2 = tournamentSelection();
                } while (parent1.getSolution() == parent2.getSolution()); // cannot repeat parent

                // Crossover, mutate and calculate fitness of generated children
                Individual[] children = crossover(parent1, parent2);

                mutate(children[0]);
                mutate(children[1]);

                // Insert children in new population
                generation.getPopulation()[2 * j] = new Individual(children[0]);
                generation.getPopulation()[2 * j + 1] = new Individual(children[1]);
            }
            generation.getPopulation()[0] = new Individual(population.getFittest()); // preserve best individual
            generation.calculateImprovedFitness(type); // calculate improved fitness of new generation
            population = generation; // Evolve into the new generation
            System.out.println("Generation: " + (i + 1));
            System.out.println("\tSolution: " + Arrays.toString(population.getFittest().getSolution()));
            System.out.println("\tFitness: " + population.getFittest().getFitness());
        }
    }

    /**
     * Tournament selection
     * @return tournament winner individual
     */
    private Individual tournamentSelection() {
        Random r = new Random();
        Set<Integer> participants = new HashSet<>();
        Population tournament = new Population(TOURNAMENT_SIZE);
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int participant;
            do {
                participant = r.nextInt(POPULATION_SIZE);
            } while (participants.contains(participant));
            tournament.getPopulation()[i] = new Individual(population.getPopulation()[participant]);
            tournaments.add(participant);
        }
        return tournament.getFittest();
    }

    /**
     * OX Crossover operation
     * @param parent0 First parent
     * @param parent1 Second parent
     */
    private Individual[] crossover(Individual parent0, Individual parent1) {
        Random r = new Random();

        // Create children
        Individual[] children  = new Individual[2];
        children[0] = new Individual(Matrices.getSize());
        children[1] = new Individual(Matrices.getSize());

        // Generating positions to cut
        int position1, position2;
        position1 = r.nextInt(Matrices.getSize());
        do {
            position2 = r.nextInt(Matrices.getSize());
        } while (position1 == position2); // cannot repeat the same position

        // First must be lower than second
        if (position2 < position1) {
            int swap = position1;
            position1 = position2;
            position2 = swap;
        }

        // Sets for a quicker search if a number is already in the solution of a child
        Set<Integer> individualsInChildren0 = new HashSet<>();
        Set<Integer> individualsInChildren1 = new HashSet<>();

        // Copy between the positions the partial solution of each parent
        for (int i = position1; i < position2; i++) {
            // Children 0 copies from parent 0
            children[0].getSolution()[i] = parent0.getSolution()[i];
            individualsInChildren0.add(children[0].getSolution()[i]);
            // Children 1 copies from parent 1
            children[1].getSolution()[i] = parent1.getSolution()[i];
            individualsInChildren1.add(children[1].getSolution()[i]);
        }

        for (int i = 0; i < Matrices.getSize(); i++) { // iterate children
            if (i < position1 || i >= position2) { // not between the positions
                // Search for the first value in the parent 1 which is not currently in the child 0
                int iterator = 0;
                while (individualsInChildren0.contains(parent1.getSolution()[iterator])) {
                    iterator++;
                }
                // Children 0 copies from parent 1
                children[0].getSolution()[i] = parent1.getSolution()[iterator];
                individualsInChildren0.add(parent1.getSolution()[iterator]);

                // Search for the first value in the parent 1 which is not currently in the child 0
                iterator = 0;
                while (individualsInChildren1.contains(parent0.getSolution()[iterator])) {
                    iterator++;
                }
                // Children 1 copies from parent 0
                children[1].getSolution()[i] = parent0.getSolution()[iterator];
                individualsInChildren1.add(parent0.getSolution()[iterator]);
            }
        }

        return children;
    }

    /**
     * Mutate operation
     * @param individual individual to mutate
     */
    private void mutate(Individual individual) {
        Random r = new Random();
        double prob = r.nextDouble();
        // Check if mutate
        if (prob < INDIVIDUAL_MUTATION_PROBABILITY) {
            for (int i = 0; i < Matrices.getSize(); i++) {
                prob = r.nextDouble();
                if (prob < GENE_MUTATION_PROBABILITY) {
                    // Position to mutate with i
                    int j;
                    do {
                        j = r.nextInt(Matrices.getSize());
                    } while (j == i); // cannot mutate the same position
                    individual.mutate(i, j); // mutate
                }
            }
        }
    }

    /**
     * Get fittest individual
     * @return fittest individual
     */
    public Individual getFittest() {
        return population.getFittest();
    }
}
