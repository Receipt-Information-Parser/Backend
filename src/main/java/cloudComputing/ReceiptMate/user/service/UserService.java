package cloudComputing.ReceiptMate.user.service;

import cloudComputing.ReceiptMate.user.dto.request.EmailRequest;
import cloudComputing.ReceiptMate.user.dto.request.LogInRequest;
import cloudComputing.ReceiptMate.user.dto.request.NicknameRequest;
import cloudComputing.ReceiptMate.user.dto.request.SignUpRequest;
import cloudComputing.ReceiptMate.base.dto.response.StringResponse;
import cloudComputing.ReceiptMate.user.dto.UserMapper;
import cloudComputing.ReceiptMate.user.dto.response.UserResponse;
import cloudComputing.ReceiptMate.user.exception.DuplicateEmailException;
import cloudComputing.ReceiptMate.user.exception.DuplicateNicknameException;
import cloudComputing.ReceiptMate.user.exception.InvalidUserException;
import cloudComputing.ReceiptMate.auth.service.AuthService;
import cloudComputing.ReceiptMate.base.util.JwtUtil;
import cloudComputing.ReceiptMate.auth.data.TokenInfo;
import cloudComputing.ReceiptMate.user.entity.User;
import cloudComputing.ReceiptMate.user.exception.InvalidPasswordException;
import cloudComputing.ReceiptMate.user.repository.UserRepository;
import cloudComputing.ReceiptMate.base.service.MailService;
import cloudComputing.ReceiptMate.base.util.PasswordUtil;
import javax.servlet.http.HttpServletRequest;
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

    public StringResponse checkNicknameAvailability(NicknameRequest nicknameRequest) {
        if (userRepository.existsByNickname(nicknameRequest.getNickname())) throw new DuplicateNicknameException();

        return new StringResponse("사용가능한 닉네임입니다");
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

    public StringResponse getEmail(NicknameRequest nicknameRequest) {

        final User user = userRepository.findUserByNickname(nicknameRequest.getNickname()).orElseThrow(InvalidUserException::new);

        return new StringResponse(user.getEmail());
    }

    public UserResponse modifyNickname(NicknameRequest nicknameRequest, HttpServletRequest httpServletRequest) {
        checkNicknameAvailability(nicknameRequest);

        User userByToken = authService.getUserByToken(httpServletRequest);

        userByToken.setNickname(nicknameRequest.getNickname());

        final User saved = userRepository.save(userByToken);

        UserResponse userResponse = UserMapper.INSTANCE.userToResponse(userByToken);
        userResponse.setTokenResponse(jwtUtil.generateToken(getTokenInfo(userByToken)));

        return userResponse;
    }

    private TokenInfo getTokenInfo(User user) { // 이거 JwtUtil로 돌릴지
        return new TokenInfo(user.getId(), user.getEmail(), user.getAuthority());
    }
}
