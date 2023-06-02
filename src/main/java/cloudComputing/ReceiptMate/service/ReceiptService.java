package cloudComputing.ReceiptMate.service;

import cloudComputing.ReceiptMate.dto.*;
import cloudComputing.ReceiptMate.entity.*;
import cloudComputing.ReceiptMate.enumerations.QuantityType;
import cloudComputing.ReceiptMate.exception.InvalidOwnerException;
import cloudComputing.ReceiptMate.exception.InvalidReceiptUserException;
import cloudComputing.ReceiptMate.exception.NotFoundException;
import cloudComputing.ReceiptMate.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.gson.Gson;
import org.json.CDL;

import java.io.*;
import java.net.URL;;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final FileService fileService;

    private final AuthService authService;

    private final UserRepository userRepository;

    private final ReceiptRepository receiptRepository;

    private final ByPeriodRepository byPeriodRepository;

    private final ByProductRepository byProductRepository;

    private final AnalysisRepository analysisRepository;

    public ReceiptResponse addReceipt(MultipartFile file, HttpServletRequest httpServletRequest)
            throws IOException {

        // check file if not image
        String filename = file.getOriginalFilename();
        if (!Files.probeContentType(Paths.get(filename)).startsWith("image")) {
            System.out.println("file.getOriginalFilename() = " + file.getOriginalFilename());
            return new ReceiptResponse();
        }

        // API 호출 DUMMY

        URL dummy = new URL("http://localhost:9999");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream is;

        is = dummy.openStream ();
        byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
        int n;

        while ( (n = is.read(byteChunk)) > 0 ) {
            byteArrayOutputStream.write(byteChunk, 0, n);
        }
        
        byte[] content = byteArrayOutputStream.toByteArray();

        String contentString =  new String(content, "UTF-8");
        Map<String, Object> jsonMap = new ObjectMapper().readValue(contentString, new TypeReference<>(){});

        Map<String, String> info = (LinkedHashMap) ((ArrayList) jsonMap.get("key_value")).get(0);

        List<Object> csv = CDL.toJSONArray((String) jsonMap.get("csv")).toList();

        String name = "test.csv";
        String originalFileName = "test.csv";
        String contentType = "text/plain";

        MultipartFile output = new MockMultipartFile(name, originalFileName, contentType, ((String)jsonMap.get("csv")) .getBytes());

        List<ByProductDTO> byProductDTOs = new ArrayList<>();

        for (Object mapObject : csv) {
            Map<String, String> map = (Map<String, String>) mapObject;

            String productName = map.get("품목");

            if (productName == null || productName.isBlank()) {
                continue;
            }

            String quantity = map.get("수량");
            if (quantity == null || quantity.isBlank()) {
                quantity = "0";
            }

            String amount = map.get("금액");
            if (amount == null || amount.isBlank() || amount.equals("0")) {
                continue;
            }

            QuantityType quantityType;
            try {
                quantityType = QuantityType.valueOf(map.get("단위"));
            } catch (IllegalArgumentException illegalArgumentException) {
                quantityType = QuantityType.NONE;
            }

            ByProductDTO byProductDTO = new ByProductDTO(
                    productName,
                    Float.parseFloat(quantity),
                    quantityType,
                    Integer.parseInt(amount)
            );
            
            byProductDTOs.add(byProductDTO);
        }
        Integer total = (Integer) jsonMap.get("total");

        ////////////////////////////////////////////

        final String receiptKey = fileService.upload(output);

        final User userByToken = authService.getUserByToken(httpServletRequest);

        Analysis analysis;

        if (!analysisRepository.existsByOwner(userByToken)) {
            Analysis newAnalysis = new Analysis();
            newAnalysis.setOwner(userByToken);
            analysis = analysisRepository.save(newAnalysis);
        } else {
            analysis = analysisRepository.findByOwner(userByToken).orElseThrow(NotFoundException::new);
        }

        Date now = Date.from(Instant.now());

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH) + 1;
        Integer date = cal.get(Calendar.DATE);

        Receipt receipt = Receipt.builder()
                .owner(userByToken)
                .detailKey(receiptKey)
                .createdDate(now)
                .info(info)
                .build();

        Receipt saved = receiptRepository.save(receipt);

        ByPeriod byPeriod = ByPeriod.builder()
                .year(year)
                .month(month)
                .date(date)
                .amount(total)
                .analysis(analysis)
                .originalReceiptId(saved.getId())
                .build();

        ByPeriod savedByPeriod = byPeriodRepository.save(byPeriod);

        for (ByProductDTO byProductDTO : byProductDTOs) {
            ByProduct byProduct = ByProduct.builder()
                    .name(byProductDTO.getName())
                    .quantity(byProductDTO.getQuantity())
                    .quantityType(byProductDTO.getQuantityType())
                    .amount(byProductDTO.getAmount())
                    .originalReceiptId(saved.getId())
                    .analysis(analysis)
                    .year(year)
                    .month(month)
                    .date(date)
                    .build();
            byProductRepository.save(byProduct);
        }

        return new ReceiptResponse(saved);
    }

    private String jsonToCSV(String json) throws IOException {

        JsonNode jsonTree = new ObjectMapper().readTree(json.getBytes());

        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode firstObject = jsonTree.elements().next();
        firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);} );
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        String returnString = csvMapper.writerFor(JsonNode.class)
                .with(csvSchema)
                .writeValueAsString(jsonTree);

        return returnString;
    }

    public StringResponse deleteReceipt(HttpServletRequest httpServletRequest, Long receiptId) {
        User owner = authService.getUserByToken(httpServletRequest);

        Receipt receipt = receiptRepository.findByOwnerAndId(owner, receiptId).orElseThrow(InvalidReceiptUserException::new);

        byProductRepository.deleteAllByOriginalReceiptId(receiptId);
        byPeriodRepository.deleteAllByOriginalReceiptId(receiptId);
        receiptRepository.delete(receipt);

        if (receiptRepository.existsById(receiptId)) {
            throw new NotFoundException();
        }

        if (byPeriodRepository.existsByOriginalReceiptId(receiptId)) {
            throw new NotFoundException();
        }

        if (byProductRepository.existsByOriginalReceiptId(receiptId)) {
            throw new NotFoundException();
        }

        StringResponse stringResponse = new StringResponse("삭제되었습니다.");

        return stringResponse;
    }

    public ReceiptResponse updateReceipt(HttpServletRequest httpServletRequest, ReceiptUpdateRequest receiptUpdateRequest) throws IOException {
        Receipt clone = receiptRepository.findByOwnerAndId(authService.getUserByToken(httpServletRequest), receiptUpdateRequest.getId()).orElseThrow(InvalidReceiptUserException::new);

        deleteReceipt(httpServletRequest, receiptUpdateRequest.getId());

        String name = "test.csv";
        String originalFileName = "test.csv";
        String contentType = "text/plain";

        Gson gson = new Gson();
        String json = gson.toJson(receiptUpdateRequest.getByProductDTOList());

        String toCSV = jsonToCSV(json);

        MultipartFile output = new MockMultipartFile(name, originalFileName, contentType, toCSV.getBytes());

        final String receiptKey = fileService.upload(output);

        final User userByToken = authService.getUserByToken(httpServletRequest);

        Analysis analysis;

        if (!analysisRepository.existsByOwner(userByToken)) {
            Analysis newAnalysis = new Analysis();
            newAnalysis.setOwner(userByToken);
            analysis = analysisRepository.save(newAnalysis);
        } else {
            analysis = analysisRepository.findByOwner(userByToken).orElseThrow(NotFoundException::new);
        }

        Date now = Date.from(Instant.now());

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH) + 1;
        Integer date = cal.get(Calendar.DATE);

        Receipt receipt = Receipt.builder()
                .owner(userByToken)
                .detailKey(receiptKey)
                .createdDate(now)
                .info(clone.getInfo())
                .build();

        Receipt saved = receiptRepository.save(receipt);

        ByPeriod byPeriod = ByPeriod.builder()
                .year(year)
                .month(month)
                .date(date)
                .amount(receiptUpdateRequest.getAmount())
                .analysis(analysis)
                .originalReceiptId(saved.getId())
                .build();

        ByPeriod savedByPeriod = byPeriodRepository.save(byPeriod);

        for (ByProductDTO byProductDTO : receiptUpdateRequest.getByProductDTOList()) {
            ByProduct byProduct = ByProduct.builder()
                    .name(byProductDTO.getName())
                    .quantity(byProductDTO.getQuantity())
                    .quantityType(byProductDTO.getQuantityType())
                    .amount(byProductDTO.getAmount())
                    .originalReceiptId(saved.getId())
                    .analysis(analysis)
                    .year(year)
                    .month(month)
                    .date(date)
                    .build();
            byProductRepository.save(byProduct);
        }

        return new ReceiptResponse(saved);
    }

    public ListReceiptResponses listReceipt(HttpServletRequest httpServletRequest) {
        final User userByToken = authService.getUserByToken(httpServletRequest);

        List<Receipt> allReceiptsByOwner = receiptRepository.findAllByOwner(userByToken);

        if (allReceiptsByOwner.isEmpty()) throw new InvalidOwnerException();

        List<ReceiptResponse> receiptResponses = new ArrayList<>();

        for (Receipt receipt : allReceiptsByOwner) {
            receiptResponses.add(new ReceiptResponse(receipt));
        }

        return new ListReceiptResponses(receiptResponses);
    }
}
