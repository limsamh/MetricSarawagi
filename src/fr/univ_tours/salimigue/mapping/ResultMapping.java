/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package fr.univ_tours.salimigue.mapping;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mondrian.olap.Cell;
import mondrian.olap.Position;

/**
 * This class is used to gather all the informations needed for one exploration result
 * @author Salim IGUE
 */
public class ResultMapping {
    
    Result res;
       
    HashMap<List,Cell> resultCoordToCell ;
    
    HashMap<Cell,List> resultCellToCoord;
    
    HashMap<EAB_Cell, Set<EAB_Cell>> cellToAggregate ;
    
    HashMap<Set<EAB_Cell>, EAB_Cell> aggregateToCells;
    
    /**
     *
     * @param res
     */
    public ResultMapping(Result res) {
        this.res = res;        
        this.init();
    }
     /**
      * Initialization method 
      */
    private void init(){
      
        resultCoordToCell = new HashMap<>();
        resultCellToCoord = new HashMap<>();
        this.computeMapping();
        cellToAggregate = this.getAggregates();
        aggregateToCells = (HashMap<Set<EAB_Cell>, EAB_Cell>) this.getConstraints();
    }

    public HashMap<EAB_Cell, Set<EAB_Cell>> getCellToAggregate() {
        return cellToAggregate;
    }

    public HashMap<Set<EAB_Cell>, EAB_Cell> getAggregateToCells() {
        return aggregateToCells;
    }
    
    /**
     *
     * @return
     */
    public Result getRes() {
        return res;
    }
    
    /**
     *
     * @param res
     */
    public void setRes(Result res) {
        this.res = res;
    }
    
   
    
    /**
     *
     * @return
     */
    public HashMap<List, Cell> getResultCoordToCell() {
        return resultCoordToCell;
    }
    
    /**
     *
     * @return
     */
    public HashMap<Cell, List> getResultCellToCoord() {
        return resultCellToCoord;
    }
    
    /**
     *
     */
    private void computeMapping(){
        
        mondrian.olap.Result mondrianR = res.getMondrianResult();
        int [] tabResu = new int[2];
        
        //Ce code ne marchera que si on a deux axes
        if(mondrianR.getAxes().length ==2){
            
            
            List<Position> listC = mondrianR.getAxes()[0].getPositions();
            List<Position> listL = mondrianR.getAxes()[1].getPositions();
            
       
            for(int i =0 ; i< listC.size(); i++){
                
                tabResu[0] = i;
                
                
                for(int k =0 ; k< listL.size(); k++){
                    
                    tabResu[1] = k;
                    
                    
                    resultCellToCoord.put(mondrianR.getCell(tabResu),mondrianR.getCell(tabResu).getCoordinateList());
                    
                    resultCoordToCell.put(mondrianR.getCell(tabResu).getCoordinateList(), mondrianR.getCell(tabResu));
                    
                
                    
                }
                
            }
        }
        else{
            
            System.out.println(" Le resultat contient plus de deux axes. ");
            
        }
        
        
        
    }
      
    
    /**
     * Return for each cell a set of aggregates cells (List of constraints)
     * @return 
     */
    private HashMap<EAB_Cell, Set<EAB_Cell>> getAggregates(){
        
        HashMap<EAB_Cell, Set<EAB_Cell>> myMap  = new HashMap<>();
        
        
        for(EAB_Cell c_tmp : res.getCellList().getCellCollection()) {
            
            Set<EAB_Cell> set_tmp   = new HashSet<>();
            
            for(EAB_Hierarchy h_tmp : res.getCube().getHierarchyList()) {
                
                if(!c_tmp.getMemberByHierarchy(h_tmp).isAll()) {
                    
                    Collection<EAB_Cell> new_cell   = new HashSet<>(c_tmp.rollOnHierarchy(h_tmp));
                    set_tmp.addAll(new_cell);
                    
                }
                
            }
            
            myMap.put(c_tmp, set_tmp);
          
        }
        
        return myMap;
    }
    /**
     * Return a map containing constraints of each aggregates rows
     * @return 
     */
    private Map<Set<EAB_Cell>, EAB_Cell> getConstraints(){
        
        HashMap<EAB_Cell, Set<EAB_Cell>> aggregates =  this.getAggregates();
        
        HashMap<Set<EAB_Cell>, EAB_Cell> myMap = new HashMap<>();
        for(Map.Entry<EAB_Cell, Set<EAB_Cell>> entry : aggregates.entrySet()){
            myMap.put(entry.getValue(), entry.getKey());
        }
         
        return myMap;
    }
    
    /**
     * Return a Set of cells according to an aggregate row
     * @param c1
     * @return the set of cells
     */
    public Set<EAB_Cell> getConstraintsCell(EAB_Cell c1){
     
        Map<Set<EAB_Cell>, EAB_Cell> myMap  =this.getConstraints();
        
        
        Set<EAB_Cell> resuSet;
        
        Iterator it = myMap.keySet().iterator();
        
        while(it.hasNext()){
           resuSet = (Set<EAB_Cell>) it.next();
           EAB_Cell tmp  = myMap.get(resuSet);
            
            if(tmp.equals(c1)) return resuSet;
            
        }
            
         return null;   
       
    
    }
}
