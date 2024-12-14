package softuni.exam.models.dto;

import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "visitor")
@XmlAccessorType(XmlAccessType.FIELD)
public class VisitorImportDto {
    @XmlElement(name = "first_name")
    @Size(min = 2, max = 20)
    private String firstName;
    @XmlElement(name = "last_name")
    @Size(min = 2, max = 20)
    private String lastName;
    @XmlElement(name = "attraction_id")
    private Long attraction;
    @XmlElement(name = "country_id")
    private Long country;
    @XmlElement(name = "personal_data_id")
    private Long personalData;

    public VisitorImportDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getAttraction() {
        return attraction;
    }

    public void setAttraction(Long attraction) {
        this.attraction = attraction;
    }

    public Long getCountry() {
        return country;
    }

    public void setCountry(Long country) {
        this.country = country;
    }

    public Long getPersonalData() {
        return personalData;
    }

    public void setPersonalData(Long personalData) {
        this.personalData = personalData;
    }
}
