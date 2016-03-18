package uk.ac.shef.dcs.sti.algorithm.tm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zqz
 * Date: 27/02/14
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
public class Creator_RelationHierarchicalBOW_Freebase implements Creator_OntologyEntityHierarchicalBOW {
    @Override
    public List<String> create(String uri) {
        List<String> bow = new ArrayList<String>();
        for (String part : uri.split("/")) {
            part = part.trim();
            for (String pp : part.split("_")) {
                pp = pp.trim();
                if (pp.length() > 0)
                    bow.add(pp);
            }
        }
        return bow;
    }
}