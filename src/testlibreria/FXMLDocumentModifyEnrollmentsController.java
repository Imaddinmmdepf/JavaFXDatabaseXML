/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testlibreria;

import accesoaBD.AccesoaBD;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import modelo.Alumno;
import modelo.Curso;
import modelo.Matricula;

/**
 * FXML Controller class
 *
 * @author imaddin
 */
public class FXMLDocumentModifyEnrollmentsController implements Initializable {

    @FXML
    private TableView<Matricula> tableView;
    
    @FXML
    private TableColumn<Matricula, String> enrColumnEnr;
    @FXML
    private TableColumn<Matricula, String> dateColumnEnr;
    @FXML
    private TableColumn<Matricula, CheckBox> activesColumn;   
    @FXML
    private Button okButtonMatriculas;
    @FXML
    private Button cancelButtonMatriculas;
        
    ObservableList<Matricula> c;
    ObservableList<Matricula> todosLasMatriculas;
 
    ObservableList<Alumno> alumnos;

    Curso a;
    Hashtable<String, Matricula> enr = new Hashtable<>();
    AccesoaBD db;
    @FXML
    private Label control;
    private ObservableList<Matricula> matriculasPrevias;
   


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
     protected void initializeData( Curso cur, CursoEquals a, ObservableList<Matricula> c, AccesoaBD bd , ObservableList<Alumno> alumnos)
    {
        this.matriculasPrevias = (ObservableList)FXCollections.observableArrayList();
        this.alumnos = alumnos;
        this.todosLasMatriculas = c;
        this.a = cur;
        this.db = bd;
       System.out.println("Soy el curso de ajora   "+bd.getMatriculasDeCurso(cur).size());
        for(Matricula matriculaPrev : c){
            System.out.println("Soy el curso de ajora   "+bd.getMatriculasDeCurso(cur).size());
            
            this.matriculasPrevias.add(matriculaPrev);
            this.enr.put( matriculaPrev.getAlumno().getDni(),  matriculaPrev  );
        }
        System.out.println(c.size());
      
        System.out.println("Imaddinnnnnnnn en modify initi  ");
         activesColumn.setCellValueFactory( cellData -> {
                return new SimpleObjectProperty<CheckBox>( new CheckBox("") );
            });
        
        
        Callback<TableColumn<Matricula, CheckBox>, TableCell<Matricula, CheckBox>> cellFactoryCheckbox = new Callback<TableColumn<Matricula, CheckBox>, TableCell<Matricula, CheckBox>>() {
            @Override
            public TableCell call(final TableColumn<Matricula, CheckBox> param) {
                final TableCell<Matricula, CheckBox> cell = new TableCell<Matricula, CheckBox>() {

                    CheckBox chk = new CheckBox("");
                    
                    @Override
                    public void updateItem(CheckBox item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            Matricula matriculaEquals =  getTableView().getItems().get( getIndex() );
                            Matricula matriculaHash = enr.get( matriculaEquals.getAlumno().getDni() );
                            if(  matriculaHash != null && matriculaHash.equals(matriculaEquals) )
                            {
                               System.out.println("Holeaaaaa hashtableee" +  matriculaHash.getAlumno().getDni()  );
                               chk.setSelected( true );
                            }
                            
                            
                            chk.setOnAction(event -> {
                                
                                Matricula co =  getTableView().getItems().get( getIndex());
                                ObservableList<Matricula> matriculaInd = obsListMatricula();
                                Curso curso = getCurso();
                                AccesoaBD database = getDatabase();
                                
                                
                                if( chk.isSelected() ) {
                                    
                                   if(curso.getNumeroMaximodeAlumnos() < database.getAlumnosDeCurso(curso).size()){
                                       chk.setSelected(false);
                                       control.setText("This course has the maximum number of students " + curso.getNumeroMaximodeAlumnos());
                                        

                                   }else {
                                       enr.put(co.getAlumno().getDni(), co);
                                       /*matriculaInd.add(co);*/
                                      /* database.getMatriculasDeCurso(curso).add(co);*/

                                   }
                                }
                                else {
                                     
                                    Matricula me = co;
                                    int index = 0;
                                   
                                    for(Matricula mo : matriculaInd){
                                        
                                        if(me.equals(mo)){
                                            
                                            System.out.println("Hello boradddoooo");
                                            enr.remove(co.getAlumno().getDni());
                                            /*database.getMatriculasDeCurso(curso).remove(mo);*/
                                            break;
                                        }
                                        index++;
                                        
                                    }
                                    /*matriculaInd.remove(index);*/
                                    
                                }
                                
                            });
                            setGraphic(chk);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        System.out.println("Que passa neeeeeeeeeeeeeeeeeeeeeee");
        this.activesColumn.setCellFactory(cellFactoryCheckbox);
        
        this.enrColumnEnr.setCellValueFactory( cellData -> new SimpleStringProperty( cellData.getValue().getAlumno().getNombre()) );
        this.dateColumnEnr.setCellValueFactory( cellData -> new SimpleStringProperty( cellData.getValue().getFecha().toString() ) );
        
        tableView.setItems(this.todosLasMatriculas);
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
    private void okHandleButtonMatriculas(ActionEvent event) {
     //getDatabase().salvar();
        //System.out.println(this.c.size() + " ++++++++++++++++++++++++++++++++++++++++++++++++++++");
        int i = 0;
        ObservableList<Matricula> nueva = FXCollections.observableArrayList();
        
        for(Matricula prev : this.todosLasMatriculas){
          nueva.add(prev);
        }
        for(Matricula prev : nueva){
              if(enr.get(prev.getAlumno().getDni()) == null){
                todosLasMatriculas.remove(i);
            }
            i++;
        }
        cancelButtonHandelMatriculas(event);
    }

    @FXML
    private void cancelButtonHandelMatriculas(ActionEvent event) {
        if( event.getSource() == cancelButtonMatriculas || event.getSource() == okButtonMatriculas ) {
            if(event.getSource() == cancelButtonMatriculas){
                 
                this.todosLasMatriculas.clear();
               for(Matricula prev : matriculasPrevias){
                     this.todosLasMatriculas.add(prev);
                }
            }
            Node n = (Node) event.getSource ();
            n.getScene().getWindow().hide();
            
          
        }
    }
    public ObservableList<Matricula> obsListMatricula(){
        return this.todosLasMatriculas;
    }
    public AccesoaBD getDatabase(){
        return this.db;
    }
    public Curso getCurso(){
        return this.a;
    }
    
}
