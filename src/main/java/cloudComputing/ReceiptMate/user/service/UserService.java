package cloudComputing.ReceiptMate.user.service;

import cloudComputing.ReceiptMate.auth.dto.request.TokenRequest;
import cloudComputing.ReceiptMate.base.dto.response.BooleanResponse;
import cloudComputing.ReceiptMate.base.exception.NotFoundException;
import cloudComputing.ReceiptMate.user.dto.request.*;
import cloudComputing.ReceiptMate.base.dto.response.StringResponse;
import cloudComputing.ReceiptMate.user.dto.UserMapper;
import cloudComputing.ReceiptMate.user.dto.response.UserResponse;
import cloudComputing.ReceiptMate.user.enumeration.Authority;
import cloudComputing.ReceiptMate.user.enumeration.Gender;
import cloudComputing.ReceiptMate.user.exception.*;
import cloudComputing.ReceiptMate.auth.service.AuthService;
import cloudComputing.ReceiptMate.base.util.JwtUtil;
import cloudComputing.ReceiptMate.auth.data.TokenInfo;
import cloudComputing.ReceiptMate.user.entity.User;
import cloudComputing.ReceiptMate.user.repository.UserRepository;
import cloudComputing.ReceiptMate.base.service.MailService;
import cloudComputing.ReceiptMate.base.util.PasswordUtil;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public JsonObject getUserDataFromKakaoToken(String token) throws InvalidKakaoTokenException {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(null, headers);

        ResponseEntity<String> response = rt.exchange(
                reqURL,
                HttpMethod.GET,
                kakaoTokenRequest,
                String.class
        );

        String json = response.getBody();

        System.out.println("json = " + json);
        return JsonParser.parseString(json).getAsJsonObject();
    }

    @Transactional
    public UserResponse signUpByKakao(KakaoSignUpRequest kakaoSignUpRequest) {

        final String accessToken = kakaoSignUpRequest.getKakaoToken();

        System.out.println("accessToken = " + accessToken);

        JsonObject jsonObject = getUserDataFromKakaoToken(accessToken);

        if (jsonObject.isJsonNull()) throw new InvalidKakaoTokenException();

        String id = jsonObject.get("id").getAsString();

        JsonObject kakaoAccount = jsonObject
                .get("kakao_account").getAsJsonObject();

        JsonObject kakaoProfile = kakaoAccount
                .get("profile").getAsJsonObject();

        boolean hasEmail = kakaoAccount
                .get("has_email").getAsBoolean();

        String name = kakaoProfile.get("nickname").getAsString();
        Gender gender;
        String email;

        if (kakaoAccount.get("has_gender").getAsBoolean()) {
            gender = Gender.valueOf(kakaoAccount.get("gender").getAsString().toUpperCase());
        } else {
            throw new NotFoundException();
        }

        if(kakaoAccount.get("has_email").getAsBoolean()){
            email = kakaoAccount.get("email").getAsString();
        } else {
            throw new NotFoundException();
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        final User user = User.builder()
                .email(email)
                .name(name)
                .nickname(kakaoSignUpRequest.getNickname())
                .gender(gender)
                .birthday(kakaoSignUpRequest.getBirthday())
                .profileImage(kakaoProfile.get("profile_image_url").getAsString())
                .authority(Authority.USER)
                .kakaoID(id)
                .isKakao(true)
                .build();

        final User savedUser = userRepository.save(user);

        UserResponse userResponse = UserMapper.INSTANCE.userToResponse(savedUser);
        userResponse.setTokenResponse(jwtUtil.generateToken(getTokenInfo(savedUser)));

        return userResponse;
    }

    public UserResponse logInByKakao(KakaoLogInRequest kakaoLogInRequest) {
        final String kakaoToken = kakaoLogInRequest.getKakaoToken();

        JsonObject jsonObject = getUserDataFromKakaoToken(kakaoToken);

        if (jsonObject.isJsonNull()) throw new InvalidKakaoTokenException();

        String id = jsonObject.get("id").getAsString();

        JsonObject kakaoAccount = jsonObject
                .get("kakao_account").getAsJsonObject();

        String email;

        if(kakaoAccount.get("has_email").getAsBoolean()){
            email = kakaoAccount.get("email").getAsString();
        } else {
            throw new NotFoundException();
        }


        User user = userRepository.findUserByEmail(email).orElseThrow(InvalidUserException::new);

        if (!user.getKakaoID().equals(id)) {
            throw new InvalidKakaoInfoException();
        }

        UserResponse userResponse = UserMapper.INSTANCE.userToResponse(user);
        userResponse.setTokenResponse(jwtUtil.generateToken(getTokenInfo(user)));

        return userResponse;
    }

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
        user.setIsKakao(false);

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

    public BooleanResponse existByKakaoId(String id) {
        return new BooleanResponse(userRepository.existsByKakaoID(id));
    }
}
