/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testlibreria;

import accesoaBD.AccesoaBD;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import modelo.Curso;
import modelo.Dias;
import modelo.Matricula;

/**
 *
 * @author icemole
 */
public class CursoEquals extends Curso {
    
    private AccesoaBD bd;
     private List<Matricula> enrollmentsStudent;
     
    public CursoEquals( Curso c)
    {
        
        super( c.getTitulodelcurso(), c.getProfesorAsignado(), c.getNumeroMaximodeAlumnos(), c.getFechainicio(), c.getFechafin(), c.getHora(), c.getDiasimparte(), c.getAula() );
        
    }
    public CursoEquals(Curso c, AccesoaBD bd){
        super( c.getTitulodelcurso(), c.getProfesorAsignado(), c.getNumeroMaximodeAlumnos(), c.getFechainicio(), c.getFechafin(), c.getHora(), c.getDiasimparte(), c.getAula() );
        this.bd = bd;
         enrollmentsStudent = new ArrayList<>();
        for( Matricula m : bd.getMatriculas() )
        {
            if( this.equals( m.getAlumno() ) )  // Quite sure it will work
            {
                enrollmentsStudent.add( m );
            }
        }
    }
     public List<Matricula> getEnrollmentsStudent()
    {
        for( Matricula m : enrollmentsStudent ) { System.out.println("En enrollmentsss  +++++++++++++++ " + m.getCurso().getTitulodelcurso() ); }
        return enrollmentsStudent;
    }
    public List<Matricula> setAllEnrollments(ObservableList l){
        this.enrollmentsStudent = l;
        return this.enrollmentsStudent;
    }
    public void setEnrollmentsStudent(Matricula matricula){
         this.enrollmentsStudent.add(matricula);
    }
    
    @Override
    public boolean equals( Object o )
    {
        if(o instanceof CursoEquals)
        {
            
            CursoEquals c = (CursoEquals) o;
            ArrayList<Dias> l = (ArrayList<Dias>) getDiasimparte().clone();
 
            int size = l.size();
            
            l.removeAll(c.getDiasimparte());
            /*
            (c.getFechainicio().isBefore( this.getFechafin() )
                    || this.getFechainicio().isBefore( c.getFechafin() ) )
*/          boolean noCollision = c.getFechafin().isBefore(this.getFechainicio()) || this.getFechafin().isBefore(c.getFechainicio());
            boolean noCollision2 = this.getFechafin().isBefore(c.getFechainicio()) || c.getFechafin().isBefore(this.getFechainicio());
            boolean theyAreEqual =  this.getFechainicio().isEqual( c.getFechainicio() ) &&  this.getFechafin().isEqual( c.getFechafin() );
           
            return  ( noCollision
                    || noCollision2
                    || theyAreEqual )
                    && l.size() < size
                    && c.getHora().equals( this.getHora() );
            
        }
        
        else if( o instanceof Curso )
        {
            Curso c = (Curso) o;
            ArrayList<Dias> l = (ArrayList<Dias>) getDiasimparte().clone();
            int size = l.size();
            l.removeAll(c.getDiasimparte());
            /*
            (c.getFechainicio().isBefore( this.getFechafin() )
                    || this.getFechainicio().isBefore( c.getFechafin() ) )
*/          boolean noCollision = c.getFechafin().isBefore(this.getFechainicio()) || this.getFechafin().isBefore(c.getFechainicio());
            boolean noCollision2 = this.getFechafin().isBefore(c.getFechainicio()) || c.getFechafin().isBefore(this.getFechainicio());
            boolean theyAreEqual =   this.getFechainicio().isEqual( c.getFechainicio() ) && this.getFechafin().isEqual( c.getFechafin() );
     
            return  ( noCollision
                    || noCollision2
                    || theyAreEqual )
                    && l.size() < size
                    && c.getHora().equals( this.getHora() );
            
        }
       
        return false;
    }
}