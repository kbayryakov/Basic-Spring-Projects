package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.AstronomerImportDto;
import softuni.exam.models.dto.AstronomerRootImportDto;
import softuni.exam.models.entity.Astronomer;
import softuni.exam.repository.AstronomerRepository;
import softuni.exam.repository.StarRepository;
import softuni.exam.service.AstronomerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XMLParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO: Implement all methods
@Service
public class AstronomerServiceImpl implements AstronomerService {
    private static final String ASTRONOMERS_PATH = "src/main/resources/files/xml/astronomers.xml";
    private final AstronomerRepository astronomerRepository;
    private final XMLParser xmlParser;
    private final ValidationUtil validationUtil;
    private final StarRepository starRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AstronomerServiceImpl(AstronomerRepository astronomerRepository, XMLParser xmlParser, ValidationUtil validationUtil, StarRepository starRepository, ModelMapper modelMapper) {
        this.astronomerRepository = astronomerRepository;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.starRepository = starRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.astronomerRepository.count() > 0;
    }

    @Override
    public String readAstronomersFromFile() throws IOException {
        return Files.readString(Path.of(ASTRONOMERS_PATH));
    }

    @Override
    public String importAstronomers() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        AstronomerRootImportDto astronomerRootImportDto = xmlParser.fromFile(ASTRONOMERS_PATH, AstronomerRootImportDto.class);
        for (AstronomerImportDto astronomerImportDto : astronomerRootImportDto.getAstronomers()) {
            if (this.astronomerRepository.findByFirstNameAndLastName(
                    astronomerImportDto.getFirstName(), astronomerImportDto.getLastName()).isPresent() ||
             !this.validationUtil.isValid(astronomerImportDto) ||
             this.starRepository.findById((long)astronomerImportDto.getObservingStarId()).isEmpty()) {
                sb.append("Invalid astronomer").append(System.lineSeparator());
                continue;
            }

            Astronomer astronomer = this.modelMapper.map(astronomerImportDto, Astronomer.class);
            astronomer.setObservingStar(this.starRepository.findById((long)astronomerImportDto.getObservingStarId()).get());
            this.astronomerRepository.saveAndFlush(astronomer);

            sb.append(String.format("Successfully imported astronomer %s %s - %.2f", astronomer.getFirstName(),
                    astronomer.getLastName(), astronomer.getAverageObservationHours())).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
