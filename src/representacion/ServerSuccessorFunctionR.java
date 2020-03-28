package representacion;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class ServerSuccessorFunctionR implements SuccessorFunction {
    @Override
    public List getSuccessors(Object state) {
        final ServerData data = (ServerData) state;
        List<Successor> list = new ArrayList<> ();
        for (int count = 0; count < 100; count += 1){
            final int i = new Random().nextInt(data.getNservers());
            final int j = new Random().nextInt(data.getNservers());
            if (data.getRequests(i).isEmpty() || data.getRequests(j).isEmpty() ) continue;

            final int k1 = data.getRequests(i).get(new Random().nextInt(data.getRequests(i).size()));
            final ServerData dataNew = new ServerData(data);
            final boolean swapSelected = 0 == new Random().nextInt(1+data.getNreq());
            
            if (swapSelected){
                final int k2 = data.getRequests(j).get(new Random().nextInt(data.getRequests(j).size()));
                if (!data.isPossibleSwap(i, k1, j, k2)) continue;
                dataNew.swapRequest(i, k1, j, k2);
                list.add(new Successor(String.format("swap i %d k1 %d j %d k2 %d",i,k1, j,k2), dataNew)); 
                return list;
            }
            else {
                if (!data.isPossibleMove(i, j, k1)) continue;
                dataNew.moveRequest(i, j, k1);
                list.add(new Successor(String.format("move i %d j %d k %d",i,j,k1), dataNew));
                return list;
            }
            
            //move far es numberServers^2 * numberRequests
            //sap far es numberServers^2 * numberRequests^2w
            // swap far / move far = numberRequests
        }
        //assert(!list.isEmpty());
        //System.out.println(list);
        return list;
    }
}