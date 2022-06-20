/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computeclient;

import Contract.*;
import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Ifte
 */
public class FXMLController implements Initializable {
    //to input name of server
    @FXML
    private TextField serverName;
    
    //to input object transfer port number
    @FXML
    private TextField portObject;

    //to input file transfer port number
    @FXML
    private TextField portFile;
    
    //button to set connection
    @FXML
    private Button set;

    //drop down menu to select a task from
    @FXML
    private ChoiceBox<String> taskList;

    @FXML
    private Button upload;

    @FXML
    private Button calculate;

    @FXML
    private TextArea results;

    Socket s;  //socket for file transfer
    Socket s1 = null;  //socket for object transfer
    ObjectInputStream obIn;   //to read object from server
    ObjectOutputStream obOut;  //to write object to server

    
    //"Set" button handle that establishes the TCP connections between the server and client
    @FXML
    void handleSetButtonAction(ActionEvent event) {
        InetAddress ServerName;
        String name = serverName.getText();                        //reads the server name the user input into the server name field
        int objectPort = Integer.parseInt(portObject.getText());  //reads the object port number the user input into the GUI
        int filePort = Integer.parseInt(portFile.getText());     //reads the file port number the user input

        //this establishes a TCP connection for file transfer using the inputs by the user
        try {
            ServerName = InetAddress.getByName(name);
            s = new Socket(ServerName, filePort);   //socket constructor for file

        } catch (UnknownHostException uhe) {
            System.out.println("UnknownHost:" + uhe.getMessage());
        } catch (IOException e) {
            System.out.println("IO errors:" + e.getMessage());
        }

        //this establishes a TCP connection for object transfer using the inputs by the user
        try {
            s1 = new Socket(name, objectPort);  ////socket constructor for object

            obOut = new ObjectOutputStream(s1.getOutputStream());
            obIn = new ObjectInputStream(s1.getInputStream());

        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        }

        
        //below codes are to enable the upload, calculate, 
        //and drop down menu(choice box) once the connections are established successfully.
        upload.setDisable(false);     
        calculate.setDisable(false);
        taskList.setDisable(false);
        //set default value of choice box
        taskList.setValue("Calculate Pi");
    }

    
    //This implements the functionality of "upload" button
    @FXML
    void handleUploadButtonAction(ActionEvent event) throws InterruptedException {
        OutputStream out;
        String ClassName;   //name of file to transfer
        String ClassPath;   //path of the file

        try {
            out = s.getOutputStream();    
            String task = taskList.getValue();    //to read the task selected by the user in drop down menu
                
            //switch case to set the appropriate class name and path 
            switch (task) {
                case "Calculate Pi":
                    ClassName = "CalculatePi.class";
                    ClassPath = "src/Contract/CalculatePi.class";
                    break;
                case "Calculate Prime":
                    ClassName = "CalculatePrime.class";
                    ClassPath = "src/Contract/CalculatePrime.class";
                    break;
                default:
                    ClassName = "CalculateGCD.class";
                    ClassPath = "src/Contract/CalculateGCD.class";
                    break;
            }

            //Read the class file into a byte array
            File ClassFile = new File(ClassPath);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(ClassFile));
            DataInputStream dis = new DataInputStream(bis);
            byte[] mybytearray = new byte[(int) ClassFile.length()];
            dis.readFully(mybytearray, 0, mybytearray.length);
            //Use a data output stream to send the class file
            DataOutputStream dos = new DataOutputStream(out);
            //Send the class file name
            dos.writeUTF(ClassPath);
            //Send the class file length
            dos.writeInt(mybytearray.length);
            //Send the class file
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            //Report the the transfer state 
            Thread.sleep(4000);  //copying and reading file takes upto 4 seconds, this is to delay the display of "File uploaded" to the user.
            System.out.println("File " + ClassName + " uploaded.");
            results.appendText("Uploading " + ClassName + " is done" + "." + "\n");  //lets the user know that file is uploaded
        } catch (UnknownHostException uhe) {
            System.out.println("UnknownHost:" + uhe.getMessage());
        } catch (IOException e) {
            System.out.println("IO errors:" + e.getMessage());
        }
    }

    
    //This implements the functionality of "calculate" button
    @FXML
    void handleCalculateButtonAction(ActionEvent event) {
        String calculation = taskList.getValue();  // to store what calculation to be done

        try {
            //if else statements to show the right dialog box and creating and sending the right task to the server
            if (calculation.equalsIgnoreCase("Calculate Pi")) {
                //to show a dialog box asking for input from user
                TextInputDialog textInput = new TextInputDialog();
                textInput.setTitle("Please enter the number of decimal places");
                textInput.getDialogPane().setContentText("Decimal places:");
                textInput.showAndWait();
                int decimalPlace = Integer.parseInt(textInput.getEditor().getText());   //reads the user input

                CalculatePi pi = new CalculatePi(decimalPlace);  //contructs the task object
                obOut.writeObject(pi);   //sends the object created to server
                
            } else if (calculation.equalsIgnoreCase("Calculate Prime")) {
                TextInputDialog textInput = new TextInputDialog();
                textInput.setTitle("Please enter an integer");
                textInput.getDialogPane().setContentText("Integer:");
                textInput.showAndWait();
                int integer = Integer.parseInt(textInput.getEditor().getText());

                CalculatePrime prime = new CalculatePrime(integer);   //contructs the task object
                obOut.writeObject(prime);    //sends the object created to server
                
            } else {
                display();   //static method defined below to create a custom dialog box and read the user inputs to create the task object
                CalculateGCD gcd = new CalculateGCD(integer1, integer2);   //contructs the task object
                obOut.writeObject(gcd);   //sends the object created to server
            }
            
            Task ts = (Task) obIn.readObject();   // to receive the object sent back by the server and type cast to type
            results.appendText(ts.getResult() + "\n");    // displays the result set on that object by the server
            results.appendText("------------------------------" + "\n");
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //disables the upload, calculate and tasklist when the program starts
        upload.setDisable(true);
        calculate.setDisable(true);

        taskList.getItems().add("Calculate Pi");
        taskList.getItems().add("Calculate Prime");
        taskList.getItems().add("Calculate the Greatest Common Divisor");
        taskList.setDisable(true);
    }

    
    static int integer1, integer2;  //to hold the integer values entered by user to calculate GCD.
    //to create custom dialog box for calculating greatest common divisor.
    //takes in two integers from the user and returns it.
    public static int display() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField text1 = new TextField();
        TextField text2 = new TextField();

        //ok button functionality
        Button button1 = new Button("Ok");
        button1.setAlignment(Pos.CENTER_RIGHT);
        button1.setOnAction(e -> {
            integer1 = Integer.parseInt(text1.getText());
            integer2 = Integer.parseInt(text2.getText());
            stage.close();
        });

        //cancel button functionality
        Button button2 = new Button("Cancel");
        button2.setOnAction(e -> {
            stage.close();
        });

        Label label1 = new Label("Integer1:");
        Label label2 = new Label("Integer2:");

        GridPane layout = new GridPane();

        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setVgap(15);
        layout.setHgap(15);

        layout.add(text1, 1, 0);
        layout.add(text2, 3, 0);
        layout.add(label1, 0, 0);
        layout.add(label2, 2, 0);
        layout.add(button1, 1, 1);
        layout.add(button2, 2, 1);

        Scene scene = new Scene(layout);
        stage.setTitle("Please enter 2 integers");
        stage.setScene(scene);
        stage.showAndWait();

        return integer1 + integer2;
    }

}
