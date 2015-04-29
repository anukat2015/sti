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
 *       &lt;group ref="{http://multimatch.org/schema/multimatchMetadata-2.2.1}EventElements"/>
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
    "type",
    "language",
    "title",
    "subject",
    "tags",
    "dateStart",
    "dateEnd",
    "location",
    "description",
    "relatedCreator",
    "relatedCreation",
    "relatedWebpage",
    "relatedWebsite"
})
@XmlRootElement(name = "Event")
public class Event {

    @XmlElement(name = "Identifier", required = true)
    protected String identifier;
    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "Language", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String language;
    @XmlElement(name = "Title")
    protected List<ManualAutoPreferredLinguisticType> title;
    @XmlElement(name = "Subject")
    protected List<ManualAutoLinguisticType> subject;
    @XmlElement(name = "Tags")
    protected List<ManualAutoLinguisticType> tags;
    @XmlElement(name = "DateStart")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateStart;
    @XmlElement(name = "DateEnd")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateEnd;
    @XmlElement(name = "Location")
    protected LocationType location;
    @XmlElement(name = "Description")
    protected List<ManualAutoPreferredLinguisticType> description;
    @XmlElement(name = "RelatedCreator")
    protected List<ManualAutoAnchorType> relatedCreator;
    @XmlElement(name = "RelatedCreation")
    protected List<ManualAutoAnchorType> relatedCreation;
    @XmlElement(name = "RelatedWebpage")
    protected List<ManualAutoAnchorType> relatedWebpage;
    @XmlElement(name = "RelatedWebsite")
    protected List<ManualAutoAnchorType> relatedWebsite;

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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
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
     * Gets the value of the title property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the title property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTitle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManualAutoPreferredLinguisticType }
     * 
     * 
     */
    public List<ManualAutoPreferredLinguisticType> getTitle() {
        if (title == null) {
            title = new ArrayList<ManualAutoPreferredLinguisticType>();
        }
        return this.title;
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
     * {@link ManualAutoLinguisticType }
     * 
     * 
     */
    public List<ManualAutoLinguisticType> getSubject() {
        if (subject == null) {
            subject = new ArrayList<ManualAutoLinguisticType>();
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
     * Gets the value of the dateStart property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateStart() {
        return dateStart;
    }

    /**
     * Sets the value of the dateStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateStart(XMLGregorianCalendar value) {
        this.dateStart = value;
    }

    /**
     * Gets the value of the dateEnd property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateEnd() {
        return dateEnd;
    }

    /**
     * Sets the value of the dateEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateEnd(XMLGregorianCalendar value) {
        this.dateEnd = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link LocationType }
     *     
     */
    public LocationType getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationType }
     *     
     */
    public void setLocation(LocationType value) {
        this.location = value;
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

}