/**
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Initial Developer of the Original Code is Sheffield University.
 * Portions created by Sheffield University are
 * Copyright &copy; 2005 Sheffield University (Web Intelligence Group)
 * All Rights Reserved.
 *
 * Contributor(s):
 *   Neil Ireson (N.Ireson@dcs.shef.ac.uk)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

// Import log4j classes.

package uk.ac.shef.wit.ie.wrapper.html.xpath;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.multimatch.schema.multimatchmetadata_2_2.Person;
import org.multimatch.schema.multimatchmetadata_2_2.MMEntity;
import org.multimatch.schema.multimatchmetadata_2_2.Creation;
import org.multimatch.schema.multimatchmetadata_2_2.Actor;
import org.multimatch.schema.multimatchmetadata_2_2.Organisation;
import org.multimatch.schema.multimatchmetadata_2_2.StillImage;
import org.multimatch.schema.multimatchmetadata_2_2.ManualAutoPreferredLinguisticType;
import org.multimatch.schema.multimatchmetadata_2_2.ManualAutoPreferredType;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Class IEXPathWrapper
 * <p/>
 * After trying various alternatives (Xalan, Saxon, Javan)
 * the best XPath tools are provided by the within the Java package.
 * <p/>
 * todo split this class up into more meaningful chunks:
 * IEXPathWrapper -> HTML -> DOM
 * htmlParser -> HTML -> well-formed HTML, text, DOM, ...
 * Uses htmlParser, JTidy, TagSoup, or IEXPathWrapper
 * XPathWrapper -> DOM & (text or XPaths) -> text & related XPaths
 * <p/>
 * todo the ability to extract links and images from the associated text
 * todo i.e. <img src="/collection/T/T01/T01142_7.jpg" border="0" alt="Alexander Calder Dots Red circa 1936">
 * todo and <a href="/servlet/ArtistWorks?cgroupid=999999961&amp;artistid=848&amp;page=1">Alexander Calder</a>
 * todo (for is the htmlNode containing the query text a subnode of the <a> element
 * todo Need to develop rules to resolve conflict between multiple occurances of query text in a DOM
 * todo based on:
 * todo     returned htmlNode contains only one query term
 * todo     minimising spurious text (i.e. text outside query term)
 * todo     deepest XPath subtree which covers all the query term nodes
 * todo     precedents of queries (e.g. from above work name takes precedent over artist name
 * todo         as in this context it's more specific. Also in this case the Artist name appears in all
 * todo         these alt text and so is not the differentiating text.
 * <p/>
 * todo Another consideration is querying for hierarchical terms such are the subject hierarchy.
 * todo In this case each subtree should contain:
 * todo     one term at level 1,
 * todo     then where n>1, number of terms at level n >= number of terms at level n-1.
 * todo This may well happen as a natural consequence of specify examples at different levels.
 * <p/>
 * todo Some thoughts on how to use this approach generally for semantic browsing...
 * When viewing a page you can click annotate, then the classifier determines what sort of page
 * we are looking at. Then we can try to find known terms in that page or find known terms on
 * similar pages from the same site:/directory. These can then be annotated and then we can use
 * some heuristics to determine how best to annotate the page recall/precision type trade-off.
 * <p/>
 * <p/>
 * <p/>
 * todo There is a problem with artist who paint each other for example Frank Auerbach and R.B. Kitaj
 * todo who cause ambiguous artist name, artwork title, pairings.
 * <p/>
 * todo In addition there is a general problem where there is ambiguity between artist names and
 * todo artwork titles, which is most prevalent where artwork titles have common names
 * todo (e.g. Sir Sidney Nolan's work "Kelly" is ambiguous with Ellsworth Kelly which causes problem in the
 * todo http://wwar.com/masters/k/kelly-ellsworth.html page.
 * todo a solution is to use the known artist names, if the title matches one or more artist names then the
 * todo title is too ambigous to use.
 * todo Obviously to general to any seed this involves having a "stopword" list for every seed, (e.g. for
 * todo artist names using ULAN) which is a strong assumption.
 * todo Alternatively it may be possible to use patterns for the seeds, so that if an artwork title matches the
 * todo data pattern then the title is considered too ambiguous to use as a seed.
 * <p/>
 * todo Or even more generally  where artwork titles are simply common words or phrases
 * todo (e.g. Phillip King's work "Call")
 * todo a solution to this is to remove stopwords from the title and then if the title is empty it is too
 * todo ambinguous to use as a seed.
 * <p/>
 * todo problem with combinatorial explosion if many matches,
 * todo (e.g. Andy Warhol, Marilyn, 1967: in http://www.artcyclopedia.com/artists/warhol_andy.html
 * todo which has Andy Warhol * 54, Marilyn * 9, 1967 * 5 = 2430 combinations
 * <p/>
 * todo define the ontology using OWL, with seeds as RDF instances
 * <p/>
 * Author: Neil Ireson (mailto:N.Ireson@dcs.shef.ac.uk)
 * Creation Date: 23-Apr-2007
 * Version: 0.1
 */
public class IEXPathWrapper
{
    /**
     * Define a static logger variable
     * todo configure logging properties
     */
    private static final Logger logger = Logger.getLogger(IEXPathWrapper.class.getName());

    private static final XPath xpath;

    static
    {
        setSystemProperties();
        final XPathFactory factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
    }

    private static void setSystemProperties()
    {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                           "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory",
                           "org.apache.xerces.jaxp.SAXParserFactoryImpl");
        System.setProperty("javax.xml.xpath.XPathFactory:" + XPathConstants.DOM_OBJECT_MODEL,
                           "org.apache.xpath.XPathFactory");

        // Following is specific to Xalan: should be in a properties file
        System.setProperty("javax.xml.transform.TransformerFactory",
                           "org.apache.xalan.processor.TransformerFactoryImpl");
/*
        // Following is specific to Saxon: should be in a properties file
        // Requires saxon.jar, saxon-dom.jar and saxon-xpath.jar
        System.setProperty("javax.xml.xpath.XPathFactory:" + XPathConstants.DOM_OBJECT_MODEL,
                "net.sf.saxon.xpath.XPathFactoryImpl");
*/
    }

