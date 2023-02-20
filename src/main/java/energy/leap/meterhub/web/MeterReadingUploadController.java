package energy.leap.meterhub.web;

import energy.leap.meterhub.service.MeterBatchReadingProcessorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Slf4j
public class MeterReadingUploadController {

    @NonNull
    private final MeterBatchReadingProcessorService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public @ResponseBody ResponseEntity<String> uploadMeterReadingReport(@RequestParam("file") MultipartFile file) throws IOException {
        String fileContent = new String(file.getInputStream().readAllBytes());
        log.info("New XML file : {}", fileContent);
        service.processBatchReading(fileContent);
        return new ResponseEntity<>("POST Response", HttpStatus.OK);
    }
}
