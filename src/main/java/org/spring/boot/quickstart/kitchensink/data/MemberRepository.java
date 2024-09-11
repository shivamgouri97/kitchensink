package org.spring.boot.quickstart.kitchensink.data;

import org.spring.boot.quickstart.kitchensink.model.Member;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends MongoRepository<Member, Long> {

    Optional<Member> findById(Long id);

    @Query("{ 'email': ?0 }")
    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m ORDER BY m.name ASC")
    List<Member> findAllOrderedByName(Sort sort);
}