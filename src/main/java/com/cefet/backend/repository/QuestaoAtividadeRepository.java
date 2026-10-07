package com.cefet.backend.repository;

import com.cefet.backend.entity.Questao;
import com.cefet.backend.entity.QuestaoAtividade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestaoAtividadeRepository extends JpaRepository<QuestaoAtividade, Long> {
        boolean existsByQuestao(Questao questao);
}
