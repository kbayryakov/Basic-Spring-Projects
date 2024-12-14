package GameStore.service.implementations;

import GameStore.data.entities.Game;
import GameStore.data.entities.User;
import GameStore.data.repositories.GameRepository;
import GameStore.data.repositories.UserRepository;
import GameStore.service.GameService;
import GameStore.service.UserService;
import GameStore.service.dtos.*;
import GameStore.utils.ValidatorUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;
    private final ValidatorUtil validatorUtil;
    private final UserService userService;
    private final Set<Game> gamesCart = new HashSet<>();
    private final UserRepository userRepository;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, ModelMapper modelMapper, ValidatorUtil validatorUtil, UserService userService, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
        this.validatorUtil = validatorUtil;
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @Override
    public String addGame(GameCreateDto gameCreateDto) {
        if (this.userService.isAdmin()) {
            return "User is not admin";
        }

        if (!this.validatorUtil.isValid(gameCreateDto)) {
            return this.validatorUtil.validate(gameCreateDto)
                    .stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
        }

        Game game = this.modelMapper.map(gameCreateDto, Game.class);
        this.gameRepository.saveAndFlush(game);
        return String.format("Added %s", game.getTitle());
    }

    @Override
    public String editGame(GameEditDto gameEditDto) {
        if (this.userService.isAdmin()) {
            return "User is not admin";
        }

        if (!this.validatorUtil.isValid(gameEditDto)) {
            return this.validatorUtil.validate(gameEditDto)
                    .stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
        }

        Optional<Game> optionalGame = this.gameRepository.findById(gameEditDto.getId());

        if (optionalGame.isEmpty()) {
            return "No such game found";
        }

        Game game = optionalGame.get();
        if (gameEditDto.getPrice() != null) {
            game.setPrice(gameEditDto.getPrice());
        }
        if (gameEditDto.getSize() != null) {
            game.setSize(gameEditDto.getSize());
        }

        this.gameRepository.saveAndFlush(game);
        return String.format("Edited %s", game.getTitle());
    }

    @Override
    public String deleteGame(int id) {
        if (this.userService.isAdmin()) {
            return "User is not admin";
        }

        Optional<Game> game = this.gameRepository.findById(id);
        if (game.isEmpty()) {
            return "No such game found";
        }
        this.gameRepository.delete(game.get());
        return String.format("Deleted %s", game.get().getTitle());
    }

    @Override
    public Set<GameViewDto> getAllGames() {
        return this.gameRepository.findAll()
                .stream()
                .map(game -> this.modelMapper.map(game, GameViewDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public GameDetailDto getDetailGame(String title) {
        Optional<Game> game = this.gameRepository.findByTitle(title);
        return game.map(value -> this.modelMapper.map(value, GameDetailDto.class)).orElse(new GameDetailDto());
    }

    @Override
    public String getOwnedGames() {
        if (this.userService.isLoggedId()) {
            return "No logged in user";
        }

        User user = this.userService.getUser();
        return user.getGames().stream().map(game -> this.modelMapper.map(game, GameOwnedDto.class))
                .map(GameOwnedDto::getTitle).collect(Collectors.joining("\n"));
    }

    @Override
    public String addItem(String title) {
        if (!this.userService.isLoggedId()) {
            return "No logged in user";
        }

        Optional<Game> game = this.gameRepository.findByTitle(title);
        if (game.isEmpty()) {
            return "No such game found";
        }

        this.gamesCart.add(game.get());
        return String.format("%s added to cart", game.get().getTitle());
    }

    @Override
    public String removeItem(String title) {
        if (!this.userService.isLoggedId()) {
            return "No logged in user";
        }

        Optional<Game> game = this.gameRepository.findByTitle(title);
        if (game.isEmpty()) {
            return "No such game found";
        }

        if (this.gamesCart.contains(game.get())) {
            this.gamesCart.remove(game.get());
            return String.format("%s removed from cart", game.get().getTitle());
        }

        return "No such game found";
    }

    @Override
    public String buyItems() {
        if (!this.userService.isLoggedId()) {
            return "No logged in user";
        }

        User user = this.userService.getUser();
        Set<Game> newGames = gamesCart.stream()
                .filter(game -> !user.getGames().contains(game))
                .collect(Collectors.toSet());
        user.getGames().addAll(newGames);
        this.userRepository.saveAndFlush(user);

        if (newGames.isEmpty()) {
            return "No new games";
        }

        return String.format("Successfully bought games: \n\t%s",
                newGames.stream().map(Game::getTitle).collect(Collectors.joining("\n")));
    }
}
