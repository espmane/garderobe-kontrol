package gruppe3.garderobekontrol.controller;

import gruppe3.garderobekontrol.model.dto.request.OutfitRequest;
import gruppe3.garderobekontrol.model.dto.response.OutfitResponse;
import gruppe3.garderobekontrol.service.OutfitService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OutfitController {
    private final OutfitService service;

    public OutfitController(OutfitService service) {
        this.service = service;
    }

    @PostMapping("/api/weather/demo")
    public OutfitResponse recommend(@RequestBody OutfitRequest request) {
        return service.recommend(request);
    }
}
