package org.waterwood.waterfunservice.DTO.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.waterwood.waterfunservice.DTO.common.LoginType;

@Data
@EqualsAndHashCode(callSuper = true)
public class PwdLoginRequestBody extends LoginRequestBody {
    private String username;
    private String password;
    private String captcha;
    public PwdLoginRequestBody() {
        this.setLoginType(LoginType.PASSWORD);
    }
}

