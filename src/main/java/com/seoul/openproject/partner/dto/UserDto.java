package com.seoul.openproject.partner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDto {
    @Schema(name = "userId" , example = "db688a4a-2f70-4265-a1ea-d15fd6c5c914")
    @NotBlank
    private String userId;

    @Schema(name = "oauth2Username" , example = "takim(intraId)")
    @NotBlank
    private String oauth2Username;

    @Schema(name = "nickname" , example = "로그인 할 때 id아니라 사용자가 변경할 수 있는 id, default는 oauth2Username와 같음.")
    @NotBlank
    private String nickname;

    @Schema(name = "email" , example = "takim@student.42seoul.kr")
    @NotBlank
    private String email;

    @Schema(name = "imageUrl" , example = "로그인 할 때 id아니라 사용자가 변경할 수 있는 id")
    @NotBlank
    private String imageUrl;

    @Schema(name = "slackEmail" , example = "slack 알림을 위해 사용될 수 있음. 아직은 사용하지 않음.")
    private String slackEmail;

}
