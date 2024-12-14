package softuni.exam.models.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "volcanologists")
@XmlAccessorType(XmlAccessType.FIELD)
public class VolcanologistImportRootDto {

    @XmlElement(name = "volcanologist")
    private List<VolcanologistImportDto> volcanologistImportDtos;

    public VolcanologistImportRootDto() {
    }

    public List<VolcanologistImportDto> getVolcanologistImportDtos() {
        return volcanologistImportDtos;
    }

    public void setVolcanologistImportDtos(List<VolcanologistImportDto> volcanologistImportDtos) {
        this.volcanologistImportDtos = volcanologistImportDtos;
    }
}
