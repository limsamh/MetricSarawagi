/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package fr.univ_tours.salimigue.metric;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.misc.KL_Divergence;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryMdx;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This metric evaluates ......
 * @author Salim IGUE
 */
public class MetricSarawagi extends Metric{
    
    CellList sutCellList;
    
    HashMap<EAB_Cell, Double> cellsToPredict ;
    
    
    /**
     *
     * @param arg_be
     */
    public MetricSarawagi(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Sarawagi";
        this.description    = "Evaluates ...";
    }
    /**
     * Computes the Sarawagi metric for the whole exploration
     * @param arg_tr
     * @return a MetricScore
     */
    @Override
    public MetricScore apply(Exploration arg_tr) {
        
        MetricScore result  = new MetricScore(this, arg_tr);
        
        this.sutCellList        = arg_tr.getWorkSession().getCellList();
        
        this.cellsToPredict  = new HashMap<>();
   
        //This list will contain the final scores
        List<Double> queryScoreList  = new ArrayList<>();
        
        //For the whole exploration
        for(Query q_tmp : arg_tr.getWorkSession().getQueryList()) {
            System.out.println("-- Requete numero:  " + q_tmp.getQid());
            
            queryScoreList.add(this.applyOnQuery(q_tmp, arg_tr));
      
        }
        
        
        result.score = Stats.average(queryScoreList);
       
        result.addScoreList(queryScoreList);
        
        return result;
    }
    
    
    /**
     * Computes the Sarawagi metric for one query
     * @param arg_q
     * @param arg_tr
     * @return the score for the query
     */
    public Double applyOnQuery(Query arg_q, Exploration arg_tr) {
        //Listes de scores nécessaires pour KL Divergence
        List<Double> queryScorePredic  = new ArrayList<>();
        List<Double> queryScoreActual  = new ArrayList<>();
        
        //Total
        Double totalReq = 0.0;
        Double result;
        
        HashMap <EAB_Cell, Double> cellPredictionQuery;
        HashMap <EAB_Cell, Double> cellPredictionTemp;
        
     
        
        //On essaie d'obtenir le total pour chaque requête
        CellList currentCells = arg_q.getResult().getCellList();
        
        //Calcul du total de la requete
        Double totalCube = computeTotalCube(arg_q.getResult());
        
        
        
        //Phase Préremplissage
        cellPredictionQuery = preRemplissage(arg_q);
        //Application de la première partition
        cellPredictionTemp =  computePartition(cellPredictionQuery, totalCube);
        
        
   
        //Mise à jour des cellules déjà vues
        for(EAB_Cell cCur : currentCells.getCellCollection() ){
            
            this.cellsToPredict.put(cCur, cCur.getValueAsDouble());
            //Ajout des valeurs réelles 
            queryScoreActual.add(cCur.getValueAsDouble());
            
        }
        
        
        for (EAB_Cell ce : cellPredictionTemp.keySet()) {
            Double temp = cellPredictionTemp.get(ce);
            queryScorePredic.add(temp);
        }
     
        for(int j = 0 ; j< queryScoreActual.size(); j++){
            System.out.println(" QueryScoreActual: " + queryScoreActual.get(j) + " | "+ "QueryScorePredicted : " + queryScorePredic.get(j) );
        }
      
        
        
        KL_Divergence k = new KL_Divergence();
        result = k.computeNormalized(queryScoreActual,queryScorePredic) ;
      
        System.out.println("-- Score: " + result);
        return result;
    }
    
    
    /**
     * Cette méthode permet de faire le pré-remplissage
     * @param arg_q
     * @return une liste de cellules avec des valeurs pré-remplies
     */
    public HashMap<EAB_Cell, Double> preRemplissage(Query arg_q){
        
        HashMap<EAB_Cell, Double> resu = new HashMap<>();
        
        
        for( EAB_Cell cell : arg_q.getResult().getCellList().getCellCollection()){
            
            if(this.cellsToPredict.containsKey(cell))
                
                resu.put(cell, cell.getValueAsDouble());
            else
                resu.put(cell, null);
            
            
        }
        
        
        return resu;
        
    }
    
