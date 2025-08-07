package com.example.demo.member.repositories;

import com.example.demo.member.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
}
