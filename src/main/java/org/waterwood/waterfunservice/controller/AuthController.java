package org.waterwood.waterfunservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.DTO.enums.ErrorCode;
import org.waterwood.waterfunservice.DTO.request.EmailLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.LoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.PwdLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.SmsLoginRequestBody;
import org.waterwood.waterfunservice.service.authServices.CaptchaService;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private CaptchaService captchaService;

    /** redis + cookie(HttpOnly) save captcha
     * Generate the captcha
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response) throws IOException{
        CaptchaService.LineCaptchaResult result = captchaService.generateCaptcha();
        Cookie cookie = new Cookie("CAPTCHA_KEY",result.uuid());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(120);
        response.addCookie(cookie);
        // set the header of response
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setDateHeader("Expires", 0);
        // write img stream to response stream
        result.captcha().write(response.getOutputStream());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestBody loginRequestBody, HttpServletRequest request) {
        if(loginRequestBody.getUsername() == null || loginRequestBody.getUsername().isEmpty()) {
            return ErrorCode.USERNAME_EMPTY.toResponseEntity();
        }

        if(loginRequestBody instanceof PwdLoginRequestBody body){
            if(body.getPassword() == null || body.getPassword().isEmpty()) {
                return ErrorCode.PASSWORD_EMPTY.toResponseEntity();
            }
            String uuid = Arrays.stream(request.getCookies())
                .filter(c->"CAPTCHA_KEY".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
            if (uuid == null) {
                return ResponseEntity.badRequest().body("can't get uuid");
    //            return ErrorCode.CAPTCHA_EXPIRED.toResponseEntity();
            }
            if(!captchaService.validateCode(uuid, body.getCaptcha())){
                return ErrorCode.CAPTCHA_INCORRECT.toResponseEntity();
            }
            if (!"admin".equals(body.getUsername()) || !"123456".equals(body.getPassword())) {
                return ErrorCode.USERNAME_OR_PASSWORD_INCORRECT.toResponseEntity();
            }

        }else if(loginRequestBody instanceof SmsLoginRequestBody body){

        } else if (loginRequestBody instanceof EmailLoginRequestBody body) {

        }
//        if (loginRequestBody.getUsername() == null) {
//            return ErrorCode.USERNAME_EMPTY.toResponseEntity();
//        }else if (loginRequestBody.getPassword() == null) {
//            return ErrorCode.PASSWORD_EMPTY.toResponseEntity();
//        }else if(loginRequestBody.getVerifyCode() == null || loginRequestBody.getVerifyCode().isEmpty()) {
//            return ErrorCode.CAPTCHA_EMPTY.toResponseEntity();
//        }
//        // get captcha key(uuid) from cookie from frontend
//        String uuid = Arrays.stream(request.getCookies())
//                .filter(c->"CAPTCHA_KEY".equals(c.getName()))
//                .findFirst()
//                .map(Cookie::getValue)
//                .orElse(null);
//        if (uuid == null) {
//            return ResponseEntity.badRequest().body("can't get uuid");
////            return ErrorCode.CAPTCHA_EXPIRED.toResponseEntity();
//        }
//        // get captcha code from redis
//        String correctCode = captchaService.getCode(uuid);
//        if(correctCode == null){
//            return ResponseEntity.badRequest().body("redis can't get captcha key");
//            //return ErrorCode.CAPTCHA_EXPIRED.toResponseEntity();
//        }
//        // verify the captcha code from user and redis
//        if(!correctCode.equals(loginRequestBody.getVerifyCode())){
//            return ErrorCode.CAPTCHA_INCORRECT.toResponseEntity();
//        }
//        if (!"admin".equals(loginRequestBody.getUsername()) ||
//                !"123456".equals(loginRequestBody.getPassword())) {
//            return ErrorCode.USERNAME_OR_PASSWORD_INCORRECT.toResponseEntity();
//        }
//        captchaService.removeCode(uuid);
        return ResponseEntity.ok("Successfully Login!");
    }
}
