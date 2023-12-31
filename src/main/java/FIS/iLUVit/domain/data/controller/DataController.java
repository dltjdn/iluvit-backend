package FIS.iLUVit.domain.data.controller;

import FIS.iLUVit.domain.data.service.DataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("data")
public class DataController {

    private final DataService dataService;

    @PatchMapping("childHouse")
    public ResponseEntity<Void> updateChildHouse() {
        dataService.updateChildHouseInformation();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("kindergarten")
    public ResponseEntity<Void> updateKindergarten() {
        dataService.updateKindergartenInformation();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
