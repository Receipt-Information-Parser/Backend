package cloudComputing.ReceiptMate.picture.service;

import cloudComputing.ReceiptMate.auth.service.AuthService;
import cloudComputing.ReceiptMate.picture.dto.response.PictureResponse;
import cloudComputing.ReceiptMate.user.entity.User;
import cloudComputing.ReceiptMate.user.repository.UserRepository;

import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;

import cloudComputing.ReceiptMate.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PictureService {

    private final FileService fileService;

    private final AuthService authService;

    private final UserRepository userRepository;

    public PictureResponse save(MultipartFile file, HttpServletRequest httpServletRequest)
        throws UnsupportedEncodingException {

        final String pictureKey = fileService.upload(file);

        User userByToken = authService.getUserByToken(httpServletRequest);

        userByToken.setProfileImage(pictureKey);

        User saved = userRepository.save(userByToken);

        return new PictureResponse(saved.getProfileImage());
    }
}
