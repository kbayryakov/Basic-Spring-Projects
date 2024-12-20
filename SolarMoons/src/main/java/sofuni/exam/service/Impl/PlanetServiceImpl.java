package sofuni.exam.service.Impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sofuni.exam.models.dto.PlanetImportDto;
import sofuni.exam.models.entity.Planet;
import sofuni.exam.repository.PlanetRepository;
import sofuni.exam.service.PlanetService;
import sofuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PlanetServiceImpl implements PlanetService {
    private final String PLANETS_PATH = "src/main/resources/files/json/planets.json";
    private final PlanetRepository planetRepository;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public PlanetServiceImpl(PlanetRepository planetRepository, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.planetRepository = planetRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean areImported() {
        return this.planetRepository.count() > 0;
    }

    @Override
    public String readPlanetsFileContent() throws IOException {
        return Files.readString(Path.of(PLANETS_PATH));
    }

    @Override
    public String importPlanets() throws IOException {
        StringBuilder sb = new StringBuilder();

        PlanetImportDto[] planetImportDtos = this.gson.fromJson(readPlanetsFileContent(), PlanetImportDto[].class);
        for (PlanetImportDto planetImportDto : planetImportDtos) {
            if (planetRepository.findByName(planetImportDto.getName()).isPresent() ||
                    !this.validationUtil.isValid(planetImportDto)) {
                sb.append("Invalid planet").append(System.lineSeparator());
                continue;
            }

            Planet planet = this.modelMapper.map(planetImportDto, Planet.class);
            this.planetRepository.saveAndFlush(planet);
            sb.append(String.format("Successfully imported planet %s", planet.getName()))
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }
}
