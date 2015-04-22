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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://multimatch.org/schema/multimatchMetadata-2.2.1}CreationElements"/>
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
    "source",
    "sourceIdentifier",
    "type",
    "title",
    "subject",
    "tags",
    "location",
    "archiveLocation",
    "format",
    "digitalRepresentation",
    "description",
    "rightsHolder",
    "copyright",
    "licenseCondition",
    "relatedActor",
    "relatedCreation",
    "relatedWebpage",
    "relatedWebsite"
})
@XmlRootElement(name = "StillImage")
public class StillImage {

    @XmlElement(name = "Identifier", required = true)
    protected String identifier;
    @XmlElement(name = "Source")
    protected List<String> source;
    @XmlElement(name = "SourceIdentifier")
    protected List<ManualAutoType> sourceIdentifier;
    @XmlElement(name = "Type")
    protected List<ManualAutoType> type;
    @XmlElement(name = "Title")
    protected List<ManualAutoPreferredLinguisticType> title;
    @XmlElement(name = "Subject")
    protected List<SubjectType> subject;
    @XmlElement(name = "Tags")
    protected List<ManualAutoLinguisticType> tags;
    @XmlElement(name = "Location")
    protected LocationType location;
    @XmlElement(name = "ArchiveLocation")
    protected LocationType archiveLocation;
    @XmlElement(name = "Format")
    protected ManualAutoType format;
    @XmlElement(name = "DigitalRepresentation")
    protected List<ManualAutoType> digitalRepresentation;
    @XmlElement(name = "Description")
    protected List<ManualAutoPreferredLinguisticType> description;
    @XmlElement(name = "RightsHolder")
    protected ManualAutoType rightsHolder;
    @XmlElement(name = "Copyright")
    protected ManualAutoType copyright;
    @XmlElement(name = "LicenseCondition")
    protected ManualAutoType licenseCondition;
    @XmlElement(name = "RelatedActor")
    protected List<org.multimatch.schema.multimatchmetadata_2_2.Audio.RelatedActor> relatedActor;
    @XmlElement(name = "RelatedCreation")
    protected List<org.multimatch.schema.multimatchmetadata_2_2.Audio.RelatedCreation> relatedCreation;
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
     * Gets the value of the source property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the source property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSource() {
        if (source == null) {
            source = new ArrayList<String>();
        }
        return this.source;
    }

    /**
     * Gets the value of the sourceIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sourceIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSourceIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManualAutoType }
     * 
     * 
     */
    public List<ManualAutoType> getSourceIdentifier() {
        if (sourceIdentifier == null) {
            sourceIdentifier = new ArrayList<ManualAutoType>();
        }
        return this.sourceIdentifier;
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
     * Gets the value of the archiveLocation property.
     * 
     * @return
     *     possible object is
     *     {@link LocationType }
     *     
     */
    public LocationType getArchiveLocation() {
        return archiveLocation;
    }

    /**
     * Sets the value of the archiveLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationType }
     *     
     */
    public void setArchiveLocation(LocationType value) {
        this.archiveLocation = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link ManualAutoType }
     *     
     */
    public ManualAutoType getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManualAutoType }
     *     
     */
    public void setFormat(ManualAutoType value) {
        this.format = value;
    }

    /**
     * Gets the value of the digitalRepresentation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the digitalRepresentation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDigitalRepresentation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManualAutoType }
     * 
     * 
     */
    public List<ManualAutoType> getDigitalRepresentation() {
        if (digitalRepresentation == null) {
            digitalRepresentation = new ArrayList<ManualAutoType>();
        }
        return this.digitalRepresentation;
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
     * Gets the value of the relatedActor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedActor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedActor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.multimatch.schema.multimatchmetadata_2_2.Audio.RelatedActor }
     * 
     * 
     */
    public List<org.multimatch.schema.multimatchmetadata_2_2.Audio.RelatedActor> getRelatedActor() {
        if (relatedActor == null) {
            relatedActor = new ArrayList<org.multimatch.schema.multimatchmetadata_2_2.Audio.RelatedActor>();
        }
        return this.relatedActor;
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
     * {@link org.multimatch.schema.multimatchmetadata_2_2.Audio.RelatedCreation }
     * 
     * 
     */
    public List<org.multimatch.schema.multimatchmetadata_2_2.Audio.RelatedCreation> getRelatedCreation() {
        if (relatedCreation == null) {
            relatedCreation = new ArrayList<org.multimatch.schema.multimatchmetadata_2_2.Audio.RelatedCreation>();
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
