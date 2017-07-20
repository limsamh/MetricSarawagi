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
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;
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
     * Computes the Sarawagi metric for the whole exploration
     * @param arg_tr
     * @return a MetricScore
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
        Double result   = 0.;
        
        HashMap <EAB_Cell, Double> cellPredictionQuery = new HashMap<>();
        HashMap <EAB_Cell, Double> cellPredictionTemp = new HashMap<>();
        
        //Pour avoir la listes des contraintes pour une cellule
        ResultMapping r = new ResultMapping(arg_q.getResult());
        HashMap<EAB_Cell, Set<EAB_Cell>> aggregates = r.getCellToAggregate();
        
       
        
        //On essaie d'obtenir le total pour chaque requête
        CellList currentCells = arg_q.getResult().getCellList();
        for(EAB_Cell ce : currentCells.getCellCollection()){
            
            totalReq+=ce.getValueAsDouble();
        }
        System.out.println("Total de la requete " + arg_q.getQid() + " est: " + totalReq);
        
        //Phase Préremplissage
        cellPredictionQuery = preRemplissage(arg_q);
        //Application de la première partition
        cellPredictionTemp =  computeFirstPartition(cellPredictionQuery,totalReq);
        
        
        
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
        
        System.out.println("-------Size of queryScorePredicted :" + queryScorePredic.size());
        System.out.println("-------Size of queryScoreActual : " + queryScoreActual.size());
        
        for(int j = 0 ; j< queryScoreActual.size(); j++){
            System.out.println(" QueryScoreActual: " + queryScoreActual.get(j) + " | "+ "QueryScorePredicted : " + queryScorePredic.get(j) );
        }
      
        
        
        KL_Divergence k = new KL_Divergence();
        result = k.computeNormalized(queryScoreActual,queryScorePredic) ;
        System.out.println(" Taille Prediction requête " + cellPredictionQuery.size());
        System.out.println(" Taille prediction exploration " + this.cellsToPredict.size());
        
        System.out.println("-- Score: " + result);
        r = null;
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
                resu.put(cell, 0.0);
            
            
        }
        
        
        return resu;
        
    }
    
    public HashMap <EAB_Cell, Double> computeFirstPartition(HashMap <EAB_Cell, Double> arg_predic, Double arg_total){
        
        HashMap<EAB_Cell, Double> resu = new HashMap<>();
        
        //Utilisé pour avoir le nombre de cellules vides
        int nb=0;
        //Contient la somme des cellules déjà vues
        Double sommeNonVide =0.0;
        for(EAB_Cell ce : arg_predic.keySet()){
            
            if (arg_predic.get(ce).equals(0.0)) {
                nb++;
            }
            else
                sommeNonVide+=arg_predic.get(ce);
        }
        
        
        //On refait un parcourt
        for(EAB_Cell ce : arg_predic.keySet()){
            
            if (arg_predic.get(ce).equals(0.0)) {
                //à améliorer. Prendre en compte les sommes temporaires (autre contraintes) où la cellule n'est pas vide
                resu.put(ce, (arg_total-sommeNonVide)/nb);
            }
            else
                resu.put(ce, arg_predic.get(ce));
        }
        
       
        return resu;
    }
    
    /**
     * Cette méthode permet d'avoir le total des valeurs de cellules en fonction d'une requête.
     * @param arg_q
     * @return le total
     */
    public Double calculTotalReq(Query arg_q){
        Double resu =0.0;
        
        CellList cells = arg_q.getResult().getCellList();
        for(EAB_Cell ce : cells.getCellCollection()){
            
            resu +=ce.getValueAsDouble();
        }
        
        return resu;
        
    }
    
    
    
    
    /**
     * Cette méthode calcule la somme des cellules déjà vues
     * @param arg_c
     * @param list_cell
     * @return la somme des cellules déjà vues
     */
    public Double calculAggregat(EAB_Cell arg_c,  Set<EAB_Cell> list_cell){
        Double somme = 0.;
     
        if(this.cellsToPredict .containsKey(arg_c)){
            
            somme = this.cellsToPredict .get(arg_c);
        }
        else{
            
            //valeur temporaire
            somme = 100.;
        }
      
        return somme;
        
    }
    
    
    
    
    /**
     * Méthode permettant de répartir des sommes partiels aux cellules enfants
     * @param cAgg
     * @param setConst
     * @return la valeur prédicte
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
        
             
        if(setConst.size() <= 1)System.out.println("ATTENTION --------- Une seule contrainte disponible ici");
        
        if(setConst.size()>= 2){
            EAB_Cell c1 = it_cell.next();
            EAB_Cell c2 = it_cell.next();
            
            while(it_cell.hasNext()){
                
                EAB_Hierarchy h_diff = c1.getDifferentialHierarchyList(c2).iterator().next();
                if(!c1.getMemberByHierarchy(h_diff).isAll())
                    nbChild+= c1.getMemberByHierarchy(h_diff).getParentMember().getChildren().size();
                
                c1 =c2;
                
                if(it_cell.hasNext())
                    c2=it_cell.next();
            }
         
            nbChild--;
        }
        
        System.out.println("NBCHILD = " + nbChild);
        
        resu  = (valeurAggregee - aggregat) / nbChild;
        
        
        return resu;
        
        
    }
    
}
