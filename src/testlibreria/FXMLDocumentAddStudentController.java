/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testlibreria;

import accesoaBD.AccesoaBD;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import modelo.Alumno;

/**
 * FXML Controller class
 *
 * @author icemole
 */
public class FXMLDocumentAddStudentController implements Initializable {

    @FXML private ImageView imageView;
    @FXML private TextField inputDni;
    @FXML private TextField inputName;
    @FXML private TextField inputAddress;
    @FXML private TextField inputAge;
    @FXML private Button inputFile;
    @FXML private DatePicker inputDate;
    
    @FXML private Button cancelButtonStudent;
    @FXML private Button okButtonStudent;
    @FXML private Label maxSizeLabel;
    
    private AccesoaBD abd;
    private ObservableList<Alumno> studentList;
    private int index;
    private boolean toModify;  // To know if we are modifying (true) or adding (false)
    private Image newImage;
    private Label control;
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    protected void initializeDataModify( ObservableList<Alumno> l, int index, AccesoaBD abd, Label control )
    {
        this.control = control;
        this.abd = abd;
        this.studentList = l;
        this.index = index;
        this.toModify = true;
        
        Alumno a = l.get( index );
        inputDni.textProperty().setValue( a.getDni() );
        inputName.textProperty().setValue( a.getNombre() );
        inputAddress.textProperty().setValue( a.getDireccion() );
        inputAge.textProperty().setValue( "" + a.getEdad() );
        inputDate.setValue( a.getFechadealta() );
        
        // Use an imageview instead of a label that shows the photo names
        if( a.getFoto() != null) {
            this.imageView.setImage( a.getFoto() );
        } else {
            this.imageView.setImage( new Image( "images/nophoto.png" ) );
        }
    }

    protected void initializeDataAdd( ObservableList<Alumno> l, AccesoaBD abd, Label control )
    {
        this.control = control;
        this.abd = abd;
        this.studentList = l;
        this.toModify = false;
    }

    @FXML
    private void handleSelectFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( "Select student photo" );
        fileChooser.getExtensionFilters().setAll(
                new ExtensionFilter( "Image files (*.png, *.jpg, *.gif)", "*.png", "*.jpg", "*.gif" )
        );
        File selectedFile = fileChooser.showOpenDialog( ( (Node)event.getSource() ).getScene().getWindow() );
        if( selectedFile != null )
        {
            if( selectedFile.length() < 200 * 1000 )
            {
                this.newImage = new Image( selectedFile.toURI().toString() );
                this.imageView.setImage( newImage );
                this.maxSizeLabel.setStyle("-fx-text-fill: black;");

            }
            else
            {
                this.maxSizeLabel.setStyle("-fx-text-fill: red;");
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("File size exceeded");
                alert.setHeaderText("The file size must not exceed 200 kB");
                alert.setContentText("Please choose another file.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleCancelButtonStudent(ActionEvent event) {
        if( event.getSource() == okButtonStudent || event.getSource() == cancelButtonStudent ) {
            Node n = (Node) event.getSource ();
            n.getScene().getWindow().hide();
        }
    }

    @FXML
    private void handleOkButtonStudent(ActionEvent event) {
        
        String newDni = inputDni.getText();
        String newName = inputName.getText();
        String newAddress = inputAddress.getText();
        int newAge;
        try {
            newAge = Integer.parseInt( inputAge.getText() );
        } catch (NumberFormatException e) {
            newAge = 0;
        }
        LocalDate newLocalDate = inputDate.getValue();

        String error = "The following fields were not correctly filled:\n\n";
        
        if( newDni.equals("") || newName.equals("") || newAddress.equals("")
               || newAge <= 0 || newLocalDate == null ) {
            
            if( newDni.equals("") ) { error += "\tDNI\n"; }
            if( newName.equals("") ) { error += "\tFull name\n"; }
            if( newAddress.equals("") ) { error += "\tAddress\n"; }
            if( newAge <= 0 ) { error += "\tAge\n"; }
            if( newLocalDate == null ) { error += "\tEnrollment date\n"; }
            
            Alert errorFulfilling = new Alert( AlertType.WARNING );
            errorFulfilling.setTitle( "Fields not filled" );
            errorFulfilling.setHeaderText( "There was an error while processing your request" );
            errorFulfilling.setContentText( error );
            errorFulfilling.showAndWait();
        }
        else {
            Alumno newAlumno = new Alumno( newDni, newName, newAge, newAddress, newLocalDate, this.newImage );
            if( this.toModify ) {
                this.studentList.set( index, newAlumno );
                this.control.setText("Student " + newName + " was modified.");
            } else {
                this.control.setText("Student " + newName + " was added.");
                this.studentList.add( newAlumno );
            }
            
            this.abd.salvar();
            handleCancelButtonStudent( event );
        }
    }
}
