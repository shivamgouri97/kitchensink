package org.spring.boot.quickstart.kitchensink.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.boot.quickstart.kitchensink.data.MemberRepository;
import org.spring.boot.quickstart.kitchensink.model.Member;
import org.spring.boot.quickstart.kitchensink.utility.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberRegistration {

    private static final Logger log = LoggerFactory.getLogger(MemberRegistration.class);

    @Autowired
    private MemberRepository repository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void register(Member member) {
        log.info("Registering " + member.getName());
        member.setId(idGenerator.getNextId());
        repository.save(member);
        applicationEventPublisher.publishEvent(member);
    }

    public void deleteById(Long id) {
        log.info("Deleting member with " + id);
        repository.deleteById(id);
    }

    public List<Member> getAllMembers() {
        log.info("Fetching all the members ");
        return repository.findAllOrderedByName(Sort.by(Sort.Order.asc("name")));
    }
}