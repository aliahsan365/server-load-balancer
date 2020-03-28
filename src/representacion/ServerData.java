package representacion;

import java.util.ArrayList;

import IA.DistFS.Requests;
import IA.DistFS.Servers;
import java.util.Random;
import java.util.Set;

public class ServerData {

    private static int nserv;
    private static int nrep;
    private static int sseed;
    private static int nusers;
    private static int nreq;
    private static int rseed;
    private static int algseed;
    private static Servers sdata;
    private static Requests rdata;
    private ArrayList<ArrayList<Integer>> state;
    private ArrayList<Integer> quality;

    public ServerData(int ns, int nr, int s1, int nu, int nrq, int s2,int aseed, int tipoGenInicial) {
        try {
            nserv = ns;
            nrep = nr;
            sseed = s1;
            nusers = nu;
            nreq = nrq;
            rseed = s2;
            algseed = aseed;
            sdata = new Servers(ns, nr, s1);
            rdata = new Requests(nu, nrq, s2);
            state = new ArrayList<>();
            quality = new ArrayList<>(sdata.size());
            for (Integer i = 0; i < ns; i +=1){
                state.add(new ArrayList<>() );
                quality.add(i, 0);
            }
            
            this.init(tipoGenInicial);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void init(int tipoGenInicial){
        int server_dest_id = 0;
        for (int i = 0; i < rdata.size();  i+=1) {
            final int fileid = rdata.getRequest(i)[1];
            final Set<Integer> s = sdata.fileLocations(fileid);
            if(tipoGenInicial == 2) {
                while(!s.contains(server_dest_id)) {
                    //volver al inicio si se llega al final del conjunto de servidores
                    server_dest_id++;
                    if(server_dest_id >= this.getNservers()) server_dest_id = 0;
                }
            }
            else {
                server_dest_id = s.iterator().next();
                if(tipoGenInicial != 1) {
                    ArrayList<Integer> ss = new ArrayList();
                    ss.addAll(s);
                    server_dest_id = ss.get(new Random(algseed).nextInt(ss.size()));
                }
            }
            this.addRequest(server_dest_id,i);
        }
    }
    public ServerData(ServerData old) {
        this.quality = new ArrayList<>();
         for (Integer i = 0; i < old.quality.size(); i +=1){
             quality.add(old.quality.get(i));
         }
        this.state = new ArrayList<>();
        this.state.ensureCapacity(sdata.size());
        for (Integer i = 0; i < old.state.size(); i +=1){
            this.state.add(new ArrayList<>());
            this.state.get(i).ensureCapacity(rdata.size());
            for (int req : old.state.get(i)){
                this.state.get(i).add(req);
            }
        }
    }
    //Operators

    public void addRequest(int server_id, int req) {
        state.get(server_id).add(req);
        quality.set(server_id, sdata.tranmissionTime(server_id, rdata.getRequest(req)[0]) + quality.get(server_id));
    }
    public boolean isPossibleMove(int server1, int server2, int req){
        return server1 != server2 && 
                state.get(server1).contains(req) &&
                sdata.fileLocations(rdata.getRequest(req)[1]).contains(server2);
    }
    public void moveRequest(int server1, int server2, int req) {
        int n = state.get(server1).indexOf(req);
        state.get(server1).remove(n);
        quality.set(server1, -sdata.tranmissionTime(server1, rdata.getRequest(req)[0]) + quality.get(server1));
        state.get(server2).add(req);
        quality.set(server2, +sdata.tranmissionTime(server2, rdata.getRequest(req)[0]) + quality.get(server2));
    }

    public Boolean isPossibleSwap (int server1, int r1, int server2, int r2) {
        return server1 != server2 &&
                state.get(server1).contains(r1) && state.get(server2).contains(r2) &&
                sdata.fileLocations(rdata.getRequest(r1)[1]).contains(server2) &&
                sdata.fileLocations(rdata.getRequest(r2)[1]).contains(server1);
    }

    public void swapRequest(int server1, int r1, int server2, int r2) {
        int i = state.get(server1).indexOf(r1);
        int j = state.get(server2).indexOf(r2);
        state.get(server1).remove(i);
        quality.set(server1, -sdata.tranmissionTime(server1, rdata.getRequest(r1)[0]) + quality.get(server1));
        state.get(server2).remove(j);
        quality.set(server2, -sdata.tranmissionTime(server2, rdata.getRequest(r2)[0]) + quality.get(server2));
        state.get(server1).add(i, r2);
        quality.set(server1, +sdata.tranmissionTime(server1, rdata.getRequest(r2)[0]) + quality.get(server1));
        state.get(server2).add(j, r1);
        quality.set(server2, +sdata.tranmissionTime(server2, rdata.getRequest(r1)[0]) + quality.get(server2));
    }

    //Getters
    public int getNservers() {
        return nserv;
    }

    public int getNrep() {
        return nrep;
    }

    public int getSseed() {
        return sseed;
    }

    public int getNusers() {
        return nusers;
    }

    public int getNreq() {
        return nreq;
    }

    public int getRseed() {
        return rseed;
    }

    public int getAlgseed() { return algseed; }

    public Servers getSdata() {
        return sdata;
    }

    public Requests getRdata() {
        return rdata;
    }

    public ArrayList<Integer> getRequests(int server_id) {
        return state.get(server_id);
    }

    /**
     *
     * @return arraylist del tiempo total de transmision de cada servidor
     */
    public ArrayList<Integer> getQuality(){
        return quality;
    }
    @Override
    public String toString(){
        return state.toString();
    }
}
