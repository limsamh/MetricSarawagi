/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.salimigue.main;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.io.SaikuLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 * Test class used for Log loader 
 * @author 21308124t
 */
public class Test_LogLoader {
       /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
        
        Test_LogLoader tll  = new Test_LogLoader();
        
        tll.saikuLogLoader("res/dopan/cleanLogs/dibstudent16--2016-10-04--23-27.log");
        
    }
    
    /**
     * 
     * @param arg_logPath 
     */
    public void saikuLogLoader(String arg_logPath) {
        Parameters params   = new Parameters();      
        params.driver           = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        params.jdbcUrl          = "jdbc:sqlserver://10.195.25.10:54027";
        params.user             = "mahfoud";
        params.password         = "AvH4My327-vd";
        params.schemaFilePath   = "res/dopan/dopan_dw3.xml";
        
        BenchmarkEngine be  = new BenchmarkEngine(params);
        
        // creates and set connection
        be.initDatasource();
        
        // pick a random session from the log and execute it
        SaikuLogLoader  sll = new SaikuLogLoader(be,arg_logPath );
        Log myLog           = sll.loadLog();
        
        System.out.println("Log summary:");
        System.out.println(myLog.logSummary());
        
        for(Session s_tmp : myLog.getSessionList()) {
            
            for(Query qs : s_tmp.getQueryList()){
            
            System.out.println(qs.getQid());
            System.out.println(qs.toString());
            
            }
        }
    }
}
