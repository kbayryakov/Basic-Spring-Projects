package GameStore.controller;

import GameStore.service.GameService;
import GameStore.service.UserService;
import GameStore.service.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {
    private final UserService userService;
    private final GameService gameService;

    @Autowired
    public CommandLineRunner(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void run(String... args) throws  Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        String line;
        while (!(line = reader.readLine()).equals("Stop")) {
            String[] tokens = line.split("\\|");
            String command = "";

            switch (tokens[0]) {
                case "RegisterUser":
                    command = this.userService.registerUser
                            (new UserCreateDto(tokens[1], tokens[2], tokens[3], tokens[4]));
                    break;
                case "LoginUser":
                    command = this.userService.loginUser(new UserLoginDto(tokens[1], tokens[2]));
                    break;
                case "Logout":
                    command = this.userService.logout();
                    break;
                case  "AddGame":
                    command = this.gameService.addGame(new GameCreateDto(tokens[1],
                            new BigDecimal(tokens[2]), Double.parseDouble(tokens[3]),
                            tokens[4], tokens[5], tokens[6],
                            LocalDate.parse(tokens[7], DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
                    break;
                case "EditGame":
                    GameEditDto gameEditDto = new GameEditDto();
                    gameEditDto.setId(Integer.parseInt(tokens[1]));
                    Arrays.stream(tokens).skip(2).forEach(v -> {
                        String[] split = v.split("=");
                        String field = split[0];
                        if ("price".equals(field)) {
                            gameEditDto.setPrice(new BigDecimal(split[1]));
                        } else if ("size".equals(field)) {
                            gameEditDto.setSize(Double.parseDouble(split[1]));
                        }
                    });
                    command = this.gameService.editGame(gameEditDto);
                    break;
                case "DeleteGame":
                    command = this.gameService.deleteGame(Integer.parseInt(tokens[1]));
                    break;
                case "AllGames":
                    command = this.gameService.getAllGames().stream()
                            .map(GameViewDto::toString).collect(Collectors.joining("\n"));
                    break;
                case "DetailGame":
                    command = this.gameService.getDetailGame(tokens[1]).toString();
                    break;
                case "OwnedGames":
                    command = this.gameService.getOwnedGames();
                    break;
                case "RemoveItem":
                    command = this.gameService.removeItem(tokens[1]);
                    break;
                case "BuyItem":
                    command = this.gameService.buyItems();
                    break;
            }

            System.out.println(command);
        }
    }
}
