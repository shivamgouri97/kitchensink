package org.spring.boot.quickstart.kitchensink.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.spring.boot.quickstart.kitchensink.data.MemberRepository;
import org.spring.boot.quickstart.kitchensink.model.Member;
import org.spring.boot.quickstart.kitchensink.utility.IdGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberRegistrationTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private MemberRegistration memberRegistration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteById() {
        Long id = 1L;
        memberRegistration.deleteById(id);
        verify(memberRepository, times(1)).deleteById(id);
    }

    @Test
    void testRegisterSuccess() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");

        when(idGenerator.getNextId()).thenReturn(1L);
        memberRegistration.register(member);
        verify(idGenerator).getNextId();
        verify(memberRepository).save(member);
        verify(applicationEventPublisher).publishEvent(member);
    }

    @Test
    void testGetAllMembers() {
        Member member1 = new Member();
        member1.setName("Alice");
        Member member2 = new Member();
        member2.setName("Bob");

        List<Member> members = Arrays.asList(member1, member2);

        when(memberRepository.findAllOrderedByName(Sort.by(Sort.Order.asc("name")))).thenReturn(members);
        List<Member> result = memberRegistration.getAllMembers();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
        assertEquals("Bob", result.get(1).getName());
    }

    @Test
    void testRegisterDuplicateEmail() {
        Member member = new Member();
        member.setName("Jane Doe");
        member.setEmail("jane.doe@example.com");
        member.setPhoneNumber("0987654321");

        when(idGenerator.getNextId()).thenReturn(2L);
        when(memberRepository.save(member)).thenThrow(new RuntimeException("Duplicate email"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            memberRegistration.register(member);
        });
        assertEquals("Duplicate email", exception.getMessage());
        verify(idGenerator).getNextId();
        verify(memberRepository).save(member);
        verify(applicationEventPublisher, never()).publishEvent(member);
    }
}