package org.waterwood.waterfunservice.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.waterwood.waterfunservice.DTO.common.result.EmailCodeSendResult;


import java.util.Map;

@Slf4j
@Service
public class EmailService {
    @Value("${mail.resend.api-key}")
    private String apiKey;
    private final SpringTemplateEngine templateEngine;


    protected EmailService( SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Send email with Html type content by {@link EmailTemplateType}
     * @param to send to whom
     * @param type type of email
     * @param data inject data to template context
     */
    public EmailCodeSendResult sendHtmlEmail(String to, EmailTemplateType type, Map<String,Object> data) {
       return sendHtmlEmail(to,type.defaultFrom,type.subject,"email_base",type.templateKey,data);
    }

    /**
     * Send email with Html type content
     * by choosing <b>base template</b> with <b>content template</b>
     * @param to send to whom
     * @param from form whom
     * @param subject subject of email
     * @param baseTemplate baseTemplate
     * @param contentTemplate content Template
     * @param data data to inject into context
     */
    public EmailCodeSendResult sendHtmlEmail(String to, String from, String subject, String baseTemplate, String contentTemplate, Map<String,Object> data) {
        Context context = new Context();
        String contentPart = templateEngine.process("email/" + contentTemplate, context);
        data.put("content", contentPart);
        context.setVariables(data);

        String emailContent = templateEngine.process("email/" + baseTemplate , context);
        Resend resend = new Resend(apiKey);
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("WaterFun<verify@waterfun.com>")
                .to(to)
                .subject(subject)
                .html(emailContent)
                .build();
        try{
            CreateEmailResponse res = resend.emails().send(params);
            return EmailCodeSendResult.builder()
                    .sendSuccess(true)
                    .email(to)
                    .responseRaw(res.getId())
                    .build();
        } catch (ResendException e) {
            EmailCodeSendResult result = EmailCodeSendResult.builder()
                    .sendSuccess(false)
                    .email(to)
                    .message("Email send fail,Please check the email provider & params.")
                    .build();
            log.error(result.getMessage(), e);
            return result;
        }
    }

    @Getter
    public enum EmailTemplateType{
        VERIFY_CODE("verify-code","WaterFun 账户验证","WaterFun<verify@mail.waterfun.com>"),
        PASSWORD_RESET("password-reset","WaterFun 密码重置","WaterFun<verify@mail.waterfun.com>"),;
        private final String templateKey;
        private final String subject;
        private final String defaultFrom;
        EmailTemplateType(String templateKey, String subject, String defaultFrom) {
            this.templateKey = templateKey;
            this.subject = subject;
            this.defaultFrom = defaultFrom;
        }
    }
}
