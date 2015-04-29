//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.28 at 04:25:03 PM GMT 
//


package org.multimatch.schema.multimatchmetadata_2_2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://multimatch.org/schema/multimatchMetadata-2.2.1}FeedElements"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identifier",
    "homepage",
    "type",
    "language",
    "dateCaptured",
    "title",
    "description",
    "publisher",
    "subject",
    "tags",
    "relatedCreator",
    "relatedCreation",
    "relatedWebsite",
    "relatedWebpage",
    "links",
    "content",
    "rightsHolder",
    "copyright",
    "licenseCondition",
    "feedInformation"
})
@XmlRootElement(name = "Feed")
public class Feed {

    @XmlElement(name = "Identifier", required = true)
    protected String identifier;
    @XmlElement(name = "Homepage", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String homepage;
    @XmlElement(name = "Type")
    protected List<ManualAutoType> type;
    @XmlElement(name = "Language", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String language;
    @XmlElement(name = "DateCaptured", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateCaptured;
    @XmlElement(name = "Title")
    protected LinguisticType title;
    @XmlElement(name = "Description")
    protected List<ManualAutoPreferredLinguisticType> description;
    @XmlElement(name = "Publisher")
    protected ManualAutoType publisher;
    @XmlElement(name = "Subject")
    protected List<SubjectType> subject;
    @XmlElement(name = "Tags")
    protected List<ManualAutoLinguisticType> tags;
    @XmlElement(name = "RelatedCreator")
    protected List<ManualAutoAnchorType> relatedCreator;
    @XmlElement(name = "RelatedCreation")
    protected List<ManualAutoAnchorType> relatedCreation;
    @XmlElement(name = "RelatedWebsite")
    protected List<ManualAutoAnchorType> relatedWebsite;
    @XmlElement(name = "RelatedWebpage")
    protected List<ManualAutoAnchorType> relatedWebpage;
    @XmlElement(name = "Links")
    protected List<ManualAutoType> links;
    @XmlElement(name = "Content")
    protected ContentType content;
    @XmlElement(name = "RightsHolder")
    protected ManualAutoType rightsHolder;
    @XmlElement(name = "Copyright")
    protected ManualAutoType copyright;
    @XmlElement(name = "LicenseCondition")
    protected ManualAutoType licenseCondition;
    @XmlElement(name = "FeedInformation")
    protected Feed.FeedInformation feedInformation;

    /**
     * Gets the value of the identifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentifier(String value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the homepage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * Sets the value of the homepage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHomepage(String value) {
        this.homepage = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the type property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManualAutoType }
     * 
     * 
     */
    public List<ManualAutoType> getType() {
        if (type == null) {
            type = new ArrayList<ManualAutoType>();
        }
        return this.type;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Gets the value of the dateCaptured property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateCaptured() {
        return dateCaptured;
    }

    /**
     * Sets the value of the dateCaptured property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateCaptured(XMLGregorianCalendar value) {
        this.dateCaptured = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link LinguisticType }
     *     
     */
    public LinguisticType getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link LinguisticType }
     *     
     */
    public void setTitle(LinguisticType value) {
        this.title = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManualAutoPreferredLinguisticType }
     * 
     * 
     */
    public List<ManualAutoPreferredLinguisticType> getDescription() {
        if (description == null) {
            description = new ArrayList<ManualAutoPreferredLinguisticType>();
        }
        return this.description;
    }

    /**
     * Gets the value of the publisher property.
     * 
     * @return
     *     possible object is
     *     {@link ManualAutoType }
     *     
     */
    public ManualAutoType getPublisher() {
        return publisher;
    }

    /**
     * Sets the value of the publisher property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManualAutoType }
     *     
     */
    public void setPublisher(ManualAutoType value) {
        this.publisher = value;
    }

    /**
     * Gets the value of the subject property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subject property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubjectType }
     * 
     * 
     */
    public List<SubjectType> getSubject() {
        if (subject == null) {
            subject = new ArrayList<SubjectType>();
        }
        return this.subject;
    }

    /**
     * Gets the value of the tags property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tags property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTags().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManualAutoLinguisticType }
     * 
     * 
     */
    public List<ManualAutoLinguisticType> getTags() {
        if (tags == null) {
            tags = new ArrayList<ManualAutoLinguisticType>();
        }
        return this.tags;
    }

    /**
     * Gets the value of the relatedCreator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedCreator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedCreator().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManualAutoAnchorType }
     * 
     * 
     */
    public List<ManualAutoAnchorType> getRelatedCreator() {
        if (relatedCreator == null) {
            relatedCreator = new ArrayList<ManualAutoAnchorType>();
        }
        return this.relatedCreator;
    }

    /**
     * Gets the value of the relatedCreation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedCreation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedCreation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManualAutoAnchorType }
     * 
     * 
     */
    public List<ManualAutoAnchorType> getRelatedCreation() {
        if (relatedCreation == null) {
            relatedCreation = new ArrayList<ManualAutoAnchorType>();
        }
        return this.relatedCreation;
    }

    /**
     * Gets the value of the relatedWebsite property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedWebsite property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedWebsite().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManualAutoAnchorType }
     * 
     * 
     */
    public List<ManualAutoAnchorType> getRelatedWebsite() {
        if (relatedWebsite == null) {
            relatedWebsite = new ArrayList<ManualAutoAnchorType>();
        }
        return this.relatedWebsite;
    }

    /**
     * Gets the value of the relatedWebpage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedWebpage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedWebpage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManualAutoAnchorType }
     * 
     * 
     */
    public List<ManualAutoAnchorType> getRelatedWebpage() {
        if (relatedWebpage == null) {
            relatedWebpage = new ArrayList<ManualAutoAnchorType>();
        }
        return this.relatedWebpage;
    }

    /**
     * Gets the value of the links property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the links property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManualAutoType }
     * 
     * 
     */
    public List<ManualAutoType> getLinks() {
        if (links == null) {
            links = new ArrayList<ManualAutoType>();
        }
        return this.links;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link ContentType }
     *     
     */
    public ContentType getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentType }
     *     
     */
    public void setContent(ContentType value) {
        this.content = value;
    }

    /**
     * Gets the value of the rightsHolder property.
     * 
     * @return
     *     possible object is
     *     {@link ManualAutoType }
     *     
     */
    public ManualAutoType getRightsHolder() {
        return rightsHolder;
    }

    /**
     * Sets the value of the rightsHolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManualAutoType }
     *     
     */
    public void setRightsHolder(ManualAutoType value) {
        this.rightsHolder = value;
    }

    /**
     * Gets the value of the copyright property.
     * 
     * @return
     *     possible object is
     *     {@link ManualAutoType }
     *     
     */
    public ManualAutoType getCopyright() {
        return copyright;
    }

    /**
     * Sets the value of the copyright property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManualAutoType }
     *     
     */
    public void setCopyright(ManualAutoType value) {
        this.copyright = value;
    }

    /**
     * Gets the value of the licenseCondition property.
     * 
     * @return
     *     possible object is
     *     {@link ManualAutoType }
     *     
     */
    public ManualAutoType getLicenseCondition() {
        return licenseCondition;
    }

    /**
     * Sets the value of the licenseCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManualAutoType }
     *     
     */
    public void setLicenseCondition(ManualAutoType value) {
        this.licenseCondition = value;
    }

    /**
     * Gets the value of the feedInformation property.
     * 
     * @return
     *     possible object is
     *     {@link Feed.FeedInformation }
     *     
     */
    public Feed.FeedInformation getFeedInformation() {
        return feedInformation;
    }

    /**
     * Sets the value of the feedInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Feed.FeedInformation }
     *     
     */
    public void setFeedInformation(Feed.FeedInformation value) {
        this.feedInformation = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;group ref="{http://multimatch.org/schema/multimatchMetadata-2.2.1}FeedInformationElements"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "feedType",
        "status",
        "engine",
        "query"
    })
    public static class FeedInformation {

        @XmlElement(name = "FeedType")
        protected Object feedType;
        @XmlElement(name = "Status")
        protected Object status;
        @XmlElement(name = "Engine")
        protected Object engine;
        @XmlElement(name = "Query")
        protected Object query;

        /**
         * Gets the value of the feedType property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getFeedType() {
            return feedType;
        }

        /**
         * Sets the value of the feedType property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setFeedType(Object value) {
            this.feedType = value;
        }

        /**
         * Gets the value of the status property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getStatus() {
            return status;
        }

        /**
         * Sets the value of the status property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setStatus(Object value) {
            this.status = value;
        }

        /**
         * Gets the value of the engine property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getEngine() {
            return engine;
        }

        /**
         * Sets the value of the engine property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setEngine(Object value) {
            this.engine = value;
        }

        /**
         * Gets the value of the query property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getQuery() {
            return query;
        }

        /**
         * Sets the value of the query property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setQuery(Object value) {
            this.query = value;
        }

    }

}