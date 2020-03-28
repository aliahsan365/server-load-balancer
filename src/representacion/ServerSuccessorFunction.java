package representacion;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

/**
 * Created by josep on 3/14/16.
 */
public class ServerSuccessorFunction implements SuccessorFunction {

    int heuristic = 0;

    public ServerSuccessorFunction(int heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public List getSuccessors(Object state) {
        ServerData data = (ServerData) state;
        ServerData dataNew;
        Successor best = null;
        List<Successor> list = new ArrayList<>();
        double oldquality = new ServerHeuristicFunction(heuristic).getHeuristicValue(data);
        for (Integer i = 0; i < data.getNservers(); i += 1) {
            if (data.getRequests(i).isEmpty()) {
                continue;
            }
            for (Integer j = 0; j < data.getNservers(); j += 1) {
                if (!Objects.equals(j, i)) {
                    for (Integer k : (ArrayList<Integer>) data.getRequests(i)) {

                        if (!data.isPossibleMove(i, j, k)) {
                            continue;
                        }
                        dataNew = new ServerData(data);
                        dataNew.moveRequest(i, j, k);
                        if (list.isEmpty()) {
                            list.add(new Successor(String.format("move s1 %d s2 %d req %d", i, j, k), dataNew));
                        }
                        double newquality = new ServerHeuristicFunction(heuristic).getHeuristicValue(dataNew);
                        if (newquality <= oldquality) {
                            oldquality = newquality;
                            best = new Successor(String.format("move s1 %d s2 %d req %d", i, j, k), dataNew);
                            list.add(best);
                        }
                    }
                }
            }
        }
        for (Integer i = 0; i < data.getNservers(); i += 1) {
            if (data.getRequests(i).isEmpty()) {
                continue;
            }
            for (Integer j = 0; j < data.getNservers(); j += 1) {
                if (!Objects.equals(j, i)) {
                    for (Integer k : (ArrayList<Integer>) data.getRequests(i)) {
                        for (Integer l : (ArrayList<Integer>) data.getRequests(j)) {
                            if (data.isPossibleSwap(i, k, j, l)) {
                                dataNew = new ServerData(data);
                                dataNew.swapRequest(i, k, j, l);
                                double newquality = new ServerHeuristicFunction(heuristic).getHeuristicValue(dataNew);
                                if (list.isEmpty()) {
                                    list.add(new Successor(String.format("swap s1 %d req1 %d s2 %d req2 %d", i, k, j, l), dataNew));
                                }
                                if (newquality <= oldquality) {
                                    oldquality = newquality;
                                    best = new Successor(String.format("swap s1 %d req1 %d s2 %d req2 %d", i, k, j, l), dataNew);
                                    list.add(best);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (null != best) list.add(best);
        if (list.isEmpty()) return null;
        assert (!list.isEmpty());
        //System.out.println(list);
        return list;
    }
}
