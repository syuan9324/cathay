package demo.repository;

import demo.doman.ExchangeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * Spring Data SQL repository for the AdmMailSend entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeDTO, Long>, JpaSpecificationExecutor<ExchangeDTO> {
    List<ExchangeDTO> findByUsdNtdAndDateBetween(Double usdNtd, LocalDate startDate, LocalDate endDate);
}
