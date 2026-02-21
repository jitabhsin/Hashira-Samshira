package com.example.Hashira;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class PolynomialSolverService {

    @EventListener(ApplicationReadyEvent.class)
    public void solve() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        ClassPathResource resource = new ClassPathResource("testcase.json");
        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode rootNode = mapper.readTree(inputStream);
            JsonNode keysNode = rootNode.get("keys");
            int k = keysNode.get("k").asInt();

            Map<Integer, BigInteger> points = new TreeMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals("keys")) {
                    continue;
                }

                int x = Integer.parseInt(field.getKey());
                int base = field.getValue().get("base").asInt();
                String value = field.getValue().get("value").asText();
                
                BigInteger y = new BigInteger(value, base);
                points.put(x, y);
            }

            List<Map.Entry<Integer, BigInteger>> allPoints = new ArrayList<>(points.entrySet());
            List<List<Map.Entry<Integer, BigInteger>>> combinations = new ArrayList<>();
            generateCombinations(allPoints, k, 0, new ArrayList<>(), combinations);

            Map<BigInteger, Integer> secretCounts = new HashMap<>();
            BigInteger secret = null;
            int maxCount = 0;

            for (List<Map.Entry<Integer, BigInteger>> selectedPoints : combinations) {
                BigInteger totalNumerator = BigInteger.ZERO;
                BigInteger totalDenominator = BigInteger.ONE;

                for (int i = 0; i < k; i++) {
                    BigInteger num = selectedPoints.get(i).getValue();
                    BigInteger den = BigInteger.ONE;

                    for (int j = 0; j < k; j++) {
                        if (i != j) {
                            BigInteger xi = BigInteger.valueOf(selectedPoints.get(i).getKey());
                            BigInteger xj = BigInteger.valueOf(selectedPoints.get(j).getKey());

                            num = num.multiply(xj.negate());
                            den = den.multiply(xi.subtract(xj));
                        }
                    }
                    
                    totalNumerator = totalNumerator.multiply(den).add(num.multiply(totalDenominator));
                    totalDenominator = totalDenominator.multiply(den);
                }

                if (totalNumerator.remainder(totalDenominator).equals(BigInteger.ZERO)) {
                    BigInteger currentSecret = totalNumerator.divide(totalDenominator);
                    int count = secretCounts.getOrDefault(currentSecret, 0) + 1;
                    secretCounts.put(currentSecret, count);
                    if (count > maxCount) {
                        maxCount = count;
                        secret = currentSecret;
                    }
                }
            }

            System.out.println("\n=======================================================");
            System.out.println("  SUCCESS! THE DECODED SECRET VALUE IS: " + secret);
            System.out.println("=======================================================\n");
        }
    }

    private void generateCombinations(List<Map.Entry<Integer, BigInteger>> allPoints, int k, int start, List<Map.Entry<Integer, BigInteger>> current, List<List<Map.Entry<Integer, BigInteger>>> combinations) {
        if (current.size() == k) {
            combinations.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < allPoints.size(); i++) {
            current.add(allPoints.get(i));
            generateCombinations(allPoints, k, i + 1, current, combinations);
            current.remove(current.size() - 1);
        }
    }
}
