package org.waterwood.waterfunservice.DTO.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CodePurpose {
    @JsonProperty("login") LOGIN,
    @JsonProperty("register") REGISTER,
    @JsonProperty("resetPassword") RESET_PASSWORD,
}
