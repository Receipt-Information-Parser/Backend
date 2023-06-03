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
import java.sql.Date;
import java.time.LocalDate;
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

    public ListByPeriodResponse getByYear(HttpServletRequest httpServletRequest) {
        List<ByPeriod> byPeriods = byPeriodRepository.findAllByAnalysis(getAnalysis(httpServletRequest));
        List<ByPeriodResponse> byPeriodResponses = byPeriods.parallelStream()
                .map(ByPeriodResponse::new)
                .collect(
                        Collectors.toMap(
                                sum -> sum.getDate().getYear() + 1900,
                                Function.identity(),
                                (sum1, sum2) -> new ByPeriodResponse(
                                        java.sql.Date.valueOf(LocalDate.of(sum1.getDate().getYear() + 1900, 1, 1)),
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

    public ListByPeriodResponse getByMonth(HttpServletRequest httpServletRequest) {
        List<ByPeriod> byPeriods = byPeriodRepository.findAllByAnalysis(getAnalysis(httpServletRequest));
        List<ByPeriodResponse> byPeriodResponses = byPeriods.parallelStream()
                .map(ByPeriodResponse::new)
                .collect(
                        Collectors.toMap(
                                sum -> (sum.getDate().getYear() + 1900) * 100 + sum.getDate().getMonth() + 1,
                                Function.identity(),
                                (sum1, sum2) -> new ByPeriodResponse(
                                        java.sql.Date.valueOf(LocalDate.of(sum1.getDate().getYear() + 1900, sum1.getDate().getMonth() + 1, 1)),
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

    @Deprecated
    public ListByPeriodResponse getByDay(ByPeriodRequest byPeriodRequest, HttpServletRequest httpServletRequest) {
        List<ByPeriod> byPeriods = byPeriodRepository.findAllByAnalysis(getAnalysis(httpServletRequest));

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
