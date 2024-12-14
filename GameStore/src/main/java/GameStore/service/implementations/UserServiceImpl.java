package GameStore.service.implementations;

import GameStore.data.entities.User;
import GameStore.data.repositories.UserRepository;
import GameStore.service.UserService;
import GameStore.service.dtos.UserCreateDto;
import GameStore.service.dtos.UserLoginDto;
import GameStore.utils.ValidatorUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ValidatorUtil validatorUtil;

    private User user;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, ValidatorUtil validatorUtil) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.validatorUtil = validatorUtil;
    }


    @Override
    public String registerUser(UserCreateDto userCreateDto) {
        if (!validatorUtil.isValid(userCreateDto)) {
            return validatorUtil.validate(userCreateDto).stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
        }

        if (!userCreateDto.getPassword().equals(userCreateDto.getConfirmPassword())) {
            return  "Passwords do not match";
        }

        if (this.userRepository.findByEmail(userCreateDto.getEmail()).isPresent()) {
            return "Email address already in use";
        }

        User user = this.modelMapper.map(userCreateDto, User.class);
        if (this.userRepository.count() == 0) {
            user.setAdmin(true);
        }

        this.userRepository.saveAndFlush(user);
        return String.format("%s was registered", user.getFullName());
    }

    @Override
    public String loginUser(UserLoginDto userLoginDto) {
        Optional<User> user = this.userRepository
                .findByEmailAndPassword(userLoginDto.getEmail(), userLoginDto.getPassword());

        if (user.isEmpty()) {
            return "Invalid email or password";
        } else {
            this.user = user.get();

            return String.format("Successfully logged in %s", this.user.getFullName());
        }
    }

    @Override
    public boolean isLoggedId() {
        return this.user != null;
    }

    @Override
    public boolean isAdmin() {
        return !this.isLoggedId() || !this.user.isAdmin();
    }

    @Override
    public String logout() {
        if (this.isLoggedId()) {
            String output = String.format("User %s successfully logged out", this.user.getFullName());
            this.user = null;
            return output;
        }
        return "No logged in user";
    }

    @Override
    public User getUser() {
        return this.user;
    }
}
