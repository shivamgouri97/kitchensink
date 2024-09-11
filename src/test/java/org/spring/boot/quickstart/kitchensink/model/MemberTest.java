package org.spring.boot.quickstart.kitchensink.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidMember() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("12345678901");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void testInvalidName() {
        Member member = new Member();
        member.setName("John123"); // Invalid name with numbers
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("12345678901");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertFalse(violations.isEmpty(), "Expected validation errors");
        assertEquals(1, violations.size());
        ConstraintViolation<Member> violation = violations.iterator().next();
        assertEquals("Must not contain numbers", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void testInvalidEmail() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("invalid-email"); // Invalid email
        member.setPhoneNumber("12345678901");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertFalse(violations.isEmpty(), "Expected validation errors");
        assertEquals(1, violations.size());
        ConstraintViolation<Member> violation = violations.iterator().next();
        assertEquals("must be a well-formed email address", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void testInvalidPhoneNumber() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("123456"); // Invalid phone number length

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertFalse(violations.isEmpty(), "Expected validation errors");
        assertEquals(1, violations.size());
        ConstraintViolation<Member> violation = violations.iterator().next();
        assertEquals("size must be between 10 and 12", violation.getMessage());
        assertEquals("phoneNumber", violation.getPropertyPath().toString());
    }

    @Test
    void testGettersAndSetters() {
        Member member = new Member();
        member.setId(1L);
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("12345678901");

        assertEquals(1L, member.getId());
        assertEquals("John Doe", member.getName());
        assertEquals("john.doe@example.com", member.getEmail());
        assertEquals("12345678901", member.getPhoneNumber());
    }
}
