package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.PayDTO;
import com.mindhub.homebanking.models.Pay;
import com.mindhub.homebanking.repositories.PayRepository;
import com.mindhub.homebanking.services.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    PayRepository payRepository;

    @Override
    public List<PayDTO> getPays() {
        return  payRepository.findAll().stream().map(PayDTO::new).collect(Collectors.toList());
    }

    @Override
    public Pay getPaysById(Long id) {
        return payRepository.findById(id).orElse(null);
    }

    @Override
    public void savePays(Pay pay) {
            payRepository.save(pay);
    }
}
