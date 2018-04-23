/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testlibreria;

import accesoaBD.AccesoaBD;
import com.sun.javafx.collections.ObservableListWrapper;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Alumno;
import modelo.Curso;
import modelo.Matricula;

/**
 * FXML Controller class
 *
 * @author icemole
 */
public class FXMLDocumentShowEnrollmentsController implements Initializable {

    @FXML private TableView<Curso> tableView;
    @FXML private TableColumn<Curso, String> courseColumnEnr;
    @FXML private TableColumn<Curso, String> dateColumnEnr;
    
    
    @FXML private Button backButtonEnr;
    
    ObservableList<Curso> c;
    ObservableList<Curso> todosLosCursos;
 
    ObservableList<Matricula> matricula;

    AlumnoEquals a;
    Hashtable<LocalTime, CursoEquals> checkHours = new Hashtable<>();
    AccesoaBD db;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
     
    }
    
    protected void initializeData( AlumnoEquals a, ObservableList<Curso> c, AccesoaBD bd , ObservableList<Matricula> matricula)
    {
        
        this.matricula = matricula;
        this.todosLosCursos = c;
        this.c  =(ObservableList) FXCollections.observableArrayList();
        this.a = a;
        this.db = bd;
       
        for( Matricula m : a.getEnrollmentsStudent() )
        {
               
                this.c.add(m.getCurso());
               // System.out.println(m.getCurso().getTitulodelcurso());
                this.checkHours.put( m.getCurso().getHora(), new CursoEquals( m.getCurso() ) );
                System.out.println(this.checkHours.size());
                
        }
        
        System.out.println("Ehhh aquii----------enn SHOWWWWWWWWW ENROLLL"  + a.getEnrollmentsStudent().size());
        courseColumnEnr.setCellValueFactory( cellData -> new SimpleStringProperty( cellData.getValue().getTitulodelcurso() ) );
        dateColumnEnr.setCellValueFactory( cellData -> new SimpleStringProperty( cellData.getValue().getFechainicio().toString() ) );
        ObservableList<TableColumn<Curso, ?>> auxTables =
            (ObservableList)FXCollections.observableArrayList( courseColumnEnr, dateColumnEnr );
        
        tableView.getColumns().setAll( auxTables );
        tableView.setItems(this.c);
    }

    private Pane parentLoader(Pane root, FXMLLoader myLoader) {
         try {
            root = (Pane) myLoader.load();
        } catch( IOException e ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error occurred");
            alert.setHeaderText("Oops! That was a mistake");
            alert.setContentText("An error has occurred while loading a new window."
                    + "\n\n" + "Please contact the support staff."
                    + "\n\n" + "The application will now exit.");
            alert.showAndWait();
            System.exit(-1);
        }
         return root;
    }
    
    @FXML
    private void addHandlerEnrollment(ActionEvent event) {
        Pane root = null;
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource( "FXMLDocumentAddEnrollment.fxml" ));
        root = parentLoader(root, myLoader);
        FXMLDocumentAddEnrollmentController addEnrollment = myLoader.<FXMLDocumentAddEnrollmentController>getController();
        
        addEnrollment.initializeEnr( this.a, this.checkHours, this.c, this.db, this.matricula, this.todosLosCursos);
        
        Scene scene = new Scene( root );
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle( "Add enrollment" );
        stage.initModality( Modality.APPLICATION_MODAL );
        stage.setResizable( false );
        stage.showAndWait();
        
    }

    @FXML
    private void cancelButtonEnr(ActionEvent event) {
        if( event.getSource() == backButtonEnr ) {
            Node n = (Node) event.getSource ();
            n.getScene().getWindow().hide();
        }
    }
    
}
