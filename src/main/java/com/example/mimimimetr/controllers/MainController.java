package com.example.mimimimetr.controllers;

import com.example.mimimimetr.dto.CreateCatDto;
import com.example.mimimimetr.exceptions.PairsNotFoundException;
import com.example.mimimimetr.exceptions.UpdateException;
import com.example.mimimimetr.models.Cat;
import com.example.mimimimetr.models.CatPair;
import com.example.mimimimetr.models.UserPairsVote;
import com.example.mimimimetr.services.CatsService;
import com.example.mimimimetr.services.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    private final CatsService catsService;
    private final SecurityService securityService;

    @GetMapping("")
    public String index(HttpServletRequest request, Model model) {
        try {
            CatPair catPair = catsService.getPair(securityService.getCurrentSession(request));
            model.addAttribute("pair", catPair);
            model.addAttribute("resultForm", new UserPairsVote());
        } catch (PairsNotFoundException e) {
            model.addAttribute("message", e.getMessage());
        }
        return "index";
    }

    @PostMapping("/add-result")
    public String addResult(@ModelAttribute("resultForm") UserPairsVote userPairsVote,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes
    ) {
        try {
            catsService.addVotesResult(userPairsVote, securityService.getCurrentSession(request));
        } catch (UpdateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/top")
    public String top(Model model) {
        model.addAttribute("top", catsService.getTop());
        return "top";
    }

    @GetMapping("/add")
    public String addCat(Model model) {
        model.addAttribute("addForm", new CreateCatDto());
        return "add";
    }

    @PostMapping("/add")
    public String addCat(RedirectAttributes redirectAttributes, @ModelAttribute("addForm") CreateCatDto cat) {
        try {
            catsService.addCat(cat);
            redirectAttributes.addFlashAttribute("message", "Кошка добавлена в конкурс");
        } catch (UpdateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/add";
    }

}
