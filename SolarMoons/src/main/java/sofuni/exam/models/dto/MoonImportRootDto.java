package sofuni.exam.models.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;
@XmlRootElement(name = "moons")
@XmlAccessorType(XmlAccessType.FIELD)
public class MoonImportRootDto {
    @XmlElement(name = "moon")
   List<MoonImportDto> moonsList;

    public List<MoonImportDto> getMoonsList() {
        return moonsList;
    }

    public void setMoonsList(List<MoonImportDto> moonsList) {
        this.moonsList = moonsList;
    }
}
