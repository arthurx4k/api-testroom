package com.cefet.backend.repository;

import com.cefet.backend.entity.Alternativa;
import com.cefet.backend.entity.Questao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlternativaRepository extends JpaRepository<Alternativa, Long> {
    List<Alternativa> findByQuestao(Questao questao);
    void deleteByQuestao(Questao questao);
}