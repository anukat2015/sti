package uk.ac.shef.dcs.oak.lodie.table.interpreter.baseline;

import uk.ac.shef.dcs.oak.lodie.table.interpreter.content.KBSearcher;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.interpret.ClassificationScorer;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.interpret.ColumnInterpreter_relDepend;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.selector.LTableContentCell_Sampler_nonEmpty;
import uk.ac.shef.dcs.oak.lodie.table.rep.*;
import uk.ac.shef.dcs.oak.triplesearch.EntityCandidate;
import uk.ac.shef.dcs.oak.util.ObjObj;

import java.io.IOException;
import java.util.*;

/**
 */
public class Base_TM_no_Update_ColumnLearner {

    private KBSearcher kbSearcher;
    private Base_TM_no_Update_Disambiguator disambiguation_learn;
    private Base_TM_no_Update_ClassificationScorer classifier_learn;


    public Base_TM_no_Update_ColumnLearner(
            KBSearcher candidateFinder,
            Base_TM_no_Update_Disambiguator disambiguation_learn,
            Base_TM_no_Update_ClassificationScorer algorithm) {
        this.kbSearcher = candidateFinder;
        this.disambiguation_learn = disambiguation_learn;
        this.classifier_learn = algorithm;

    }

    public void learn(LTable table, LTableAnnotation table_annotation, int column, Integer... skipRows) throws IOException {

        //1. gather list of strings from this column to be interpreted

        //3. score column and also disambiguate initial rows in the selected sample
        Map<Integer, List<ObjObj<EntityCandidate, Map<String, Double>>>> candidates_and_scores_for_each_row =
                new HashMap<Integer, List<ObjObj<EntityCandidate, Map<String, Double>>>>();
        Set<HeaderAnnotation> headerAnnotationScores = new HashSet<HeaderAnnotation>();

        int countRows = 0;
        Map<Object, Double> state = new HashMap<Object, Double>();

        for (int row_index = 0; row_index < table.getNumRows(); row_index++) {

            /* if(row_index<39)
            continue;*/
            /* if(row_index==13)
            System.out.println();*/
            countRows++;
            //find candidate entities
            LTableContentCell tcc = table.getContentCell(row_index, column);
            System.out.println("\t>> Classification-LEARN, row " + row_index + "," + tcc);
            if (tcc.getText().length() < 2) {
                System.out.println("\t\t>>> Very short text cell skipped: " + row_index + "," + column + " " + tcc.getText());
                continue;
            }

            boolean skip = false;
            for (int row : skipRows) {
                if (row == row_index) {
                    skip = true;
                    break;
                }
            }

            List<ObjObj<EntityCandidate, Map<String, Double>>> candidates_and_scores_on_this_row;
            if (skip) {
                candidates_and_scores_on_this_row = collect_existing(table_annotation, row_index, column);
            } else {
                List<EntityCandidate> candidates = kbSearcher.find_matchingEntitiesForCell(tcc);
                //do disambiguation scoring
                candidates_and_scores_on_this_row =
                        disambiguation_learn.disambiguate_learn(
                                candidates, table, row_index, column
                        );
                candidates_and_scores_for_each_row.put(row_index, candidates_and_scores_on_this_row);
            }
            //todo: wrong, state should be created based on the map object
            //run algorithm to learn column typing; header annotation scores are updated constantly, but supporting rows are not.
            state = update_column_class(
                    classifier_learn.score(candidates_and_scores_on_this_row, headerAnnotationScores, table, row_index, column),
                    table.getNumRows()
            );


        }


        System.out.println("\t>> All rows processed");
        create_typing_annotations(state, table_annotation, column); //supporting rows not added
        revise_disambiguation_and_create_annotation(table_annotation, table, candidates_and_scores_for_each_row, column);

    }

    private List<ObjObj<EntityCandidate, Map<String, Double>>> collect_existing(LTableAnnotation table_annotation, int row_index, int column) {
        List<ObjObj<EntityCandidate, Map<String, Double>>> candidates = new ArrayList<ObjObj<EntityCandidate, Map<String, Double>>>();
        CellAnnotation[] annotations = table_annotation.getContentCellAnnotations(row_index, column);
        for (CellAnnotation can : annotations) {
            EntityCandidate ec = can.getAnnotation();
            Map<String, Double> scoreElements = can.getScore_element_map();
            scoreElements.put(CellAnnotation.SCORE_FINAL, can.getFinalScore());
            candidates.add(new ObjObj<EntityCandidate, Map<String, Double>>(ec, scoreElements));
        }

        return candidates;
    }

    private Map<Object, Double> update_column_class(
            Set<HeaderAnnotation> scores, int tableRowsTotal) {
        Map<Object, Double> state = new HashMap<Object, Double>();
        for (HeaderAnnotation ha : scores) {
            //Map<String, Double> scoreElements =ha.getScoreElements();
            ha.getScoreElements().put(
                    HeaderAnnotation.FINAL,
                    classifier_learn.compute_final_score(ha, tableRowsTotal).get(HeaderAnnotation.FINAL)
            );
            state.put(ha, ha.getFinalScore());
        }
        return state;
    }


