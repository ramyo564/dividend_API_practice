package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    /*
    1. 요청이 자주 들어오는가? -> O
    2. 자주 변경되는 데이터인가? -> X
        -> 과거 배당금이 바뀌거나 회사명이 바뀔 일은 거의 없음
        -> 배당금도 많아야 한 달에 한 번 업데이트
      -> 배당금 정보는 캐싱으로 처리가 적
     */
    @Cacheable(key = "#companyName", value = "finance")
    public ScrapedResult getDividendByCompanyName(String companyName){

        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company =
                this.companyRepository.findByName(companyName)
                .orElseThrow(()->
                        new RuntimeException("존재하지 않는 회사명입니다."));
        // orElseThrow() -> 회사명이 없을 경우 null 이 생길 수 있어서 사용
        //-> 값이 있을 경우 Optional 을 벗겨 주고 그렇지 않을 경우 null 처리를 해줌

        // 2. 조회된 회사 ID 로 배당금 정보 조회
        List<DividendEntity> dividendEntities =
                this.dividendRepository.findAllByCompanyId(
                        company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());
        return new ScrapedResult(
                new Company(
                        company.getTicker(),
                        company.getName()),
                        dividends
                );
    }
}
