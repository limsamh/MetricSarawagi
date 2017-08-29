/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.salimigue.main;

import fr.univ_tours.salimigue.algo.IPF;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *Test class for IPF
 * @author Salim IGUE
 */
public class TestIPF {
    
    
    public static void main(String[] args) {
        
        IPF t_ipf = new IPF(0);
        
        HashMap<Integer,List>arg_data = new HashMap<>();
        List<Double> arg_data_double1 = new ArrayList<>();
        arg_data_double1.add(6.);
        arg_data_double1.add(6.);
        arg_data_double1.add(3.);
        List<Double> arg_data_double2 = new ArrayList<>();
        arg_data_double2.add(8.);
        arg_data_double2.add(10.);
        arg_data_double2.add(10.);
        List<Double> arg_data_double3 = new ArrayList<>();
        arg_data_double3.add(9.);
        arg_data_double3.add(10.);
        arg_data_double3.add(9.);
        List<Double> arg_data_double4 = new ArrayList<>();
        arg_data_double4.add(3.);
        arg_data_double4.add(14.);
        arg_data_double4.add(8.);
        
        arg_data.put(1, arg_data_double1);
        arg_data.put(2, arg_data_double2);
        arg_data.put(3, arg_data_double3);
        arg_data.put(4, arg_data_double4);
       
        
        HashMap<Integer,Double>arg_totaux = new HashMap<>();
        arg_totaux.put(1, 20.);
        arg_totaux.put(2, 30.);
        arg_totaux.put(3, 35.);
        arg_totaux.put(4, 15.);
        arg_totaux.put(5, 35.);
        arg_totaux.put(6, 40.);
        arg_totaux.put(7, 25.);
                 
        
        HashMap<Integer,List> res = t_ipf.computeIPF(arg_data, arg_totaux, 100.);
        System.out.println("Map results");
        for(Integer i : res.keySet()){
            
            Iterator it = res.get(i).iterator();
            
            while(it.hasNext())
                System.out.println("Cell value : " + it.next());
        }
    }
}
