package cloudComputing.ReceiptMate.service;

import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Autowired
    private MinioService minioService;

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

    public void getFile(String object) throws MinioException, IOException {
        InputStream inputStream = minioService.get(Path.of(object));
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        File file = inputStreamResource.getFile();

        file.createNewFile();
    }
}
