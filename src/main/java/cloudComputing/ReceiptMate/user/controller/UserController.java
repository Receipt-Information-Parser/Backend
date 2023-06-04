package cloudComputing.ReceiptMate.user.controller;

import cloudComputing.ReceiptMate.user.dto.request.NicknameRequest;
import cloudComputing.ReceiptMate.user.service.UserService;
import cloudComputing.ReceiptMate.user.dto.request.EmailRequest;
import cloudComputing.ReceiptMate.user.dto.request.LogInRequest;
import cloudComputing.ReceiptMate.user.dto.request.SignUpRequest;
import cloudComputing.ReceiptMate.base.dto.response.StringResponse;
import cloudComputing.ReceiptMate.user.dto.response.UserResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/existsEmail")
    public ResponseEntity<StringResponse> checkEmailAvailability(@Valid @RequestBody EmailRequest emailRequest) {
        return ResponseEntity.ok().body(userService.checkEmailAvailability(emailRequest));
    }

    @PostMapping("/existsNickname")
    public ResponseEntity<StringResponse> checkNicknameAvailability(@RequestBody NicknameRequest nicknameRequest) {
        return ResponseEntity.ok().body(userService.checkNicknameAvailability(nicknameRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok().body(userService.signUp(signUpRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> logIn(@RequestBody LogInRequest logInRequest) {
        return ResponseEntity.ok().body(userService.logIn(logInRequest));
    }

    @PostMapping("/reset")
    public ResponseEntity<StringResponse> resetPassword(@Valid @RequestBody EmailRequest emailRequest) {
        return ResponseEntity.ok().body(userService.resetPassword(emailRequest));
    }

    @PostMapping("/getNickname")
    public ResponseEntity<StringResponse> getEmail(@RequestBody NicknameRequest nicknameRequest) {
        return ResponseEntity.ok().body(userService.getEmail(nicknameRequest));
    }

    @PostMapping("/modifyNickname")
    public ResponseEntity<UserResponse> modifyNickname(@RequestBody NicknameRequest nicknameRequest, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(userService.modifyNickname(nicknameRequest, httpServletRequest));
    }
}