package org.spring.boot.quickstart.kitchensink;

import jakarta.annotation.PostConstruct;
import org.spring.boot.quickstart.kitchensink.data.MemberRepository;
import org.spring.boot.quickstart.kitchensink.model.Member;
import org.spring.boot.quickstart.kitchensink.utility.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@ComponentScan(basePackages = {"org.spring.boot.quickstarts.kitchensink.data", "org.spring.boot.quickstarts.kitchensink.*"})
public class KitchensinkApplication {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IdGenerator idGenerator;

    @PostConstruct
    public void run() throws Exception {
        // Initialize data
        memberRepository.deleteAll();
        Member member = new Member();
        member.setId(idGenerator.getNextId());
        member.setName("Shivam Gouri");
        member.setEmail("shivamGouri@example.com");
        member.setPhoneNumber("1234567890");
        memberRepository.save(member);
    }

    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }

}
