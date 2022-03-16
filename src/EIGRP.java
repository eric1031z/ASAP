import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class EIGRP {
    private final static File folder = new File("test");

    private static void loadLink(){
        for(File f : Objects.requireNonNull(folder.listFiles())) {
            if(f.getName().contains("EIGRP_in")) {
                try (FileReader fr = new FileReader(f.getPath())) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Hello\n");
                    BufferedReader br;
                    br = new BufferedReader(fr);
                    String root = br.readLine();
                    HashMap<String, Pair<Integer,String>> relatedLink = new HashMap<>();
                    relatedLink.put(root, new Pair<>(0, root));
                    int count = Integer.parseInt(br.readLine());
                    for(int i = 0; i < count; i++){
                        String[] data = br.readLine().split("\\s+");
                        relatedLink.putIfAbsent(data[0], new Pair<>(Integer.parseInt(data[1]),data[0]));
                    }
                    br.readLine();
                    while (br.ready()) {
                        String name = br.readLine();
                        int linkCounts = Integer.parseInt(br.readLine());
                        boolean update = false;
                        for (int i = 0; i < linkCounts; i++) {
                            String[] data = br.readLine().split("\\s+");
                            Pair<Integer,String> index = relatedLink.getOrDefault(data[0],new Pair<>(-1,""));
                            Pair<Integer,String> mid = relatedLink.get(name);
                            int length = mid.getKey() + Integer.parseInt(data[1]);
                            if(index.getKey() == -1){
                                relatedLink.put(data[0], new Pair<>(Integer.parseInt(data[1]) + mid.getKey(),mid.getValue()));
                                update = true;
                            }else if(length < index.getKey()){
                                relatedLink.remove(data[0]);
                                Pair newP = new Pair<>(length,mid.getValue());
                                relatedLink.put(data[0],newP);
                                update = true;
                            }
                        }
                        if(update){
                            sb.append("Update Hello\n");
                        }else{
                            sb.append("Update\n");
                        }
                    }
                    sb.append("Switch ID:").append(root).append("\n");
                    sb.append("paths:\n");
                    for(String p : relatedLink.keySet().stream().sorted(Comparator.comparingInt(Integer::parseInt)).collect(Collectors.toList())){
                        Pair<Integer,String> s = relatedLink.get(p);
                        sb.append("ID:").append(p).append(", next hop:").append(s.getValue()).append(", cost:").append(s.getKey()).append("\n");
                    }
                    folderOut(f,sb,folder);
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
        loadLink();
    }
}
