import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class OSPF {

    private final static File folder = new File("test");

    /**Switch*/
    public static class Switch{
        private final HashSet<Link> Links = new HashSet<>();
        public Switch() { }
        public void addLink(Link newLink){
            Links.add(newLink);
        }
        public HashSet<Link> getLinks(){
            return Links;
        }
    }

    /**Link*/
    public static class Link{
        private final String name;
        private Integer length = Integer.MAX_VALUE;
        private ArrayList<Link> sPath = new ArrayList<>();
        public HashMap<Link,Integer> neighbors = new HashMap<>();
        public Link(String name) { this.name = name;}
        public void setsPath(ArrayList<Link> links){this.sPath = links;}
        public ArrayList<Link> getsPath(){return this.sPath;}
        public void setLength(int length){ this.length = length;}
        public Link nextHop = this;
        public void setNextHop(Link link){this.nextHop = link;}
        public int getLength(){
            return length;
        }
        public void addNeighbors(Link dst, int length){
            neighbors.put(dst,length);
        }
        public HashMap<Link,Integer> getNeighbors(){
            return this.neighbors;
        }
    }


    /**load test data form source and run dijkstra*/
    private static void loadLink(String arg){
        for(File f : Objects.requireNonNull(folder.listFiles())) {
            if(f.getName().contains(arg)) {
                try (FileReader fr = new FileReader(f.getPath())) {
                    BufferedReader br;
                    HashMap<String,Link> LinkData = new HashMap<>();
                    String lastLink;
                    Switch NetWork = new Switch();
                    br = new BufferedReader(fr);

                    int pointer = 0; // track line number
                    String temp;
                    int counts = Integer.parseInt(br.readLine());

                    while ((temp = br.readLine()) != null && pointer < counts) {
                        Link newLink = LinkData.getOrDefault(temp, new Link(temp));
                        LinkData.putIfAbsent(temp, newLink);
                        temp = br.readLine();
                        int linkCounts = Integer.parseInt(temp);
                        for (int i = 0; i < linkCounts; i++) {
                            temp = br.readLine();
                            String[] data = temp.split("\\s+");
                            Link n = LinkData.getOrDefault(data[0], new Link(data[0]));
                            LinkData.putIfAbsent(data[0], n);
                            newLink.addNeighbors(n, Integer.parseInt(data[1]));
                        }
                        NetWork.addLink(newLink);
                        pointer++;
                    }
                    lastLink = temp;

                    StringBuilder sb = new StringBuilder();
                    Link targetLink = LinkData.get(lastLink);
                    Switch ret = SingleSourceShortestPath(NetWork, targetLink);
                    List<Link> list = ret.getLinks().stream().sorted(Comparator.comparingInt(o -> Integer.parseInt(o.name))).collect(Collectors.toList());
                    for (Link l : list) {
                        sb.append("ID :").append(l.name).append(", next hop : ").append(l.nextHop.name).append(", cost : ").append(l.getLength());
                        sb.append("\n");
                    }
                    STP.folderOut(f, sb, folder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**main function running dijkstra*/
    public static Switch SingleSourceShortestPath(Switch ntw, Link src) {
        HashSet<Link> relaxedLink = new HashSet<>();
        HashSet<Link> freeLink = new HashSet<>();
        freeLink.add(src);
        src.setLength(0);
        while (freeLink.size() > 0) {
            Link current = findMinPath(freeLink);
            freeLink.remove(current);
            for (Link neighbor : current.getNeighbors().keySet()) {
                int edge = current.getNeighbors().get(neighbor);
                if (!relaxedLink.contains(neighbor)) {
                    relax(neighbor,current, edge);
                    freeLink.add(neighbor);
                }
            }
            relaxedLink.add(current);
        }
        return ntw;
    }

    /**relax procedure call, with addition to path link*/
    private static void relax(Link candidate, Link src, int edge) {
        int srcLength = src.getLength();
        if (srcLength + edge <= candidate.getLength()) { //maybe <= ?
            candidate.setLength(srcLength + edge);
            ArrayList<Link> shortestPath = new ArrayList<>(src.getsPath());
            shortestPath.add(src);
            candidate.setsPath(shortestPath);// println actual path if needed
            if(candidate.getsPath().size() > 1) { //finding next hop
                candidate.setNextHop(shortestPath.get(1));
            }
        }

    }

    /**greedy finding min path*/
    private static Link findMinPath(HashSet<Link> neighbors) {
        int currentMin = Integer.MAX_VALUE;
        Link ret = null;
        for (Link link: neighbors) {
            int nodeDistance = link.getLength();
            if (nodeDistance <= currentMin) { //maybe <= is fine, affect next hop choice
                currentMin = nodeDistance;
                ret = link;
            }
        }
        return ret;
    }


    private static void test(int[][] A){
        for(int k = 0; k < A.length; k++) {
            int[][] d = new int[A.length][A.length];
            for (int i = 0; i < A.length; i++) {
                for (int j = 0; j < A[i].length; j++) {
                    if(d[i][j] > d[i][k] + d[k][j]){
                        d[i][j] = d[i][k] + d[k][j];
                    }
                }
            }
            System.out.println(Arrays.deepToString(d));
        }
    }
    public static void main(String[] args){
        int[][] A = new int[][] {{0,Integer.MAX_VALUE,Integer.MAX_VALUE,-5,2},{6,0,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE},{1,7,0,Integer.MAX_VALUE,Integer.MAX_VALUE},{Integer.MAX_VALUE,Integer.MAX_VALUE,4,0,Integer.MAX_VALUE},{Integer.MAX_VALUE,-4,3,8,0}};
        test(A);

    }
}