/*
    public static void printRelatedNodes(final List<IEXPath> ieXPathList, final Document document)
            throws XPathExpressionException
    {
        for (IEXPath ieXPath : ieXPathList)
        {
            for (; ieXPath != null; ieXPath = ieXPath.next)
            {
                final NodeList commonXPathNodeList =
                        (NodeList) xpath.evaluate(ieXPath.commonXPath, document, XPathConstants.NODESET);
                for (int i = 0; i < commonXPathNodeList.getLength(); i++)
                {
                    final Node commonNode = commonXPathNodeList.item(i);

                    System.out.println(DOMUtil.getXPath(commonNode));

                    for (int seedIndex = 0; seedIndex < ieXPath.nodes.length; seedIndex++)
                    {
                        System.out.println(" : " + ieXPath.xpaths[seedIndex]);
                        if (!"".equals(ieXPath.xpaths[seedIndex]))
                        {
                            final NodeList nodeList =
                                    (NodeList) xpath
                                            .evaluate(ieXPath.xpaths[seedIndex], commonNode, XPathConstants.NODESET);

                            for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++)
                            {
                                final Node node = nodeList.item(nodeIndex);
                                if (node != null)
                                {
                                    final SeedNode seedNode = ieXPath.nodes[seedIndex];
                                    final HtmlNode htmlNode = new HtmlNode(node);
                                    if (seedNode.getSeed().getConcept().getPattern() == null ||
                                        seedNode.getSeed().getConcept().getPattern().matcher(htmlNode.getText())
                                                .matches())
                                    {
                                        System.out.print(" :: " + htmlNode.getText() + ": ");
                                        if (seedNode.getAHrefElement() != null && htmlNode.getAHrefElement() != null)
                                        {
                                            System.out.print(htmlNode.getAHrefElement().getAttribute("href"));
                                        }
                                        System.out.print(": ");
                                        if (seedNode.getImgSrcElement() != null && htmlNode.getImgSrcElement() != null)
                                        {
                                            System.out.print(htmlNode.getImgSrcElement().getAttribute("src"));
                                        }
                                        System.out.println();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
*/

    private static class NodePair
    {
        final SeedNode seedNode;
        final HtmlNode htmlNode;

        NodePair(final SeedNode seedNode, final HtmlNode htmlNode)
        {
            this.seedNode = seedNode;
            this.htmlNode = htmlNode;
        }

        public String toString()
        {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(seedNode.getSeed().getConcept().getName()).append(" :: ");
            buffer.append(htmlNode.getText()).append(": ");
            if (seedNode.getHtmlNode().getAHrefElement() != null && htmlNode.getAHrefElement() != null)
            {
                buffer.append(htmlNode.getAHrefElement().getAttribute("href"));
            }
            buffer.append(": ");
            if (seedNode.getHtmlNode().getImgSrcElement() != null && htmlNode.getImgSrcElement() != null)
            {
                buffer.append(htmlNode.getImgSrcElement().getAttribute("src"));
            }
            return buffer.toString();
        }
    }

    public static class Result
    {
        private static int counter = 0;
        final String name;
        final private Map<Node, List<NodePair>> nodePairMap = new LinkedHashMap<Node, List<NodePair>>();
        final List<Result> next = new ArrayList<Result>();
        Result previous = null;

        public Result(final Result parent)
        {
            name = (parent == null ? "" : parent.name + ".") + counter++;
        }

        public void add(final Node commonNode, final SeedNode seedNode, final HtmlNode htmlNode)
        {
            List<NodePair> list = nodePairMap.get(commonNode);
            if (list == null)
            {
                list = new ArrayList<NodePair>();
                nodePairMap.put(commonNode, list);
            }
            list.add(new NodePair(seedNode, htmlNode));
        }

        public void add(final Node commonNode, final List<NodePair> nodePairs)
        {
            final List<NodePair> list = nodePairMap.get(commonNode);
            if (list == null)
            {
                nodePairMap.put(commonNode, nodePairs);
            }
            else
            {
                list.addAll(nodePairs);
            }
        }

        public void printTree()
        {
            printTree("");
        }

        public void printTree(final String indent)
        {
            System.out.println(name + ": " + nodePairMap.size() + ": " + next.size());
            for (final Node commonNode : nodePairMap.keySet())
            {
                System.out.println(indent + DOMUtil.getXPath(commonNode));
                final List<NodePair> nodePairList = nodePairMap.get(commonNode);
                for (final NodePair nodePair : nodePairList)
                {
                    System.out.println(indent + nodePair);
                }
            }
            for (final Result result : next)
            {
                result.printTree(indent + "  ");
            }
        }
    }

    /*
        public static boolean consistentResults(final Result results1, final Result results2)
        {
            if (results1.seedGroup != results2.seedGroup)
                return false;

            for (final Seed seed : results1.seedGroup.getSeeds())
            {
                for (final NodePair nodePair1 : results1.getNodePairList(seed.getConcept()))
                {
                    final List<NodePair> list2 = results2.getNodePairList(seed.getConcept());
                    for (final NodePair nodePair2 : list2)
                    {
                        if (!compareText(nodePair1, nodePair2)) return false;
                        if (!compareText(nodePair2, nodePair1)) return false;
                    }
                }
            }
            return true;
        }
    */
    public static boolean compareText(final NodePair nodePair1, final NodePair nodePair2)
    {
        final String text1 = nodePair1.htmlNode.getText().trim();
        final String text2 = nodePair2.htmlNode.getText().trim();
        if (nodePair1.seedNode.getTextPosition() == SeedNode.EQUALS)
        {
            if (nodePair2.seedNode.getTextPosition() == SeedNode.EQUALS)
            {
                if (!text2.equals(text1))
                {
                    return false;
                }
            }
            else if (nodePair2.seedNode.getTextPosition() == SeedNode.STARTSWITH)
            {
                if (!text2.startsWith(text1))
                {
                    return false;
                }
            }
            else if (nodePair2.seedNode.getTextPosition() == SeedNode.ENDSWITH)
            {
                if (!text2.endsWith(text1))
                {
                    return false;
                }
            }
            else
            {
                if (!text2.contains(text1))
                {
                    return false;
                }
            }
        }

        return true;
    }

    /*
        public static void getResults(final List<IEXPath> ieXPathList, final Document document)
                throws XPathExpressionException
        {
            for (final IEXPath ieXPath : ieXPathList)
            {
                final Result result = new Result(null);
                getResults(ieXPath, "", document, result);
                result.printTree();
            }
        }

        public static void getResults(final IEXPath ieXPath, String commonXPath,
                                      final Node contextNode, Result result)
        {
            if (ieXPath != null)
            {
                try
                {
                    commonXPath = "." + ieXPath.commonXPath.substring(commonXPath.length());
                    final NodeList commonXPathNodeList =
                            (NodeList) xpath.evaluate(commonXPath, contextNode, XPathConstants.NODESET);

                    for (int i = 0; i < commonXPathNodeList.getLength(); i++)
                    {
                        final Node commonNode = commonXPathNodeList.item(i);
                        final List<NodePair> nodePairs = new ArrayList<NodePair>();
                        for (int seedIndex = 0; seedIndex < ieXPath.nodes.length; seedIndex++)
                        {
                            final SeedNode seedNode = ieXPath.nodes[seedIndex];
                            if (seedNode != null)
                            {
                                final NodeList nodeList = (NodeList) xpath
                                        .evaluate(ieXPath.xpaths[seedIndex], commonNode, XPathConstants.NODESET);
                                for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++)
                                {
                                    final Node node = nodeList.item(nodeIndex);
                                    if (node != null)
                                    {
                                        final HtmlNode htmlNode = new HtmlNode(node);
                                        final Pattern conceptPattern = seedNode.getSeed().getConcept().getPattern();
                                        if (conceptPattern == null || conceptPattern.matcher(htmlNode.getText()).matches())
                                        {
                                            final NodePair nodePair = new NodePair(seedNode, htmlNode);
                                            nodePairs.add(nodePair);
                                        }
                                    }
                                }
                            }
                        }
                        if (!nodePairs.isEmpty())
                        {
                            result.add(commonNode, nodePairs);
                            final Result nextResult = new Result(result);
                            result.next.add(nextResult);
                            nextResult.previous = result;
                            result = nextResult;
                        }
                        getResults(ieXPath.next, ieXPath.commonXPath, commonNode, result);
                        if ((i == commonXPathNodeList.getLength() - 1) && result.nodePairMap.isEmpty())
                        {
                            if (result.previous != null)
                            {
                                result.previous.next.remove(result);
                            }
                        }
                    }
                }
                catch (XPathExpressionException e)
                {
                    e.printStackTrace();
                }
            }
        }
    */
    /*
        private static final Pattern HTML_TEXT_FORMAT_ELEMENTS =
                Pattern.compile("(<b>|<i>|<u>|<sub>|<sup>|<big>|<small>|<blink>|<strike>|<tt>|<pre>|<em>|<strong>|<var>)",
                        Pattern.CASE_INSENSITIVE);
    */
    private static final Pattern HTML_FONT_ELEMENT =
            Pattern.compile("<font(>|\\s+.*?>)",
                            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    // todo make sure that the text is case insensitive
    // todo this involves both the query terms and html document being in lower case
    private static String preprocessHTML(String html)
    {
        // text formatting elements can cause query string to be split
        // however they can also provide useful information in dividing related text
        // this removes the opening tag and lets the Mozilla parser sort out the close
        //html = HTML_TEXT_FORMAT_ELEMENTS.matcher(html).replaceAll("");
        html = HTML_FONT_ELEMENT.matcher(html).replaceAll("");

        // Unfortunately it there is a problem with newlines/spaces which are percieved in some way as nodes.
        // need to remove some spaces to make the child::text XPath works
        html = html.replaceAll("[\\s]+", " ");
        html = html.replaceAll("[ ]+<", "<");

        // these are handled by my changes to code within the Mozilla Parser
        //html = html.replaceAll("<br>", "\n");
        //html = html.replaceAll("&nbsp;", " ");

        return html.toLowerCase();
    }

    public static Document parseparse(final File file)
            throws IOException, ParserConfigurationException
    {
        final String html = FileUtils.readFileToString(file);
        // todo the Mozilla Parser only considers the HTML body therefore the <title> text is ignored
        final Document document = DOMUtil.parse(preprocessHTML(html), "utf8");
        document.setDocumentURI(file.toURI().toString());
        return document;
    }

    private static class CreationTitleDate
    {
        final String title;
        final String date;

        public CreationTitleDate(final String title, final String date)
        {
            this.title = title;
            this.date = date;
        }
    }

    private final static Pattern ONLY_CIRCA_OR_NMBERS_PATTERN =
            Pattern.compile("(circa\\s*)?\\d+", Pattern.CASE_INSENSITIVE);
    private final static Pattern CIRCA_PATTERN = Pattern.compile("(circa\\s*)", Pattern.CASE_INSENSITIVE);
    private final static Pattern NO_OR_UNKNOWN_TITLE_PATTERN =
            Pattern.compile("[,\\s]*\\[?(no title|untitled|unknown)\\]?", Pattern.CASE_INSENSITIVE);

    private static Map<Person, List<CreationTitleDate>> getTateCollection()
            throws FileNotFoundException, JAXBException
    {
        //final String xmlFilename = "C:\\home\\nsi\\experiments\\nutch\\tate\\TateCollectionMMMD1.1.xml";
        final String xmlFilename =
                "/data/multimatch/PT2/out/out2/out3/out4/actor/urn_multimatch_nonCached_text_xml_455cf1bf48a9984a1372532e1e7f8786";
        System.out.print("Unmarshalling Tate Collection XML file: " + xmlFilename + "...");
        // create a JAXBContext capable of handling classes generated into
        // the org.multimatch.metadata package
        final JAXBContext jaxbContext = JAXBContext.newInstance("org.multimatch.schema.multimatchmetadata_2_2");

        // create an Unmarshaller
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        // unmarshal a MMEntity instance document into a tree of Java content
        // objects composed of classes from this package.
        final MMEntity mmEntity = (MMEntity) unmarshaller.unmarshal(new FileInputStream(xmlFilename));
        System.out.println(" done.");

        System.out.print("Creating Tate Collection Creator - Creation(Title,Date) Map");
        int personCounter = 0;
        final Map<Person, List<CreationTitleDate>> creators = new HashMap<Person, List<CreationTitleDate>>();
        if (mmEntity != null)
        {
            final List<Creation> creations = mmEntity.getCreation();
            final List<Actor> actors = mmEntity.getActor();
            if (actors != null && creations != null)
            {
                for (final Actor actor : actors)
                {
                    final Person person = actor.getPerson();
                    if (person != null)
                    {
                        if ((++personCounter % 1000) == 0)
                        {
                            System.out.print('.');
                            break;
                        }
                        final List<Organisation.RelatedCreation> relatedCreations = person.getRelatedCreation();
                        if (relatedCreations != null)
                        {
                            final List<CreationTitleDate> creationTitleDates = new ArrayList<CreationTitleDate>();
                            for (final Organisation.RelatedCreation relatedCreation : relatedCreations)
                            {
                                final Organisation.RelatedCreation.Date date = relatedCreation.getDate();
                                if (date != null)
                                {
                                    String creationDate = date.getDisplayDate();
                                    if (creationDate != null &&
                                        ONLY_CIRCA_OR_NMBERS_PATTERN.matcher(creationDate.trim()).matches())
                                    {
                                        creationDate = CIRCA_PATTERN.matcher(creationDate).replaceAll("c");
                                        // Compares the RelatedCreation identifier to the Creation identifier
                                        //noinspection SuspiciousMethodCalls
                                        final int creationIndex = creations.indexOf(relatedCreation);
                                        if (creationIndex != -1)
                                        {
                                            final Creation creation = creations.get(creationIndex);
                                            final StillImage stillImage = creation.getStillImage();
                                            if (stillImage != null)
                                            {
                                                final List<ManualAutoPreferredLinguisticType> titles =
                                                        stillImage.getTitle();
                                                if (titles != null)
                                                {
                                                    for (final ManualAutoPreferredLinguisticType title : titles)
                                                    {
                                                        final String safeTitle = NO_OR_UNKNOWN_TITLE_PATTERN
                                                                .matcher(title.getValue()).replaceAll("").trim();
                                                        if (!"".equals(safeTitle))
                                                        {
                                                            creationTitleDates
                                                                    .add(new CreationTitleDate(safeTitle,
                                                                                               creationDate.trim()));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (!creationTitleDates.isEmpty())
                            {
                                creators.put(person, creationTitleDates);
                            }
                        }
                    }
                }
            }
        }
        System.out.println(" done.");

        return creators;
    }

    private static void process(final SeedNodeGroup seedNodeGroup)
            throws XPathExpressionException
    {
        seedNodeGroup.addToSeedGroup();
        removeConflictingNodes(seedNodeGroup);
        selectMinTextNodes(seedNodeGroup);
        removeConflictingNodes(seedNodeGroup);
        seedNodeGroup.calculateCommonNodes();
/*
        System.out.println("*********** Common Sub Group Nodes for:\n" + seedNodeGroup + "\n***********");
        for (final CommonNode commonNode : seedNodeGroup.getCommonSubGroupNodes())
        {
            System.out.println(commonNode.toString());
            System.out.println();
            for (final SeedNode seedNode : commonNode.getSeedNodes())
            {
                System.out.println(seedNode);
            }
        }

        final List<List<CommonNode>> ieXPathNodes = new ArrayList<List<CommonNode>>();
        for (final CommonNode commonNode : seedNodeGroup.getCommonSubGroupNodes())
        {
            final List<CommonNode> ieXPath = new ArrayList<CommonNode>();
            commonNode.collectCommonNodes(ieXPath);
            ieXPathNodes.add(ieXPath);
        }

        // convert to XPaths
        for (final List<CommonNode> commonNodes : ieXPathNodes)
        {
            System.out.println("************************************");
            for (final CommonNode commonNode : commonNodes)
            {
                if (commonNode.isLeafNode())
                {
                    System.out.println(DOMUtil.getXPath(commonNode.getNode()));
                    for (final SeedNode seedNode : commonNode.getSeedNodes())
                    {
                        System.out.println(DOMUtil.getXPath(seedNode.getNode()));
                    }
                    //System.out.println(commonNode);
                }
                else
                {
                    System.out.println(DOMUtil.getXPath(commonNode.getNode()));
                }
            }
            System.out.println("------------------------------------");
            for (final CommonNode commonNode : commonNodes)
            {
                final String parentXPath =
                        (commonNode.getParent() == null) ? "" : DOMUtil.getXPath(commonNode.getParent().getNode());
                System.out.println("Parent: " + parentXPath);
                if (commonNode.getParent() != null)
                {
                    System.out.print(".");
                }
                if (commonNode.isLeafNode())
                {
                    System.out.println(DOMUtil.getXPath(commonNode.getNode()).substring(parentXPath.length()));
                    final String commonNodeXPath = DOMUtil.getXPath(commonNode.getNode());
                    for (final SeedNode seedNode : commonNode.getSeedNodes())
                    {
                        System.out.println(
                                "." + DOMUtil.getXPath(seedNode.getNode()).substring(commonNodeXPath.length()));
                    }
                    //System.out.println(commonNode);
                }
                else
                {
                    System.out.println(DOMUtil.getXPath(commonNode.getNode()).substring(parentXPath.length()));
                }
            }
            System.out.println("************************************");
        }
*/
        for (final CommonNode commonNode : seedNodeGroup.getCommonSubGroupNodes())
        {
            relax(seedNodeGroup.getDocument(), commonNode);
        }

/*
            System.out.println("*** getIEXPath ***");
            final List<IEXPath> ieXPathList = IEXPath.getIEXPath(seedNodeGroup);

            System.out.println("*** " + args[0] + " ***");

            printRelatedNodes(ieXPathList, document);

            System.out.println("******* GET RESULTS *******");
            getResults(ieXPathList, document);

            for (int argIndex = 1000; argIndex < args.length; argIndex++)
            {
                final Document otherDocument = parse(new File(args[argIndex]));
                System.out.println("*** " + args[argIndex] + " ***");
                printRelatedNodes(ieXPathList, otherDocument);
            }
*/
        System.out.println("done...");
    }

    /*
     * todo this may well result in adverse effects
     * For example the main text may be "Name (Dates)" and later there could be some random reference to the name
     * in an ahref or bold element.
     */
    private static void selectMinTextNodes(final SeedNodeGroup seedNodeGroup)
    {
        System.out.println("*********** selectMinTextNodes ***********");
        final Map<Seed, List<SeedNode>> documentSeedNodes = seedNodeGroup.getAllSeedNodes();
        for (final Seed seed : documentSeedNodes.keySet())
        {
            final List<SeedNode> seedNodes = documentSeedNodes.get(seed);
            int minTextLength = Integer.MAX_VALUE;
            for (final SeedNode seedNode : seedNodes)
            {
                //System.out.println(seedNode);
                final String text = seedNode.getHtmlNode().getText();
                if (text.length() < minTextLength)
                {
                    minTextLength = text.length();
                }
            }
            /*
            for (final SeedNode seedNode : seedNodes.toArray(new SeedNode[seedNodes.size()]))
            {
                final String text = seedNode.getHtmlNode().getText();
                if (text.length() > minTextLength)
                {
                    System.out.println(seed.getConcept() + "  " + text);
                    System.out.println(seed.getGroup().getDocumentNodes(seedNodeGroup.getDocument())
                                    .removeNode(seed, seedNode.getNode()));
                }
            }
            */
        }
    }

    private static void removeConflictingNodes(final SeedNodeGroup seedNodeGroup)
    {
        System.out.println("*********** removeConflictingNodes ***********");
        final Map<Seed, List<SeedNode>> documentSeedNodes = seedNodeGroup.getAllSeedNodes();
        final Map<Node, List<SeedNode>> documentNodes = new LinkedHashMap<Node, List<SeedNode>>();
        final Map<String, List<SeedNode>> documentXPaths = new LinkedHashMap<String, List<SeedNode>>();
        for (final Seed seed : documentSeedNodes.keySet())
        {
            final List<SeedNode> seedNodes = documentSeedNodes.get(seed);
            for (final SeedNode seedNode : seedNodes)
            {
                List<SeedNode> nodeSeedNodes = documentNodes.get(seedNode.getNode());
                if (nodeSeedNodes == null)
                {
                    nodeSeedNodes = new ArrayList<SeedNode>();
                    documentNodes.put(seedNode.getNode(), nodeSeedNodes);
                }
                nodeSeedNodes.add(seedNode);

                final String seedNodeXPath = DOMUtil.getXPath(seedNode.getNode(), false);
                List<SeedNode> xpathSeedNodes = documentXPaths.get(seedNodeXPath);
                if (xpathSeedNodes == null)
                {
                    xpathSeedNodes = new ArrayList<SeedNode>();
                    documentXPaths.put(seedNodeXPath, xpathSeedNodes);
                }
                xpathSeedNodes.add(seedNode);
            }
        }
/*
        System.out.println("*********** Document Nodes ***********");
        for (final Node node : documentNodes.keySet())
        {
            final List<SeedNode> seedNodes = documentNodes.get(node);
            System.out.println(node.getNodeName());
            for (final SeedNode seedNode : seedNodes)
            {
                System.out.println(DOMUtil.getXPath(seedNode.getNode()));
                System.out.println("    " + seedNode.getSeed().getConcept() + "  " + seedNode.getHtmlNode().getText());
            }
        }

        System.out.println("*********** Document XPaths ***********");
        for (final String seedNodeXPath : documentXPaths.keySet())
        {
            final List<SeedNode> seedNodes = documentXPaths.get(seedNodeXPath);
            final Set<Seed> seeds = new HashSet<Seed>();
            System.out.println(seedNodeXPath);
            for (final SeedNode seedNode : seedNodes)
            {
                seeds.add(seedNode.getSeed());
                //System.out.println("    " + seedNode.getSeed().getConcept() + "  " + seedNode.getHtmlNode().getText());
            }
            for (final SeedNode seedNode : seedNodes.toArray(new SeedNode[seedNodes.size()]))
            {
                final Seed seed = seedNode.getSeed();
                final SeedGroup seedGroup = seed.getGroup();
                final List<SeedGroup> subGroups = seedGroup.getSubGroups();
                for (final SeedGroup subGroup : subGroups)
                {
                    for (final Seed subGroupSeed : subGroup.getAllSeeds())
                    {
                        if (seeds.contains(subGroupSeed))
                        {
                            System.out.println("Parent Seed: " + seed.getConcept() + "  " +
                                               seedNode.getHtmlNode().getText());
                            System.out.println(seedGroup.getDocumentNodes(seedNodeGroup.getDocument())
                                    .removeNode(seed, seedNode.getNode()));
                        }
                    }
                }
            }
        }
*/
    }

    public static void printAllNodes(final Document document, final CommonNode commonNode)
            throws XPathExpressionException
    {
        final List<SeedNode> seedNodes = commonNode.getSeedNodes();
        for (final SeedNode seedNode : seedNodes)
        {
            final String seedNodeXPath = DOMUtil.getXPath(seedNode.getNode());
            final Node otherNode = (Node) xpath.evaluate(seedNodeXPath, document, XPathConstants.NODE);

            final SeedNode otherSeedNode = new SeedNode(seedNode.getSeed(), otherNode);
            System.out.println(seedNode.getSeed().getConcept().getName() + " : " +
                               otherSeedNode.getHtmlNode().getText().replaceAll("[\\s]+", " "));
        }
    }

    // todo should recieve results from super seed node groups and ensure that they results are consistent.
    // todo i.e. if the node is expected to contain a seed of type X does it contain text from the seed node X.
    public static void relax(final Node contextNode, final CommonNode commonNode)
            throws XPathExpressionException
    {
        final String commonNodeXPath = DOMUtil.getXPath(commonNode.getNode());
        final String relaxedcommonNodeXPath;
        // if parent is null then this is the root of the tree and varying the commonNode should get
        // whole new tree.
        if (commonNode.getParent() == null)
        {
            relaxedcommonNodeXPath = "." + DOMUtil.removeXPathPositionFilters(commonNodeXPath);
        }
        else
        {
            final SeedGroup thisSeedGroup = commonNode.seedNodeGroup.getSeedGroup();
            final SeedGroup parentSeedGroup = commonNode.getParent().seedNodeGroup.getSeedGroup();
            final Relation.Cardinality cardinality = parentSeedGroup.getbGroupCardinality(thisSeedGroup);
            if (cardinality == null || Relation.Cardinality.isSingleRecipient(cardinality))
            {
                return;
            }

            final String parentXPath = DOMUtil.getXPath(commonNode.getParent().getNode());
            final String localCommonNodeXPath = commonNodeXPath.substring(parentXPath.length());
            relaxedcommonNodeXPath = "." + parentXPath + DOMUtil.removeXPathPositionFilters(localCommonNodeXPath);
        }

        //System.out.println("  CommonXPath: " + relaxedcommonNodeXPath);
        final NodeList nodeList =
                (NodeList) xpath.evaluate(relaxedcommonNodeXPath, contextNode, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            final Node node = nodeList.item(i);
            final List<SeedNode> seedNodes = commonNode.getSeedNodes();

            if ("true".equals("true"))
            {
                final Map<SeedNode, HtmlNode> nodes = new HashMap<SeedNode, HtmlNode>(seedNodes.size());
                for (final SeedNode seedNode : seedNodes)
                {
                    final Seed seed = seedNode.getSeed();
                    final Concept concept = seed.getConcept();
                    String seedNodeXPath = DOMUtil.getXPath(seedNode.getNode());
                    seedNodeXPath = "." + seedNodeXPath.substring(commonNodeXPath.length());
                    //System.out.println("SeedNodeXPath (" + concept + "): " + seedNodeXPath);
                    final Node otherNode = (Node) xpath.evaluate(seedNodeXPath, node, XPathConstants.NODE);

                    if (otherNode != null)
                    {
                        final HtmlNode htmlNode = HtmlNode.getNode(otherNode);
                        final String text = htmlNode.getText();
                        //System.out.println("        " + concept.getName() + " : " + text.replaceAll("[\\s]+", " "));
                        if (!"".equals(text) &&
                            (concept.getPattern() == null || concept.getPattern().matcher(text).matches()))
                        {
                            nodes.put(seedNode, htmlNode);
                        }
                    }
                }
                if (nodes.values().size() == seedNodes.size())
                {
                    for (final SeedNode seedNode : nodes.keySet())
                    {
                        final HtmlNode otherNode = nodes.get(seedNode);
                        if (otherNode == seedNode.getHtmlNode())
                        {
                            System.out.print("#####     ");
                        }
                        else
                        {
                            System.out.print("*****     ");
                        }
                        System.out.println(seedNode.getSeed().getConcept() + " : " +
                                           otherNode.getText().replaceAll("[\\s]+", " "));
                    }
                    System.out.println();
                }
            }
            // todo *** NOTE THIS DOES NOT WORK ***
            // todo this approach needs to group the related, valid nodes
            // todo it is possible this can be based on relative positions i.e.
            // todo ./a[N]/text()[2] link to  ./p[N+2]/text()[1]
            // todo
            // todo
            else
            {
                if ("".equals(""))
                {
                    throw new UnsupportedOperationException("Relaxing of path to specific seed node does not yet work");
                }
                final Map<Seed, List<HtmlNode>> nodes = new HashMap<Seed, List<HtmlNode>>(seedNodes.size());
                for (final SeedNode seedNode : seedNodes)
                {
                    nodes.put(seedNode.getSeed(), new ArrayList<HtmlNode>());
                    // todo throws an exception to show this method is not yet implemented
                }
                for (final SeedNode seedNode : seedNodes)
                {
                    final Seed seed = seedNode.getSeed();
                    final Concept concept = seed.getConcept();
                    String seedNodeXPath = DOMUtil.getXPath(seedNode.getNode());
                    seedNodeXPath = "." + seedNodeXPath.substring(commonNodeXPath.length());
                    // todo removing filters may be too relaxed
                    seedNodeXPath = DOMUtil.removeXPathPositionFilters(seedNodeXPath);
                    //System.out.println("SeedNodeXPath (" + concept + "): " + seedNodeXPath);
                    final NodeList otherNodeList =
                            (NodeList) xpath.evaluate(seedNodeXPath, node, XPathConstants.NODESET);

                    for (int j = 0; j < otherNodeList.getLength(); j++)
                    {
                        final Node otherNode = otherNodeList.item(j);
                        if (otherNode != null)
                        {
                            final HtmlNode htmlNode = HtmlNode.getNode(otherNode);
                            final String text = htmlNode.getText();
                            //System.out.println("        " + concept.getName() + " : " + text.replaceAll("[\\s]+", " "));
                            if (otherNode != seedNode.getNode() && !"".equals(text) &&
                                (concept.getPattern() == null || concept.getPattern().matcher(text).matches()))
                            {
                                nodes.get(seedNode.getSeed()).add(htmlNode);
                            }
                        }
                    }
                }
                boolean validSeedGroup = true;
                for (final List<HtmlNode> otherSeedNodes : nodes.values())
                {
                    if (otherSeedNodes.isEmpty())
                    {
                        validSeedGroup = false;
                        break;
                    }
                }
                if (validSeedGroup)
                {
                    for (final Seed seed : nodes.keySet())
                    {
                        for (final HtmlNode otherNode : nodes.get(seed))
                        {
                            System.out.println("*****     " + seed.getConcept() + " : " +
                                               otherNode.getText().replaceAll("[\\s]+", " "));
                        }
                    }
                }
            }
        }
        if (!commonNode.isLeafNode())
        {
            for (final CommonNode node : (CommonNode[]) commonNode.getNodes())
            {
                relax(contextNode, node);
            }
        }

    }

    private static final Pattern MULTIPLE_NON_DIGITS_PATTERN = Pattern.compile("\\D+");
    private static final Pattern MULTIPLE_NON_UNICODE_CHARACTERS_PATTERN = Pattern.compile("\\P{L}+");
    private static final String MULTIPLE_SPACE_PUNCTUATION_STRING = "(\\s|\\p{Punct})+";
    private static final Pattern MULTIPLE_SPACE_PUNCTUATION_PATTERN =
            Pattern.compile(MULTIPLE_SPACE_PUNCTUATION_STRING);
    private static final Pattern CIRCA_DATE_PATTERN =
            Pattern.compile("(?<=[^a-z]|\\G|\\b)(circa|ca|c)[.]?[\\s]*([0-9]{1,4})(?=[^0-9]|$)",
                            Pattern.CASE_INSENSITIVE);
    private static final String PLACE_HOLDER_FOR_CIRCA = "PlAcEhOlDeRfOrCiRcA";
    private static final Pattern STARTS_WITH_NUMBER = Pattern.compile("^([0-9])");
    private static final Pattern ENDS_WITH_NUMBER = Pattern.compile("([0-9])$");

    private static String digitsToXPathContainsString(final String date, final String nodeName)
    {
        final StringBuffer buffer = new StringBuffer();
        final String[] splits = MULTIPLE_NON_DIGITS_PATTERN.split(date);
        for (int i = splits.length - 1; i > 0; i--)
        {
            buffer.append("contains(").append(nodeName).append(",'").append(splits[i]).append("') and ");
        }
        buffer.append("contains(").append(nodeName).append(",'").append(splits[0]).append("')");
        return buffer.toString();
    }

    private static String unicodeCharactersToXPathContainsString(final String words, final String nodeName)
    {
        final StringBuffer buffer = new StringBuffer();
        final String[] splits = MULTIPLE_NON_UNICODE_CHARACTERS_PATTERN.split(words);
        for (int i = splits.length - 1; i > 0; i--)
        {
            buffer.append("contains(").append(nodeName).append(",'").append(splits[i]).append("') and ");
        }
        buffer.append("contains(").append(nodeName).append(",'").append(splits[0]).append("')");
        return buffer.toString();
    }

    /**
     * Returns a Pattern matching a generalised version of the date.
     * Generalises circa to "(circa|ca|c)[.]?" and space/punctuation to "(\s|\p{Punct})+"
     * If the date begins or ends with a numeric then the returned Pattern does not match
     * if preceeded or followed by a numeric, respectively.
     * If the date begins with a circa (i.e. "(circa|ca|c)[.]?") then the returned Pattern
     * does not match if preceeded by an [a-zA-Z\b] character.
     * Thus "c1945-47circa1923" matches "Item1239ca. 1945-47:c. 1923"
     *
     * @param date a String representing a date.
     * @return Pattern matching a generalised version of the date.
     *         The first group ($1) matches the string matching the generalised date.
     */
    private static Pattern dateToRegexp(final String date)
    {
        String normalisedDate = CIRCA_DATE_PATTERN.matcher(date).replaceAll(PLACE_HOLDER_FOR_CIRCA + "$2");
        normalisedDate = MULTIPLE_SPACE_PUNCTUATION_PATTERN.matcher(normalisedDate).replaceAll("(\\\\s|\\\\p{Punct})+");
        if (normalisedDate.startsWith(PLACE_HOLDER_FOR_CIRCA))
        {
            normalisedDate = normalisedDate.replaceFirst(
                    PLACE_HOLDER_FOR_CIRCA,
                    "(?<=[^a-z]|\\\\G|\\\\b)(circa|ca|c)[.]?[\\\\s]*");
        }
        normalisedDate = normalisedDate.replace(PLACE_HOLDER_FOR_CIRCA, "(\\s|\\p{Punct})*(circa|ca|c)[.]?[\\s]*");
        normalisedDate = STARTS_WITH_NUMBER.matcher(normalisedDate).replaceAll("(?<=[^0-9]|^)$1");
        normalisedDate = ENDS_WITH_NUMBER.matcher(normalisedDate).replaceAll("$1(?<=[^0-9]|\\$)");
        return Pattern.compile(".*?(" + normalisedDate + ").*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }

    private static Pattern nameToRegexp(final String name)
    {
        final StringBuffer buffer = new StringBuffer();
        final String[] splits = MULTIPLE_SPACE_PUNCTUATION_PATTERN.split(name);
        final Permutation permutation = new Permutation(splits);
        while (permutation.hasMoreElements())
        {
            final Object[] nextPermutation = (Object[]) permutation.nextElement();
            for (int i = nextPermutation.length - 1; i > 0; i--)
            {
                buffer.append(nextPermutation[i]);
                buffer.append(MULTIPLE_SPACE_PUNCTUATION_STRING);
            }
            buffer.append(nextPermutation[0]);
            if (permutation.hasMoreElements())
            {
                buffer.append("|");
            }
        }
        return Pattern.compile(".*?(" + buffer + ").*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    }

    private static final long RANDOM_SEED = 12345;

    private void processTate(String tateCrawlDir)
    {
        // todo fix for new nutch, which uses solr
//        NutchQuery nutchQuery = new NutchQuery(tateCrawlDir);
        final Concept artistName =
                new Concept("ArtistName", Concept.TEXT | Concept.A_HREF | Concept.IMG_SRC);
        final Concept artworkTitle =
                new Concept("ArtworkTitle", Concept.TEXT | Concept.A_HREF | Concept.IMG_SRC);
        final Concept artworkDate =
                new Concept("ArtworkDate", Concept.TEXT | Concept.A_HREF | Concept.IMG_SRC,
                            Pattern.compile("(^|[^0-9])[0-9]{2,4}([^0-9]|$)"));

        final Random random = new Random(RANDOM_SEED);

        try
        {
            final Map<Person, List<CreationTitleDate>> tateCreatorsCreations = getTateCollection();
            final Person[] tateCreators =
                    tateCreatorsCreations.keySet().toArray(new Person[tateCreatorsCreations.size()]);
            for (int i = tateCreators.length - 1; i >= 0; i--)
            {
                final int j = random.nextInt(i + 1);
                if (i != j)
                {
                    final Person temp = tateCreators[i];
                    tateCreators[i] = tateCreators[j];
                    tateCreators[j] = temp;
                }
            }

            for (final Person tateCreator : tateCreators)
            {
                final List<ManualAutoPreferredType> creatorNames = tateCreator.getName();
                for (ManualAutoPreferredType creatorName : creatorNames)
                {
                    final String name = creatorName.getValue().toLowerCase();
                    System.out.println(tateCreator.getName());

                    for (final CreationTitleDate tateCreatorCreation : tateCreatorsCreations.get(tateCreator))
                    {
                        System.out.println(tateCreatorCreation.title);
                        System.out.println(tateCreatorCreation.date);

                        final String queryString =
                                tateCreator.getName() + " " +
                                tateCreatorCreation.title + " " +
                                tateCreatorCreation.date;

                        final Seed artistNameSeed =
                                new Seed(artistName, creatorName.getValue(),
                                         unicodeCharactersToXPathContainsString(name, "."),
                                         unicodeCharactersToXPathContainsString(name, "@alt"),
                                         nameToRegexp(name));

                        final String creationSafeTitle =
                                MULTIPLE_SPACE_PUNCTUATION_PATTERN.matcher(tateCreatorCreation.title.toLowerCase())
                                                                  .replaceAll(" ");

                        final Seed artworkTitleSeed =
                                new Seed(artworkTitle, tateCreatorCreation.title,
                                         "contains(.,'" + creationSafeTitle + "')",
                                         "contains(@alt,'" + creationSafeTitle + "')");

                        final String creationDate = tateCreatorCreation.date.toLowerCase();
                        final Seed artworkDateSeed =
                                new Seed(artworkDate, tateCreatorCreation.date,
                                         digitsToXPathContainsString(creationDate, "."),
                                         digitsToXPathContainsString(creationDate, "@alt"),
                                         dateToRegexp(creationDate));

                        final List<Seed> artistNameSeeds = new ArrayList<Seed>();
                        artistNameSeeds.add(artistNameSeed);
                        final List<Seed> artworkTitleDateSeeds = new ArrayList<Seed>();
                        artworkTitleDateSeeds.add(artworkTitleSeed);
                        artworkTitleDateSeeds.add(artworkDateSeed);

                        final SeedGroup rootSeedGroup = new SeedGroup(null, artistNameSeeds);
                        final SeedGroup subGroup = new SeedGroup(rootSeedGroup, artworkTitleDateSeeds);
                        rootSeedGroup.addSubGroup(subGroup, Relation.Cardinality.ONE_TO_MANY);

                        try
                        {
                            // get html pages matching the tate seed from NUTCH
                            // todo fix for new nutch, which uses solr
//                            final HitDetails[] hitDetails = nutchQuery.queryNutch(queryString);
//
//                            for (final HitDetails hitDetail : hitDetails)
//                            {
//                                final byte[] content = nutchQuery.getContent(hitDetail);
//                                final String html = new String(content);
//                                final String uri = hitDetail.getValue("url");
//
//                                final Document document = DOMUtil.parse(preprocessHTML(html).getBytes(), "utf8");
//                                document.setDocumentURI(uri);
//                                System.out.println(uri);
//
//                                try
//                                {
//                                    //System.out.println("Generated document:\n" + DOMUtil.getNodeSubtreeXMLString(document));
//                                    //DOMUtil.printNodeSubtree(document);
//
//                                    if (logger.getLevel() == Level.INFO)
//                                    {
//                                        logger.info(
//                                                "Generated document:\n" + DOMUtil.getNodeSubtreeXMLString(document));
//                                    }
//
//                                    final SeedNodeGroup seedNodeGroup =
//                                            new SeedNodeGroup(rootSeedGroup, document, xpath);
//                                    process(seedNodeGroup);
//                                }
//                                catch (XPathExpressionException e)
//                                {
//                                    e.printStackTrace();
//                                }
//                                catch (SeedNotFoundException e)
//                                {
//                                    System.out.println(e.getMessage());
//                                }
//                            }
//                        }
//                        catch (DOMException e)
//                        {
//                            System.err.println(e);
//                        }
//                        catch (IOException e)
//                        {
//                            e.printStackTrace();
//                        }
//                        catch (ParserConfigurationException e)
//                        {
//                            e.printStackTrace();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Return a string describing the usage of this class.
     *
     * @return <code>String</code> describing the usage of this class.
     */
    public static String usage()
    {
        return "Usage: java " + IEXPathWrapper.class.getCanonicalName()
               + "\n\t-?|--help         : print this"
               + "\n\t-d|--dir          : crawl directory"
               + "\n\t-q|--query        : query"
                ;
    }

    /**
     * Main method for command line interface.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args)
    {
        String crawlDir = null;
        String query = null;

        int argIndex = 0;
        while (argIndex < args.length)
        {
            final String argument = args[argIndex++];
            if ("-?".equals(argument) || "--help".equals(argument))
            {
                System.out.println(usage());
            }
            else if ("-d".equals(argument) || "--dir".equals(argument))
            {
                crawlDir = args[argIndex++];
            }
            else if ("-q".equals(argument) || "--query".equals(argument))
            {
                query = args[argIndex++];
            }
            else
            {
                throw new IllegalArgumentException("Unknown argument: " + argument);
            }
        }
        
        

        // todo fix for new nutch, which uses solr
//        try
//        {
//            NutchQuery nutchQuery = new NutchQuery(crawlDir);
//
//            // get html pages matching the tate seed from NUTCH
//            final HitDetails[] hitDetails = nutchQuery.queryNutch(query);
//
//            for (final HitDetails hitDetail : hitDetails)
//            {
//                final byte[] content = nutchQuery.getContent(hitDetail);
//                final String html = new String(content);
//                final String uri = hitDetail.getValue("url");
//
//                final Document document = DOMUtil.parse(preprocessHTML(html).getBytes(), "utf8");
//                document.setDocumentURI(uri);
//                System.out.println(uri);
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }

    }


}