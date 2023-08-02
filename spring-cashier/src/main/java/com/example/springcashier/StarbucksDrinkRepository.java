package com.example.springcashier;

/* https://docs.spring.io/spring-data/jpa/docs/2.4.6/reference/html/#repositories */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
public interface StarbucksDrinkRepository extends JpaRepository<StarbucksDrink, Long> {

    }