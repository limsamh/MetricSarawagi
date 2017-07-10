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
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.salimigue.mapping.ResultMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
     * Computes..
     * @param arg_tr
     * @return
     */
    @Override
    public MetricScore apply(Exploration arg_tr) {
        
        MetricScore result  = new MetricScore(this, arg_tr);
        
        this.sutCellList        = arg_tr.getWorkSession().getCellList();
        
        this.cellsToPredict  = new HashMap<>();
        
        
        
        //Liste qui contiendra les scores finaux
        List<Double> queryScoreList  = new ArrayList<>();
        
        //Parcours de toutes les requêtes de l'exploration
        for(Query q_tmp : arg_tr.getWorkSession().getQueryList()) {
            
            queryScoreList.add(this.applyOnQuery(q_tmp, arg_tr));
        }
        
        result.score = new KL_Divergence().compute(queryScoreList, queryScoreList);
        
        result.addScoreList(queryScoreList);
        
        return result;
    }
    
    
    /**
     * Computes
     * @param arg_q
     * @param arg_tr
     * @return
     */
    public Double applyOnQuery(Query arg_q, Exploration arg_tr) {
        
        HashMap <EAB_Cell, Double> cellPredictionQuery = new HashMap<>();
        ResultMapping r = new ResultMapping(arg_q.getResult());
        
        HashMap<EAB_Cell, Set<EAB_Cell>> aggregates = r.getCellToAggregate();
        
        Double result   = 0.;
        
        
        
        CellList currentCells = arg_q.getResult().getCellList();
        
        
        
        cellPredictionQuery = preRemplissage(arg_q);
        
        Double score = 0.;
        
        for(EAB_Cell cCur : currentCells.getCellCollection() ){
            Double temp;
            //Calcul de l'aggrégat à comparer
            
            //liste des contraintes et l'aggregat predit
            result =  computeRepartition(cCur, aggregates.get(cCur));
            
            this.cellsToPredict.put(cCur, cCur.getValueAsDouble());
            
            temp = result-cCur.getValueAsDouble();
            
            score += temp;
            System.out.println("Actual: " + cCur.getValueAsDouble() + " Expected " + result );
            //Remplissage de la prédiction
            cellPredictionQuery.put(cCur, result);
        }
        
        int size = currentCells.getCellCollection().size();
        
        
        result = score/size ;
      
        System.out.println(" Prediction Query : " + cellPredictionQuery);
  
        
        r = null;
        return result;
    }
    
    
    /**
     * Cette méthode permet de faire le pré-remplissage
     * @param arg_q
     * @return
     */
    public HashMap<EAB_Cell, Double> preRemplissage(Query arg_q){
        
        HashMap<EAB_Cell, Double> resu = new HashMap<>();
        
        
        for( EAB_Cell cell : arg_q.getResult().getCellList().getCellCollection()){
            
            if(this.cellsToPredict.containsKey(cell))
                
                resu.put(cell, cell.getValueAsDouble());
            
            
            
        }
        
        
        return resu;
        
    }
    
    /**
     * Cette méthode calcule la somme des cellules déjà vues
     * @param arg_c
     * @param list_cell
     * @return
     */
    public Double calculAggregat(EAB_Cell arg_c,  Set<EAB_Cell> list_cell){
        Double somme = 0.;
        
        
        
        if(this.cellsToPredict .containsKey(arg_c)){
            
            somme = this.cellsToPredict .get(arg_c);
        }
        else{
            
            //temporaire
            somme = 100.;
        }
        
        
        
        
        return somme;
        
    }
    
    
    
    
    /**
     *
     * @param cAgg
     * @param setConst
     * @return
     */
    public Double computeRepartition(EAB_Cell cAgg, Set<EAB_Cell> setConst){
        Double resu;
        Double valeurAggregee;
        Integer nbChild = 1;
        
        
        //Initialisation de la la valeur aggrégée
        
        if(this.sutCellList.contains(cAgg)) {
            valeurAggregee = cAgg.getValueAsDouble();
        } else {
            //temporaire
            valeurAggregee = 100.;
        }
        
        Double aggregat = calculAggregat(cAgg, setConst);
        
        
        
        Iterator<EAB_Cell> it_cell  = setConst.iterator();
        
        if(setConst.size()>= 2){
            
            
            
            EAB_Cell c1 = it_cell.next();
            EAB_Cell c2 = it_cell.next();
            
            //La hierachie differentielle
            EAB_Hierarchy h_diff = c1.getDifferentialHierarchyList(c2).iterator().next();
            
            if(!c1.getMemberByHierarchy(h_diff).isAll())
                nbChild = c1.getMemberByHierarchy(h_diff).getParentMember().getChildren().size();
            
        }
        
        resu  = (valeurAggregee - aggregat) / nbChild;
        
        
        return resu;
        
        
    }
    
}
