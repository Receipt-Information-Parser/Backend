package cloudComputing.ReceiptMate.service;

import cloudComputing.ReceiptMate.dto.PictureResponse;
import cloudComputing.ReceiptMate.dto.ReceiptResponse;
import cloudComputing.ReceiptMate.entity.Receipt;
import cloudComputing.ReceiptMate.entity.User;
import cloudComputing.ReceiptMate.exception.InvalidOwnerException;
import cloudComputing.ReceiptMate.repository.ReceiptRepository;
import cloudComputing.ReceiptMate.repository.UserRepository;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
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

    private final FileService fileService;

    private final AuthService authService;

    private final UserRepository userRepository;

    private final ReceiptRepository receiptRepository;

    public ReceiptResponse addReceipt(MultipartFile file, HttpServletRequest httpServletRequest)
        throws UnsupportedEncodingException {

        final String receiptKey = fileService.upload(file);

        final User userByToken = authService.getUserByToken(httpServletRequest);

        Receipt receipt = new Receipt();

        receipt.setDetailKey(receiptKey);
        receipt.setOwner(userByToken);

        Receipt saved = receiptRepository.save(receipt);

        return new ReceiptResponse(saved.getDetailKey());
    }

    public List<ReceiptResponse> listReceipt(HttpServletRequest httpServletRequest) {
        final User userByToken = authService.getUserByToken(httpServletRequest);

        List<Receipt> allReceiptsByOwner = receiptRepository.findAllByOwner(userByToken);

        if (allReceiptsByOwner.isEmpty()) throw new InvalidOwnerException();

        List<ReceiptResponse> receiptResponses = new ArrayList<>();

        for (Receipt receipt : allReceiptsByOwner) {
            receiptResponses.add(new ReceiptResponse(receipt.getDetailKey()));
        }

        return receiptResponses;
    }
}