    private void revise_disambiguation_and_create_annotation(LTableAnnotation table_annotation,
                                                             LTable table,
                                                             Map<Integer, List<ObjObj<EntityCandidate, Map<String, Double>>>> candidates_and_scores_for_each_row,
                                                             int column) {
        List<HeaderAnnotation> bestHeaderAnnotations = table_annotation.getBestHeaderAnnotations(column);
        List<String> types = new ArrayList<String>();
        for (HeaderAnnotation ha : bestHeaderAnnotations)
            types.add(ha.getAnnotation_url());
        for (Map.Entry<Integer, List<ObjObj<EntityCandidate, Map<String, Double>>>> e :
                candidates_and_scores_for_each_row.entrySet()) {

            int row = e.getKey();
            List<ObjObj<EntityCandidate, Map<String, Double>>> entities_for_this_cell_and_scores = e.getValue();
            if (entities_for_this_cell_and_scores.size() == 0)
                continue;

            List<ObjObj<EntityCandidate, Map<String, Double>>> revised = disambiguation_learn.revise(entities_for_this_cell_and_scores, types);
            if (revised.size() != 0)
                entities_for_this_cell_and_scores = revised;
            List<EntityCandidate> best_entities = create_entity_annotations(table, table_annotation, row, column, entities_for_this_cell_and_scores
            ); //supporting rows are added here, impossible other places
            update_typing_supporting_rows(best_entities, row, column, table_annotation);
        }
    }

    //assigns highest scoring column_type_label to the column;
    //then disambiguate those rows that contributed to the prediction to column_type_scorings
    //WARNING: SUPPORTING ROWS NOT ADDED HERE
    private void create_typing_annotations(final Map<Object, Double> state,
                                           LTableAnnotation table_annotation,
                                           int column) {
        if (state.size() > 0) {
            List<Object> candidate_header_annotations = new ArrayList<Object>(state.keySet());
            Collections.sort(candidate_header_annotations, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    return state.get(o2).compareTo(state.get(o1));
                }
            });
            //insert column type annotations
            HeaderAnnotation[] final_header_annotations = new HeaderAnnotation[candidate_header_annotations.size()];
            for (int i = 0; i < candidate_header_annotations.size(); i++)
                final_header_annotations[i] = (HeaderAnnotation) candidate_header_annotations.get(i);
            table_annotation.setHeaderAnnotation(column, final_header_annotations);
        }
    }

    private List<EntityCandidate> create_entity_annotations(
            LTable table,
            LTableAnnotation table_annotation,
            int table_cell_row,
            int table_cell_col,
            List<ObjObj<EntityCandidate, Map<String, Double>>> candidates_and_scores_for_cell) {

        Collections.sort(candidates_and_scores_for_cell, new Comparator<ObjObj<EntityCandidate, Map<String, Double>>>() {
            @Override
            public int compare(ObjObj<EntityCandidate, Map<String, Double>> o1, ObjObj<EntityCandidate, Map<String, Double>> o2) {
                Double o2_score = o2.getOtherObject().get("final");
                Double o1_score = o1.getOtherObject().get("final");
                return o2_score.compareTo(o1_score);
            }
        });

        double max = 0.0;
        CellAnnotation[] annotationsForCell = new CellAnnotation[candidates_and_scores_for_cell.size()];
        for (int i = 0; i < candidates_and_scores_for_cell.size(); i++) {
            ObjObj<EntityCandidate, Map<String, Double>> e = candidates_and_scores_for_cell.get(i);
            double score = e.getOtherObject().get("final");
            if (score > max)
                max = score;
            annotationsForCell[i] = new CellAnnotation(table.getContentCell(table_cell_row, table_cell_col).getText(),
                    e.getMainObject(), e.getOtherObject().get("final"), e.getOtherObject());

        }
        table_annotation.setContentCellAnnotations(table_cell_row, table_cell_col, annotationsForCell);

        List<EntityCandidate> best = new ArrayList<EntityCandidate>();
        for (int i = 0; i < candidates_and_scores_for_cell.size(); i++) {
            ObjObj<EntityCandidate, Map<String, Double>> e = candidates_and_scores_for_cell.get(i);
            double score = e.getOtherObject().get("final");
            if (score == max)
                best.add(e.getMainObject());
        }
        return best;
    }

    private void update_typing_supporting_rows(List<EntityCandidate> bestCandidates,
                                               int row,
                                               int column,
                                               LTableAnnotation table_annotation) {
        HeaderAnnotation[] headers = table_annotation.getHeaderAnnotation(column);
        if (headers != null) {
            for (HeaderAnnotation ha : headers) {
                for (EntityCandidate ec : bestCandidates) {
                    if (ec.getTypeIds().contains(ha.getAnnotation_url()))
                        ha.addSupportingRow(row);
                }
            }
        }
    }
}
