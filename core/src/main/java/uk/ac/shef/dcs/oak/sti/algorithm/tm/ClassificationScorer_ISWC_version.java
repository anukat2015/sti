package uk.ac.shef.dcs.oak.sti.algorithm.tm;

import uk.ac.shef.dcs.oak.sti.PlaceHolder;
import uk.ac.shef.dcs.oak.sti.nlp.Lemmatizer;
import uk.ac.shef.dcs.oak.sti.nlp.NLPTools;
import uk.ac.shef.dcs.oak.sti.rep.*;
import uk.ac.shef.dcs.oak.sti.experiment.TableMinerConstants;
import uk.ac.shef.dcs.oak.triplesearch.EntityCandidate;
import uk.ac.shef.dcs.oak.util.CollectionUtils;
import uk.ac.shef.dcs.oak.util.ObjObj;
import uk.ac.shef.dcs.oak.util.StringUtils;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: zqz
 * Date: 30/05/14
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class ClassificationScorer_ISWC_version implements ClassificationScorer {

    private Lemmatizer lemmatizer;
    private List<String> stopWords;
    //private Levenshtein stringSimilarityMetric;
    //private Jaro stringSimilarityMetric;
    //private Levenshtein stringSimilarityMetric;
    private AbstractStringMetric stringSimilarityMetric;
    private Creator_OntologyEntityHierarchicalBOW bow_creator;

    public ClassificationScorer_ISWC_version(String nlpResources, Creator_OntologyEntityHierarchicalBOW bow_creator, List<String> stopWords,
                                             double[] weights) throws IOException {
        this.lemmatizer = NLPTools.getInstance(nlpResources).getLemmatizer();
        this.bow_creator = bow_creator;
        this.stopWords = stopWords;
        this.stringSimilarityMetric = new Levenshtein();
    }


    @Override
    public Set<HeaderAnnotation> score(List<ObjObj<EntityCandidate, Map<String, Double>>> input,
                                       Set<HeaderAnnotation> headerAnnotations_prev,
                                       LTable table,
                                       List<Integer> rows, int column) {
        Set<HeaderAnnotation> candidates = new HashSet<HeaderAnnotation>();
        if (TableMinerConstants.CLASSIFICATION_CANDIDATE_CONTRIBUTION_METHOD == 0) {
            for (int row : rows)
                candidates = score_entity_best_candidate_contribute(input, headerAnnotations_prev, table, row, column);
        } else {
            for (int row : rows)
                candidates = score_entity_all_candidate_contribute(input, headerAnnotations_prev, table, row, column);
        }
        candidates = score_context(candidates, table, column, false);

        return candidates;
    }

    public Set<HeaderAnnotation> score_entity_all_candidate_contribute(List<ObjObj<EntityCandidate, Map<String, Double>>> input,
                                                                       Set<HeaderAnnotation> headerAnnotations_prev, LTable table,
                                                                       int row, int column) {
        final Set<HeaderAnnotation> candidate_header_annotations =
                headerAnnotations_prev;

        //for this row
        Map<String, Double> tmp_header_annotation_and_max_score = new HashMap<String, Double>();
        Map<String, String> header_annotation_and_text = new HashMap<String, String>();

        for (ObjObj<EntityCandidate, Map<String, Double>> es : input) { //each candidate entity in this cell
            EntityCandidate entity = es.getMainObject();
            //each assigned type receives a score of 1, and the bonus score due to disambiguation result
            double entity_disamb_score = es.getOtherObject().get(CellAnnotation.SCORE_FINAL);
            for (String[] type : entity.getTypes()) {
                String url = type[0];
                header_annotation_and_text.put(url, type[1]);
                Double score = tmp_header_annotation_and_max_score.get(url);
                score = score == null ? 0 : score;
                if (entity_disamb_score > score) {
                    tmp_header_annotation_and_max_score.put(url, entity_disamb_score);
                    /*if(score!=0)
                        System.out.println();*/
                }
            }
        }
        if (input.size() == 0 || tmp_header_annotation_and_max_score.size() == 0) {
            //this entity has a score of 0.0, it should not contribute to the header typing, but we may still keep it as candidate for this cell
            System.out.print("x(" + row + "," + column + ")");
            return candidate_header_annotations;
        }

        //consolidate scores from this cell
        for (Map.Entry<String, Double> e : tmp_header_annotation_and_max_score.entrySet()) {
            String headerText = table.getColumnHeader(column).getHeaderText();

            HeaderAnnotation hAnnotation = null;

            for (HeaderAnnotation key : candidate_header_annotations) {
                if (key.getTerm().equals(headerText) && key.getAnnotation_url().equals(e.getKey()
                )) {
                    hAnnotation = key;
                    break;
                }
            }
            if (hAnnotation == null) {
                hAnnotation = new HeaderAnnotation(headerText, e.getKey(),
                        header_annotation_and_text.get(e.getKey()), e.getValue());
            }

            Map<String, Double> tmp_score_elements = hAnnotation.getScoreElements();
            if (tmp_score_elements == null || tmp_score_elements.size() == 0) {
                tmp_score_elements = new HashMap<String, Double>();
                tmp_score_elements.put(HeaderAnnotation.SUM_ENTITY_DISAMB, 0.0);
                tmp_score_elements.put(HeaderAnnotation.SUM_ENTITY_VOTE, 0.0);
            }
            tmp_score_elements.put(HeaderAnnotation.SUM_ENTITY_DISAMB,
                    tmp_score_elements.get(HeaderAnnotation.SUM_ENTITY_DISAMB) + e.getValue());
            tmp_score_elements.put(HeaderAnnotation.SUM_ENTITY_VOTE,
                    tmp_score_elements.get(HeaderAnnotation.SUM_ENTITY_VOTE) + 1.0);
            hAnnotation.setScoreElements(tmp_score_elements);

            candidate_header_annotations.add(hAnnotation);
        }

        return candidate_header_annotations;
    }

    public Set<HeaderAnnotation> score_entity_best_candidate_contribute(List<ObjObj<EntityCandidate, Map<String, Double>>> input,
                                                                        Set<HeaderAnnotation> headerAnnotations_prev, LTable table,
                                                                        int row, int column) {
        final Set<HeaderAnnotation> candidate_header_annotations =
                headerAnnotations_prev;

        //for this row
        EntityCandidate entity_with_highest_disamb_score = null;
        double best_score = 0.0;
        for (ObjObj<EntityCandidate, Map<String, Double>> es : input) { //each candidate entity in this cell
            EntityCandidate entity = es.getMainObject();
            //each assigned type receives a score of 1, and the bonus score due to disambiguation result
            double entity_disamb_score = es.getOtherObject().get(CellAnnotation.SCORE_FINAL);
            if (entity_disamb_score > best_score) {
                best_score = entity_disamb_score;
                entity_with_highest_disamb_score = entity;
            }
        }
        if (input.size() == 0 || entity_with_highest_disamb_score == null) {
            //this entity has a score of 0.0, it should not contribute to the header typing, but we may still keep it as candidate for this cell
            System.out.print("x(" + row + "," + column + ")");
            return candidate_header_annotations;
        }


        for (ObjObj<EntityCandidate, Map<String, Double>> es : input) {
            EntityCandidate current_candidate = es.getMainObject();
            double entity_disamb_score = es.getOtherObject().get(CellAnnotation.SCORE_FINAL);
            if (entity_disamb_score != best_score)
                continue;

            Set<String> types_already_received_votes_by_cell = new HashSet<String>();    //each type will receive a max of 1 vote from each cell. If multiple candidates have the same highest score and casts same votes, they are counted oly once
            List<String[]> type_voted_by_this_cell = current_candidate.getTypes();

            //consolidate scores from this cell
            for (String[] type : type_voted_by_this_cell) {
                if (TableMinerConstants.BEST_CANDIDATE_CONTRIBUTE_COUNT_ONLY_ONCE
                        && types_already_received_votes_by_cell.contains(type[0]))
                    continue;

                types_already_received_votes_by_cell.add(type[0]);

                String headerText = table.getColumnHeader(column).getHeaderText();

                HeaderAnnotation hAnnotation = null;
                for (HeaderAnnotation key : candidate_header_annotations) {
                    if (key.getTerm().equals(headerText) && key.getAnnotation_url().equals(type[0]
                    )) {
                        hAnnotation = key;
                        break;
                    }
                }
                if (hAnnotation == null) {
                    hAnnotation = new HeaderAnnotation(headerText, type[0], type[1], 0.0);
                }
                Map<String, Double> tmp_score_elements = hAnnotation.getScoreElements();
                if (tmp_score_elements == null || tmp_score_elements.size() == 0) {
                    tmp_score_elements = new HashMap<String, Double>();
                    tmp_score_elements.put(HeaderAnnotation.SUM_ENTITY_DISAMB, 0.0);
                    tmp_score_elements.put(HeaderAnnotation.SUM_ENTITY_VOTE, 0.0);
                }
                tmp_score_elements.put(HeaderAnnotation.SUM_ENTITY_DISAMB,
                        tmp_score_elements.get(HeaderAnnotation.SUM_ENTITY_DISAMB) + best_score);
                tmp_score_elements.put(HeaderAnnotation.SUM_ENTITY_VOTE,
                        tmp_score_elements.get(HeaderAnnotation.SUM_ENTITY_VOTE) + 1.0);
                hAnnotation.setScoreElements(tmp_score_elements);

                candidate_header_annotations.add(hAnnotation);
            }
        }

        return candidate_header_annotations;
    }

    public Set<HeaderAnnotation> score_context(Set<HeaderAnnotation> candidates, LTable table, int column, boolean overwrite) {
        List<String> bag_of_words_for_header = null, bag_of_words_for_other_headers = null;
        List<String> bag_of_words_for_column = null, bag_of_words_for_table_major_context = null, bag_of_words_for_table_other_context = null;
        for (HeaderAnnotation ha : candidates) {
            Double score_ctx_header_text = ha.getScoreElements().get(HeaderAnnotation.SCORE_CTX_NAME_MATCH);
            Double score_ctx_column_text = ha.getScoreElements().get(HeaderAnnotation.SCORE_CTX_COLUMN_TEXT);
            Double score_ctx_table_context = ha.getScoreElements().get(HeaderAnnotation.SCORE_CTX_TABLE_CONTEXT);

            if (score_ctx_column_text != null &&
                    score_ctx_header_text != null
                    && score_ctx_table_context != null && !overwrite)
                continue;

            Set<String> annotation_bow = new HashSet<String>(create_annotation_bow(ha,
                    true,
                    TableMinerConstants.DISCARD_SINGLE_CHAR_IN_BOW,
                    TableMinerConstants.INCLUDE_URL_IN_CLASS_BOW));

            if (overwrite || (!overwrite && score_ctx_header_text == null)) {
                String headerText = "";
                LTableColumnHeader header = table.getColumnHeader(column);
                if (header != null &&
                        header.getHeaderText() != null &&
                        !header.getHeaderText().equals(PlaceHolder.TABLE_HEADER_UNKNOWN.getValue())) {
                    headerText = header.getHeaderText();
                }

                bag_of_words_for_header = create_header_bow(bag_of_words_for_header, table, column);
                double ctx_header_text = CollectionUtils.scoreOverlap_dice_keepFrequency(annotation_bow, bag_of_words_for_header);
                //double ctx_header_text = stringSimilarityMetric.getSimilarity(ha.getAnnotation_url(), headerText);


                ha.getScoreElements().put(HeaderAnnotation.SCORE_CTX_NAME_MATCH, ctx_header_text);
            }

            if (overwrite || (!overwrite && score_ctx_column_text == null)) {
                bag_of_words_for_column = create_column_bow(bag_of_words_for_column, table, column);
                double ctx_column =
                        CollectionUtils.scoreOverlap_dice_keepFrequency(annotation_bow, bag_of_words_for_column);
                //CollectionUtils.scoreCoverage_against_b(bag_of_words_for_column, new ArrayList<String>(annotation_bow)) * weights[1];
                ha.getScoreElements().put(HeaderAnnotation.SCORE_CTX_COLUMN_TEXT, ctx_column);
            }

            if (overwrite || (!overwrite && score_ctx_table_context == null)) {
                bag_of_words_for_table_major_context = create_table_context_major_bow(bag_of_words_for_table_major_context, table);
                double ctx_table_major =
                        CollectionUtils.scoreOverlap_dice_keepFrequency(annotation_bow, bag_of_words_for_table_major_context);
                //CollectionUtils.scoreCoverage_against_b(bag_of_words_for_table_major_context, new ArrayList<String>(annotation_bow)) * weights[3];
                bag_of_words_for_table_other_context = create_table_context_other_bow(bag_of_words_for_table_other_context, table);
                double ctx_table_other =
                        CollectionUtils.scoreOverlap_dice_keepFrequency(annotation_bow, bag_of_words_for_table_other_context);
                //CollectionUtils.scoreCoverage_against_b(bag_of_words_for_table_other_context, new ArrayList<String>(annotation_bow)) * weights[2];
                ha.getScoreElements().put(HeaderAnnotation.SCORE_CTX_TABLE_CONTEXT,
                        ctx_table_major + ctx_table_other);
            }

        }

        return candidates;
    }

    @Override
    public double score_domain_consensus(HeaderAnnotation ha, List<String> domain_representation) {
        return 0.0;
    }

    private List<String> create_table_context_major_bow(List<String> bag_of_words_for_table_context, LTable table) {
        if (bag_of_words_for_table_context != null)
            return bag_of_words_for_table_context;
        if (table.getContexts() == null)
            return new ArrayList<String>();

        List<String> bow = new ArrayList<String>();
        for (int i = 0; i < table.getContexts().size(); i++) {
            LTableContext tx = table.getContexts().get(i);
            if (tx.getType().equals(LTableContext.TableContextType.PAGETITLE) ||
                    tx.getType().equals(LTableContext.TableContextType.CAPTION)) {
                bow.addAll(lemmatizer.lemmatize(
                        StringUtils.toBagOfWords(tx.getText(), true, true, TableMinerConstants.DISCARD_SINGLE_CHAR_IN_BOW))
                );
            }
        }
        bow.removeAll(stopWords);
        return bow;
    }

    private List<String> create_table_context_other_bow(List<String> bag_of_words_for_table_context, LTable table) {
        if (bag_of_words_for_table_context != null)
            return bag_of_words_for_table_context;
        if (table.getContexts() == null)
            return new ArrayList<String>();

        List<String> bow = new ArrayList<String>();
        for (int i = 0; i < table.getContexts().size(); i++) {
            LTableContext tx = table.getContexts().get(i);
            if (!tx.getType().equals(LTableContext.TableContextType.PAGETITLE) &&
                    !tx.getType().equals(LTableContext.TableContextType.CAPTION)) {
                bow.addAll(lemmatizer.lemmatize(
                        StringUtils.toBagOfWords(tx.getText(), true, true, TableMinerConstants.DISCARD_SINGLE_CHAR_IN_BOW))
                );
            }
        }
        bow.removeAll(stopWords);
        return bow;
    }

    private List<String> create_column_bow(List<String> bag_of_words_for_column, LTable table, int column) {
        if (bag_of_words_for_column != null)
            return bag_of_words_for_column;
        List<String> bow = new ArrayList<String>();
        for (int row = 0; row < table.getNumRows(); row++) {
            LTableContentCell tcc = table.getContentCell(row, column);
            if (tcc.getText() != null) {
                bow.addAll(lemmatizer.lemmatize(
                        StringUtils.toBagOfWords(tcc.getText(), true, true, TableMinerConstants.DISCARD_SINGLE_CHAR_IN_BOW))
                );
            }
        }
        bow.removeAll(stopWords);
        return bow;
    }

    private List<String> create_header_bow(List<String> bag_of_words_for_header, LTable table, int column) {
        if (bag_of_words_for_header != null)
            return bag_of_words_for_header;
        List<String> bow = new ArrayList<String>();

        // for (int c = 0; c < table.getNumCols(); c++) {
        LTableColumnHeader header = table.getColumnHeader(column);
        if (header != null &&
                header.getHeaderText() != null &&
                !header.getHeaderText().equals(PlaceHolder.TABLE_HEADER_UNKNOWN.getValue())) {
            bow.addAll(lemmatizer.lemmatize(
                    StringUtils.toBagOfWords(header.getHeaderText(), true, true, TableMinerConstants.DISCARD_SINGLE_CHAR_IN_BOW))
            );
        }
        //   }
        bow.removeAll(stopWords);
        //also remove special, generic words, like "title", "name"
        bow.remove("title");
        bow.remove("name");
        return bow;
    }

    private List<String> create_annotation_bow(HeaderAnnotation ha, boolean lowercase, boolean discard_single_char, boolean include_url) {
        List<String> bow = new ArrayList<String>();
        if (include_url) {
            bow.addAll(bow_creator.create(ha.getAnnotation_url()));
        }

        String label = StringUtils.toAlphaNumericWhitechar(ha.getAnnotation_label()).trim();
        for (String s : label.split("\\s+")) {
            s = s.trim();
            if (s.length() > 0) {
                if (lowercase) s = s.toLowerCase();
                bow.add(s);
            }
        }

        if (discard_single_char) {
            Iterator<String> it = bow.iterator();
            while (it.hasNext()) {
                String t = it.next();
                if (t.length() < 2)
                    it.remove();
            }
        }
        bow.removeAll(TableMinerConstants.stopwords_small);
        return bow;
    }



    public Map<String, Double> compute_final_score(HeaderAnnotation ha, int tableRowsTotal) {
        Map<String, Double> scoreElements = ha.getScoreElements();
        double sum_entity_disamb =
                scoreElements.get(HeaderAnnotation.SUM_ENTITY_DISAMB);
        double sum_entity_vote = scoreElements.get(HeaderAnnotation.SUM_ENTITY_VOTE);

        scoreElements.put(HeaderAnnotation.SCORE_ENTITY_DISAMB, sum_entity_disamb / sum_entity_vote);

        double score_entity_vote = scoreElements.get(HeaderAnnotation.SUM_ENTITY_VOTE) / (double) tableRowsTotal;
        scoreElements.put(HeaderAnnotation.SCORE_ENTITY_VOTE, score_entity_vote);

        double base_score = compute_typing_base_score(sum_entity_disamb, scoreElements.get(HeaderAnnotation.SUM_ENTITY_VOTE),
                (double) tableRowsTotal);

        for (Map.Entry<String, Double> e : scoreElements.entrySet()) {
            if (e.getKey().equals(HeaderAnnotation.SUM_ENTITY_DISAMB) ||
                    e.getKey().equals(HeaderAnnotation.SUM_ENTITY_VOTE) ||
                    e.getKey().equals(HeaderAnnotation.SCORE_ENTITY_DISAMB) ||
                    e.getKey().equals(HeaderAnnotation.SCORE_ENTITY_VOTE) ||
                    e.getKey().equals(HeaderAnnotation.FINAL))
                continue;

            base_score += e.getValue();
        }
        scoreElements.put(HeaderAnnotation.FINAL, base_score);
        ha.setFinalScore(base_score);
        return scoreElements;
    }

    public static double compute_typing_base_score(double sum_entity_disamb,
                                                   double sum_entity_vote,
                                                   double total_table_rows) {
        if (sum_entity_vote == 0)
            return 0.0;

        double score_entity_vote = sum_entity_vote / total_table_rows;
        double base_score = score_entity_vote * (sum_entity_disamb / sum_entity_vote);
        return base_score;
    }
}