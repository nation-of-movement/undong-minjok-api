package com.undongminjok.api.parts.service;

import com.undongminjok.api.parts.dto.response.PartResponse;
import com.undongminjok.api.parts.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartService {

    private final PartRepository partRepository;

    @Transactional(readOnly = true)
    public List<PartResponse> getAllParts() {
        return partRepository.findAll()
                .stream()
                .map(PartResponse::from)
                .toList();
    }
}
