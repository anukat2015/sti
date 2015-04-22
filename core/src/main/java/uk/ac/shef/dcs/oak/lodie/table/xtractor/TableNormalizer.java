package uk.ac.shef.dcs.oak.lodie.table.xtractor;

import org.w3c.dom.Node;

import java.util.List;

/**
 * Author: Ziqi Zhang (z.zhang@dcs.shef.ac.uk)
 * Date: 05/10/12
 * Time: 11:05
 *
 * Normalizes a &lt;table&gt; Element (Jsoup) to a regular n x m table stored by a matrix of Elements
 * (e.g., deletes spanned cols/rows)
 *
 */
public abstract interface TableNormalizer{

    List<List<Node>> apply(Node tableNode);

}
