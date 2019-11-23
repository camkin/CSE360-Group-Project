import java.util.*;
import static java.lang.System.*;
public class GradeAnalyzer {
    private static Scanner input = new Scanner(in);

    public static void main(String[] args) {
        int command = -1;

        displayMainMenu();
        while(command !=0) {
            command = getMenuChoice();
            switch (command) {
                case 1:


                case 2:


                case 3:


                case 4:

                case 5:


                case 6:

                case 7:

                case 0:
                    out.println("The program will be terminated...");

                default:
                    out.println("Invalid Command!!!");

            }
        }
    }

    public static void displayMainMenu(){
        out.println("----------------------------------------");
        out.println("1. Load data from a txt or csv file");
        out.println("2. Append data set");
        out.println("3. Add new data");
        out.println("4. Delete data set");
        out.println("5. Analyze data set");
        out.println("6. Display data");
        out.println("7. Display graph");
        out.println("0. Shutdown the program");
    }

    private static int getMenuChoice() {
        out.print("Please enter the number of your choice: ");
        while(! input.hasNextInt() )
        {
            input.nextLine();
            out.print("Please enter the number of your choice: ");
        }

        int menuChoice = input.nextInt();

        return menuChoice;
    }


}
