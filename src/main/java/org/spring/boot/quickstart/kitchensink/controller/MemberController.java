package org.spring.boot.quickstart.kitchensink.controller;


import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.spring.boot.quickstart.kitchensink.model.Member;
import org.spring.boot.quickstart.kitchensink.service.MemberRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController {

    @Autowired
    private MemberRegistration memberRegistration;

    @Autowired
    private MemberResourceRestController restService;


    @GetMapping("/index")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String showRegistrationForm(Model model) {
        model.addAttribute("newMember", new Member());
        List<Member> members = restService.listAllMembers();
        model.addAttribute("members", members);
        members.forEach(member -> System.out.println("Member: " + member.getId() + ", " + member.getName()));
        return "index";
    }

    @PostMapping("/members")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String addMember(@Valid @ModelAttribute("newMember") Member member, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // If there are validation errors, return to the form with error messages
            model.addAttribute("members", restService.listAllMembers());
            return "index";
        }
        try {
            restService.createMember(member);
        } catch (ValidationException e) {
            model.addAttribute("errorMessage", "Email is already taken. Please choose a different email.");
            return "index";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            return "index";
        }

        return "redirect:/index";
    }


    @PostMapping("/members/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteMember(@PathVariable Long id) {
        restService.deleteMember(id);
        return "redirect:/index";
    }

    @GetMapping("/default")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String defaultPage(Model model) {
        return "default";
    }
}