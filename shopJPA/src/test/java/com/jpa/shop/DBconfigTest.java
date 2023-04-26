package com.jpa.shop;


import com.jpa.shop.domain.Member;
import com.jpa.shop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@Rollback(value = false)
public class DBconfigTest {
    //    @Autowired
//    DataSource dataSource;  // db 객체
//    @Autowired
//    TransactionManager transactionManager; // db연결 객체
    @Autowired
    EntityManager entityManager;

    @Autowired
    MemberRepository memberRepository;
    @Test
    void db연동(){
        Assertions.assertNotNull(entityManager);
    }
    @Test
    void 회원가입(){
        Member member1 = new Member();
        member1.setName("test");
        memberRepository.save(member1);
    }

}