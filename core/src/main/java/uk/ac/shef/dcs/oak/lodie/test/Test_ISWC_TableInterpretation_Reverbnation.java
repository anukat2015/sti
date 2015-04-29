package uk.ac.shef.dcs.oak.lodie.test;

import com.google.api.client.http.HttpResponseException;
import org.apache.any23.util.FileUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.content.KBSearcher_Freebase;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.interpret.*;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.io.LTableAnnotationWriter;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.maincol.MainColumnFinder;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.selector.CellSelector;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.selector.Sampler_LTableContentCell_OSPD_nonEmpty;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.selector.LTableContentRow_Sampler_nonEmpty;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.stopping.EntropyConvergence;
import uk.ac.shef.dcs.oak.lodie.table.rep.HeaderAnnotation;
import uk.ac.shef.dcs.oak.lodie.table.rep.LTable;
import uk.ac.shef.dcs.oak.lodie.table.rep.LTableAnnotation;
import uk.ac.shef.dcs.oak.lodie.table.rep.LTableContentCell;
import uk.ac.shef.dcs.oak.lodie.table.validator.TabValGeneric;
import uk.ac.shef.dcs.oak.lodie.table.xtractor.*;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.logging.Logger;

/**
 */
public class Test_ISWC_TableInterpretation_Reverbnation {
    private static Logger log = Logger.getLogger(Test_ISWC_TableInterpretation_Reverbnation.class.getName());

    public static int[] IGNORE_COLUMNS = new int[]{2, 3};

