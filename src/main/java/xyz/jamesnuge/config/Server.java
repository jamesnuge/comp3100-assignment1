//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.02.25 at 10:34:07 AM AEDT 
//


package xyz.jamesnuge.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="limit" type="{http://www.w3.org/2001/XMLSchema}byte" /&gt;
 *       &lt;attribute name="bootupTime" type="{http://www.w3.org/2001/XMLSchema}byte" /&gt;
 *       &lt;attribute name="hourlyRate" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="cores" type="{http://www.w3.org/2001/XMLSchema}byte" /&gt;
 *       &lt;attribute name="memory" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="disk" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "server")
public class Server {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "limit")
    protected Byte limit;
    @XmlAttribute(name = "bootupTime")
    protected Byte bootupTime;
    @XmlAttribute(name = "hourlyRate")
    protected Float hourlyRate;
    @XmlAttribute(name = "cores")
    protected Integer cores;
    @XmlAttribute(name = "memory")
    protected Integer memory;
    @XmlAttribute(name = "disk")
    protected Integer disk;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
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
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setLimit(Byte value) {
        this.limit = value;
    }

    /**
     * Gets the value of the bootupTime property.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getBootupTime() {
        return bootupTime;
    }

    /**
     * Sets the value of the bootupTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setBootupTime(Byte value) {
        this.bootupTime = value;
    }

    /**
     * Gets the value of the hourlyRate property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getHourlyRate() {
        return hourlyRate;
    }

    /**
     * Sets the value of the hourlyRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setHourlyRate(Float value) {
        this.hourlyRate = value;
    }

    /**
     * Gets the value of the cores property.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Integer getCores() {
        return cores;
    }

    /**
     * Sets the value of the cores property.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setCores(Integer value) {
        this.cores = value;
    }

    /**
     * Gets the value of the memory property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMemory() {
        return memory;
    }

    /**
     * Sets the value of the memory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMemory(Integer value) {
        this.memory = value;
    }

    /**
     * Gets the value of the disk property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDisk() {
        return disk;
    }

    /**
     * Sets the value of the disk property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDisk(Integer value) {
        this.disk = value;
    }

    @Override
    public String toString() {
        return "Server{" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", limit=" + limit +
                ", bootupTime=" + bootupTime +
                ", hourlyRate=" + hourlyRate +
                ", cores=" + cores +
                ", memory=" + memory +
                ", disk=" + disk +
                '}';
    }
}
