import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;

public class KNN {
    //klasa objektu na ktory patrzyymy
    static class Object {
        double[] point;
        String label;

        public Object(double[] point, String label) {
            this.point = point;
            this.label = label;
        }
    }

    //szukanie dystansy
    static double Distance(Object a, Object b) {
        double sum = 0.0;
        for (int i = 0; i < a.point.length; i++) {
            sum += Math.pow(a.point[i] - b.point[i], 2);
        }
        return Math.sqrt(sum);
    }

    //zwraca prediction
    static String Predict(List<Object> trainList, Object test, int k) {
        //liczenie odlegnosci miedzy pociagiem a pociagiem w pliku testowym
        List<double[]> distances = new ArrayList<>();
        for (Object object : trainList) {
            double distance = Distance(object, test);
            if (object.label.equals(test.label)) {
                distances.add(new double[]{distance, 1});
            } else {
                distances.add(new double[]{distance, 0});
            }
        }

        Collections.sort(distances, new Comparator<double[]>() {
            public int compare(double[] a, double[] b) {
                return Double.compare(a[0], b[0]);
            }
        });

        //ile mamy podobnych knn
        int similar = 0;
        for (int i = 0; i < k; i++) {
            similar += distances.get(i)[1];
        }


        if (similar > k / 2) {
            return test.label;
        } else if (similar < k / 2) {
            return trainList.get(0).label;
        } else {
            //jesli kilka sasiadow obok siebie
            List<String> labels = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                int index = (int) distances.get(i)[1];
                labels.add(trainList.get(index).label);
            }
            Collections.shuffle(labels);
            return labels.get(0);
        }
    }

    public static void main(String[] args) throws IOException {
        //odczyt z pliku train i parsowanie

        List<Object> trainList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("train.txt"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            double[] point = new double[data.length - 1];
            for (int i = 0; i < point.length; i++) {
                point[i] = Double.parseDouble(data[i]);
            }
            trainList.add(new Object(point, data[data.length - 1]));
        }
        br.close();

        //odczyt z pliku text i parsowanie
        List<Object> testList = new ArrayList<>();
        br = new BufferedReader(new FileReader("test.txt"));
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            double[] point = new double[data.length - 1];
            for (int i = 0; i < point.length; i++) {
                point[i] = Double.parseDouble(data[i]);
            }
            testList.add(new Object(point, data[data.length - 1]));
        }
        br.close();
        List<Object> testListForOwnVector = new ArrayList<>();


        Scanner scanner = new Scanner(System.in);
        String choice = "";
        while (!choice.equals("3")) {

            System.out.println("Select option: ");
            System.out.println("1 - Write your own observation");
            System.out.println("2 - Show observation and prediction for vectors in text file");
            System.out.println("3 - End program");

            choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.println("How many neighbours you would like to have:");
                Scanner scanner1 = new Scanner(System.in);
                int neighbour = scanner1.nextInt();
                System.out.println("Write your own vector:");
                String input = scanner.nextLine();
                String[] data = input.split(",");
                double[] point = new double[data.length];
                for (int i = 0; i < point.length; i++) {
                    point[i] = Double.parseDouble(data[i]);
                }
                System.out.println("Write your own label: ");
                Scanner scan = new Scanner(System.in);
                String label = scan.nextLine();

                testListForOwnVector.add(new Object(point, label));

                for(Object testOwnVector : testListForOwnVector) {
                    String predicted = Predict(trainList, testOwnVector, neighbour);
                    System.out.println(Arrays.toString(testOwnVector.point) + " " + testOwnVector.label + " predicted: " + predicted);
                }
            } else if (choice.equals("2")) {

                System.out.println("Podaj K");
                Scanner s = new Scanner(System.in);
                int k = s.nextInt();

                    int rightPrediction = 0;
                    for (Object test : testList) {
                        String predicted = Predict(trainList, test, k);
                        System.out.println(Arrays.toString(test.point) + " " + test.label + " predicted: " + predicted);
                        if (predicted.equals(test.label)) {
                            rightPrediction++;
                        }
                }
                double accuracy = 100.0 * rightPrediction / testList.size();
                System.out.println("Accuracy: " + accuracy + "%");
                System.out.println("Correct " + rightPrediction);
            }
        }

        scanner.close();
    }
}