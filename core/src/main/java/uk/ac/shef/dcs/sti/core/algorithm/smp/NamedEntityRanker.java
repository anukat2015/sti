package uk.ac.shef.dcs.sti.core.algorithm.smp;

import javafx.util.Pair;
import uk.ac.shef.dcs.kbsearch.KBSearch;
import uk.ac.shef.dcs.kbsearch.KBSearchException;
import uk.ac.shef.dcs.kbsearch.model.Attribute;
import uk.ac.shef.dcs.sti.core.scorer.EntityScorer;
import uk.ac.shef.dcs.kbsearch.model.Entity;
import uk.ac.shef.dcs.sti.core.model.TCellAnnotation;
import uk.ac.shef.dcs.sti.core.model.TAnnotation;
import uk.ac.shef.dcs.sti.core.model.TCell;
import uk.ac.shef.dcs.sti.core.model.Table;

import java.util.*;

/**
 * NE ranker creates initial disambiguation of an NE column
 */
public class NamedEntityRanker {

    private KBSearch kbSearch;
    private EntityScorer entityScorer;
    //private static Logger LOG = Logger.getLogger(TCellDisambiguator.class.getName());

    public NamedEntityRanker(KBSearch kbSearch, EntityScorer entityScorer) {
        this.kbSearch = kbSearch;
        this.entityScorer = entityScorer;
    }

    public void rankCandidateNamedEntities(
            TAnnotation tableAnnotations, Table table,
            int row, int column
    ) throws KBSearchException {
        List<Pair<Entity, Map<String, Double>>> scores = scoreCandidateNamedEntities(table, row, column);
        List<Pair<Entity, Double>> sorted = new ArrayList<>();
        for (Pair<Entity, Map<String, Double>> e : scores) {
            double score = e.getValue().get(TCellAnnotation.SCORE_FINAL);
            sorted.add(new Pair<>(e.getKey(), score));
        }
        Collections.sort(sorted, new Comparator<Pair<Entity, Double>>() {
            @Override
            public int compare(Pair<Entity, Double> o1, Pair<Entity, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        TCell tcc = table.getContentCell(row, column);
        TCellAnnotation[] annotations = new TCellAnnotation[scores.size()];
        int i = 0;
        for (Pair<Entity, Map<String, Double>> oo : scores) {
            TCellAnnotation ca = new TCellAnnotation(tcc.getText(), oo.getKey(),
                    oo.getValue().get(TCellAnnotation.SCORE_FINAL), oo.getValue());
            annotations[i] = ca;
            i++;
        }
        tableAnnotations.setContentCellAnnotations(row, column, annotations);
        //return sorted;
    }

    public List<Pair<Entity, Map<String, Double>>> scoreCandidateNamedEntities(Table table,
                                                                                          int row, int column
    ) throws KBSearchException {
        //do disambiguation scoring
        //LOG.info("\t>> Disambiguation-LEARN, position at (" + entity_row + "," + entity_column + ") candidates=" + candidates.size());
        TCell cell = table.getContentCell(row, column);
        System.out.print("\t\t>> NamedEntityRanker, position at (" + row + "," + column + ") " +
                cell);
       /* if(row==11)
            System.out.println();*/
        List<Entity> candidates = kbSearch.findEntityCandidates(cell.getText());
        System.out.println(" candidates=" + candidates.size());
        //each candidate will have a map containing multiple elements of scores. See SMPAdaptedEntityScorer
        List<Pair<Entity, Map<String, Double>>> disambiguationScores =
                new ArrayList<>();
        for (Entity c : candidates) {
            //find facts of each entity
            if (c.getAttributes() == null || c.getAttributes().size() == 0) {
                List<Attribute> facts = kbSearch.findAttributesOfEntities(c);
                c.setAttributes(facts);
            }
            Map<String, Double> scoreMap = entityScorer.
                    computeElementScores(c, candidates,
                            column, row, Arrays.asList(row),
                            table);
            entityScorer.computeFinal(scoreMap, cell.getText());
            Pair<Entity, Map<String, Double>> entry = new Pair<>(c,scoreMap);
            disambiguationScores.add(entry);
        }
        return disambiguationScores;
    }


}