    public static void main(String[] args) throws IOException {
        String inFolder = args[0];
        String outFolder = args[1];
        String freebaseProperties = args[2]; //"D:\\Work\\lodiecrawler\\src\\main\\java/freebase.properties"
        String cacheFolder = args[3];  //String cacheFolder = "D:\\Work\\lodiedata\\tableminer_cache\\solrindex_cache\\zookeeper\\solr";
        String nlpResources = args[4]; //"D:\\Work\\lodie\\resources\\nlp_resources";
        int start = Integer.valueOf(args[5]);
        boolean relationLearning = Boolean.valueOf(args[6]);
        //cache target location

        File configFile = new File(cacheFolder + File.separator + "solr.xml");
        CoreContainer container = new CoreContainer(cacheFolder,
                configFile);
        SolrServer server = new EmbeddedSolrServer(container, "collection1");

        //object to fetch things from KB
        KBSearcher_Freebase freebaseMatcher = new KBSearcher_Freebase(freebaseProperties, server, true);
        List<String> stopWords = uk.ac.shef.dcs.oak.util.FileUtils.readList(nlpResources + "/stoplist.txt", true);
        //object to find main subject column
        MainColumnFinder main_col_finder = new MainColumnFinder(
                new LTableContentRow_Sampler_nonEmpty(),
                EntropyConvergence.class.getName(),
                new String[]{"0.0", "1", "0.01"},
                server,
                nlpResources, false, stopWords,
                "/BlhLSReljQ3Koh+vDSOaYMji9/Ccwe/7/b9mGJLwDQ=");


        //stop words and stop properties (freebase) are used for disambiguation
        //List<String> stopProperties = FileUtils.readList("D:\\Work\\lodie\\resources\\nlp_resources/stopproperties_freebase.txt", true);

        //object to score columns, and disambiguate entities
        Disambiguator disambiguator = new Disambiguator(freebaseMatcher, new DisambiguationScorer_Overlap(
                stopWords,
                new double[]{1.0, 0.5, 0.5, 1.0, 1.0}, //row,column, tablecontext other,refent, tablecontext pagetitle (unused)
                nlpResources));
        ClassificationScorer class_scorer = new ClassificationScorer_Vote(nlpResources,
                new Creator_ConceptHierarchicalBOW_Freebase(),
                stopWords,
                new double[]{1.0, 1.0, 1.0, 1.0}         //all 1.0
        );

        CellSelector selector = new Sampler_LTableContentCell_OSPD_nonEmpty();
        ColumnLearner_LEARN_Seeding column_learnerSeeding = new ColumnLearner_LEARN_Seeding(
                selector,
                EntropyConvergence.class.getName(),
                new String[]{"0.0", "2", "0.01"},
                freebaseMatcher,
                disambiguator,
                class_scorer
        );

        ColumnLearner_LEARN_Update column_updater = new ColumnLearner_LEARN_Update(
                freebaseMatcher, disambiguator, class_scorer
        );


        ColumnInterpreter columnInterpreter = new ColumnInterpreter(column_learnerSeeding, column_updater, TableMinerConstants.MAX_REFERENCE_ENTITY_FOR_DISAMBIGUATION);

        //object to interpret relations between columns
        HeaderBinaryRelationScorer relation_scorer = new HeaderBinaryRelationScorer_Vote(nlpResources,
                new Creator_RelationHierarchicalBOW_Freebase(),
                stopWords,
                new double[]{1.0, 1.0, 0.0, 0.0, 1.0}    //entity, header text, column, title&caption, other
        );
        BinaryRelationInterpreter interpreter_relation = new BinaryRelationInterpreter(
                new RelationTextMatch_Scorer(0.0, stopWords),
                relation_scorer
        );

        //object to consolidate previous output, further score columns and disamgiuate entities
        ColumnInterpreter_relDepend interpreter_with_knownRelations = new ColumnInterpreter_relDepend_exclude_entity_col(
                IGNORE_COLUMNS
        );


        Backward_updater updater = new Backward_updater(selector, freebaseMatcher, disambiguator, class_scorer, stopWords, nlpResources);
        MainInterpreter_and_Forward_learner interpreter = new MainInterpreter_and_Forward_learner(
                main_col_finder,
                columnInterpreter,
                interpreter_with_knownRelations,
                interpreter_relation,
                IGNORE_COLUMNS, new int[]{0},
                updater, relation_scorer);

        LTableAnnotationWriter writer = new LTableAnnotationWriter(
                new TripleGenerator("http://www.freebase.com", "http://lodie.dcs.shef.ac.uk"));


        TableXtractorReverbnation xtractor = new TableXtractorReverbnation(new TableNormalizer_fromList(),
                new TableHODetectorByHTMLTag(),
                new TableObjCreator_fromList_Reverbnation(),
                new TabValGeneric());
        int count = 0;
        List<File> all = Arrays.asList(new File(inFolder).listFiles());
        Collections.sort(all);
        System.out.println(all.size());

        for (File f : all) {
            count++;
            if (count - 1 < start)
                continue;
            /*if(count>1)
                continue;*/
            /*boolean found = false;
            for (int od : onlyDo) {
                if (od == count)
                    found = true;
            }
            if (!found)
                continue;*/

            boolean complete = false;
            String inFile = f.toString();
            try {
                String fileContent = FileUtils.readFileContent(new File(inFile));
                List<LTable> tables = xtractor.extract(fileContent, inFile);

                if (tables.size() == 0)
                    continue;

                LTable table = tables.get(0);

                String sourceTableFile = inFile;
                if (sourceTableFile.startsWith("\"") && sourceTableFile.endsWith("\""))
                    sourceTableFile = sourceTableFile.substring(1, sourceTableFile.length() - 1).trim();
                System.out.println(count + "_" + sourceTableFile + " " + new Date());
                log.info(">>>" + count + "_" + sourceTableFile);

                complete = process(interpreter, table, sourceTableFile, writer, outFolder, relationLearning);
                //server.commit();
                if (TableMinerConstants.COMMIT_SOLR_PER_FILE)
                    server.commit();
                if (!complete) {
                    System.out.println("\t\t\t missed: " + count + "_" + sourceTableFile);
                    PrintWriter missedWriter = null;
                    try {
                        missedWriter = new PrintWriter(new FileWriter("ti_reverb(iswc)_missed.csv", true));
                        missedWriter.println(count + "," + inFile);
                        missedWriter.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                //gs annotator

            } catch (Exception e) {
                e.printStackTrace();
                PrintWriter missedWriter = null;
                try {
                    missedWriter = new PrintWriter(new FileWriter("ti_reverb(iswc).csv", true));
                    missedWriter.println(count + "," + inFile);
                    missedWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                e.printStackTrace();
                server.shutdown();
                System.exit(1);
            }

        }
        server.shutdown();
        System.out.println(new Date());
    }


    public static boolean process(MainInterpreter_and_Forward_learner interpreter, LTable table, String sourceTableFile, LTableAnnotationWriter writer,
                                  String outFolder, boolean relationLearning) throws FileNotFoundException {
        String outFilename = sourceTableFile.replaceAll("\\\\", "/");
        try {
            LTableAnnotation annotations = interpreter.start(table, relationLearning);

            int startIndex = outFilename.lastIndexOf("/");
            if (startIndex != -1) {
                outFilename = outFilename.substring(startIndex + 1).trim();
            }
            writer.writeHTML(table, annotations, outFolder + "/" + outFilename + ".html");
            //System.out.println("writing to output");
            write_iswc_output(table,annotations,new File(outFilename).getName(), "music-reverb-song.txt.tableminer");

        } catch (Exception ste) {
            if (ste instanceof SocketTimeoutException || ste instanceof HttpResponseException) {
                ste.printStackTrace();
                System.out.println("Remote server timed out, continue 10 seconds. Missed." + outFilename);
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                }
                return false;
            } else
                ste.printStackTrace();

        }
        return true;
    }


    public static void write_iswc_output(LTable table, LTableAnnotation tableAnnotation, String imdb_file_name, String outFile) throws IOException {
        List<HeaderAnnotation> has = tableAnnotation.getBestHeaderAnnotations(0);
        if (has != null && has.size() > 0) {
            boolean correct = false;
            for (HeaderAnnotation ha : has) {
                if (ha.getAnnotation_url().equals("/music/recording")
                    || ha.getAnnotation_url().equals("/music/release")
                   || ha.getAnnotation_url().equals("/music/release_track")
                   || ha.getAnnotation_url().equals("/music/soundtrack")
                        || ha.getAnnotation_url().equals("/music/single")
                        || ha.getAnnotation_url().equals("/music/composition")
                        || ha.getAnnotation_url().equals("/music/album")
                        ) {
                    correct=true;
                    System.out.println("found matching...");
                    break;
                }
            }

            if (correct){
                FileOutputStream fileStream = new FileOutputStream(new File(outFile),true);
                OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");
                PrintWriter p = new PrintWriter(writer);

                String append = "";
                int count=0;
                for(int r=0; r<table.getNumRows(); r++){
                    LTableContentCell tcc =table.getContentCell(r, 0);
                    if(tcc!=null && tcc.getText()!=null && tcc.getText().length()>0){
                        count++;
                        append+=tcc.getText()+"\t";
                    }
                }
                p.println(imdb_file_name+"\t"+count+"\t"+append);
                p.close();
            }

        }
    }
}