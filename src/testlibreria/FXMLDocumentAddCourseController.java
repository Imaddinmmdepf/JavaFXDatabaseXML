/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testlibreria;

import accesoaBD.AccesoaBD;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.Alumno;
import modelo.Curso;
import modelo.Dias;

/**
 * FXML Controller class
 *
 * @author icemole
 */
public class FXMLDocumentAddCourseController implements Initializable {

    @FXML private TextField inputTitle;
    @FXML private TextField inputMaxStud;
    @FXML private TextField inputProfessor;
    @FXML private TextField inputClassroom;
    @FXML private TextField inputTime;
    
    @FXML private DatePicker inputStartDate;
    @FXML private DatePicker inputEndDate;
    @FXML private CheckBox checkWednesday;
    @FXML private CheckBox checkMonday;
    @FXML private CheckBox checkTuesday;
    @FXML private CheckBox checkThursday;
    @FXML private CheckBox checkFriday;
    @FXML private CheckBox checkSaturday;
    @FXML private CheckBox checkSunday;
    
    @FXML private Button cancelButtonCourse;
    @FXML private Button okButtonCourse;
    
    private AccesoaBD abd;
    private ObservableList<Curso> coursesList;
    private int index;
    private boolean toModify;  // To know if we are modifying (true) or adding (false)
    private Label control;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    protected void initializeDataModify( ObservableList<Curso> l, int index, AccesoaBD abd, Label control )
    {
        this.control = control;
        this.abd = abd;
        this.coursesList = l;
        this.index = index;
        this.toModify = true;
        
        Curso c = l.get( index );
        this.inputTitle.setText(c.getTitulodelcurso());
        this.inputProfessor.setText(c.getProfesorAsignado());
        this.inputMaxStud.setText("" + c.getNumeroMaximodeAlumnos());
        this.inputStartDate.setValue(c.getFechainicio());
        this.inputEndDate.setValue(c.getFechafin());
        this.inputTime.setText(c.getHora().toString());
        this.inputClassroom.setText(c.getAula());
        List<Dias> listDays = c.getDiasimparte();
       
        for(Dias e : listDays){
            
            switch(e){
                case Lunes:
                    this.checkMonday.selectedProperty().set(true);
                    break;
                case Martes:
                    this.checkTuesday.selectedProperty().set(true);
                    break;
                case Miercoles:
                    this.checkWednesday.selectedProperty().set(true);
                    break;
                case Jueves:
                    this.checkThursday.selectedProperty().set(true);
                    break;
                case Viernes:
                    this.checkFriday.selectedProperty().set(true);
                    break;
                case Sabado:
                    this.checkSaturday.selectedProperty().set(true);
                    break;
                case Domingo:
                    this.checkSunday.selectedProperty().set(true);
                    break;
            }
        }
    }
    

    protected void initializeDataAdd( ObservableList<Curso> l, AccesoaBD abd, Label control )
    {
        this.control = control;
        this.abd = abd;
        this.coursesList = l;
        this.toModify = false;
    }
    
    @FXML
    private void handleCancelButtonCourse(ActionEvent event) {
        if( event.getSource() == okButtonCourse || event.getSource() == cancelButtonCourse ) {
            Node n = (Node) event.getSource ();
            n.getScene().getWindow().hide();
        }
    }

    @FXML
    private void handleOkButtonCourse(ActionEvent event) {
        
        String newName  = inputTitle.getText();  // inputTitle inputMaxStud inputProfessor inputClassroom inputTime
        int newMax;
        try {
            newMax = Integer.parseInt( inputMaxStud.getText() );
        } catch( NumberFormatException e ) {
            newMax = 0;
        }
        String newProf     = inputProfessor.getText();
        String newClass    = inputClassroom.getText();
        LocalTime newTime;
        try {
            newTime  = LocalTime.parse( inputTime.getText() );
        } catch( DateTimeParseException e ) {
            newTime = null;
        }
        LocalDate newStart = inputStartDate.getValue();
        LocalDate newEnd   = inputEndDate.getValue();
        
        ArrayList<Dias> d = new ArrayList<>();
        if( this.checkMonday.isSelected() ) { d.add( Dias.Lunes ); }
        if( this.checkTuesday.isSelected() ) { d.add( Dias.Martes ); }
        if( this.checkWednesday.isSelected() ) { d.add( Dias.Miercoles ); }
        if( this.checkThursday.isSelected() ) { d.add( Dias.Jueves ); }
        if( this.checkFriday.isSelected() ) { d.add( Dias.Viernes ); }
        if( this.checkSaturday.isSelected() ) { d.add( Dias.Sabado ); }
        if( this.checkSunday.isSelected() ) { d.add( Dias.Domingo ); }
        
        
        String error = "The following fields were not correctly filled:\n\n";
        if( newName.equals( "" ) || newMax <= 0 || newProf.equals( "" )
                || newClass.equals( "" ) || newTime == null || d.isEmpty()
                || newStart == null || newEnd == null || newStart.isAfter( newEnd ) ) {
            if( newName.equals( "" ) ) { error += "\tCourse title\n"; }
            if( newProf.equals( "" ) ) { error += "\tAssigned professor\n"; }
            if( newMax <= 0 ) { error += "\tMax students\n"; }
            if( newStart == null ) { error += "\tStart date\n"; }
            if( newEnd == null ) { error += "\tEnd date\n"; }
            if( newTime == null ) { error += "\tTime\n"; }
            if( d.isEmpty() ) { error += "\tScheduled days (no day selected)\n"; }
            if( newClass.equals( "" ) ) { error += "\tClassroom\n"; }
            
            Alert errorFulfilling = new Alert( Alert.AlertType.WARNING );
            errorFulfilling.setTitle( "Fields not filled" );
            errorFulfilling.setHeaderText( "There was an error while processing your request" );
            errorFulfilling.setContentText( error );
            errorFulfilling.showAndWait();
            
        } else {  // Add the course
            Curso c = new Curso( newName, newProf, newMax, newStart, newEnd, newTime, d, newClass );
            if( this.toModify ) {
                this.coursesList.set( this.index, c );
                this.control.setText( "Course " + newName + " was modified." );
            } else {
                this.coursesList.add( c );
                this.control.setText( "Course " + newName + " was added." );
            }
            //this.abd.salvar();
            handleCancelButtonCourse( event );
        }
    }
}
