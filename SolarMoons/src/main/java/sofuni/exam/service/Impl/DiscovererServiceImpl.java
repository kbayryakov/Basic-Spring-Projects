package sofuni.exam.service.Impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sofuni.exam.models.dto.DiscovererImportDto;
import sofuni.exam.models.entity.Discoverer;
import sofuni.exam.repository.DiscovererRepository;
import sofuni.exam.service.DiscovererService;
import sofuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DiscovererServiceImpl implements DiscovererService {
    private final String DISCOVERERS_PATH = "src/main/resources/files/json/discoverers.json";
    private final DiscovererRepository discovererRepository;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public DiscovererServiceImpl(DiscovererRepository discovererRepository, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.discovererRepository = discovererRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean areImported() {
        return this.discovererRepository.count() > 0;
    }

    @Override
    public String readDiscovererFileContent() throws IOException {
        return Files.readString(Path.of(DISCOVERERS_PATH));
    }

    @Override
    public String importDiscoverers() throws IOException {
        StringBuilder sb = new StringBuilder();

        DiscovererImportDto[] discovererImportDtos = gson.fromJson(readDiscovererFileContent(), DiscovererImportDto[].class);
        for (DiscovererImportDto discovererImportDto : discovererImportDtos){
            if (this.discovererRepository.findByFirstNameAndLastName(
                    discovererImportDto.getFirstName(), discovererImportDto.getLastName()).isPresent() ||
            !this.validationUtil.isValid(discovererImportDto)){
                sb.append("Invalid discoverer").append(System.lineSeparator());
                continue;
            }

            Discoverer discoverer = this.modelMapper.map(discovererImportDto, Discoverer.class);
            this.discovererRepository.saveAndFlush(discoverer);
            sb.append(String.format("Successfully imported discoverer %s %s",
                    discoverer.getFirstName(), discoverer.getLastName()))
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }
}
