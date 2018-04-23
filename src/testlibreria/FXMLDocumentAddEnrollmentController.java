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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import modelo.Alumno;
import modelo.Curso;
import modelo.Matricula;

/**
 * FXML Controller class
 *
 * @author icemole
 */
public class FXMLDocumentAddEnrollmentController implements Initializable {

    
    
    
    @FXML
    private Button cancelButtonEnr;
    @FXML
    private Button okButtonEnr;
    @FXML
    private TableColumn<Curso, CheckBox> enrollStudentColumn;
    @FXML
    private TableColumn<Curso, String> courseNameColumn;
    @FXML
    private TableColumn<Curso, String> courseDateColumn;
    @FXML
    private TableColumn<Curso, String> courseTimeColumn;
    @FXML
    private Label control;
    @FXML
    private TableView<Curso> tableView;
    
    private AlumnoEquals alumno;
    private AccesoaBD abd;
    private ObservableList<Curso> cursosEnrol;
    private ObservableList<Curso> todosLosCursos;
    private ObservableList<Matricula> matriculas;
    
    Map<LocalTime, CursoEquals> enr = new Hashtable<>();
    
    private ObservableList<Curso> cursosEnrolPrevio;
    private ObservableList<Matricula> matriculasPrevio;
    private Hashtable<LocalTime, CursoEquals> hashTablePrevio;
    private AlumnoEquals alumnoPrev;
    private AccesoaBD  abdPrevia;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    protected void initializeEnr( AlumnoEquals a, Hashtable<LocalTime, CursoEquals> h, ObservableList<Curso> c,AccesoaBD bd, 
            ObservableList<Matricula> m,ObservableList<Curso> todosLosCursos)
        {
     
        
        
        this.todosLosCursos = todosLosCursos;
        this.matriculas = m;
        this.cursosEnrol = c;
        this.enr = h;
        this.alumno = a;
        this.abd = bd;
        this.cursosEnrolPrevio = (ObservableList)FXCollections.observableArrayList();
        this.matriculasPrevio = (ObservableList)FXCollections.observableArrayList();
        this.hashTablePrevio = new Hashtable<>();

        for(Curso curso: this.cursosEnrol){
                    this.cursosEnrolPrevio.add(curso);
                }
        for(Matricula matricula: this.matriculas){
                   this.matriculasPrevio.add(matricula);
        }
        
       
        
        for(Map.Entry<LocalTime, CursoEquals> entry : enr.entrySet()) {
            System.out.println(entry.getValue().getTitulodelcurso());
            hashTablePrevio.put(entry.getKey(),entry.getValue());
        }
                
       
        enrollStudentColumn.setCellValueFactory( cellData -> {
                return new SimpleObjectProperty<CheckBox>( new CheckBox("") );
            });
        
        
        Callback<TableColumn<Curso, CheckBox>, TableCell<Curso, CheckBox>> cellFactoryCheckbox = new Callback<TableColumn<Curso, CheckBox>, TableCell<Curso, CheckBox>>() {
            @Override
            public TableCell call(final TableColumn<Curso, CheckBox> param) {
                final TableCell<Curso, CheckBox> cell = new TableCell<Curso, CheckBox>() {

                    CheckBox chk = new CheckBox("");
                    
                    @Override
                    public void updateItem(CheckBox item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            CursoEquals cursoEquals = new CursoEquals( getTableView().getItems().get( getIndex() ) );
                            CursoEquals cursoHash = enr.get( cursoEquals.getHora() );
                            if(  cursoHash != null && cursoHash.equals(cursoEquals) )
                            {
                               System.out.println("Holeaaaaa hashtableee" +  cursoHash.getHora().toString()  );
                               chk.setSelected( true );
                            }
                            
                            
                            chk.setOnAction(event -> {
                                
                                CursoEquals co = new CursoEquals( getTableView().getItems().get( getIndex() ) );
                                Curso cDeverdad = getTableView().getItems().get(getIndex());
                                ObservableList<Curso> cursoInd = getObsListCurs();
                                ObservableList<Matricula> mat = getMatriculaCurs();
                                AccesoaBD database = getDatabase();
                                Matricula nueva = new Matricula( co.getFechainicio(), co, getAlumno() );
                                
                                if( chk.isSelected() ) {
                                    
                                    

                                    if( enr.get( co.getHora() ) != null )
                                    {
                                        control.setText( "Course selected overlaps with\n" + enr.get( co.getHora() ).getTitulodelcurso() );
                                        chk.setSelected( false );
                                    }
                                    
                                    else if( abd.getAlumnosDeCurso((Curso)co) != null
                                            && abd.getAlumnosDeCurso((Curso)co).size() > co.getNumeroMaximodeAlumnos() )
                                    {
                                        control.setText( "The course has the maximum students." );
                                    }
                                    else {
                                        
                                        m.add(nueva);
                                  
                                        enr.put(co.getHora(),co);
                                        control.setText( "" );
                                        cursoInd.add((Curso)co);
                                       // co.setEnrollmentsStudent(nueva);
                                        getDatabase().getMatriculasDeCurso(cDeverdad).add(nueva);
                                        getDatabase().getMatriculasDeCurso(co).add(nueva);
                                        
                                    }
                                }
                                else {
                                     
                                    MatriculaEquals me = new MatriculaEquals( new Matricula(co.getFechainicio(), co, getAlumno()), getDatabase() );
                                    int index = 0;
                                   
                                    for(Matricula mo : m){
                                        
                                        if(me.equals(mo)){
                                            
                                            cursoInd.remove((Curso)co);
                                            getDatabase().getMatriculasDeCurso(co).remove(nueva);
                                            getDatabase().getMatriculasDeCurso(cDeverdad).remove(nueva);
                                            
                                            enr.remove(co.getHora());
                                            //co.getEnrollmentsStudent().remove(me);
                                            break;
                                        }
                                        index++;
                                        
                                    }
                                    m.remove(index);
                                    
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
        enrollStudentColumn.setCellFactory(cellFactoryCheckbox);
        
        courseNameColumn.setCellValueFactory( cellData -> 
                new SimpleStringProperty( cellData.getValue().getTitulodelcurso() ) );
        courseDateColumn.setCellValueFactory( cellData -> new SimpleStringProperty( cellData.getValue().getFechainicio().toString() ) );
        courseTimeColumn.setCellValueFactory( cellData -> new SimpleStringProperty( cellData.getValue().getHora().toString() ) );
        
        ObservableList<TableColumn<Curso, ?>> auxTables = 
            FXCollections.observableArrayList( enrollStudentColumn, courseNameColumn, courseDateColumn, courseTimeColumn );
        
        tableView.getColumns().setAll( auxTables );
        tableView.setItems( (ObservableList) this.todosLosCursos );
    }
    
    public AlumnoEquals getAlumno() {
        return this.alumno;
    }
    public ObservableList<Curso> getObsListCurs(){
        return this.cursosEnrol;
    }
    public AccesoaBD getDatabase() {
        return this.abd;
    }
    public ObservableList<Matricula> getMatriculaCurs(){
        return this.matriculas;
    }
    
    @FXML
    private void handleCancelEnr(ActionEvent event) {
        if( event.getSource() == cancelButtonEnr || event.getSource() == okButtonEnr ) {
            if(event.getSource() == cancelButtonEnr){
                this.matriculas.clear();
                this.cursosEnrol.clear();
                this.enr.clear();
                for(Curso curso: this.cursosEnrolPrevio){
                    this.cursosEnrol.add(curso);
                }
                for(Matricula matricula: this.matriculasPrevio){
                    this.matriculas.add(matricula);
                    getAlumno().getEnrollmentsStudent().add(matricula);

                }
                for(Map.Entry<LocalTime, CursoEquals> entry : hashTablePrevio.entrySet()) {
                    enr.put(entry.getKey(), entry.getValue());
                }
                this.alumno.setAllEnrollments(matriculas);
                
            }
            Node n = (Node) event.getSource ();
            n.getScene().getWindow().hide();
            
          
        }
    }

    @FXML
    private void handleOkEnr(ActionEvent event) {
        //getDatabase().salvar();
        System.out.println(this.alumno.getEnrollmentsStudent().size() + " ++++++++++++++++++++++++++++++++++++++++++++++++++++");
        
        handleCancelEnr(event);
    }
}
