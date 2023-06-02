package cloudComputing.ReceiptMate.controller;

import cloudComputing.ReceiptMate.dto.*;
import cloudComputing.ReceiptMate.entity.User;
import cloudComputing.ReceiptMate.exception.InvalidObjectException;
import cloudComputing.ReceiptMate.exception.InvalidProfileException;
import cloudComputing.ReceiptMate.exception.InvalidReceiptUserException;
import cloudComputing.ReceiptMate.repository.ReceiptRepository;
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
import org.springframework.web.bind.annotation.*;
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
    private final ReceiptRepository receiptRepository;

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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StringResponse> deleteReceipt(@PathVariable("id") Long id, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(receiptService.deleteReceipt(id, httpServletRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<ReceiptResponse> updateReceipt(@RequestBody ReceiptUpdateRequest receiptUpdateRequest, HttpServletRequest httpServletRequest) {
        ReceiptResponse receiptResponse = null;

        try {
            receiptResponse = receiptService.updateReceipt(httpServletRequest, receiptUpdateRequest);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(receiptResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<ListReceiptResponses> listReceipt(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(receiptService.listReceipt(httpServletRequest));
    }

    @GetMapping("/{object}")
    public void getObject(@PathVariable("object") String object, HttpServletResponse response, HttpServletRequest httpServletRequest) throws MinioException, IOException {
        final User owner = receiptRepository.findReceiptByDetailKey(object).orElseThrow(InvalidObjectException::new).getOwner();

        Long id = authService.getUserByToken(httpServletRequest).getId();

        if (owner.getId() != id) throw new InvalidReceiptUserException();

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
