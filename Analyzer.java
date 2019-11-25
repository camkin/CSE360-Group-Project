import java.util.*;
import java.io.*;
//import java.lang.*;

public class Analyzer {
    ArrayList<Double> dataSet = new ArrayList<Double>();
    Scanner input = new Scanner(System.in);

    public String readFromFile(ArrayList<Double> data) throws FileNotFoundException {
        String fileName = input.next();

        if(fileName.substring(fileName.length() - 3, fileName.length() - 1).equals("txt")) {
            File file = new File(fileName);
            Scanner infile = new Scanner(file);

            while (infile.hasNextLine()) {
                Double lines = infile.nextDouble();
                data.add(lines);
            }
            infile.close();

            return "Data appended!";
        }
        else if(fileName.substring(fileName.length() - 3, fileName.length() - 1).equals("csv")) {
            return "Data appended!";
        }
        else{
            return "Invalid file type!";
        }
    }

    public void addFromKeyboard(ArrayList<Double> data, double lowerBound, int higherBound){
        double value = input.nextDouble();
        if(value > lowerBound && value < higherBound) {
            data.add(value);
        }
    }

    public double[] analyzeData(ArrayList<Double> data){
        //initialize an array to store calculated values
        double[] arr = new double[5];
        Double sum = 0.0;
        Double median;

        //check if the list is empty
        if(!data.isEmpty()){

            //first element in return array is # of entries
            arr[0] = data.size();

            for(Double each: data){
                sum += each;
            }

            //second element is mean of the list
            arr[1] = sum/data.size();

            //sort data set to let first number minimum and last number maximum
            Collections.sort(data);
            arr[2] = data.get(1);
            arr[3] = data.get(data.size() - 1);

            if(data.size() % 2 == 0) {
                median = data.get(data.size() / 2);
            }
            else{
                median = (data.get(data.size() / 2) + data.get(data.size() / 2 + 1))/2;
            }

            //fifth element is the median
            arr[4] = median;
        }
        else{
            //set all element in return array to 0 if the list is empty
            for(int i = 0; i < 5; i++){
                arr[i] = 0;
            }
        }

        return arr;
    }

    public int[] distribution(ArrayList<Double> data, double lowerBound, double upperBound){
        //10 elements array hold the values
        int[] distArr = new int[10];

        for (double each : data) {
            if (each >= lowerBound) {

                //# of people in each 10% interval of full score
                if (each >= 0 * upperBound && each <= 0.09 * upperBound) {
                    distArr[0]++;
                } else if (each >= 0.1 * upperBound && each <= 0.19 * upperBound) {
                    distArr[1]++;
                } else if (each >= 0.2 * upperBound && each <= 0.29 * upperBound) {
                    distArr[2]++;
                } else if (each >= 0.3 * upperBound && each <= 0.39 * upperBound) {
                    distArr[3]++;
                } else if (each >= 0.4 * upperBound && each <= 0.49 * upperBound) {
                    distArr[4]++;
                } else if (each >= 0.5 * upperBound && each <= 0.59 * upperBound) {
                    distArr[5]++;
                } else if (each >= 0.6 * upperBound && each <= 0.69 * upperBound) {
                    distArr[6]++;
                } else if (each >= 0.7 * upperBound && each <= 0.79 * upperBound) {
                    distArr[7]++;
                } else if (each >= 0.8 * upperBound && each <= 0.89 * upperBound) {
                    distArr[8]++;
                } else if (each >= 0.9 * upperBound && each <= upperBound) {
                    distArr[9]++;
                }
            }
        }
        
        return distArr;
    }

}
