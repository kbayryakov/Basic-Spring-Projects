package softuni.exam.models.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "personal_datas")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonalDataImportRootDto {

    @XmlElement(name = "personal_data")
    List<PersonalDataImportDto> personalDataImportDtoList;

    public PersonalDataImportRootDto() {
    }

    public List<PersonalDataImportDto> getPersonalDataImportDtoList() {
        return personalDataImportDtoList;
    }

    public void setPersonalDataImportDtoList(List<PersonalDataImportDto> personalDataImportDtoList) {
        this.personalDataImportDtoList = personalDataImportDtoList;
    }
}
