import java.io.*;
import java.util.*;

public class STP {

    private final static File folder = new File("test");

    private static void loadNode(HashMap<Integer, Integer> index){
        for(File f : Objects.requireNonNull(folder.listFiles())) {
            if(f.getName().contains("STP_in")) {
                try (FileReader fr = new FileReader(f.getPath())) {
                    BufferedReader br;
                    br = new BufferedReader(fr);
                    String name = br.readLine();
                    int NodeName = 0; //root name
                    int target; //current cost
                    int min = Integer.parseInt(br.readLine()); //priority
                    int p = min;// :(
                    int root = Integer.parseInt(name); //rootID
                    int cost = Integer.MAX_VALUE; //final cost
                    StringBuilder sb = new StringBuilder();
                    sb.append("Hello\n");
                    br.readLine();
                    while (br.ready()) {
                        int[] para = new int[6];
                        for(int i = 0; i < 6; i++){
                            para[i] = Integer.parseInt(br.readLine());
                        }
                        target = para[4] + index.get(para[5]);
                        if(min > para[3] || (min == para[3] && root > para[2]) ||(root == para[2] && min == para[3] && (cost > target || (NodeName > para[0] && cost == target)))){
                            NodeName = para[0];
                            root = para[2];
                            min = para[3];
                            cost = target;
                            sb.append("NewCost:").append(target).append(" ,Hello\n");
                        }else{
                            sb.append("Ignore\n");
                        }
                    }

                    sb.append("==========\n");
                    sb.append("ID :").append(name).append("\n");
                    sb.append("Prority:").append(p).append("\n");
                    sb.append("RootID:").append(root).append("\n");
                    sb.append("RootPriortity:").append(min).append("\n");
                    sb.append("RootPortTo:").append(NodeName).append("\n");
                    sb.append("Cost:").append(cost).append("\n");
                    sb.append("==========\n");
                    folderOut(f, sb, folder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void folderOut(File f, StringBuilder sb, File folder) throws IOException {
        File output = new File(folder.getPath() + "/" + f.getName().replace("in","out"));
        output.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(output));
        out.append(sb);
        out.flush();
        out.close();
    }

    public static void main(String[] args){
        HashMap<Integer,Integer> index = new HashMap<>();
        index.put(4,250);
        index.put(10,100);
        index.put(16,62);
        index.put(45,39);
        index.put(100,19);
        index.put(155,14);
        index.put(1000,4);
        index.put(10000,2);
        loadNode(index);
        System.out.println("Done^_^");
    }
}
