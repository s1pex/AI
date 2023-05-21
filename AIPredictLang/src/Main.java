import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    ArrayList<String> languages;
    Map<Character, Double> weights;
    ArrayList<Map<Character, Integer>> trainingData;

    public Main() {
        languages = new ArrayList<>();
        weights = new HashMap<>();
        trainingData = new ArrayList<>();
    }

    public void train() {
        File trainingDir = new File("train");
        File[] languageDirs = trainingDir.listFiles();

        for (File languageDir : languageDirs) {
                String lang = languageDir.getName();
                languages.add(lang);
                //маппер для рахування скільки малих літер і яких існюють в мові + на підставіц ього потім робиться предікт яка це мова
                Map<Character, Integer> languageData = new HashMap<>();
                File[] files = languageDir.listFiles();

                for (File file : files) {
                    if (file.isFile()) {
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(file));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                for (int i = 0; i < line.length(); i++) {
                                    char c = line.charAt(i);
                                    if (Character.isLowerCase(c)) {
                                        //збільшує вартість якщо літера мала
                                        languageData.put(c, languageData.getOrDefault(c, 0) + 1);
                                    }
                                }
                            }
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("File not found exception");
                    }
                }
                trainingData.add(languageData);
        }

        //Обчислює скільки літер в кожній мові зявляється, на підставі чого, потім робить висновок яка це мова
        int totalCount = 0;
        for (Map<Character, Integer> languageData : trainingData) {
            for (int count : languageData.values()) {
                totalCount += count;
            }
        }

        //алгоритм проходить через трейнінг дату, де вся інформація вже і так збережена
        for (Map<Character, Integer> languageData : trainingData) {
            for (Map.Entry<Character, Integer> entry : languageData.entrySet()) {
                //ключ - символ з алфавіту
                char c = entry.getKey();
                //вартість - скільки разів воно появляється в тексті з файлу прикладу
                int count = entry.getValue();
                double weight = (double) count / totalCount;
                //додає вартості до маппера, для подальших операцій
                weights.put(c, weights.getOrDefault(c, 0.0) + weight);
            }
        }
    }

    public String predict(String text) {
        //маппер з нульовим значенням для кожної мови
        Map<String, Double> net = new HashMap<>();
        languages.forEach(language -> net.put(language, 0.0));

        for (int i = 0; i < text.length(); i++) {
            //для кожного символу у вагах виконується розрахунок ймовісрності мови тексту
            char c = Character.toLowerCase(text.charAt(i));
            if (weights.containsKey(c)) {
                for (int j = 0; j < languages.size(); j++) {
                    String language = languages.get(j);
                    Map<Character, Integer> languageData = trainingData.get(j);
                    double count = (double) languageData.getOrDefault(c, 0);
                    double weight = weights.get(c);
                    net.put(language, net.get(language) + count * weight);
                }
            }
        }

        String predictedLanguage = "";
        double maxNet = Double.NEGATIVE_INFINITY;
        for (Map.Entry<String, Double> entry : net.entrySet()) {
            String language = entry.getKey();
            double netValue = entry.getValue();
            if (netValue > maxNet) {
                maxNet = netValue;
                predictedLanguage = language;
            }
        }
        return predictedLanguage;
    }

    public static void main(String[] args) {
        Main language = new Main();
        language.train();

        System.out.println("Enter text : ");
        Scanner sc = new Scanner(System.in);
        String text = sc.nextLine();
        String predictedLanguage = language.predict(text);
        System.out.println("Predicted language : " + predictedLanguage);
    }
}
