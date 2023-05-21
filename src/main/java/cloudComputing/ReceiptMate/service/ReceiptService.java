package cloudComputing.ReceiptMate.service;

import cloudComputing.ReceiptMate.dto.PictureResponse;
import cloudComputing.ReceiptMate.dto.ReceiptResponse;
import cloudComputing.ReceiptMate.entity.User;
import cloudComputing.ReceiptMate.repository.UserRepository;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
public class ReceiptService {

    @Autowired
    private MinioService minioService;

    private final AuthService authService;

    private final UserRepository userRepository;

    public ReceiptResponse addReceipt(MultipartFile file, HttpServletRequest httpServletRequest)
        throws UnsupportedEncodingException {
        return new ReceiptResponse("");
    }

    public List<ReceiptResponse> listReceipt(HttpServletRequest httpServletRequest) {
        return new ArrayList<>();
    }
}
