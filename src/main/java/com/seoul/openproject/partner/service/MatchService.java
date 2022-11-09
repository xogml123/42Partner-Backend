package com.seoul.openproject.partner.service;

import com.seoul.openproject.partner.controller.match.MatchSearch;
import com.seoul.openproject.partner.domain.model.match.Match;
import com.seoul.openproject.partner.repository.match.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public Match.MatchDto readMyMatches(String apiId, MatchSearch matchSearch, Pageable pageable) {
        return null;
    }
}
