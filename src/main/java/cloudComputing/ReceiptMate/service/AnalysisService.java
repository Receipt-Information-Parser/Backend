package cloudComputing.ReceiptMate.service;

import cloudComputing.ReceiptMate.dto.*;
import cloudComputing.ReceiptMate.entity.Analysis;
import cloudComputing.ReceiptMate.entity.ByPeriod;
import cloudComputing.ReceiptMate.entity.ByProduct;
import cloudComputing.ReceiptMate.entity.User;
import cloudComputing.ReceiptMate.exception.IllegalTokenException;
import cloudComputing.ReceiptMate.exception.InvalidOwnerException;
import cloudComputing.ReceiptMate.exception.InvalidTokenException;
import cloudComputing.ReceiptMate.exception.InvalidUserException;
import cloudComputing.ReceiptMate.repository.AnalysisRepository;
import cloudComputing.ReceiptMate.repository.ByPeriodRepository;
import cloudComputing.ReceiptMate.repository.ByProductRepository;
import cloudComputing.ReceiptMate.repository.UserRepository;
import cloudComputing.ReceiptMate.util.JwtUtil;
import com.google.common.net.HttpHeaders;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnalysisService {
    private final ByProductRepository byProductRepository;

    private final ByPeriodRepository byPeriodRepository;

    private final AuthService authService;

    private final AnalysisRepository analysisRepository;

    private Analysis getAnalysis(HttpServletRequest httpServletRequest) {
        User userByToken = authService.getUserByToken(httpServletRequest);
        Analysis analysis = analysisRepository.findByOwner(userByToken).orElseThrow(InvalidOwnerException::new);

        return analysis;
    }

    public ListByPeriodResponse getByYear(ByPeriodRequest byPeriodRequest, HttpServletRequest httpServletRequest) {
        List<ByPeriod> byPeriods = byPeriodRepository.findAllByYearAndAnalysis(byPeriodRequest.getYear(), getAnalysis(httpServletRequest));
        List<ByPeriodResponse> byPeriodResponses = byPeriods.parallelStream()
                .map(byPeriod -> new ByPeriodResponse(byPeriod, byPeriodRequest.getYear()))
                .collect(
                        Collectors.toMap(
                                ByPeriodResponse::getDate,
                                Function.identity(),
                                (sum1, sum2) -> new ByPeriodResponse(
                                        sum1.getDate(),
                                        sum1.getAmount() + sum2.getAmount(),
                                        sum1.getAnalysisId()
                                )
                        )
                )
                .values()
                .stream()
                .sorted(Comparator.comparing(ByPeriodResponse::getDate))
                .collect(Collectors.toList());

        ListByPeriodResponse listByPeriodResponse = new ListByPeriodResponse();

        listByPeriodResponse.setByPeriods(byPeriodResponses);

        return listByPeriodResponse;
    }

    public ListByPeriodResponse getByMonth(ByPeriodRequest byPeriodRequest, HttpServletRequest httpServletRequest) {
        List<ByPeriod> byPeriods = byPeriodRepository.findAllByYearAndMonthAndAnalysis(byPeriodRequest.getYear(), byPeriodRequest.getMonth(), getAnalysis(httpServletRequest));
        List<ByPeriodResponse> byPeriodResponses = byPeriods.parallelStream()
                .map(byPeriod -> new ByPeriodResponse(byPeriod, byPeriodRequest.getYear(), byPeriodRequest.getMonth()))
                .collect(
                        Collectors.toMap(
                                ByPeriodResponse::getDate,
                                Function.identity(),
                                (sum1, sum2) -> new ByPeriodResponse(
                                        sum1.getDate(),
                                        sum1.getAmount() + sum2.getAmount(),
                                        sum1.getAnalysisId()
                                )
                        )
                )
                .values()
                .stream()
                .sorted(Comparator.comparing(ByPeriodResponse::getDate))
                .collect(Collectors.toList());

        ListByPeriodResponse listByPeriodResponse = new ListByPeriodResponse();

        listByPeriodResponse.setByPeriods(byPeriodResponses);

        return listByPeriodResponse;
    }

    public ListByPeriodResponse getByDay(ByPeriodRequest byPeriodRequest, HttpServletRequest httpServletRequest) {
        List<ByPeriod> byPeriods = byPeriodRepository.findAllByYearAndMonthAndDayAndAnalysis(byPeriodRequest.getYear(), byPeriodRequest.getMonth(), byPeriodRequest.getDay(), getAnalysis(httpServletRequest));
        List<ByPeriodResponse> byPeriodResponses = byPeriods.parallelStream()
                .map(ByPeriodResponse::new)
                .collect(
                        Collectors.toMap(
                                ByPeriodResponse::getDate,
                                Function.identity(),
                                (sum1, sum2) -> new ByPeriodResponse(
                                        sum1.getDate(),
                                        sum1.getAmount() + sum2.getAmount(),
                                        sum1.getAnalysisId()
                                )
                        )
                )
                .values()
                .stream()
                .sorted(Comparator.comparing(ByPeriodResponse::getDate))
                .collect(Collectors.toList());

        ListByPeriodResponse listByPeriodResponse = new ListByPeriodResponse();

        listByPeriodResponse.setByPeriods(byPeriodResponses);

        return listByPeriodResponse;
    }

    public ListByProductResponse getByName(ByProductRequest byProductRequest, HttpServletRequest httpServletRequest) {
        List<ByProduct> byProducts = byProductRepository.findAllByNameAndAnalysis(byProductRequest.getName(), getAnalysis(httpServletRequest));

        ListByProductResponse listByProductResponse = new ListByProductResponse();

        List<ByProductResponse> byProductResponses = new ArrayList<>();

        for (ByProduct byProduct : byProducts) {
            byProductResponses.add(new ByProductResponse(byProduct));
        }

        listByProductResponse.setByProducts(byProductResponses);

        return listByProductResponse;
    }
}
