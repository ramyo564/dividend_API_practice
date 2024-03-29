package com.dayone.scheduler;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    // 스캐줄러가 돌아갈 때 캐싱비워주고 시작
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling(){

        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for(var company : companies){

            log.info("Scraping scheduler is started-> "
                            + company.getName());

            ScrapedResult scrapedResult =
                    this.yahooFinanceScraper.scrap(
                            new Company(
                                    company.getTicker(),
                                    company.getTicker()));

            // 스크패핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapedResult.getDividends().stream()
                    // map 으로 배당금 모델을 베당금 엔티티로 매핑
                    .map(e-> new DividendEntity(company.getId(), e))
                    // forEach 문으로 엘리먼트를 하나씩 배당금 레포지토리에 삽입
                    .forEach(e-> {
                        boolean exists =
                                this.dividendRepository
                                        .existsByCompanyIdAndDate(
                                                e.getCompanyId(),
                                                e.getDate()
                                        );
                        // 존재하지 않는 경우에만 데이터 삽입
                        if (!exists){
                            this.dividendRepository.save(e);
                            log.info("insert new dividend ->" + e.toString());

                        }
                    });

            // 연속적으로 스크래핑 되지 않도록 일시정지
            try {
                Thread.sleep(3000); //-> 3초
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("스크래핑 실패 -> " + e.toString() );
                throw new RuntimeException(e);
            }
        }


    }
}
