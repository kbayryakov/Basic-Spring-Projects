package sofuni.exam.models.dto;

import jakarta.validation.constraints.Positive;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.hibernate.validator.constraints.Length;

@XmlRootElement(name = "moon")
@XmlAccessorType(XmlAccessType.FIELD)
public class MoonImportDto {
    @XmlElement
    @Length(min = 2, max = 10)
    private String name;
    @XmlElement
    private String discovered;
    @XmlElement(name = "distance_from_planet")
    @Positive
    private Integer distanceFromPlanet;
    @XmlElement
    @Positive
    private Double radius;
    @XmlElement(name = "discoverer_id")
    private Long discoverer;
    @XmlElement(name = "planet_id")
    private Long planet;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscovered() {
        return discovered;
    }

    public void setDiscovered(String discovered) {
        this.discovered = discovered;
    }

    public Integer getDistanceFromPlanet() {
        return distanceFromPlanet;
    }

    public void setDistanceFromPlanet(Integer distanceFromPlanet) {
        this.distanceFromPlanet = distanceFromPlanet;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Long getDiscoverer() {
        return discoverer;
    }

    public void setDiscoverer(Long discoverer) {
        this.discoverer = discoverer;
    }

    public Long getPlanet() {
        return planet;
    }

    public void setPlanet(Long planet) {
        this.planet = planet;
    }
}
