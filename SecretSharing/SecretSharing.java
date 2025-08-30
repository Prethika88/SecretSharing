import java.io.FileReader;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
public class SecretSharing {
    public static double reconstructSecret(List<int[]> shares, int k) {
        double secret = 0;
        for (int i = 0; i < k; i++) {
            int xi = shares.get(i)[0];
            int yi = shares.get(i)[1];
            double term = yi;
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    int xj = shares.get(j)[0];
                    term *= (0 - xj) * 1.0 / (xi - xj);
                }
            }
            secret += term;
        }
        return Math.round(secret); 
    }
    public static void main(String[] args) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(new FileReader("input.json"));
            JSONObject keys = (JSONObject) obj.get("keys");
            int n = ((Long) keys.get("n")).intValue();
            int k = ((Long) keys.get("k")).intValue();
            List<int[]> shares = new ArrayList<>();
            for (Object keyObj : obj.keySet()) {
                String key = (String) keyObj;
                if (key.equals("keys")) continue;
                JSONObject share = (JSONObject) obj.get(key);
                int x = Integer.parseInt(key);
                int base = Integer.parseInt((String) share.get("base"));
                String valueStr = (String) share.get("value");
                int y = Integer.parseInt(valueStr, base);
                shares.add(new int[]{x, y});
            }
            double secret = reconstructSecret(shares, k);
            System.out.println("Reconstructed Secret: " + secret);
            for (int i = 0; i < n; i++) {
                List<int[]> subset = new ArrayList<>(shares);
                subset.remove(i);
                if (subset.size() >= k) {
                    double sec2 = reconstructSecret(subset.subList(0, k), k);
                    if (sec2 != secret) {
                        System.out.println("Wrong share detected: (x=" + shares.get(i)[0] + ", y=" + shares.get(i)[1] + ")");
                        System.out.println("Correct Secret: " + sec2);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
