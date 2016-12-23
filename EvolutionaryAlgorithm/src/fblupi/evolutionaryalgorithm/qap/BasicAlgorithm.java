package fblupi.evolutionaryalgorithm.qap;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Basic evolutionary algorithm
 */
public class BasicAlgorithm {
    /**
     * Mutation probability after crossover
     */
    private final double MUTATION_PROBABILITY = 0.3;

    /**
     * Number of generations
     */
    private final int NUMBER_OF_GENERATIONS = 10000;

    /**
     * Population size
     */
    private final int POPULATION_SIZE = 50;

    /**
     * Number of tournament participants
     */
    private final int TOURNAMENT_SIZE = 16;
    private Population population;
    private QAPMatrices matrices;

    /**
     * Constructor. Copy input matrices and initialize population
     * @param matrices input matrices
     */
    public BasicAlgorithm(QAPMatrices matrices) {
        this.matrices = matrices;
        population = new Population(POPULATION_SIZE);
        population.initialize(matrices);
    }

    public void execute() {
        for (int i = 0; i < NUMBER_OF_GENERATIONS; i++) {
            System.out.println("Generation " + (i + 1) + "...");
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

                children[0].calculateFitness();
                children[1].calculateFitness();

                // Insert children in new population
                generation.getPopulation()[2 * j] = children[0];
                generation.getPopulation()[2 * j + 1] = children[1];
            }
            population = generation; // Evolve into the new generation
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
            tournament.getPopulation()[i] = population.getPopulation()[participant];
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
        children[0] = new Individual(matrices);
        children[1] = new Individual(matrices);

        // Generating positions to cut
        int position1, position2;
        position1 = r.nextInt(matrices.getSize());
        do {
            position2 = r.nextInt(matrices.getSize());
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

        for (int i = 0; i < matrices.getSize(); i++) { // iterate children
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
        if (prob < MUTATION_PROBABILITY) {
            // Positions to mutate
            int pos1, pos2;
            pos1 = r.nextInt(matrices.getSize());
            do {
                pos2 = r.nextInt(matrices.getSize());
            } while (pos1 == pos2); // cannot mutate the same position
            individual.mutate(pos1, pos2); // mutate
        }
    }

    /**
     * Get population
     * @return population
     */
    public Population getPopulation() {
        return population;
    }

}
