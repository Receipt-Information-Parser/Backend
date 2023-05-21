package cloudComputing.ReceiptMate.service;

import cloudComputing.ReceiptMate.dto.PictureResponse;
import cloudComputing.ReceiptMate.entity.User;
import cloudComputing.ReceiptMate.repository.UserRepository;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PictureService {

    @Autowired
    private MinioService minioService;

    private final AuthService authService;

    private final UserRepository userRepository;

    public PictureResponse save(MultipartFile file, HttpServletRequest httpServletRequest)
        throws UnsupportedEncodingException {

        final String pictureKey = upload(file);

        User userByToken = authService.getUserByToken(httpServletRequest);

        userByToken.setProfileImage(pictureKey);

        User saved = userRepository.save(userByToken);

        return new PictureResponse(saved.getProfileImage());
    }

    public String upload(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();

        String extension = FilenameUtils.getExtension(originalFilename);

        final String filename = UUID.randomUUID().toString() + "." + extension;

        // System.out.println("filename = " + filename);

        Path path = Path.of(filename);

        try {
            minioService.upload(path, file.getInputStream(), file.getContentType());
        } catch (MinioException e) {
            throw new IllegalStateException("The file cannot be upload on the internal storage. Please retry later", e);
        } catch (IOException e) {
            throw new IllegalStateException("The file cannot be read", e);
        }

        return filename;
    }

    public void getPhoto(String object) throws MinioException, IOException {
        InputStream inputStream = minioService.get(Path.of(object));
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        File file = inputStreamResource.getFile();

        file.createNewFile();
    }
}
