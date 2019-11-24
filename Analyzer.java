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

}
