package study.querydsl;


import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;


import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

@Slf4j
@SpringBootTest
@Transactional
public class QuerydslBasicTest {


    @PersistenceContext
    EntityManager em;

    JPAQueryFactory query;


    @BeforeEach
    public void init() {
        query = new JPAQueryFactory(em);

        Team teamA = new Team("team_A");

        Team teamB = new Team("team_B");



        em.persist(teamA);
        em.persist(teamB);


        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);


        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);


    }


    @Test
    public void InnerJoin() {


        Member member1 = query
                .selectFrom(member)
                .join(member.team, team)
                .where(member.username.eq("member1"))
                .fetchOne();

        System.out.println("member = " + member1);

    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchJoin() {

        em.flush();
        em.clear();

        Member findMember = query
                .selectFrom(member)
                .join(member.team , team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        System.out.println(findMember);


        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        System.out.println(loaded);

        assertThat(loaded).isTrue();
    }

    @Test
    public void subQuery() {

        QMember memberSubQuery = new QMember("s");

        List<Member> list = query.selectFrom(member)
                .where(member.age.eq(JPAExpressions
                        .select(memberSubQuery.age.max())
                        .from(memberSubQuery))).fetch();

        for (Member sub : list) {
            System.out.println("sub = " + sub);
        }
    }

}
