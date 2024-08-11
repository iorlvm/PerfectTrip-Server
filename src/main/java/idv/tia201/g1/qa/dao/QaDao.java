package idv.tia201.g1.qa.dao;

import idv.tia201.g1.entity.QuestionAnswer;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QaDao extends JpaRepository<QuestionAnswer, Integer> {

}
