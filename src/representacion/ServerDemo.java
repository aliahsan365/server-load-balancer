package representacion;

import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * copipasteado de private static void eightPuzzleDLSDemo() ...
 *
 * Since 2016_03_16
 *
 * @author felix.axel.gimeno
 *
 */
public class ServerDemo {
    public static final int ALGSEED = 22;
    public static final int INITSEED = 1234;

    public static void main(String[] args) {
        try {
            System.out.println("Bienvenido");
            //Algoritmos: true - Hill Climbing, false - Simulated Annealing
            boolean saORhc = true;
            //Heuristicas: 1 - t.max servidor mínimo, 2 - t.max servidor mínimo & balanceo de carga
            final Integer heuristicCriteria = 2;
            //Criterios generacion: 1 - fijo, 2 - iterativo, Otro - random
            final int criterioGeneracionEstadosIniciales = 2;
            Problem problem;
            Search search;

            //HILL CLIMBING
            if (saORhc) {
                problem = new Problem(
                               //ns, nrep, seed1, nu, nreq, seed2, algseed, criteria
                    new ServerData(50, 5, INITSEED, 200, 5, INITSEED, ALGSEED, criterioGeneracionEstadosIniciales),
                    new ServerSuccessorFunction(heuristicCriteria),
                    new ServerGoalTest(),
                    new ServerHeuristicFunction(heuristicCriteria)
                );
                search = new HillClimbingSearch();

                System.out.println("Procederemos a ejecutar la busqueda");
                long begin = System.nanoTime();

                SearchAgent agent = new SearchAgent(problem, search);

                long total = System.nanoTime() - begin;

                printActions(agent.getActions());
                printInstrumentation(agent.getInstrumentation());
                ServerData goal = (ServerData) search.getGoalState();
                if (goal != null){
                    System.out.println(goal.getQuality());
                    System.out.println(new ServerHeuristicFunction(heuristicCriteria).getHeuristicValue(goal));
                    double n = goal.getQuality().stream().mapToDouble((a)->a).reduce(0,(a,b)->a+b);
                    System.out.println("Sum of transmisison times: "+n);
                }
                int min = (int)(total/1000000000)/60;
                int sec = (int)(total/1000000000)%60;
                System.out.println("Time: " + min + " min, " + sec + " s");
                System.out.println("Total: " + total/1000000 + " ms");


            //SIMULATED ANNEALING
            } else {
                problem = new Problem(
                        new ServerData(50, 5, INITSEED, 200, 5, INITSEED, ALGSEED, criterioGeneracionEstadosIniciales),
                        new ServerSuccessorFunctionR(),
                        new ServerGoalTest(),
                        new ServerHeuristicFunction(heuristicCriteria)
                );

                int steps = 300000;
                int stiter = 100;
                double hs = 0;
                double ms = 0;
                double hst = 0;
                for (double lamb : Arrays.asList(0.000001)) {
                    for (int k : Arrays.asList(15)) {
                        int n = 1;

                        search = new SimulatedAnnealingSearch(steps, stiter, k, lamb);

                        long begin = System.nanoTime();
                        SearchAgent agent = new SearchAgent(problem, search);
                        long total = System.nanoTime() - begin;
                        ms += total / 1000000;
                        hs += new ServerHeuristicFunction(2).getHeuristicValue((ServerData) search.getGoalState());
                        System.out.println(((ServerData) search.getGoalState()).getQuality());
                        hst = ((ServerData) search.getGoalState()).getQuality().stream().mapToDouble((a)->a).reduce(0,(a,b)->a+b);

                        double hs_avg = hs/n;
                        System.out.printf("lamb %f; k %d; tiempo %d; heur. avg. %d; " +
                                "heur. sum. %d \n", lamb, k, (int) ms, (int) hs_avg, (int) hst);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }
    }

    private static void printActions(List actions) {
        actions.stream().map((action1) -> action1.toString()).
                forEach((action) -> {
                    System.out.println(action);
                });
    }
}
