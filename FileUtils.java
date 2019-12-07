package main;

import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtils {
    ErrorLogger ERROR_LOGGER = new ErrorLogger();
    ArrayList<Grade> file_to_tokens(File file) throws IOException {
        ArrayList<Grade> data = new ArrayList<>();
        String csvFile = "";
        try {
            csvFile = file.getAbsolutePath();
        } catch (NullPointerException e){
            throw new NullPointerException("No file selected.");
        }
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "";
        String extension = "";

        int i = csvFile.lastIndexOf('.');
        if (i > 0) extension = csvFile.substring(i + 1);

        if (extension.equals("csv")) {
            System.out.println("CSV File Detected.");
            cvsSplitBy = ",";
        } else if (extension.equals("txt")) {
            System.out.println("TXT File Detected.");
            cvsSplitBy = "\n";
        }
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                if (Settings.GRADE_LEFT_BOUNDARY <= Float.parseFloat(country[0]) && Settings.GRADE_RIGHT_BOUNDARY >= Float.parseFloat(country[0]))
                {
                    data.add(new Grade(Double.parseDouble(country[0])));
                }
                else ERROR_LOGGER.LOG_ERROR("ERROR : INPUT OUT OF BOUNDS < " + country[0] + " >");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException | NumberFormatException err){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Illegal data detected.");
            a.show();
            return data;
        }

        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }
}
