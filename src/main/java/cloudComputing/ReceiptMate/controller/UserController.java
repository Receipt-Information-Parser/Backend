package cloudComputing.ReceiptMate.controller;

import cloudComputing.ReceiptMate.service.UserService;
import cloudComputing.ReceiptMate.dto.EmailRequest;
import cloudComputing.ReceiptMate.dto.LogInRequest;
import cloudComputing.ReceiptMate.dto.SignUpRequest;
import cloudComputing.ReceiptMate.dto.StringResponse;
import cloudComputing.ReceiptMate.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/exists")
    public ResponseEntity<StringResponse> checkEmailAvailability(@Valid @RequestBody EmailRequest emailRequest) {
        return ResponseEntity.ok().body(userService.checkEmailAvailability(emailRequest));
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
}
