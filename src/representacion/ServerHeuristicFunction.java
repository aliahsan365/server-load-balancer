package representacion;

import aima.search.framework.HeuristicFunction;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by josep on 3/14/16.
 */
public class ServerHeuristicFunction implements HeuristicFunction {
    int selection = 1;
    public ServerHeuristicFunction(int selection){
        this.selection = selection;
    }
    @Override
    public double getHeuristicValue(Object state) {
        ServerData data = (ServerData) state;
        
        if (1 == selection) {
            //t.max servers mínimo
            return (double)data.getQuality().stream().max((a,b)-> Integer.compare(a,b)).get();
        }
        if (2 == selection) {
            //Valor del estado: t.max servers mínimo mas el sumatorio de
            //las diferencias de tiempos de servidor respecto a la media
            double avg = (double)data.getQuality().stream().mapToDouble((a)->a).average().getAsDouble();
            double n = (double)data.getQuality().stream().max((a,b)-> Integer.compare(a,b)).get();
            ArrayList<Integer> q = (ArrayList<Integer>) data.getQuality().clone();
            double sum = 0;
            for (int i = 0; i < q.size(); i++) {
                sum += Math.abs(q.get(i) - avg);
            }
            return n + sum;
        }
        return 0.0;
    }
}
