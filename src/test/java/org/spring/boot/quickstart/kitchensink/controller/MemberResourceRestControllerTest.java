package org.spring.boot.quickstart.kitchensink.controller;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.spring.boot.quickstart.kitchensink.data.MemberRepository;
import org.spring.boot.quickstart.kitchensink.exception.MemberNotFoundException;
import org.spring.boot.quickstart.kitchensink.model.Member;
import org.spring.boot.quickstart.kitchensink.service.MemberRegistration;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        Member expectedMember = new Member(1L, "John", "john@gmail.com", "1234567890");
        when(repository.findAllOrderedByName(Sort.by(Sort.Order.asc("id")))).thenReturn(List.of(expectedMember));
        List<Member> response = memberResourceRestController.listAllMembers();

        assertNotNull(response, "Response should not be null");
        assertEquals(1, response.size(), "Response size should be 1");

        Member actualMember = response.get(0);
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
    void lookupMemberById_ReturnsMember_WhenFound() {
        // Arrange
        long memberId = 1L;
        Member mockMember = new Member();
        mockMember.setId(memberId);
        mockMember.setName("John Doe");
        when(repository.findById(memberId)).thenReturn(Optional.of(mockMember));

        ResponseEntity<Member> response = memberResourceRestController.lookupMemberById(memberId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockMember, response.getBody());
        verify(repository, times(1)).findById(memberId);
    }

    @Test
    void lookupMemberById_ThrowsException_WhenNotFound() {

        long memberId = 1L;
        when(repository.findById(memberId)).thenReturn(Optional.empty());

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
            memberResourceRestController.lookupMemberById(memberId);
        });

        assertEquals("Member not found with id: " + memberId, exception.getMessage());
        verify(repository, times(1)).findById(memberId);
    }


    @Test
    void testCreateMemberSuccess() throws Exception {
        Member newMember = new Member(1L, "John", "john@gmail.com", "9898989898");
        when(validator.validate(newMember)).thenReturn(new HashSet<>());

        ResponseEntity<Member> member = memberResourceRestController.createMember(newMember);

        assertEquals(HttpStatus.CREATED, member.getStatusCode(), "Status code should be CREATED");
    }

    @Test
    void testCreateMemberValidationException() throws Exception {
        Member newMember = new Member(1L, "John", "john@gmail.com", "9898989898");
        when(validator.validate(newMember)).thenReturn(new HashSet<>());
        when(repository.findByEmail(newMember.getEmail())).thenReturn(Optional.of(newMember));


        assertThrows(Exception.class, () -> {
            memberResourceRestController.createMember(newMember);
        });

    }

    @Test
    void testCreateMemberException() throws Exception {
        Member newMember = new Member(1L, "John", "john@gmail.com", "9898989898");
        when(validator.validate(newMember)).thenReturn(new HashSet<>());
        when(repository.findByEmail(newMember.getEmail())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Error")).when(registration).register(newMember);

        assertThrows(Exception.class, () -> {
            memberResourceRestController.createMember(newMember);
        });


    }
}
