package cloudComputing.ReceiptMate.base.controller;

import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileController {

    private final MinioService minioService;

    @GetMapping("/list")
    public List<Item> list() throws MinioException {
        return minioService.list();
    }
}

