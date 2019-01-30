import java.util.ArrayList;

public class Graph {

    private static final int CAPACITY = 6;
    protected boolean[] nodes;
    protected int[][] edges;
    protected int[][] dist;
    protected int size;
    protected int numedges;
    protected int cont=0;

    public Graph() {
        // TODO Auto-generated constructor stub
        super();
        this.nodes=new boolean[CAPACITY];
        this.edges=new int[CAPACITY][CAPACITY];
        this.dist=new int[CAPACITY][CAPACITY];
        this.size=0;
        this.numedges=0;
    }

    public boolean nodeExists(int id)throws InvalidNodeIDException{
        if (id<0||id>CAPACITY)
            throw new InvalidNodeIDException("valor invalido");
        return(nodes[id]==true);
    }

    public int getNumedges(int origin, int dest)throws InvalidNodeIDException{
        if((origin<0||origin>CAPACITY)||(dest<0||dest>CAPACITY))
            throw new InvalidNodeIDException("invalid Node ID");
        return numedges;
    }

    public void addNode(int id) throws NodeAlreadyExistsException, InvalidNodeIDException{
        if (id<0||id>CAPACITY)
            throw new InvalidNodeIDException("valor invalido");
        if (nodes[id]==true)
            throw new NodeAlreadyExistsException("ya existe");
        nodes[id]=true;
        size++;
    }

    public void addEdge(int origin, int dest, int distance) throws InvalidNodeIDException, NodeDoesNotExistException{
        if(origin<0||origin>CAPACITY||dest<0||dest>CAPACITY)
            throw new InvalidNodeIDException("Invalid node ID");
        if(nodes[origin]==false||nodes[dest]==false)
            throw new NodeDoesNotExistException("Ya existe");
        edges[origin][dest]++;
        numedges++;
        dist[origin][dest]=distance;
    }

    public ArrayList<String> dijkstra(int origin) throws InvalidNodeIDException{
    	ArrayList<String> dijkstraValue = new ArrayList<String>();
        boolean[] known=new boolean[CAPACITY];
        int[] dv = new int[CAPACITY];
        int[] pv = new int[CAPACITY];
        for(int i=0;i<CAPACITY;i++){
            dv[i]=Integer.MAX_VALUE;
            pv[i]=-1;
        }
        dv[origin]=0;
        while(true){
            //Paso 1: Buscar el nodo actual
            int actual=-1;
            int mindv=Integer.MAX_VALUE;
            for(int i=0;i<CAPACITY;i++){
                //Si nodo existe y no es conocido
                if(nodes[i] && !known[i]){
                    if(dv[i]<mindv){
                        actual=i;
                        mindv=dv[i];
                    }
                }
            }
            //no econtramos nada
            if(actual==-1){
                break;
            }
            known[actual]=true;
            //mirar los vecinos del nodo actual
            for(int i=0;i<CAPACITY;i++){
                if(edges[actual][i]>0){
                    if(dv[actual]+dist[actual][i]<dv[i]){
                        dv[i] = dv[actual]+dist[actual][i];
                        pv[i] = actual;
                    }
                }
            }
            //Mostrar la tabla
                                                   
            for(int i=0;i<CAPACITY;i++){
                if(nodes[i]){
                	dijkstraValue.add(" "+Integer.toString(i) +"    " +known[i]+"     " +dv[i]+"     "+ pv[i]+"\r\n");
                }
            }
            
            
        }
		return dijkstraValue;
    }

    public String shortestPath(int origin, int dest) throws InvalidNodeIDException{
    	String shortest ="";
        boolean[] known = new boolean[CAPACITY];
        int[] dv = new int[CAPACITY];
        int[] pv = new int[CAPACITY];
        for(int i=0;i<CAPACITY;i++){
            dv[i]=Integer.MAX_VALUE;
            pv[i]=-1;
        }
        dv[origin]=0;
        while(true) {

            //Paso 1: Buscar el nodo actual
            int actual = -1;
            //if (dest != actual) {
            int mindv = Integer.MAX_VALUE;
            for (int i = 0; i < CAPACITY; i++) {
                //Si nodo existe y no es conocido
                if (nodes[i] && !known[i]) {
                    if (dv[i] < mindv) {
                        actual = i;
                        mindv = dv[i];
                    }
                }
            }
            //no econtramos nada
            if (actual == -1) {
                break;
            }
            known[actual] = true;
            if (dest != actual) {
                //mirar los vecinos del nodo actual
                for (int i = 0; i < CAPACITY; i++) {
                    if (edges[actual][i] > 0) {
                        if (dv[actual] + dist[actual][i] < dv[i]) {
                            dv[i] = dv[actual] + dist[actual][i];
                            pv[i] = actual;
                        }
                    }
                }
                //Mostrar la tabla

                for (int i = 0; i < CAPACITY; i++) {
                    if (nodes[i]) {
                        if ((i == dest) && known[dest]) {

                            shortest+="=============================\r\n";
                            shortest+=" v    known    dv    pv \r\n";
                            shortest+="=============================\r\n";                           
                            shortest+=" "+Integer.toString(i) +"    " +known[i]+"     " +dv[i]+"     "+ pv[i]+"\r\n";
                            shortest+="=============================\r\n";
                        }
                    }
                }

            }
        }
        if (!known[dest]) {
            System.out.println("");
            System.out.println("Destiny node is not accessible.");
        }
        return shortest;
    }

}
