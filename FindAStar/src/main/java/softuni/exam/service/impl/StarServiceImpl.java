package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.StarImportDto;
import softuni.exam.models.entity.Star;
import softuni.exam.models.entity.enums.StarType;
import softuni.exam.repository.ConstellationRepository;
import softuni.exam.repository.StarRepository;
import softuni.exam.service.StarService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// TODO: Implement all methods
@Service
public class StarServiceImpl implements StarService {
    private static final String STARS_PATH = "src/main/resources/files/json/stars.json";
    private final StarRepository starRepository;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final ConstellationRepository constellationRepository;
    @Autowired
    public StarServiceImpl(StarRepository starRepository, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper, ConstellationRepository constellationRepository) {
        this.starRepository = starRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.constellationRepository = constellationRepository;
    }

    @Override
    public boolean areImported() {
        return this.starRepository.count() > 0;
    }

    @Override
    public String readStarsFileContent() throws IOException {
        return Files.readString(Path.of(STARS_PATH));
    }

    @Override
    public String importStars() throws IOException {
        StringBuilder sb = new StringBuilder();

        StarImportDto[] starImportDtos = this.gson.fromJson(readStarsFileContent(), StarImportDto[].class);
        for (StarImportDto starImportDto : starImportDtos) {
            if (this.starRepository.findByName(starImportDto.getName()).isPresent() ||
            !this.validationUtil.isValid(starImportDto)) {
                sb.append("Invalid star").append(System.lineSeparator());
                continue;
            }

            Star star = this.modelMapper.map(starImportDto, Star.class);
            star.setStarType(StarType.valueOf(starImportDto.getStarType()));
            star.setConstellation(this.constellationRepository.findById(starImportDto.getConstellation()).get());
            this.starRepository.saveAndFlush(star);
            sb.append(String.format("Successfully imported star %s - %.2f light years", star.getName(), star.getLightYears()))
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }

    @Override
    public String exportStars() {
        List<Star> stars = this.starRepository.findByStarTypeOrderByLightYears();
        StringBuilder sb = new StringBuilder();

        for (Star star : stars) {
            sb.append(String.format("Star: %s\n   *Distance: %.2f light years\n   **Description: %s\n   ***Constellation: %s\n",
                    star.getName(), star.getLightYears(), star.getDescription(), star.getConstellation().getName()));
        }

        return sb.toString();
    }
}
