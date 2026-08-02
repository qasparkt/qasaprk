package com.qaspark.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    @GetMapping({"/", "/index", "/index.html"})
    public String index(Model model) {
        model.addAttribute("activePage", "home");
        model.addAttribute("pageTitle", "Home | QASpark");
        return "index";
    }

    @GetMapping({"/courses", "/courses.html"})
    public String courses(Model model) {
        model.addAttribute("activePage", "courses");
        model.addAttribute("pageTitle", "Courses & Curriculum | QASpark");
        return "courses";
    }

    @GetMapping({"/pricing", "/pricing.html"})
    public String pricing(Model model) {
        model.addAttribute("activePage", "pricing");
        model.addAttribute("pageTitle", "Pricing & Plans | QASpark");
        return "pricing";
    }

    @GetMapping({"/contact", "/contact.html"})
    public String contact(@RequestParam(value = "course", required = false) String course, Model model) {
        model.addAttribute("activePage", "contact");
        model.addAttribute("pageTitle", "Contact Us | QASpark");
        model.addAttribute("selectedCourse", course != null ? course : "");
        return "contact";
    }
}
