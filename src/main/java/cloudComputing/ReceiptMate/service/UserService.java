package cloudComputing.ReceiptMate.service;

import cloudComputing.ReceiptMate.dto.EmailRequest;
import cloudComputing.ReceiptMate.dto.LogInRequest;
import cloudComputing.ReceiptMate.dto.SignUpRequest;
import cloudComputing.ReceiptMate.dto.StringResponse;
import cloudComputing.ReceiptMate.dto.UserMapper;
import cloudComputing.ReceiptMate.dto.UserResponse;
import cloudComputing.ReceiptMate.exception.DuplicateEmailException;
import cloudComputing.ReceiptMate.exception.DuplicateNicknameException;
import cloudComputing.ReceiptMate.exception.InvalidUserException;
import cloudComputing.ReceiptMate.util.JwtUtil;
import cloudComputing.ReceiptMate.auth.TokenInfo;
import cloudComputing.ReceiptMate.entity.User;
import cloudComputing.ReceiptMate.exception.InvalidPasswordException;
import cloudComputing.ReceiptMate.repository.UserRepository;
import cloudComputing.ReceiptMate.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public StringResponse checkEmailAvailability(EmailRequest emailRequest) {
        if (userRepository.existsByEmail(emailRequest.getEmail())) throw new DuplicateEmailException();

        return new StringResponse("사용가능한 이메일입니다");
    }

    @Transactional
    public UserResponse signUp(SignUpRequest signUpRequest) {

        if (userRepository.existsByEmail(signUpRequest.getEmail())) throw new DuplicateEmailException(); // 혹시 모름
        if (userRepository.existsByNickname(signUpRequest.getNickname())) throw new DuplicateNicknameException();

        signUpRequest.setPassword(authService.encodePassword(signUpRequest.getPassword()));

        User user = UserMapper.INSTANCE.requestToUser(signUpRequest);

        final User savedUser = userRepository.save(user);

        UserResponse userResponse = UserMapper.INSTANCE.userToResponse(savedUser);
        userResponse.setTokenResponse(jwtUtil.generateToken(getTokenInfo(savedUser)));

        return userResponse;
    }

    public UserResponse logIn(LogInRequest logInRequest) {

        final User user = userRepository.findUserByEmail(logInRequest.getEmail()).orElseThrow(InvalidUserException::new);

        if (!passwordEncoder.matches(logInRequest.getPassword(), user.getPassword())) throw new InvalidPasswordException();

        UserResponse userResponse = UserMapper.INSTANCE.userToResponse(user);
        userResponse.setTokenResponse(jwtUtil.generateToken(getTokenInfo(user)));

        return userResponse;
    }

    @Transactional
    public StringResponse resetPassword(EmailRequest emailRequest) {

        User user = userRepository.findUserByEmail(emailRequest.getEmail()).orElseThrow(InvalidUserException::new);

        final String randomPassword = PasswordUtil.randomPw();
        mailService.sendPasswordMail(emailRequest.getEmail(), randomPassword);
        user.setPassword(authService.encodePassword(randomPassword));
        userRepository.save(user);

        return new StringResponse("해당 계정의 이메일로 임시 비밀번호를 발송하였습니다");
    }

    private TokenInfo getTokenInfo(User user) { // 이거 JwtUtil로 돌릴지
        return new TokenInfo(user.getId(), user.getEmail(), user.getAuthority());
    }

}
