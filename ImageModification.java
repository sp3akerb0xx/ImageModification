import com.sun.tools.javac.util.ArrayUtils;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
/**
 * This program uses the file included in the repository to plot a 3-D shape, in this case a cube, and project it onto
 * a 2-D JavaFX WritableImage. This program is also capable of modifying the original projection by means of translating
 * it, scaling it, and rotating it in any of the three dimensions. The shape is always viewed from the same point,
 * (6, 8, 7.5) in space, and is projected to be viewed from 60 centimeters away.
 *
 * Note: Go to the "transform" method and change the pathname of the file to the new pathname before executing the program
 *
 * @author Nitin Chennam
 * @version 1.1
 */
public class ImageModification extends Application {

    Stage transformStage;
    Stage createStage;
    String fileNameString;
    String data;
    int[] dataArray;
    int[] finalDataArray;
    File file;
    WritableImage w = new WritableImage(1920, 1080); // creates a image that I can write pixel parameters to
    PixelWriter pixelWriter = w.getPixelWriter();
    Button translate = new Button("Translate");
    Button scale = new Button("Scale");
    Button rotate = new Button("Rotate");
    Button back = new Button("Back");
    Button calculate = new Button("Calculate");
    VBox basicRoot = new VBox();
    Scene basicScene = new Scene(basicRoot, 500, 500);
    String switchText;

    TextField X = new TextField("X: ");
    TextField Y = new TextField("Y: ");
    TextField angle = new TextField("Angle: ");

    TextField ComplexX = new TextField("X: ");
    TextField ComplexY = new TextField("Y: ");
    TextField scaleX = new TextField("X Scale: ");
    TextField scaleY = new TextField("Y Scale: ");
    TextField complexAngle = new TextField("Angle: ");

    public static void main(String[] args) {
        launch(args);
    }
    /**
     * This method is the main driver class. It contains all of the necessary components for the rest of the program to
     * execute.
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        TilePane root = new TilePane();
        Scene rootScene = new Scene(root, 500, 50);
        Text rootText = new Text("What would you like to do?");
        Button transformFile = new Button("Transform a file");
        Button createFile = new Button("Create a file");
        root.getChildren().addAll(rootText, transformFile, createFile);

        transformFile.setOnAction( e -> transform());
        createFile.setOnAction(e -> create());


        primaryStage.setScene(rootScene);
        primaryStage.show();
    }//start()

    /**
     * This method creates the file with the name and contents of the user's preferences
     */
    private void create(){
        VBox createRoot = new VBox();
        createStage = new Stage();
        Scene createScene = new Scene(createRoot, 500, 500);

        TextField fileName = new TextField("Enter file name here: ");
        Text text = new Text("Enter the text for the file\nUse the following format for each line:\nstartX startY endX endY");
        TextArea fileContent = new TextArea("Enter the file contents here : ");
        Button createFile = new Button("Create File");

        createRoot.getChildren().addAll(fileName, text, fileContent, createFile);

        createFile.setOnAction(e -> {
            fileNameString = fileName.getText();
            data = fileContent.getText();
            createFile(fileNameString);
        });//createFile setOnAction

        createStage.setScene(createScene);
        createStage.show();

    }//create

