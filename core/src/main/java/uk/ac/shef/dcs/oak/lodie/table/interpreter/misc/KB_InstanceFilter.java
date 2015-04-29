package uk.ac.shef.dcs.oak.lodie.table.interpreter.misc;

import uk.ac.shef.dcs.oak.lodie.test.TableMinerConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zqz
 * Date: 16/02/14
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class KB_InstanceFilter {

    private static List<String> ignorePredicates_from_triples = new ArrayList<String>();

    static {
        ignorePredicates_from_triples = Arrays.asList(
                new String[]{
                        "/media_common/creative_work/credit",
                        "/type/object/timestamp",
                        "/type/object/creator",
                        "creator",
                        "lang",
                        "timestamp",
                        "count",
                        "/type/object/key",
                        "/type/object/attribution",
                        "/type/object/permission",
                        "/type/object/guid"
                }
        );
    }

    public static boolean ignoreType(String type, String label) {

        if (type.startsWith("/user/") ||
                type.equals("/type/content") || type.equals("/common/image") ||
                type.endsWith("topic") || type.startsWith("/pipeline/") ||
                type.endsWith("skos_concept") ||
                type.endsWith("_instance") ||
                type.startsWith("/base/type_ontology")

                ||label.equalsIgnoreCase("topic")||label.equalsIgnoreCase("thing")||label.equalsIgnoreCase("concept")
                ||label.equalsIgnoreCase("things")||label.equalsIgnoreCase("entity"))
            return true;

        if (TableMinerConstants.IGNORE_NOTABLE_EXTRACTED_TYPE &&
                type.startsWith("/m/"))
            return true;

        return false;

    }

    public static boolean ignoreRelation_from_relInterpreter(String relation) {
        if (relation.equals("/common/topic/article") ||
                relation.equals("/type/object/mid") ||
                //relation.equals("/common/document/source_uri") ||
                relation.equals("/common/topic/image") ||
                relation.equals("id") ||
                relation.equals("/common/document/text") ||
                relation.equals("/common/topic/description") ||
                relation.equals("/type/object/type") ||
                relation.equals("/type/object/name") ||
                relation.equals("/common/topic/notable_properties")
                )
            return true;
        return false;
    }

    public static boolean ignoreFact_from_bow(String predicate) {
        if (predicate.equals("/type/object/mid") ||
                predicate.equals("/type/object/key") ||
                predicate.equals("/common/topic/image")
            /*||predicate.endsWith("_webpage")*/)
            return true;
        return false;
    }

    public static boolean ignorePredicate_from_triple(String s) {
        if (ignorePredicates_from_triples.contains(s))
            return true;
        return false;
    }
}