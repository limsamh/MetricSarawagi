/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.salimigue.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *Iterative Proportional Fitting implementation (2D for now)
 * @author Salim IGUE
 */
public class IPF {
    //Attributes
    int nbDim;
    HashMap<Integer,List>dataAlgo;
    Double valConv;
    
    public IPF(int arg_nbDim){
        if(arg_nbDim <2) this.nbDim =2; 
        else this.nbDim = arg_nbDim; 
        
        dataAlgo = new HashMap<>();
        valConv=0.;
    }
    
    
     
    
    /**
     * Compute IPF 2D (for now)
     * @param arg_data
     * @param arg_totaux
     * @param arg_gTotal
     * @return 
     */
    public HashMap<Integer,List> computeIPF(HashMap<Integer,List>arg_data, HashMap<Integer,Double>arg_totaux, Double arg_gTotal){
             
       
       List resTemp2=new ArrayList<>();
       Double totTemp;
        
       //Checking if data contains null values 
        dataAlgo= checkMap(arg_data,null);
        
        //int used for the iteration
        int k=1;
        
      //One iteration  
       while(convergeAlgo(dataAlgo,arg_gTotal)){
           
           if(k>1) dataAlgo = transposeMap(dataAlgo);
           System.out.println("---------------------------------------");
           System.out.println("Begin Iteration " + k );
           System.out.println("---------------------------------------");
           
       //Row Iteration
           System.out.println("Row adjustment");
       for(Integer i : dataAlgo.keySet()){
           
           for(Integer j : arg_totaux.keySet()){
               
               
               if(i.equals(j)){
                   
                   Iterator it = dataAlgo.get(i).iterator();
                   while(it.hasNext()){
                       totTemp = arg_totaux.get(j);
                   Double t = calculSomme(dataAlgo.get(i)); 
                   Double cellule = (Double) it.next();
                       System.out.println("Old cell value: " + cellule);
                       
                       Double tp =(cellule/t)*totTemp;
                       System.out.println("Value: " + tp);
                       
                       resTemp2.add(tp);
                       
                   }
                              
               }
           }
       
           dataAlgo.replace(i, resTemp2);
           resTemp2=new ArrayList<>();
       }
       
       System.out.println("---------------------------------------");
       //Column Iteration 
       System.out.println("Column adjustment");
       HashMap<Integer,List>test1 = transposeMap(dataAlgo);
       dataAlgo = test1;
       int l = arg_data.size();
        for(Integer i : test1.keySet()){
           
           for(Integer j : arg_totaux.keySet()){
               
               
               if(i.equals(j-l)){
                   
                   Iterator it = dataAlgo.get(i).iterator();
                   while(it.hasNext()){
                       totTemp = arg_totaux.get(j);
                   Double t = calculSomme(dataAlgo.get(i)); 
                   Double cellule = (Double) it.next();
                       System.out.println("Old cell value: " + cellule);
                       
                       Double tp =(cellule/t)*totTemp;
                       System.out.println("Value: " + tp);
                       
                       resTemp2.add(tp);
                       
                   }
                              
               }
           }
       
           dataAlgo.replace(i, resTemp2);
          resTemp2=new ArrayList<>();
       }
        
        //To complete
        //If it was 3D here we will add another loop for the slice adjustment 
        //If it was 4D here we will add another loop for the stack adjustment 
        
       System.out.println("---------------------------------------");
       System.out.println("End Iteration");
       System.out.println("---------------------------------------");
       
       k++;
       }
       
       
       
        
        
        System.out.println("Number of iteration " + (--k));
        
     return  dataAlgo;  
         
    } 
    /**
     * Return the transpose T of the  map
     * @param arg_data
     * @return 
     */
    public HashMap<Integer,List> transposeMap(HashMap<Integer,List>arg_data){
   
        HashMap<Integer,List>resu = new HashMap<>();
        
        List rtmp = new ArrayList<>();
        int k = arg_data.get(1).size(); 
        int l = 0;
        while(l<k){
        for(Integer i : arg_data.keySet()){
            
            List tmp = arg_data.get(i);
                     
            rtmp.add(tmp.get(l));
        }
         resu.put(l+1, rtmp);
         l++;
         rtmp=new ArrayList<>();
        } 
        
        
        return resu;
       
    }
    
     
    /**
     * Allow us to know when the algorithm converges
     * @param arg_data
     * @param arg_gTotal
     * @return 
     */
    public Boolean convergeAlgo(HashMap<Integer,List>arg_data,Double arg_gTotal){
        
     
        Boolean resu = true;
       
         Double val =0.;
       
        for (Integer i : arg_data.keySet()) {
            Iterator it = arg_data.get(i).iterator();
            while(it.hasNext()){
                val+=(Double)it.next();
              
            }
        }
     
        if(valConv.equals(val) && val.equals(arg_gTotal) ) resu = false;
        valConv = val;
        return resu;
        
        
        
    }
    /**
     * Compute the sum of the given list
     * @param arg_l
     * @return 
     */
    public Double calculSomme(List<Double> arg_l){
        Double resu =0.;
       
        for(Double a : arg_l){
            resu +=a;
        }
        return resu;
    }
    /**
     * Check if the map contains null values and replace them by ones
     * @param arg_data
     * @param tab_taille
     * @return 
     */
    public HashMap<Integer,List> checkMap(HashMap<Integer,List>arg_data, int[] tab_taille){
     HashMap<Integer,List>resu = new HashMap<>();
  
       List rtmp;
      
        for(Integer i : arg_data.keySet()){
            rtmp = arg_data.get(i);
            
            
            for(int j =0 ; j < rtmp.size(); j++)
                if(rtmp.get(j)==null)
                 rtmp.set(j, 1.);
         
           resu.put(i, rtmp);
          
       } 
      
        return resu;
    }
}
