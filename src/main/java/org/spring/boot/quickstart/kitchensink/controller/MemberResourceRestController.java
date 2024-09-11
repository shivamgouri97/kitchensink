package org.spring.boot.quickstart.kitchensink.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.boot.quickstart.kitchensink.data.MemberRepository;
import org.spring.boot.quickstart.kitchensink.exception.CustomValidationException;
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

import java.util.List;
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


    /**
     * @return
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<Member> listAllMembers() {
        return repository.findAllOrderedByName(Sort.by(Sort.Order.asc("id")));
    }


    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Member> lookupMemberById(@PathVariable long id) {
        log.info("Retrieve Member with member id {}", id);
        Member member = repository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));
        return ResponseEntity.ok(member);
    }

    /**
     * @param member
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Member> createMember(@RequestBody Member member) throws Exception {
        try {
            validateMember(member);
            log.info("Add Member with email address  {}", member.getEmail());
            Member registeredMember = service.register(member);
            log.info("Member added successfully with email address  {}", member.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredMember);
        } catch (ValidationException e) {
            throw new CustomValidationException("Unique Email Violation");
        } catch (Exception e) {
            throw new Exception("Generic Exception");
        }
    }


    /**
     * @param id
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id:[0-9][0-9]*}")
    public ResponseEntity<Message> deleteMember(@PathVariable("id") long id) {

        Message message = new Message();
        repository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));

        service.deleteById(id);
        message.setMessage("Member deleted successfully with member id " + id);
        message.setStatus(HttpStatus.OK);
        return ResponseEntity.ok(message);
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

    /**
     * @param email
     * @return
     */
    private boolean emailAlreadyExists(String email) {
        return repository.findByEmail(email).isPresent();
    }
}