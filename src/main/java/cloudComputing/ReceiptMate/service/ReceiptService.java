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
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import java.net.http.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.CDL;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;



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

    @Value("${lambda.path}")
    private String path;

    public ReceiptResponse addReceipt(MultipartFile file, HttpServletRequest httpServletRequest)
            throws IOException, URISyntaxException, InterruptedException {

        // check file if not image

        String filename = file.getOriginalFilename();
        if (!Files.probeContentType(Paths.get(filename)).startsWith("image")) {
            System.out.println("file.getOriginalFilename() = " + file.getOriginalFilename());
            return new ReceiptResponse();
        }

        HttpClient httpClient = HttpClient.newHttpClient();

        InputStream inputStream = file.getInputStream();

        File tempFile = File.createTempFile(String.valueOf(inputStream.hashCode()), ".tmp");
        tempFile.deleteOnExit();

        copyInputStreamToFile(inputStream, tempFile);

        HttpEntity httpEntity = MultipartEntityBuilder.create()
                // FILE
                .addBinaryBody("file", tempFile, ContentType.IMAGE_JPEG,
                        "file.jpg")
                .build();

        Pipe pipe = Pipe.open();

        new Thread(() -> {
            try (OutputStream outputStream = Channels.newOutputStream(pipe.sink())) {
                // Write the encoded data to the pipeline.
                httpEntity.writeTo(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();


        HttpRequest request = HttpRequest.newBuilder(new URI(path))
                .header("Content-Type", httpEntity.getContentType().getValue())
                .POST(HttpRequest.BodyPublishers.ofInputStream(() -> Channels.newInputStream(pipe.source()))).build();

        HttpResponse<String> responseBody = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        String contentString = new String(responseBody.body().getBytes());

        System.out.println("contentString = " + contentString);
        Map<String, Object> jsonMap = new ObjectMapper().readValue(contentString, new TypeReference<>(){});

        Map<String, String> info = (LinkedHashMap) ((ArrayList) jsonMap.get("key_value")).get(0);

        List<Object> csv = CDL.toJSONArray((String) jsonMap.get("csv")).toList();

        String name = "test.csv";
        String originalFileName = "test.csv";
        String contentType = "text/plain";

        MultipartFile output = new MockMultipartFile(name, originalFileName, contentType, ((String)jsonMap.get("csv")).getBytes());

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
        //Integer total = (Integer) jsonMap.get("total");


        //// rancom total
        Integer[] available_totals = {1200, 1300, 3100, 2220, 7210, 1350, 10200, 8110, 29010, 4770, 13450, 74490, 3500, 6600};
        java.util.Random random = new java.util.Random();
        int random_computer_card = random.nextInt(available_totals.length);
        Integer total = available_totals[random_computer_card];


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

        //Date now = Date.from(Instant.now());

        // random date
        Instant twoYearsAgo = Instant.now().minus(Duration.ofDays(2 * 365));
        Instant tenDaysAgo = Instant.now().minus(Duration.ofDays(10));

        long startSeconds = twoYearsAgo.getEpochSecond();
        long endSeconds = tenDaysAgo.getEpochSecond();
        long randomDate = ThreadLocalRandom
                .current()
                .nextLong(startSeconds, endSeconds);

        Date now = Date.from(Instant.ofEpochSecond(randomDate));

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
                .day(date)
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
                    .day(date)
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

    @Transactional
    public ReceiptResponse updateReceipt(HttpServletRequest httpServletRequest, ReceiptUpdateRequest receiptUpdateRequest) throws IOException {
        Map<String, String> info = Map.copyOf(receiptRepository.findByOwnerAndId(authService.getUserByToken(httpServletRequest), receiptUpdateRequest.getId()).orElseThrow(InvalidReceiptUserException::new).getInfo());

        deleteReceipt(httpServletRequest, receiptUpdateRequest.getId());

        String name = "test.csv";
        String originalFileName = "test.csv";
        String contentType = "text/plain";

        Gson gson = new Gson();
        String json = new String(gson.toJson(receiptUpdateRequest.getByProductDTOList()).getBytes("EUC-KR"), StandardCharsets.UTF_8);

        String toCSV = jsonToCSV(json);

        System.out.println("toCSV = " + toCSV);

        MultipartFile output = new MockMultipartFile(name, originalFileName, contentType, toCSV.getBytes());

        final String receiptKey = fileService.upload(output);

        final User userByToken = authService.getUserByToken(httpServletRequest);

        Analysis analysis = analysisRepository.findByOwner(userByToken).orElseThrow(NotFoundException::new);

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
                .day(date)
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
                    .day(date)
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

    @Transactional
    public StringResponse deleteReceipt(Long id, HttpServletRequest httpServletRequest) {
        final User userByToken = authService.getUserByToken(httpServletRequest);

        Receipt receipt = receiptRepository.findByOwnerAndId(userByToken, id).orElseThrow(InvalidReceiptUserException::new);

        receiptRepository.delete(receipt);

        return new StringResponse("영수증을 삭제했습니다");
    }
}
