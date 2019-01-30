import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    private static Graph graph = new Graph();
    private static HashMap<String, Integer> map = new HashMap<>();
    private static  Huffman huff;
	private static boolean add;

    public static void main(String[] Args) throws InvalidNodeIDException, IOException, NodeAlreadyExistsException, NodeDoesNotExistException {
    	huff = new Huffman();
        readFileNodes("input.txt", map, graph);
        readFileEdges("input.txt", map, graph);
        //graph.printEdges();
        ArrayList<String> dijkstra = new ArrayList<String>();
        ArrayList<String> shortestRoad= new ArrayList<String>();
        ArrayList<String> dijs = new ArrayList<String>();
        dijkstra.add("=============================\r\n");
        dijkstra.add(" v    known    dv    pv \r\n");
        dijkstra.add("=============================\r\n");
                
        shortestRoad.add("=============================\r\n");
        shortestRoad.add(" v    known    dv    pv \r\n");
        shortestRoad.add("=============================\r\n");
        dijs = graph.dijkstra(0);
        for(String dijks: dijs) {
            dijkstra.add(dijks);
        }        
        dijkstra.add("=============================\r\n");
        huff.writeFile(System.getProperty("user.dir")+"\\"+"dijk.txt", dijkstra);
        shortestRoad.add(graph.shortestPath(0, 3));
        shortestRoad.add("=============================\r\n");
        huff.writeFile(System.getProperty("user.dir")+"\\"+"shortest.txt",shortestRoad);
        huff.main(null);

        System.out.println(graph.nodeExists(0));
        System.out.println(graph.nodeExists(1));
        System.out.println(graph.nodeExists(2));
        System.out.println(graph.nodeExists(3));
        System.out.println(graph.nodeExists(4));

    }

    private static void readFileNodes(String filePath, HashMap<String, Integer> m, Graph g) throws IOException, NodeAlreadyExistsException, InvalidNodeIDException, NodeDoesNotExistException {
        int count = 0;
        String line = "";
        String[] nd = new String[3];
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            while ((line = br.readLine()) != null) {
                if (line.contains(",")) {
                    nd = line.split(",");
                    if (m.containsKey(nd[0]) == false) {
                        m.put(nd[0], count);
                        g.addNode(m.get(nd[0]));
                        count++;
                    }
                } else {
                    nd[0] = line;
                    m.put(nd[0], count);
                    g.addNode(m.get(nd[0]));
                    count++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFileEdges(String filePath, HashMap<String, Integer> m, Graph g) throws InvalidNodeIDException, NodeDoesNotExistException, NodeAlreadyExistsException {
        String[] ed = new String[3];
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
            while ((line = br.readLine()) != null) {
                if (line.contains(",")) {
                    ed = line.split(",");
                    if (m.containsKey(ed[1]) == false) {
                        m.put(ed[1], m.size());
                        g.addNode(m.get(ed[1]));
                    }
                    g.addEdge(m.get(ed[0]), m.get(ed[1]), Integer.parseInt(ed[2]));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
