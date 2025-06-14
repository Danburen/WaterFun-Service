package org.waterwood.waterfunservice.DTO.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.waterwood.waterfunservice.service.common.ServiceErrorCode;

/**
 * Represents the result of sending an SMS code.
 */
@Getter
@AllArgsConstructor
@Builder
public class SmsCodeResult{
    private final Boolean trySendSuccess;
    private @Nullable final String msg;
    private @Nullable final ServiceErrorCode serviceErrorCode;
    private @Nullable final SmsCodeSendResult result;
}
