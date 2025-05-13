package user.dto;

import lombok.Data;

@Data
public class UserResponce {
    private boolean success;
    private String accessToken;
    private String refreshToken;
    private UserRequest userRequest;
}
