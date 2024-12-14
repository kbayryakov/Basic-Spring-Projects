package softuni.exam.models.entity;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "volcanologist")
@XmlAccessorType(XmlAccessType.FIELD)
public class VolcanologistImportDto {

    @XmlElement(name = "first_name")
    @Length(min = 2, max = 30)
    private String firstName;
    @XmlElement(name = "last_name")
    @Length(min = 2, max = 30)
    private String lastName;
    @XmlElement
    private Double salary;
    @XmlElement
    @Min(18)
    @Max(80)
    private Integer age;
    @XmlElement(name = "exploring_from")
    private String exploringFrom;
    @XmlElement(name = "exploring_volcano_id")
    private Integer exploringVolcanoId;

    public VolcanologistImportDto() {
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

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getExploringFrom() {
        return exploringFrom;
    }

    public void setExploringFrom(String exploringFrom) {
        this.exploringFrom = exploringFrom;
    }

    public Integer getExploringVolcanoId() {
        return exploringVolcanoId;
    }

    public void setExploringVolcanoId(Integer exploringVolcanoId) {
        this.exploringVolcanoId = exploringVolcanoId;
    }
}