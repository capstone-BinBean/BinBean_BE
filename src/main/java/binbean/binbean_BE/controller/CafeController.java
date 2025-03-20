package binbean.binbean_BE.controller;

import binbean.binbean_BE.service.CafeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cafes")
public class CafeController {

    private final CafeService cafeService;

    public CafeController(CafeService cafeService){
        this.cafeService = cafeService;
    }

    @PostMapping("/registration")
    public ResponseEntity<Void> registerCafe() {
        cafeService.registerCafe();

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
