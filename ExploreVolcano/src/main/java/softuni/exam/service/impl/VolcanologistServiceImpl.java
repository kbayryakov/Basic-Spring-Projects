package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.entity.Volcanologist;
import softuni.exam.models.entity.VolcanologistImportDto;
import softuni.exam.models.entity.VolcanologistImportRootDto;
import softuni.exam.repository.VolcanoRepository;
import softuni.exam.repository.VolcanologistRepository;
import softuni.exam.service.VolcanologistService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class VolcanologistServiceImpl implements VolcanologistService {

    private static final String VOLCANOLOGIST_PATH = "src/main/resources/files/xml/volcanologists.xml";

    private final VolcanologistRepository volcanologistRepository;
    private final VolcanoRepository volcanoRepository;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    @Autowired
    public VolcanologistServiceImpl(VolcanologistRepository volcanologistRepository, VolcanoRepository volcanoRepository, XmlParser xmlParser, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.volcanologistRepository = volcanologistRepository;
        this.volcanoRepository = volcanoRepository;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return this.volcanologistRepository.count() > 0;
    }

    @Override
    public String readVolcanologistsFromFile() throws IOException {
        return Files.readString(Path.of(VOLCANOLOGIST_PATH));
    }

    @Override
    public String importVolcanologists() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        VolcanologistImportRootDto volcanologistImportRootDto = this.xmlParser.
                fromFile(VOLCANOLOGIST_PATH, VolcanologistImportRootDto.class);
        for (VolcanologistImportDto volcanologistImportDto :
                volcanologistImportRootDto.getVolcanologistImportDtos()) {
            if (this.volcanologistRepository.findByFirstNameAndLastName(
                    volcanologistImportDto.getFirstName(), volcanologistImportDto.getLastName()).isPresent() ||
            !this.validationUtil.isValid(volcanologistImportDto) ||
                    this.volcanoRepository.findById((long) volcanologistImportDto.getExploringVolcanoId()).isEmpty()) {
                sb.append("Invalid volcanologist").append(System.lineSeparator());
                continue;
            }

            Volcanologist volcanologist =  this.modelMapper.map(volcanologistImportDto, Volcanologist.class);
            volcanologist.setVolcano(this.volcanoRepository.findById((long) volcanologistImportDto.getExploringVolcanoId()).get());
            this.volcanologistRepository.saveAndFlush(volcanologist);
            sb.append(String.format("Successfully imported volcanologist %s %s",
                    volcanologist.getFirstName(), volcanologist.getLastName())).append(System.lineSeparator());
        }

        return sb.toString();
    }
}