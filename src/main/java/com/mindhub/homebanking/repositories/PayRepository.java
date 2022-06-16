package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRepository extends JpaRepository<Pay ,Long> {

}
