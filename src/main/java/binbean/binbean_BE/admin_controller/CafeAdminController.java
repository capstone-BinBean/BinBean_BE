package binbean.binbean_BE.admin_controller;

import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.service.CafeService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/admin")
public class CafeAdminController {

    private final CafeService cafeService;

    public CafeAdminController(CafeService cafeService) {
        this.cafeService = cafeService;
    }

    @GetMapping("/cafes")
    public String getCafeList(Model model) {
        List<Cafe> cafes = cafeService.getAllCafe();
        model.addAttribute("cafes", cafes);
        return "cafe-list";
    }
}
