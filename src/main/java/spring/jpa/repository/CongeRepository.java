package spring.jpa.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import spring.jpa.model.Conge;
import spring.jpa.model.Employe;
import spring.jpa.model.EtatConge;

public interface CongeRepository extends JpaRepository<Conge, Long> {

    List<Conge> findByEmploye(Employe emp);
    List<Conge> findByEtatconge(EtatConge etatConge);
    @Query("SELECT c FROM Conge c WHERE c.employe.id = :employeId AND YEAR(c.dateDebut) = :year")
    List<Conge> findByEmployeAndYear(Long employeId, int year);
    List<Conge> findByDateDebutBetween(Date startDate, Date endDate);
    List<Conge> findByDateFinBetween(Date startDate, Date endDate);
    List<Conge> findByDateDebutBetweenAndEtatconge(Date startDate, Date endDate, EtatConge etatConge);
    List<Conge> findByDateFinBetweenAndEtatconge(Date startDate, Date endDate, EtatConge etatConge);
    List<Conge> findAllByIdNot(Long id);

}
