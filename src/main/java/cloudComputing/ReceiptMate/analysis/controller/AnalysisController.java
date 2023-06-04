package cloudComputing.ReceiptMate.analysis.controller;

import cloudComputing.ReceiptMate.analysis.dto.request.ByProductRequest;
import cloudComputing.ReceiptMate.analysis.dto.response.ByProductNameResponse;
import cloudComputing.ReceiptMate.analysis.dto.response.ListByPeriodResponse;
import cloudComputing.ReceiptMate.analysis.dto.response.ListByProductResponse;
import cloudComputing.ReceiptMate.analysis.service.AnalysisService;
import lombok.RequiredArgsConstructor;
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


    @PostMapping("/product")
    public ResponseEntity<ListByProductResponse> getByName(@RequestBody ByProductRequest byProductRequest, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(analysisService.getByName(byProductRequest, httpServletRequest));
    }

    @GetMapping("/names")
    public ResponseEntity<ByProductNameResponse> getNames(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(analysisService.getNames(httpServletRequest));
    }
}
