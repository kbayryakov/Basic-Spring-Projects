package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.AttractionImportDto;
import softuni.exam.models.entity.Attraction;
import softuni.exam.repository.AttractionRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.AttractionService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

//ToDo - Implement all the methods
@Service
public class AttractionServiceImpl implements AttractionService {
    private static final String ATTRACTION_PATH = "src/main/resources/files/json/attractions.json";
    private final AttractionRepository attractionRepository;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final CountryRepository countryRepository;

    @Autowired
    public AttractionServiceImpl(AttractionRepository attractionRepository, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper, CountryRepository countryRepository) {
        this.attractionRepository = attractionRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.countryRepository = countryRepository;
    }

    @Override
    public boolean areImported() {
        return this.attractionRepository.count() > 0;
    }

    @Override
    public String readAttractionsFileContent() throws IOException {
        return Files.readString(Path.of(ATTRACTION_PATH));
    }

    @Override
    public String importAttractions() throws IOException {
        StringBuilder sb = new StringBuilder();

        AttractionImportDto[] attractionImportDtos = this.gson
                .fromJson(readAttractionsFileContent(), AttractionImportDto[].class);
        for (AttractionImportDto attractionImportDto : attractionImportDtos) {
            if (this.attractionRepository.findByName(attractionImportDto.getName()).isPresent() ||
            !this.validationUtil.isValid(attractionImportDto)) {
                sb.append("Invalid attraction").append(System.lineSeparator());
                continue;
            }

            Attraction attraction = this.modelMapper.map(attractionImportDto, Attraction.class);
            attraction.setCountry(this.countryRepository.findById(attractionImportDto.getCountry()).get());
            this.attractionRepository.saveAndFlush(attraction);
            sb.append(String.format("Successfully imported attraction %s", attraction.getName()))
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }

    @Override
    public String exportAttractions() {
        StringBuilder sb = new StringBuilder();
        List<Attraction> attractions = this.attractionRepository.findAllByTypeAndElevation();

        for (Attraction attraction : attractions) {
            sb.append(String.format(
                    "Attraction with ID%d:\n***%s - %s at an altitude of %dm. somewhere in %s.\n",
                    attraction.getId(), attraction.getName(), attraction.getDescription(),
                    attraction.getElevation(), attraction.getCountry().getName()));
        }

        return sb.toString();
    }
}
