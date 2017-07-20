/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package fr.univ_tours.salimigue.main;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIsRefine;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIterativeCommonAggregation;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.ExplorationScore;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.ExplorationScorer;
import fr.univ_tours.li.mdjedaini.ideb.io.SaikuLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.salimigue.mapping.ResultMapping;
import fr.univ_tours.salimigue.metric.MetricSarawagi;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mondrian.olap.Cell;

/**
 * Test class used for Sarawagi metric 
 * @author Salim IGUE
 */
public class TestMetric {
    
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        
        TestMetric test = new TestMetric();
 
         test.evaluateDopanLog("res/dopan/cleanLogs/dibstudent04--2016-09-25--21-46.log");

    }
    
    /**
     * Evaluates a Dopan session.
     * @param arg_logFile can be a file or a directory. If it is a file, all the files contained in it are evaluated.
     */
    public void evaluateDopanLog(String arg_logFile) {
        //Connection parameters
        Parameters params   = new Parameters();
        
        params.driver           = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        params.jdbcUrl          = "jdbc:sqlserver://10.195.25.10:54027";
        params.user             = "mahfoud";
        params.password         = "AvH4My327-vd";
        params.schemaFilePath   = "res/dopan/dopan_dw3.xml";
        
        //Benchmark initialization
        BenchmarkEngine be  = new BenchmarkEngine(params);
        
        be.initDatasource();
        be.initDefaultModules();
        
        ExplorationScorer es = new ExplorationScorer(be);
        
        es.addMetric(new MetricSarawagi(be));
        es.addMetric(new MetricIsRefine(be));
        
        //Random session from the log
        SaikuLogLoader  sll = new SaikuLogLoader(be, arg_logFile);
        Log myLog           = sll.loadLog();
        
        System.out.println(myLog);
     
        Session sRandom = myLog.pickRandomSession();
        
        //for(Session s_tmp : myLog.getSessionList()) {
           // Exploration e   = new Exploration(be, s_tmp);
             Exploration e   = new Exploration(be, sRandom);   
            ExplorationScore trs = es.score(e);
                
          //  System.out.println("Session: " + s_tmp.getMetadata("name"));
            System.out.println(trs);
      //  }
        
        
        /*
        
        //Random session from the log
        Session s = myLog.pickRandomSession();
        //Print the chosen query
        System.out.println("SessionID picked " + s.getSid());
        //Random query
        Query qs = s.pickRandomQuery();
        //Print the chosen query
        System.out.println("QueryID picked " + qs.getQid() + " Query value : " + qs.toString());
        
        //Get the result
        Result res = qs.getResult();
        
        //Creating a ResultMapping object
        ResultMapping rMatrix = new ResultMapping(res);
        
        
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Test du mapping Coordonnées et cellules");
        
        //Testing the Coordinates to Cell method
        HashMap<List, Cell> cToCell= rMatrix.getResultCoordToCell();
        
        Iterator itCe = cToCell.keySet().iterator();
        while(itCe.hasNext()){
            
            Cell c1 = cToCell.get(itCe.next());
            
            System.out.println(" La valeur de la cellule est: " + c1.getValue() +" ");
            
        }
        
        
        System.out.println("-----------------------------------------------------------------------");
        //Testing the aggregate cells
        Map<EAB_Cell,Set<EAB_Cell>> cAggregate= rMatrix.getCellToAggregate();
        
        Iterator it = cAggregate.keySet().iterator();
        
        while(it.hasNext()){
            
            Object cle = it.next();
            Set<EAB_Cell> scell = cAggregate.get(cle);
            
            System.out.println(" La valeur de la cellule est :" + cle.toString() +" ");
            
            Iterator its = scell.iterator();
            
            while(its.hasNext()){
                System.out.println(" La valeur de l'aggregat pour la cellule: "+ cle.toString() +
                        " est " + its.next() +" ");
                
            }
        }
        
        
        System.out.println("-----------------------------------------------------------------------");
        
        Collection<EAB_Cell> cell1 = res.getCellList().getCellCollection();
        
        Set<EAB_Cell> resuSet = rMatrix.getConstraintsCell(cell1.iterator().next());
        
        Iterator rT = resuSet.iterator();
        
        while (rT.hasNext()){
            System.out.println("La liste des contraintes pour la cellule est " + rT.next());
        }
        
        System.out.println("------------------------------------------------------------------------------------------------------");
        
        
        
        //Testing the inverse mapping
        Map<Set<EAB_Cell>, EAB_Cell> myMap  = rMatrix.getAggregateToCells();
        
        
        Iterator inv = myMap.keySet().iterator();
        int i = 0;
        
        while(inv.hasNext()){
            
            EAB_Cell smap = myMap.get(inv.next());
            
            System.out.println(" Pour le set n° "  + i + " de contrainte, la  cellule aggrégé est :" + smap.getValue() +" ");
            
            i++;
            
            
        }
        */
        
        
        
    }
    
}
