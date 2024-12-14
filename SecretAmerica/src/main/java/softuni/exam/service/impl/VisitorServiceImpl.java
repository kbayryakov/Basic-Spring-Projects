package softuni.exam.service.impl;

import jakarta.xml.bind.JAXBException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.Mapping;
import softuni.exam.models.dto.VisitorImportDto;
import softuni.exam.models.dto.VisitorImportRootDto;
import softuni.exam.models.entity.PersonalData;
import softuni.exam.models.entity.Visitor;
import softuni.exam.repository.AttractionRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.repository.PersonalDataRepository;
import softuni.exam.repository.VisitorRepository;
import softuni.exam.service.VisitorService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XMLParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//ToDo - Implement all the methods

@Service
public class VisitorServiceImpl implements VisitorService {
    private static final String VISITORS_PATH = "src/main/resources/files/xml/visitors.xml";
    private final VisitorRepository visitorRepository;
    private final XMLParser xmlParser;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final CountryRepository countryRepository;
    private final AttractionRepository attractionRepository;
    private final PersonalDataRepository personalDataRepository;

    @Autowired
    public VisitorServiceImpl(VisitorRepository visitorRepository, XMLParser xmlParser, ValidationUtil validationUtil, ModelMapper modelMapper, CountryRepository countryRepository, AttractionRepository attractionRepository, PersonalDataRepository personalDataRepository) {
        this.visitorRepository = visitorRepository;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.countryRepository = countryRepository;
        this.attractionRepository = attractionRepository;
        this.personalDataRepository = personalDataRepository;
    }

    @Override
    public boolean areImported() {
        return this.visitorRepository.count() > 0;
    }

    @Override
    public String readVisitorsFileContent() throws IOException {
        return Files.readString(Path.of(VISITORS_PATH));
    }

    @Override
    public String importVisitors() throws JAXBException {
        StringBuilder sb = new StringBuilder();

        VisitorImportRootDto visitorImportRootDto = xmlParser
                .fromFile(VISITORS_PATH, VisitorImportRootDto.class);

        for (VisitorImportDto visitorImportDto : visitorImportRootDto.getVisitorsList()) {
            if (this.visitorRepository.findByFirstNameAndLastName(visitorImportDto.getFirstName(), visitorImportDto.getLastName()).isPresent() ||
            !this.validationUtil.isValid(visitorImportDto) ||
                    this.visitorRepository.findByPersonalData(this.personalDataRepository.findById(visitorImportDto.getPersonalData()).get()).isPresent()) {
                sb.append("Invalid visitor").append(System.lineSeparator());
                continue;
            }

            Visitor visitor = this.modelMapper.map(visitorImportDto, Visitor.class);
            visitor.setCountry(this.countryRepository.findById(visitorImportDto.getCountry()).get());
            visitor.setAttraction(this.attractionRepository.findById(visitorImportDto.getAttraction()).get());
            visitor.setPersonalData(this.personalDataRepository.findById(visitorImportDto.getPersonalData()).get());

            this.visitorRepository.saveAndFlush(visitor);

            sb.append(String.format("Successfully imported visitor %s %s", visitor.getFirstName(), visitor.getLastName())).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
