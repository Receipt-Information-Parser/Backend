package cloudComputing.ReceiptMate.controller;

import cloudComputing.ReceiptMate.dto.*;
import cloudComputing.ReceiptMate.entity.User;
import cloudComputing.ReceiptMate.exception.InvalidObjectException;
import cloudComputing.ReceiptMate.exception.InvalidReceiptUserException;
import cloudComputing.ReceiptMate.repository.ReceiptRepository;
import cloudComputing.ReceiptMate.repository.UserRepository;
import cloudComputing.ReceiptMate.service.AnalysisService;
import cloudComputing.ReceiptMate.service.AuthService;
import cloudComputing.ReceiptMate.service.ReceiptService;
import com.google.api.client.util.IOUtils;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/year")
    public ResponseEntity<ListByPeriodResponse> getByYear(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(analysisService.getByYear(httpServletRequest));
    }

    @GetMapping("/month")
    public ResponseEntity<ListByPeriodResponse> getByMonth(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(analysisService.getByMonth(httpServletRequest));
    }

//    @PostMapping("/day")
//    public ResponseEntity<ListByPeriodResponse> getByDay(@RequestBody ByPeriodRequest byPeriodRequest, HttpServletRequest httpServletRequest) {
//        return ResponseEntity.ok().body(analysisService.getByDay(byPeriodRequest, httpServletRequest));
//    }

    @PostMapping("/product")
    public ResponseEntity<ListByProductResponse> getByName(@RequestBody ByProductRequest byProductRequest, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(analysisService.getByName(byProductRequest, httpServletRequest));
    }
}
