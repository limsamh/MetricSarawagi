/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package fr.univ_tours.salimigue.main;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.ExplorationScore;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.ExplorationScorer;
import fr.univ_tours.li.mdjedaini.ideb.io.SaikuLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.salimigue.metric.MetricSarawagi;

/**
 * Test class used for Sarawagi metric 
 * @author Salim IGUE
 */
public class TestMetricSarawagi {
    
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        
        TestMetricSarawagi test = new TestMetricSarawagi();
 
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
        //es.addMetric(new MetricIsRefine(be));
        
        //Random session from the log
        SaikuLogLoader  sll = new SaikuLogLoader(be, arg_logFile);
        Log myLog           = sll.loadLog();
        
        System.out.println(myLog);
     
        Session sRandom = myLog.pickRandomSession();
        
             Exploration e   = new Exploration(be, sRandom);   
            ExplorationScore trs = es.score(e);
                
            System.out.println(trs);
      
        
      
        
        
        
    }
    
}
