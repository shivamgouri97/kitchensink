package org.spring.boot.quickstart.kitchensink.service;

import lombok.extern.slf4j.Slf4j;
import org.spring.boot.quickstart.kitchensink.data.MemberRepository;
import org.spring.boot.quickstart.kitchensink.model.Member;
import org.spring.boot.quickstart.kitchensink.utility.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MemberRegistration {


    @Autowired
    private MemberRepository repository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public Member register(Member member) {
        log.info("Registering {}", member.getName());
        member.setId(idGenerator.getNextId());
        Member result = repository.save(member);
        applicationEventPublisher.publishEvent(member);
        return result;
    }

    public void deleteById(Long id) {
        log.info("Deleting member with {}", id);
        repository.deleteById(id);
    }

    public List<Member> getAllMembers() {
        log.info("Fetching all the members ");
        return repository.findAllOrderedByName(Sort.by(Sort.Order.asc("name")));
    }
}