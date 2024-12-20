package sofuni.exam.service.Impl;

import jakarta.xml.bind.JAXBException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sofuni.exam.models.dto.MoonImportDto;
import sofuni.exam.models.dto.MoonImportRootDto;
import sofuni.exam.models.entity.Moon;
import sofuni.exam.models.enums.Type;
import sofuni.exam.repository.DiscovererRepository;
import sofuni.exam.repository.MoonRepository;
import sofuni.exam.repository.PlanetRepository;
import sofuni.exam.service.MoonService;
import sofuni.exam.util.ValidationUtil;
import sofuni.exam.util.XmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class MoonServiceImpl implements MoonService {
    private final String MOONS_PATH = "src/main/resources/files/xml/moons.xml";
    private final MoonRepository moonRepository;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final DiscovererRepository discovererRepository;
    private final PlanetRepository planetRepository;

    @Autowired
    public MoonServiceImpl(MoonRepository moonRepository, XmlParser xmlParser, ValidationUtil validationUtil, ModelMapper modelMapper, DiscovererRepository discovererRepository, PlanetRepository planetRepository) {
        this.moonRepository = moonRepository;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.discovererRepository = discovererRepository;
        this.planetRepository = planetRepository;
    }

    @Override
    public boolean areImported() {
        return this.moonRepository.count() > 0;
    }

    @Override
    public String readMoonsFileContent() throws IOException {
        return Files.readString(Path.of(MOONS_PATH));
    }

    @Override
    public String importMoons() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        MoonImportRootDto moonImportRootDto = xmlParser.fromFile(MOONS_PATH, MoonImportRootDto.class);
        for (MoonImportDto moonImportDto : moonImportRootDto.getMoonsList()) {
            if (this.moonRepository.findByName(moonImportDto.getName()).isPresent() ||
            !this.validationUtil.isValid(moonImportDto)){
                sb.append("Invalid moon").append(System.lineSeparator());
                continue;
            }

            Moon moon = this.modelMapper.map(moonImportDto, Moon.class);
            moon.setDiscoverer(this.discovererRepository.findById(moonImportDto.getDiscoverer()).get());
            moon.setPlanet(this.planetRepository.findById(moonImportDto.getPlanet()).get());
            this.moonRepository.saveAndFlush(moon);

            sb.append(String.format("Successfully imported moon %s", moon.getName())).append(System.lineSeparator());
        }

        return sb.toString();
    }

    @Override
    public String exportMoons() {
        StringBuilder sb = new StringBuilder();

        List<Moon> moonsList = this.moonRepository
                .findByPlanet_TypeAndRadiusBetweenOrderByNameAsc(Type.GAS_GIANT, 700.0, 2000.0);

        moonsList.forEach(moon -> {
            sb.append(String.format("***Moon %s is a natural satellite of %s and has a radius of %.2f km.\n",
                            moon.getName(), moon.getPlanet().getName(), moon.getRadius()))
                    .append(String.format("****Discovered by %s %s\n",
                    moon.getDiscoverer().getFirstName(), moon.getDiscoverer().getLastName()))
                    .append(System.lineSeparator());

        });

        return sb.toString();
    }
}
