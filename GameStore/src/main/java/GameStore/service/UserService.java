package GameStore.service;

import GameStore.data.entities.User;
import GameStore.service.dtos.UserCreateDto;
import GameStore.service.dtos.UserLoginDto;

public interface UserService {
    String registerUser(UserCreateDto userCreateDto);
    String loginUser(UserLoginDto userLoginDto);
    boolean isLoggedId();
    boolean isAdmin();
    String logout();
    User getUser();
}
