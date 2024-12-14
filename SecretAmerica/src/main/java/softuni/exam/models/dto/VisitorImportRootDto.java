package softuni.exam.models.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import softuni.exam.models.entity.Visitor;

import java.util.List;

@XmlRootElement(name = "visitors")
@XmlAccessorType(XmlAccessType.FIELD)
public class VisitorImportRootDto {

    @XmlElement(name = "visitor")
    List<VisitorImportDto> visitorsList;

    public VisitorImportRootDto() {
    }

    public List<VisitorImportDto> getVisitorsList() {
        return visitorsList;
    }

    public void setVisitorsList(List<VisitorImportDto> visitorsList) {
        this.visitorsList = visitorsList;
    }
}