    /**
     * 
     * @param arg_predic
     * @param arg_total
     * @return 
     */
    public HashMap <EAB_Cell, Double> computePartition(HashMap <EAB_Cell, Double> arg_predic, Double arg_total){
        
        HashMap<EAB_Cell, Double> resu = new HashMap<>();
        
        //Utilisé pour avoir le nombre de cellules vides
        int nb=0;
        //Contient la somme des cellules déjà vues
        Double sommeNonVide =0.0;
        for(EAB_Cell ce : arg_predic.keySet()){
            
            if (arg_predic.get(ce)==null) {
                nb++;
            }
            else
                sommeNonVide+=arg_predic.get(ce);
        }
        
        
        //On refait un parcourt
        for(EAB_Cell ce : arg_predic.keySet()){
            
            if (arg_predic.get(ce)==null) {
               
                resu.put(ce, (arg_total-sommeNonVide)/nb);
            }
            else
                resu.put(ce, arg_predic.get(ce));
        }
        
       
        return resu;
    }
    

    /**
     * 
     * @param arg_r
     * @return 
     */
    public Double computeTotalCube(Result arg_r){

        QueryMdx qMdx = new QueryMdx(arg_r.getCube(), arg_r.getQuery().toString());
        
        CellList cCell =qMdx.execute(Boolean.TRUE).getCellList();
        Double resu =0.0;      
        for(EAB_Cell ce : cCell.getCellCollection()){
            
            resu += ce.getValueAsDouble();
        }
        
        return resu;
    }
    
    /**
     * 
     * @param arg_predic 
     */
    public void computePartition2(HashMap <EAB_Cell, Double> arg_predic){
        
        HashMap<Integer, Double> resu = new HashMap<>();
        HashMap<Set<EAB_Cell>,HashMap<Integer,Double>> myMap = new HashMap<>();
        
     
        for(EAB_Cell ce : arg_predic.keySet()){
            System.out.println("Cellule " + ce.toString());
            
            //On recupère la liste des contraintes
            myMap = getCellConstraints(ce);
          
            System.out.println("Nombre de contraintes :" + myMap.size());
         
    
        }
    
      
    }
    
     /**
     * Return for each cell a set of aggregates cells (One drill up)
     * @param arg_c
     * @return 
     */
    public HashMap<Set<EAB_Cell>,HashMap<Integer,Double>> getCellConstraints( EAB_Cell arg_c){
     
        HashMap<Set<EAB_Cell>,HashMap<Integer,Double>> resu  = new HashMap<>();
        
        HashMap<Integer,Double> resuTot = new HashMap<>();
      
            Set<EAB_Cell> set_tmp   = new HashSet<>();
            int i =1;
            int j =arg_c.getCube().getHierarchyList().size();
            int k = 0;
            for(EAB_Hierarchy h_tmp : arg_c.getCube().getHierarchyList()) {
                
               Double total = 0.0; 
                if(!arg_c.getMemberByHierarchy(h_tmp).isAll()) {
                if(!arg_c.getMemberByHierarchy(h_tmp).isAll()) {
                    
                   
                    System.out.println("Nom de la hierarchie : " + h_tmp.getName());
                    
                    Collection<EAB_Cell> new_cell   = new HashSet<>(arg_c.rollOnHierarchy(h_tmp));
                    set_tmp.addAll(new_cell);
                    
                   for (EAB_Cell ce : new_cell) {
                       total +=ce.getValueAsDouble();
                   }
                  
                   resuTot.put(i, total);
                  i++;
                }
                
               resu.put(set_tmp,resuTot);
                }
            k++;
         
        }
        
        return resu;
    }
   
    /**
     * Cette méthode calcule la somme des cellules déjà vues
     * @param arg_c
     * @return la somme des cellules déjà vues
     */
    public Double calculAggregat(EAB_Cell arg_c){
        Double somme;
     
        if(this.cellsToPredict .containsKey(arg_c)){
            
            somme = this.cellsToPredict .get(arg_c);
        }
        else{
            
            //valeur temporaire
            somme = 100.;
        }
      
        return somme;
        
    }
    
 
   
}
