package org.spring.boot.quickstart.kitchensink.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.boot.quickstart.kitchensink.data.MemberRepository;
import org.spring.boot.quickstart.kitchensink.exception.MemberNotFoundException;
import org.spring.boot.quickstart.kitchensink.model.Member;
import org.spring.boot.quickstart.kitchensink.model.Message;
import org.spring.boot.quickstart.kitchensink.service.MemberRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/rest/members")
@Validated
public class MemberResourceRestController {

    private static final Logger log = LoggerFactory.getLogger(MemberResourceRestController.class);

    @Autowired
    private Validator validator;

    @Autowired
    private MemberRepository repository;

    @Autowired
    private MemberRegistration service;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<Member> listAllMembers() {
        return repository.findAllOrderedByName(Sort.by(Sort.Order.asc("id")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Member> lookupMemberById(@PathVariable long id) {
        log.info("Retrieve Member with member id {}", id);
        Member member = repository.findById(id).orElse(null);
        if (member == null) {
            throw new MemberNotFoundException("Member not found with id: " + id);
        }
        return ResponseEntity.ok(member);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, String>> createMember(@RequestBody Member member) {
        try {
            validateMember(member);
            log.info("Add Member with email address  {}", member.getEmail());
            service.register(member);
            log.info("Member added successfully with email address  {}", member.getEmail());
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", member.getEmail());
            responseObj.put("name", member.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseObj);
        } catch (ConstraintViolationException e) {
            return createViolationResponse(e.getConstraintViolations());
        } catch (ValidationException e) {
            Map<String, String> responseObj = new HashMap<>();
            log.info("Member is already present with email address  " + member.getEmail());
            responseObj.put("email", "Email taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
        } catch (Exception e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            log.info("error" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id:[0-9][0-9]*}")
    public ResponseEntity<Message> deleteMember(@PathVariable("id") long id) {
        Message message = new Message();
        if (repository.findById(id).isPresent()) {
            service.deleteById(id);
            log.info("Member with id {} deleted successfully", id);
            message.setMessage("Member deleted successfully with member id " + id);
            message.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else {
            log.warn("Member with id {} not found", id);
            message.setMessage("Member not found with given id " + id);
            message.setStatus(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    private void validateMember(Member member) throws ValidationException {
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        if (emailAlreadyExists(member.getEmail())) {
            throw new ValidationException("Unique Email Violation");
        }
    }

    private ResponseEntity<Map<String, String>> createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.debug("Validation completed. Violations found: {}", violations.size());

        Map<String, String> responseObj = new HashMap<>();
        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
    }

    private boolean emailAlreadyExists(String email) {
        return repository.findByEmail(email).isPresent();
    }
}