    /**
     * This method is root for the part of the program that allows the user to modify the lines that were taken from
     * the provided file
     */
    private void transform(){

        VBox transformRoot = new VBox();
        transformStage = new Stage();
        Scene transformScene = new Scene(transformRoot, 500, 500);
        Text fileNameText = new Text("Enter the name of the file to be read");
        TextField fileNameTextField = new TextField();


        transformRoot.getChildren().addAll(fileNameText, fileNameTextField);

        fileNameTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    fileNameString = fileNameTextField.getText();
                    try {
                        data = new String(Files.readAllBytes(Paths.get("/Users/nitin_c/Desktop/" + fileNameString)));
                    }//try
                    catch(IOException e){
                        e.printStackTrace();
                    }//catch
                    transform2();
                }
            }
        });
        transformStage.initModality(Modality.APPLICATION_MODAL);
        transformStage.setScene(transformScene);
        transformStage.show();
    }//transform()

    /**
     * This method allows the user to choose what kind of transformation to apply to the lines that were retrieved from
     * the file
     */
    private void transform2() {
        VBox transform2Root = new VBox();
        Scene transform2Scene = new Scene(transform2Root, 500, 200);

        Text pickOne = new Text("Please pick one of the following options");
        Button basicTransform = new Button("Basic Transformation");
        Button complexTransform = new Button("Complex Transformation");
        transform2Root.getChildren().addAll(pickOne, basicTransform, complexTransform);

        basicTransform.setOnAction(e -> {
            basic();
            System.out.println(data);
        });

        complexTransform.setOnAction(e -> {
            complex();
        });

        transformStage.setScene(transform2Scene);
        transformStage.show();

    }//transform2()

    /**
     * This method allows the user to choose which basic transformation to apply to the points that were retrieved from
     * the file provided. Once the user chooses which transformation to apply, it allows the user to choose the
     * parameters for the chosen transformation.
     */
    private void basic(){
        basicRoot.getChildren().addAll(translate, scale, rotate);

        transformStage.setScene(basicScene);
        transformStage.show();

        translate.setOnAction(e -> {
            basicRoot.getChildren().removeAll(scale, rotate);
            basicRoot.getChildren().addAll(X, Y, calculate, back);

            calculate.setOnAction(n -> {
                executeTranslate();
            });

            back.setOnAction(e1 -> {
                basicRoot.getChildren().removeAll(X, Y, calculate, back);
                basicRoot.getChildren().addAll(scale, rotate);
            });//back setOnAction

        });//translate setOnAction


        scale.setOnAction(e -> {
            basicRoot.getChildren().removeAll(translate, rotate);
            basicRoot.getChildren().addAll(X, Y, calculate, back);

            back.setOnAction(e1 -> {
                basicRoot.getChildren().removeAll(X, Y, calculate, back);
                basicRoot.getChildren().addAll(translate, rotate);
            });//back setOnAction

            calculate.setOnAction(n -> {
                executeScale();
            });

        });//scale setOnAction

        rotate.setOnAction(e -> {
            basicRoot.getChildren().removeAll(translate, scale);
            basicRoot.getChildren().addAll(angle, calculate, back);

            back.setOnAction(e1 -> {
                basicRoot.getChildren().removeAll(angle, calculate, back);
                basicRoot.getChildren().addAll(translate, scale);
            });//back setOnAction

            calculate.setOnAction(n -> {
                executeRotate();
            });

        });//rotate setOnAction
    }//basic
    /**
     * This method allows the user to choose which complex transformation to apply to the points that were retrieved from
     * the file provided. Once the user chooses which transformation to apply, it allows the user to choose the
     * parameters for the chosen transformation.
     */
    private void complex(){
        VBox complexRoot = new VBox();
        Scene complexScene = new Scene(complexRoot, 500, 500);

        Button rotate = new Button("Rotate");
        Button scale = new Button("Scale");
        Button back = new Button("Back");
        Button calculate = new Button("Calculate");

        complexRoot.getChildren().addAll(rotate, scale);



        scale.setOnAction(e -> {
            complexRoot.getChildren().removeAll(rotate);
            complexRoot.getChildren().addAll(scaleX, scaleY, ComplexX, ComplexY, calculate, back);

            back.setOnAction(e1 -> {
                complexRoot.getChildren().removeAll(scaleX, scaleY, ComplexX, ComplexY, calculate, back);
                complexRoot.getChildren().addAll(rotate);
            });//back setOnAction

            calculate.setOnAction(n -> {
                executeComplexScale();
            });
        });//scale setOnAction

        rotate.setOnAction(e -> {
            complexRoot.getChildren().removeAll(scale);
            complexRoot.getChildren().addAll(ComplexX, ComplexY, calculate, complexAngle);

            back.setOnAction(e1 -> {
                complexRoot.getChildren().removeAll(ComplexX, ComplexY, calculate, complexAngle);
                complexRoot.getChildren().addAll(scale);
            });//back setOnAction

            calculate.setOnAction(n -> {
                executeComplexRotate();
            });
        });//rotate setOnAction

        transformStage.setScene(complexScene);
        transformStage.show();
    }//complex()

    /**
     * This method takes the string representation of the the provided file and parses the elements into an array that
     * the program can modify
     * @param newData The string representation of the file
     */
    private void createDataArray(String newData){
        Scanner scan0 = new Scanner(newData);
        int count = 0;
        int temp = 0;
        while (scan0.hasNextInt()){
            temp = scan0.nextInt();
            count++;}
        dataArray = new int[count];
        Scanner scan1 = new Scanner(newData);
        for(int i = 0; i < dataArray.length; i++)
            dataArray[i] = scan1.nextInt();
    }

    /**
     * This is a simple method that creates a file with contents entered by the user. The file name is chosen by the
     * user
     * @param fileName The chosen file name
     */
    private void createFile(String fileName){
        this.file = new File("/Users/nitin_c/Desktop/" + fileName);

        try {
            FileWriter writer = new FileWriter(file);
            file.createNewFile();
            writer.write(data);
            writer.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }//createFile()

    /**
     * This method takes the numbers for X and Y that were entered by the user and calls the methods needed to translate
     * the file in those directions.
     */
    private void executeTranslate(){
        int x;
        int y;
        x = Integer.parseInt(X.getText());
        y = Integer.parseInt(Y.getText());
        try {
            data = new String(Files.readAllBytes(Paths.get("/Users/nitin_c/Desktop/" + fileNameString)));
        }//try
        catch(IOException e3){
            e3.printStackTrace();
        }//catch
        createDataArray(data);
        translate(dataArray, x, y);
        writeToImage(dataArray, false);
        writeToImage(finalDataArray, true);
        try {
            displayImage();
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
            Stage dialogStage = new Stage();
            VBox root = new VBox();
            Scene scene = new Scene(root);
            Text text = new Text("That don't fit, bro");
            root.getChildren().add(text);
            dialogStage.setScene(scene);
            dialogStage.show();
        }
    }//executeTranslate()

    /**
     * This method takes the numbers for X and Y that were entered by the user and calls the methods needed to scale
     * the file in those directions.
     */
    private void executeScale(){
        int x;
        int y;
        x = Integer.parseInt(X.getText());
        y = Integer.parseInt(Y.getText());
        try{
            data = new String(Files.readAllBytes(Paths.get("/Users/nitin_c/Desktop/" + fileNameString)));
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
        createDataArray(data);
        scale(dataArray, x, y);
        writeToImage(dataArray, false);
        writeToImage(finalDataArray, true);
        try {
            displayImage();
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
            Stage dialogStage = new Stage();
            VBox root = new VBox();
            Scene scene = new Scene(root);
            Text text = new Text("That don't fit, bro");
            root.getChildren().add(text);
            dialogStage.setScene(scene);
            dialogStage.show();
        }

    }//executeScale()

    /**
     * This method takes the numbers for X and Y that were entered by the user and calls the methods needed to translate
     * the file back to the origin, scale it in the requested directions, and translate it back to the original location
     */
    private void executeComplexScale(){
        int translateX = Integer.parseInt(ComplexX.getText());
        int translateY = Integer.parseInt(ComplexY.getText());
        int complexScaleX = Integer.parseInt(scaleX.getText());
        int complexScaleY = Integer.parseInt(scaleY.getText());
        try{
            data = new String(Files.readAllBytes(Paths.get("/Users/nitin_c/Desktop/" + fileNameString)));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        createDataArray(data);
        writeToImage(dataArray, false);
        complexScale(dataArray, translateX, translateY, complexScaleX, complexScaleY);
        writeToImage(finalDataArray, true);
        displayImage();
    }//executeComplexScale()

    /**
     * This method takes the numbers for X and Y that were entered by the user and calls the methods needed to rotate
     * the file in the specified number of degrees.
     */
    private void executeRotate(){
        int angle0;
        angle0 = Integer.parseInt(angle.getText());
        try{
            data = new String(Files.readAllBytes(Paths.get("/users/nitin_c/Desktop/" + fileNameString)));
        }
        catch(Exception exception) {
            exception.printStackTrace();
        }
        createDataArray(data);
        rotate(dataArray, angle0);
        writeToImage(dataArray, false);
        writeToImage(finalDataArray, true);
        try {
            displayImage();
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
            Stage dialogStage = new Stage();
            VBox root = new VBox();
            Scene scene = new Scene(root);
            Text text = new Text("That don't fit, bro");
            root.getChildren().add(text);
            dialogStage.setScene(scene);
            dialogStage.show();
        }
    }//executeRotate()

    /**
     * This method takes the numbers for X and Y that were entered by the user and calls the methods needed to translate
     * the file back to the origin, rotate it in the requested direction, and translate it back to the original location
     */
    private void executeComplexRotate(){
        int translateX = Integer.parseInt(ComplexX.getText());
        int translateY = Integer.parseInt(ComplexY.getText());
        int angle = Integer.parseInt(complexAngle.getText());
        try{
            data = new String(Files.readAllBytes(Paths.get("/Users/nitin_c/Desktop/" + fileNameString)));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        createDataArray(data);
        writeToImage(dataArray, false);
        complexRotate(dataArray, translateX, translateY, angle);
        writeToImage(finalDataArray, true);
        displayImage();
    }//executeComplexRotate()

    /**
     * This method actually executes the translation of the points specified
     * @param points A 1-D array of the point that needs to be translated
     * @param x The amount to be translated in the X direction
     * @param y The amount to be translated in the Y direction
     */
    private void translate(int[] points, int x, int y){
        for(int i = 0; i < points.length/2; i++) {

            int[] matrix1 = new int[]{points[i * 2], points[i * 2 + 1], 1};
            int[][] matrix2 = new int[][]{{1, 0, x}, {0, 1, y}, {0, 0, 1}};

            int[] initialReturnMatrix = multiplyMatrix(matrix1, matrix2);
            int[] returnMatrix = new int[] {initialReturnMatrix[0], initialReturnMatrix[1]};
            if (i > 0) {
                finalDataArray = appendArrays(finalDataArray, returnMatrix);
                System.out.println(Arrays.toString(finalDataArray));
            }
            else {
                System.out.println(Arrays.toString(finalDataArray));
                finalDataArray = returnMatrix;
            }
        }
    }//translate()

    /**
     * This method actually executes the scale of the points specified
     * @param points A 1-D array of the point that needs to be scaled
     * @param x The amount to be scaled in the X direction
     * @param y The amount to be scaled in the Y direction
     */
    private void scale(int[] points, int x, int y){
        for(int i = 0; i < points.length/2; i++) {
            int[] matrix1 = new int[]{points[i * 2], points[i * 2 + 1], 1};
            int[][] matrix2 = new int[][]{{x, 0, 0}, {0, y, 0}, {0, 0, 1}};


            int[] initialReturnMatrix = multiplyMatrix(matrix1, matrix2);
            int[] returnMatrix = new int[] {initialReturnMatrix[0], initialReturnMatrix[1]};
            if (i > 0)
                finalDataArray = appendArrays(finalDataArray, returnMatrix);
            else
                finalDataArray = returnMatrix;
        }
    }//scale()
    /**
     * This method actually executes the rotation of the points specified
     * @param points A 1-D array of the point that needs to be rotated
     * @param angle0 the number of degrees that the point is to be rotated
     */
    private void rotate(int[] points, int angle0){

        double angle = convertToRadians(angle0);

        for(int i = 0; i < points.length/2; i++) {
            int[] matrix1 = new int[] {points[i * 2], points[i * 2 + 1], 1};
            double[][] matrix2 = new double[][] {{Math.cos(angle), -1 * Math.sin(angle), 0},
                    {Math.sin(angle), Math.cos(angle), 0}, {0, 0, 1}};

            System.out.println(Arrays.toString(matrix2[0]));
            System.out.println(Arrays.toString(matrix2[1]));
            System.out.println(Arrays.toString(matrix2[2]));

            double[] initialReturnMatrix = multiplyMatrix(matrix1, matrix2);
            int[] initialReturnMatrix0 = new int[] {(int)Math.round(initialReturnMatrix[0]), (int)Math.round(initialReturnMatrix[1]), 1};
            int[] returnMatrix = new int[] {initialReturnMatrix0[0], initialReturnMatrix0[1]};
            if (i > 0)
                finalDataArray = appendArrays(finalDataArray, returnMatrix);
            else
                finalDataArray = returnMatrix;
        }
    }//rotate
    /**
     * This method actually executes the complex scale of the points specified
     * @param points A 1-D array of the point that needs to be scaled
     * @param translateX The distance to the origin in the X direction
     * @param translateY The distance to the origin in the Y direction
     * @param x The amount to be scaled in the X direction
     * @param y The amount to be scaled in the Y direction
     */
    private void complexScale(int[] points, int translateX, int translateY, int x, int y){
        translate(points, -1 * translateX, -1 * translateY);
        scale(finalDataArray, x, y);
        translate(finalDataArray, translateX, translateY);
    }//complexScale()

    /**
     * This method actually executes the complex rotate of the points specified
     * @param points a 1-D array of the point that needs to be scaled
     * @param translateX The distance to the origin in the X direction
     * @param translateY The distance to the origin in the Y direction
     * @param angle The number of degrees that the point in question is to be rotated
     */
    private void complexRotate(int[] points, int translateX, int translateY, int angle){

        translate(points, -1 * translateX, -1 * translateY);
        rotate(finalDataArray, angle);
        translate(finalDataArray, translateX, translateY);

    }//complexRotate()

    /**
     * This method multiplies two matrices using matrix multiplication
     * @param m1 the first matrix
     * @param m2 the second matrix
     * @return a 1D integer array that contains the new set of points
     */
    private int[] multiplyMatrix(int[] m1, int[][] m2){
        int[] result = new int [m2[0].length];
            for (int j = 0; j < m2[0].length; j++) {
                for (int k = 0; k < m1.length; k++) {
                    System.out.println(m1[k] * m2[k][j]);
                    result[j] += m1[k] * m2[j][k];
                }
                System.out.println();
                System.out.println(result[j]);
            }
        return result;
    }//multiplyMatrix()

    /**
     * This method multiplies two matrices using matrix multiplication
     * @param m1 the first matrix
     * @param m2 the second matrix
     * @return a 1D double array that contains the new set of points
     */
    private double[] multiplyMatrix(int[] m1, double[][] m2){
        double[] result = new double[m2[0].length];
        for (int j = 0; j < m2[0].length; j++) {
            for (int k = 0; k < m1.length; k++) {
                System.out.println(m1[k] * m2[k][j]);
                result[j] += m1[k] * m2[j][k];
            }
            System.out.println();
            System.out.println(result[j]);
        }
        return result;
    }

    /**
     * This method uses a line-drawing algorithm to write the lines between the provided points to the WritableImage.
     */
    private void writeToImage(int[] array, boolean transform){
        System.out.println(Arrays.toString(array));
        for(int i = 0; i < array.length/4; i++) {

            int startX = array[i*4];
            int startY = array[i*4+1];
            int endX = array[i*4+2];
            int endY = array[i*4+3];

            Color before = Color.RED;
            Color after = Color.BLACK;

            double x = startX;
            double y = startY;
            double slope = getM(startX, endX, startY, endY);
            if (Math.abs(slope) < 1) {
                for (x = startX; x <= endX; x++) {
                    if (transform)
                        pixelWriter.setColor((int) x, (int) Math.round(y), after);
                    else
                        pixelWriter.setColor((int) x, (int) Math.round(y), before);
                    y += slope;
                }//for
            }//slope < 1

            else {
                for (y = startY; y <= endY; y++) {
                    if (transform)
                        pixelWriter.setColor((int) x, (int) Math.round(y), after);
                    else
                        pixelWriter.setColor((int) x, (int) Math.round(y), before);
                    x += 1 / slope;
                }//for
            }// slope > 1
        }
    }

    /**
     * This method created a stage and a scene to place the WritableImage onto and displays the image
     */
    private void displayImage(){
        ImageView wImage = new ImageView(w);
        VBox root1 = new VBox();
        root1.getChildren().addAll(wImage);
        final Stage basicStage = new Stage();
        basicStage.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(root1);
        basicStage.setScene(dialogScene);
        basicStage.show();

    }//displayImage()

    /**
     * A very simple method that simply returns the slope between the provided points
     * @param startX The X coordinate of the start point
     * @param endX The X coordinate of the end point
     * @param startY The Y coordinate of the start point
     * @param endY The Y coordinate of the end point
     * @return The slope
     */
    private double getM(int startX, int endX, int startY, int endY){
        double slope = ((double)endY - (double)startY)/((double)endX - (double)startX);
        return slope;
    }//getM()

    private int[] appendArrays(int[] array1, int[] array2){
        int length = array1.length + array2.length;
        int whatsLeft = length - array1.length;
        int[] newArray = new int[length];
        for(int i = 0; i < array1.length; i++)
            newArray[i] = array1[i];
        for(int i = 0; i < whatsLeft; i++)
            newArray[array1.length + i] = array2[i];
        return newArray;
    }

    /**
     * A simple method that converts degrees to radians
     * @param degrees the number of degrees
     * @return the degree value in radians
     */
    private double convertToRadians(int degrees){
        double result = degrees * (Math.PI/180);
        return result;
    }//convertToRadians()
}
