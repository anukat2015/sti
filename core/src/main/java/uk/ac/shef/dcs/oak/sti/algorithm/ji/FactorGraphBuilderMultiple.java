package uk.ac.shef.dcs.oak.sti.algorithm.ji;

import cc.mallet.grmm.types.FactorGraph;
import cc.mallet.grmm.types.Variable;
import uk.ac.shef.dcs.oak.sti.rep.Key_SubjectCol_ObjectCol;
import uk.ac.shef.dcs.oak.sti.rep.LTableAnnotation;

import java.util.*;

/**
 * Created by zqz on 15/05/2015.
 */
public class FactorGraphBuilderMultiple extends FactorGraphBuilder {
    public List<FactorGraph> buildDisconnectedGraphs(LTableAnnotation_JI_Freebase annotation,
                                                     boolean relationLearning,
                                                     String tableId) {
        List<FactorGraph> out=new ArrayList<FactorGraph>();

        FactorGraph graph = new FactorGraph();
        //cell text and entity label
        Map<String, Variable> cellAnnotations = factorBuilderCell.addFactors(annotation, graph,
                typeOfVariable);
        //column header and type label
        Map<Integer, Variable> columnHeaders = factorBuilderHeader.addFactors(annotation, graph,
                typeOfVariable);
        //column type and cell entities
        new FactorBuilderHeaderAndCell().addFactors(cellAnnotations,
                columnHeaders,
                annotation,
                graph,tableId);
        //relation and pair of column types
        if (relationLearning) {
            Map<String, Variable> relations = factorBuilderHeaderAndRelation.addFactors(
                    columnHeaders,
                    annotation,
                    graph,
                    typeOfVariable,tableId
            );

            //relation and entity pairs
            new FactorBuilderCellAndRelation().addFactors(
                    relations,
                    cellAnnotations,
                    annotation,
                    graph,
                    factorBuilderHeaderAndRelation.getRelationVarOutcomeDirection(),tableId
            );
        }
        return out;
    }

    private Map<String, Set<Integer>> computeDisconnectedTableColumns(LTableAnnotation annotation,
                                                                      boolean relationLearning) {
        Map<String, Set<Integer>> result = new HashMap<String, Set<Integer>>();
        int counter = 0;
        String key = null;
        if (relationLearning) {
            for (Key_SubjectCol_ObjectCol rel : annotation.getRelationAnnotations_across_columns().keySet()) {
                Set<Integer> components = findContainingGraph(result, rel.getSubjectCol(), rel.getObjectCol());
                if (components == null) {
                    components = new HashSet<Integer>();
                    key = "part" + counter;
                    counter++;
                }
                components.add(rel.getSubjectCol());
                components.add(rel.getObjectCol());

                result.put(key, components);
            }
            return result;
        } else {
            for (int c = 0; c < annotation.getCols(); c++) {
                if (annotation.getHeaderAnnotation(c).length != 0) {
                    Set<Integer> cols = new HashSet<Integer>();
                    cols.add(c);
                    result.put(String.valueOf(c), cols);
                }
            }
            return result;
        }
    }

    private Set<Integer> findContainingGraph(Map<String, Set<Integer>> parts, int col1, int col2) {
        for (Set<Integer> values : parts.values()) {
            if (values.contains(col1) || values.contains(col2))
                return values;
        }
        return null;
    }
}
