package org.spring.boot.quickstart.kitchensink.controller;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.spring.boot.quickstart.kitchensink.data.MemberRepository;
import org.spring.boot.quickstart.kitchensink.model.Member;
import org.spring.boot.quickstart.kitchensink.service.MemberRegistration;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class MemberResourceRestControllerTest {

    @InjectMocks
    private MemberResourceRestController memberResourceRestController;

    @Mock
    private Validator validator;

    @Mock
    private MemberRepository repository;

    @Mock
    private MemberRegistration registration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAllMembers() {
        Member expectedMember = new Member(1L, "John", "john@gmail.com", "9898989898");
        when(repository.findAllOrderedByName(Sort.by(Sort.Order.asc("name")))).thenReturn(List.of(expectedMember));
        List<Member> response = memberResourceRestController.listAllMembers();

        assertNotNull(response, "Response should not be null");
        assertEquals(1, response.size(), "Response size should be 1");

        Member actualMember = response.getFirst();
        assertEquals(expectedMember.getId(), actualMember.getId(), "Member ID should match");
        assertEquals(expectedMember.getName(), actualMember.getName(), "Member name should match");
        assertEquals(expectedMember.getEmail(), actualMember.getEmail(), "Member email should match");
        assertEquals(expectedMember.getPhoneNumber(), actualMember.getPhoneNumber(), "Member phone number should match");
    }

    @Test
    void testLookupMemberByIdFound() {
        Member expectedMember = new Member(1L, "John", "john@gmail.com", "9898989898");
        when(repository.findById(1L)).thenReturn(Optional.of(expectedMember));

        ResponseEntity<Member> response = memberResourceRestController.lookupMemberById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK");
        assertEquals(expectedMember, response.getBody(), "Response body should match the expected member");
    }
    @Test
    void testLookupMemberByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Member> response = memberResourceRestController.lookupMemberById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status code should be NOT FOUND");
    }

    @Test
    void testCreateMemberSuccess() {
        Member newMember = new Member(1L, "John", "john@gmail.com", "9898989898");
        when(validator.validate(newMember)).thenReturn(new HashSet<>());

        ResponseEntity<Map<String, String>> response = memberResourceRestController.createMember(newMember);

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Status code should be CREATED");
    }

    @Test
    void testCreateMemberValidationException() {
        Member newMember = new Member(1L, "John", "john@gmail.com", "9898989898");
        when(validator.validate(newMember)).thenReturn(new HashSet<>());
        when(repository.findByEmail(newMember.getEmail())).thenReturn(Optional.of(newMember));

        ResponseEntity<Map<String, String>> response = memberResourceRestController.createMember(newMember);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Status code should be CONFLICT");
    }

    @Test
    void testCreateMemberException() {
        Member newMember = new Member(1L, "John", "john@gmail.com", "9898989898");
        when(validator.validate(newMember)).thenReturn(new HashSet<>());
        when(repository.findByEmail(newMember.getEmail())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Error")).when(registration).register(newMember);

        ResponseEntity<Map<String, String>> response = memberResourceRestController.createMember(newMember);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status code should be BAD REQUEST");
    }
}
