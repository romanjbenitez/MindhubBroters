package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.PayDTO;
import com.mindhub.homebanking.models.Pay;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface PayService {
    List<PayDTO> getPays();

    Pay getPaysById(Long id);

    void savePays(Pay pay);
}
