package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.log;

/*
 * Main class
 */
public class Main extends Application {
    private TableView table = new TableView<>(); //
    private TableColumn<Grade, Double> columnOne = new TableColumn<>("Grades");//
    private Scene prev_scene;
    private ArrayList<Grade> grades_list = new ArrayList<>();
    private ErrorLogger ERROR_LOGGER = new ErrorLogger();
    private double mean, mode, median, max, min;

    public static void main(String[] args) {
        launch(args);
    }


    /**
     * @param grades takes an arraylist of Grade class
     * @return the mode
     */
    private static double get_mode(ArrayList<Grade> grades) {
        Double mode = grades.get(0).getGrade();
        int maxCount = 0;
        for (int i = 0; i < grades.size(); i++) {
            double value = grades.get(i).getGrade();
            int count = 0;
            for (Grade grade : grades) {
                if (grade.getGrade() == value) count++;
                if (count > maxCount) {
                    mode = value;
                    maxCount = count;
                }
            }
        }
        if (maxCount > 1) {
            return mode;
        }
        return 0;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // entry screen
        FXMLLoader entry_screen_loader = new FXMLLoader();
        entry_screen_loader.setLocation(getClass().getResource("entry_screen.fxml"));
        Parent entry_screen_root = entry_screen_loader.load();
        primaryStage.setTitle("Grade Analytics Tool");
        Scene entry_screen_scene = new Scene(entry_screen_root, 600, 352);

        // settings screen
        FXMLLoader settings_screen_loader = new FXMLLoader();
        settings_screen_loader.setLocation(getClass().getResource("settings_screen.fxml"));
        Parent settings_screen_root = settings_screen_loader.load();
        primaryStage.setTitle("Grade Analytics Tool");
        Scene settings_screen_scene = new Scene(settings_screen_root, 600, 352);

        // data screen
        FXMLLoader data_screen_loader = new FXMLLoader();
        data_screen_loader.setLocation(getClass().getResource("data_screen.fxml"));
        Parent data_screen_root = data_screen_loader.load();
        primaryStage.setTitle("Grade Analytics Tool");
        Scene data_screen_scene = new Scene(data_screen_root, 625, 352);

        // analysis screen
        FXMLLoader analysis_screen_loader = new FXMLLoader();
        analysis_screen_loader.setLocation(getClass().getResource("analysis_screen.fxml"));
        Parent analysis_screen_root = analysis_screen_loader.load();
        primaryStage.setTitle("Grade Analytics Tool");
        Scene analysis_screen_scene = new Scene(analysis_screen_root, 680, 420);

        // Error log screen
        FXMLLoader error_log_loader = new FXMLLoader();
        error_log_loader.setLocation(getClass().getResource("error_log_screen.fxml"));
        Parent error_screen_root = error_log_loader.load();

        Stage error_stage = new Stage();
        error_stage.setTitle("Grade Analytics Tool");
        Scene error_log_scene = new Scene(error_screen_root, 600, 352);

        table = (TableView) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(0);
        table.getColumns().addAll(columnOne);
        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-ENTRY SCREEN HANDLERS-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-//
        // handler for when enter manually image is clicked
        ImageView enter_manually_img = (ImageView) entry_screen_scene.lookup("#enter_manually_img");
        enter_manually_img.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                primaryStage.setScene(data_screen_scene);
                primaryStage.show();
                prev_scene = entry_screen_scene;
            }
        });

        // handler for when add file image is clicked
        ImageView add_file_img = (ImageView) entry_screen_scene.lookup("#add_file_img");
        add_file_img.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // prompt for file
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Text Files", "*.txt")
                        , new FileChooser.ExtensionFilter("CSV Files", "*.csv")
                );
                File selectedFile = fileChooser.showOpenDialog(new Stage());

                FileUtils fileUtils = new FileUtils();
                try {
                    // clear the data and add new.
                    grades_list.clear();
                    grades_list = fileUtils.file_to_tokens(selectedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException err){
                    ERROR_LOGGER.LOG_ERROR("ERROR: NO FILE SELECTED.");
                    return;
                }

                System.out.println(grades_list.size());

                ObservableList<Grade> data = FXCollections.<Grade>observableArrayList();
                data.addAll(grades_list);

                columnOne.setCellValueFactory(new PropertyValueFactory<Grade, Double>("grade"));

                table.setItems(data);

                TextArea c_tf_1 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_1");
                TextArea c_tf_2 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_2");
                TextArea c_tf_3 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_3");
                TextArea c_tf_4 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_4");

                ArrayList<Grade> sorted_list = grades_list;
                Collections.sort(sorted_list);
                Collections.reverse(sorted_list);

                ArrayList<ArrayList<Grade>> chopped_sorted_list = chopped(sorted_list, sorted_list.size() / 4);
                if (chopped_sorted_list.size() == 0){
                    return;
                }

                try{
                    for (int i = 0; i < 4; i++){
                        for (int j = 0; j < chopped_sorted_list.get(0).size(); j++) {
                            switch (i) {
                                case 0:
                                    c_tf_1.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                    c_tf_1.appendText("\n");
                                    break;
                                case 1:
                                    c_tf_2.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                    c_tf_2.appendText("\n");
                                    break;
                                case 2:
                                    c_tf_3.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                    c_tf_3.appendText("\n");
                                    break;
                                case 3:
                                    c_tf_4.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                    c_tf_4.appendText("\n");
                                    break;
                                default:
                                    throw new UnsupportedOperationException("");
                            }
                        }
                    }
                }catch (IndexOutOfBoundsException e){
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Illegal data detected");
                    a.show();
                    ERROR_LOGGER.LOG_ERROR("ERROR : ILLEGAL DETECTED IN FILE.");
                }

                prev_scene = entry_screen_scene;
                primaryStage.setScene(data_screen_scene);
                primaryStage.show();
            }
        });
        // handler for when settings image is clicked
        ImageView settings_img = (ImageView) entry_screen_scene.lookup("#settings_img");
        settings_img.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // show settings screen
                TextField left_boundary_tf = (TextField) settings_screen_scene.lookup("#grade_left_boundary");
                TextField right_boundary_tf = (TextField) settings_screen_scene.lookup("#grade_right_boundary");

                left_boundary_tf.setText(String.valueOf(Settings.GRADE_LEFT_BOUNDARY));
                right_boundary_tf.setText(String.valueOf(Settings.GRADE_RIGHT_BOUNDARY));

                prev_scene = entry_screen_scene;
                primaryStage.setScene(settings_screen_scene);
                primaryStage.show();
            }
        });

        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-SETTINGS SCREEN HANDLERS-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-//
        Button apply_btn = (Button) settings_screen_scene.lookup("#apply_settings_btn");
        apply_btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                TextField left_boundary_tf = (TextField) settings_screen_scene.lookup("#grade_left_boundary");
                TextField right_boundary_tf = (TextField) settings_screen_scene.lookup("#grade_right_boundary");
                try {
                    Settings.GRADE_LEFT_BOUNDARY = Integer.parseInt(left_boundary_tf.getText());
                    Settings.GRADE_RIGHT_BOUNDARY = Integer.parseInt(right_boundary_tf.getText());
                }catch (NumberFormatException e){
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Illegal boundary setting");
                    a.show();
                    ERROR_LOGGER.LOG_ERROR("ERROR : ILLEGAL INPUT FOR BOUNDARIES.");
                }
                if (prev_scene == entry_screen_scene) {
                    primaryStage.setScene(entry_screen_scene);
                } else if (prev_scene == data_screen_scene) {
                    primaryStage.setScene(data_screen_scene);
                }
                primaryStage.show();
            }
        });


        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-DATA SCREEN HANDLERS-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-//
        Button data_scr_settings_btn = (Button) ((ToolBar) data_screen_scene.lookup("#upper_toolbar")).getItems().get(0);
        data_scr_settings_btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                TextField left_boundary_tf = (TextField) settings_screen_scene.lookup("#grade_left_boundary");
                TextField right_boundary_tf = (TextField) settings_screen_scene.lookup("#grade_right_boundary");

                left_boundary_tf.setText(String.valueOf(Settings.GRADE_LEFT_BOUNDARY));
                right_boundary_tf.setText(String.valueOf(Settings.GRADE_RIGHT_BOUNDARY));

                prev_scene = data_screen_scene;
                primaryStage.setScene(settings_screen_scene);
                primaryStage.show();
            }
        });
        Button data_screen_append_btn = (Button) ((ToolBar) data_screen_scene.lookup("#upper_toolbar")).getItems().get(1);
        data_screen_append_btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Text Files", "*.txt")
                        , new FileChooser.ExtensionFilter("CSV Files", "*.csv")
                );
                File selectedFile = fileChooser.showOpenDialog(new Stage());

                FileUtils fileUtils = new FileUtils();
                try {
                    grades_list.addAll(fileUtils.file_to_tokens(selectedFile));
                } catch (IOException | NumberFormatException e) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Illegal file imported");
                    a.show();
                    ERROR_LOGGER.LOG_ERROR("ERROR : ILLEGAL FILE IMPORTED.");
                    return;
                }

                System.out.println(grades_list.size());
                ObservableList<Grade> data = FXCollections.<Grade>observableArrayList();
                data.addAll(grades_list);
                columnOne.setCellValueFactory(new PropertyValueFactory<Grade, Double>("grade"));
                table.setItems(data);
                TextArea c_tf_1 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_1");
                TextArea c_tf_2 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_2");
                TextArea c_tf_3 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_3");
                TextArea c_tf_4 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_4");

                c_tf_1.clear();
                c_tf_2.clear();
                c_tf_3.clear();
                c_tf_4.clear();
                ArrayList<Grade> sorted_list = grades_list;
                Collections.sort(sorted_list);
                Collections.reverse(sorted_list);

                ArrayList<ArrayList<Grade>> chopped_sorted_list = chopped(sorted_list, sorted_list.size() / 4);

                for (int i = 0; i < 4; i++){
                    for (int j = 0; j < chopped_sorted_list.get(0).size(); j++) {
                        switch (i) {
                            case 0:
                                c_tf_1.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                c_tf_1.appendText("\n");
                                break;
                            case 1:
                                c_tf_2.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                c_tf_2.appendText("\n");
                                break;
                            case 2:
                                c_tf_3.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                c_tf_3.appendText("\n");
                                break;
                            case 3:
                                c_tf_4.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                c_tf_4.appendText("\n");
                                break;
                            default:
                                throw new UnsupportedOperationException("");
                        }
                    }
                }

                prev_scene = entry_screen_scene;
                primaryStage.setScene(data_screen_scene);
                primaryStage.show();
            }
        });

        Button data_screen_delete_btn = (Button) data_screen_scene.lookup("#delete_btn");
        data_screen_delete_btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                TextField value_edit_tf = (TextField) data_screen_scene.lookup("#value_edit_tf");
                try {
                    Double value = (Double.parseDouble(value_edit_tf.getText()));

                    for (Grade g : grades_list) {
                        if (g.getGrade().equals(value)) {
                            grades_list.remove(g);
                            break;
                        }
                    }
                    ObservableList<Grade> data = FXCollections.<Grade>observableArrayList();
                    data.addAll(grades_list);

                    columnOne.setCellValueFactory(new PropertyValueFactory<Grade, Double>("grade"));
                    table.setItems(data);
                    table.refresh();
                    TextArea c_tf_1 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_1");
                    TextArea c_tf_2 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_2");
                    TextArea c_tf_3 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_3");
                    TextArea c_tf_4 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_4");

                    c_tf_1.clear();
                    c_tf_2.clear();
                    c_tf_3.clear();
                    c_tf_4.clear();
                    ArrayList<Grade> sorted_list = grades_list;
                    Collections.sort(sorted_list);
                    Collections.reverse(sorted_list);

                    ArrayList<ArrayList<Grade>> chopped_sorted_list = chopped(sorted_list, sorted_list.size() / 4);

                    for (int i = 0; i < 4; i++){
                        for (int j = 0; j < chopped_sorted_list.get(0).size(); j++) {
                            switch (i) {
                                case 0:
                                    c_tf_1.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                    c_tf_1.appendText("\n");
                                    break;
                                case 1:
                                    c_tf_2.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                    c_tf_2.appendText("\n");
                                    break;
                                case 2:
                                    c_tf_3.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                    c_tf_3.appendText("\n");
                                    break;
                                case 3:
                                    c_tf_4.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                    c_tf_4.appendText("\n");
                                    break;
                                default:
                                    throw new UnsupportedOperationException("");
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Incorrect Number Format. Integer or Decimal values expected.");
                    a.show();
                } catch (IndexOutOfBoundsException ignored){

                }

            }
        });
        Button data_screen_add_btn = (Button) data_screen_scene.lookup("#add_btn");
        data_screen_add_btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                TextField value_edit_tf = (TextField) data_screen_scene.lookup("#value_edit_tf");
                try {
                    Double value = (Double.parseDouble(value_edit_tf.getText()));
                    if (value >= Settings.GRADE_LEFT_BOUNDARY && value <= Settings.GRADE_RIGHT_BOUNDARY) {
                        grades_list.add(new Grade(value));
                        ObservableList<Grade> data = FXCollections.<Grade>observableArrayList();
                        data.addAll(grades_list);
                        columnOne.setCellValueFactory(new PropertyValueFactory<Grade, Double>("grade"));
                        table.setItems(data);
                        table.refresh();
                        table.scrollTo(grades_list.size()-1);
                        TextArea c_tf_1 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_1");
                        TextArea c_tf_2 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_2");
                        TextArea c_tf_3 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_3");
                        TextArea c_tf_4 = (TextArea) ((TitledPane) ((SplitPane) data_screen_scene.lookup("#split_pane")).getItems().get(1)).getContent().lookup("#c_tf_4");

                        c_tf_1.clear();
                        c_tf_2.clear();
                        c_tf_3.clear();
                        c_tf_4.clear();
                        ArrayList<Grade> sorted_list = grades_list;
                        Collections.sort(sorted_list);
                        Collections.reverse(sorted_list);

                        int partition_size = 0;
                        if (sorted_list.size() % 4 == 0)
                        {
                            partition_size = sorted_list.size()/4;
                        }
                        else {
                            partition_size = (sorted_list.size()/4) % 4;
                        }
                        ArrayList<ArrayList<Grade>> chopped_sorted_list = chopped(sorted_list, sorted_list.size() / 4);

                        try {
                            for (int i = 0; i < 4; i++){
                                for (int j = 0; j < chopped_sorted_list.get(0).size(); j++) {
                                    switch (i) {
                                        case 0:
                                            c_tf_1.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                            c_tf_1.appendText("\n");
                                            break;
                                        case 1:
                                            c_tf_2.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                            c_tf_2.appendText("\n");
                                            break;
                                        case 2:
                                            c_tf_3.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                            c_tf_3.appendText("\n");
                                            break;
                                        case 3:
                                            c_tf_4.appendText(String.valueOf(chopped_sorted_list.get(i).get(j).getGrade()));
                                            c_tf_4.appendText("\n");
                                            break;
                                        default:
                                            throw new UnsupportedOperationException("");
                                    }
                                }
                            }
                        } catch (IndexOutOfBoundsException ignored){

                        }

                    } else {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Value Entered is out of accepted bounds.");
                        a.show();
                        ERROR_LOGGER.LOG_ERROR("ERROR : INPUT OUT OF BOUNDS < " + value + " >");
                    }
                } catch (NumberFormatException e) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Incorrect Number Format. Integer or Decimal values expected.");
                    a.show();
                }

            }
        });
        Button data_screen_analyze_btn = (Button) data_screen_scene.lookup("#analyze_btn");
        data_screen_analyze_btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            private DecimalFormat df = new DecimalFormat("0.00");

            @Override
            public void handle(MouseEvent mouseEvent) {
                if (grades_list.size() > 0) {
                    Label mean_lbl = (Label) analysis_screen_scene.lookup("#mean_lbl");
                    Label median_lbl = (Label) analysis_screen_scene.lookup("#median_lbl");
                    Label max_lbl = (Label) analysis_screen_scene.lookup("#max_lbl");
                    Label min_lbl = (Label) analysis_screen_scene.lookup("#min_lbl");
                    Label mode_lbl = (Label) analysis_screen_scene.lookup("#mode_lbl");
                    int count = 0;
                    Double sum = 0.0;
                    for (Grade g : grades_list) {
                        sum += g.getGrade();
                        count++;
                    }
                    ArrayList<Grade> sorted_list = grades_list;
                    Collections.sort(sorted_list);

                    mean_lbl.setText(String.valueOf(df.format(sum / count))); // set mean
                    mean = sum / count;
                    if (sorted_list.size() % 2 != 0) {
                        median_lbl.setText(String.valueOf(df.format(sorted_list.get(sorted_list.size() / 2).getGrade())));
                        median = sorted_list.get(sorted_list.size() / 2).getGrade();
                    } else {
                        median_lbl.setText(String.valueOf(df.format(
                                (sorted_list.get(sorted_list.size() / 2).getGrade()
                                        + sorted_list.get(sorted_list.size() / 2 - 1).getGrade()) / 2
                        )));
                        median = sorted_list.get(sorted_list.size() / 2).getGrade()
                                + sorted_list.get(sorted_list.size() / 2 - 1).getGrade() / 2;
                    }

                    mode_lbl.setText(String.valueOf(df.format(get_mode(grades_list))));
                    mode = get_mode(grades_list);
                    System.out.println(Collections.frequency(grades_list, new Grade(1.0)));
                    max_lbl.setText(String.valueOf(df.format(sorted_list.get(sorted_list.size() - 1).getGrade()))); // set the max grade
                    min_lbl.setText(String.valueOf(df.format(sorted_list.get(0).getGrade()))); // set the min grade
                    max = sorted_list.get(sorted_list.size() - 1).getGrade();
                    min = sorted_list.get(0).getGrade();
                    // histogram stuff
                    ArrayList<Double> degree_distribution = new ArrayList<>();
                    final CategoryAxis xAxis = new CategoryAxis();
                    final NumberAxis yAxis = new NumberAxis();
                    final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                    barChart.setCategoryGap(2);
                    barChart.setBarGap(2);


                    for (Grade g : grades_list) {
                        if (!degree_distribution.contains(g.getGrade())) {
                            degree_distribution.add(g.getGrade());
                        }
                    }

                    for (double i : degree_distribution) {
                        System.out.print(i + " ");
                    }
                    System.out.println();

                    for (Grade g : sorted_list) {
                        System.out.print(g.getGrade() + " ");
                    }
                    System.out.println();
                    for (Grade g : grades_list) {
                        System.out.print(g.getGrade() + " ");
                    }
                    System.out.println();
                    Collections.sort(degree_distribution);
                    xAxis.setLabel("Grade Range");
                    yAxis.setLabel("Grade Frequency");

                    XYChart.Series series1 = new XYChart.Series();
                    series1.setName("Histogram for data");

                    int n_bins = (int) (1 + 3.322 * log(grades_list.size()));
                    System.out.println("Number of bins = " + n_bins);
                    double bin_size = sorted_list.size() / n_bins;
                    System.out.println("Bin Size = " + bin_size);

                    for (double grade : degree_distribution) {
                        series1.getData().add(new XYChart.Data(String.valueOf(grade), Collections.frequency(grades_list, new Grade(grade))));
                    }

                    barChart.getData().addAll(series1);

                    HBox pane = (HBox) analysis_screen_scene.lookup("#histogram_hbox");
                    pane.getChildren().add(barChart);

                    primaryStage.setScene(analysis_screen_scene);
                    primaryStage.show();
                    prev_scene = data_screen_scene;
                } else {
                    Alert a = new Alert(Alert.AlertType.WARNING);
                    a.setContentText("No data detected");
                    a.show();
                }
            }
        });


        Button error_log_btn = (Button) ((ToolBar) data_screen_scene.lookup("#upper_toolbar")).getItems().get(2);
        error_log_btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                TextArea error_log_ta = (TextArea) error_log_scene.lookup("#error_log_ta");
                error_log_ta.setText(ERROR_LOGGER.getErrorLogString());

                error_stage.setScene(error_log_scene);
                error_stage.show();
            }
        });

        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-ANALYSIS SCREEN HANDLERS-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-//
        Button export_data_btn = (Button) analysis_screen_scene.lookup("#export_data_btn");
        export_data_btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                CheckBox export_error_log_tgl = (CheckBox) analysis_screen_scene.lookup("#export_error_log_tgl");
                boolean export_error_log = export_error_log_tgl.isSelected();
                // try exporting the data.
                try {
                    export_data(export_error_log);
                } catch (IOException e) {
                    // show the error dialog if the export is failed.
                    e.printStackTrace();
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Data export Failed.");
                    a.show();
                }
            }
        });


        primaryStage.setScene(entry_screen_scene);
        primaryStage.show();
    }

    /**
     * @param list of grades to chop
     * @param L    length of each chopped list
     * @return the ArrayList of chopped List.
     */
    // chop in 4
    ArrayList<ArrayList<Grade>> chopped(ArrayList<Grade> list, int L) {
        ArrayList<ArrayList<Grade>> parts = new ArrayList<>();
        final int N = list.size();
        if (N % 4 != 0) {
            parts.add(list);
            return parts;
        }
        else {
            for (int i = 0; i < N; i += L) {
                parts.add(new ArrayList<Grade>(
                        list.subList(i, Math.min(N, i + L)))
                );
            }
        }
        return parts;
    }

    void export_data(boolean export_error_log) throws IOException {
        StringBuilder export_data = new StringBuilder("EXPORTED DATA \n");
        String separator = "\n-----------------------------------------------\n";

        //
        export_data.append(separator);
        export_data.append("Data used in the analysis : \n");
        for (Grade g : grades_list) {
            export_data.append(String.valueOf(g.getGrade()) + " , ");
        }
        export_data.append(separator);
        export_data.append("Size of the dataset : " + grades_list.size() + "\n");
        export_data.append("Boundary settings : " + Settings.GRADE_LEFT_BOUNDARY + " <" + " Grade " + "< " + Settings.GRADE_RIGHT_BOUNDARY + "\n");
        export_data.append("Grade data statistics : " + "\n");
        export_data.append("\tMean : " + mean + "\n");
        export_data.append("\tMedian : " + median + "\n");
        export_data.append("\tMode : " + mode + "\n");
        export_data.append("\tMin : " + min + "\n");
        export_data.append("\tMax : " + max + "\n");

        export_data.append(separator);

        if (export_error_log) {
            export_data.append(ERROR_LOGGER.getErrorLogString());
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter("Data.txt"));
        writer.write(export_data.toString());
        writer.close();
    }
}


