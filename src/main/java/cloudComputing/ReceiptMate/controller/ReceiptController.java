package cloudComputing.ReceiptMate.controller;

import cloudComputing.ReceiptMate.dto.EmailRequest;
import cloudComputing.ReceiptMate.dto.LogInRequest;
import cloudComputing.ReceiptMate.dto.NicknameRequest;
import cloudComputing.ReceiptMate.dto.PictureResponse;
import cloudComputing.ReceiptMate.dto.ReceiptResponse;
import cloudComputing.ReceiptMate.dto.SignUpRequest;
import cloudComputing.ReceiptMate.dto.StringResponse;
import cloudComputing.ReceiptMate.dto.UserResponse;
import cloudComputing.ReceiptMate.exception.InvalidObjectException;
import cloudComputing.ReceiptMate.exception.InvalidProfileException;
import cloudComputing.ReceiptMate.repository.UserRepository;
import cloudComputing.ReceiptMate.service.AuthService;
import cloudComputing.ReceiptMate.service.ReceiptService;
import cloudComputing.ReceiptMate.service.UserService;
import com.google.api.client.util.IOUtils;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipt")
public class ReceiptController {

    @Autowired
    private final MinioService minioService;
    private final ReceiptService receiptService;
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ReceiptResponse> addReceipt(@RequestPart("file") MultipartFile file, HttpServletRequest httpServletRequest) {
        ReceiptResponse receiptResponse = null;

        try {
            receiptResponse = receiptService.addReceipt(file, httpServletRequest);
            if (receiptResponse == null) throw new Exception();
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(receiptResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ReceiptResponse>> listReceipt(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(receiptService.listReceipt(httpServletRequest));
    }

    @GetMapping("/{object}")
    public void getObject(@PathVariable("object") String object, HttpServletResponse response, HttpServletRequest httpServletRequest) throws MinioException, IOException {
        Long id1 = authService.getUserByToken(httpServletRequest).getId();

        Long id2 = userRepository.findUserByProfileImage(object).orElseThrow(
            InvalidObjectException::new).getId();

        if (id1 != id2) throw new InvalidProfileException();

        InputStream inputStream = minioService.get(Path.of(object));
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        // Set the content type and attachment header.
        response.addHeader("Content-disposition", "attachment;filename=" + object);
        response.setContentType(URLConnection.guessContentTypeFromName(object));

        // Copy the stream to the response's output stream.
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }
}
