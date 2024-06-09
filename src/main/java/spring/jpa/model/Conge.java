package spring.jpa.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Conge {
	@Id
	@GeneratedValue
	private Long id;
	
	private String description;
	
	@DateTimeFormat (pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date dateDebut;
	
	@DateTimeFormat (pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date dateFin;
	
	@DateTimeFormat (pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date dateRupture = new Date();
	private EtatConge etatconge = EtatConge.Sollicite;
	
	@ManyToOne
	private Employe employe;
	
	public Conge() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Conge(String description, Date dateDebut, Date dateFin, EtatConge etatconge,
			Employe employe) {
		super();
		this.description = description;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
		this.etatconge = etatconge;
		this.employe = employe;
	}

	public Conge(String description, Date dateDebut, Date dateFin, Employe employe) {
		super();
		this.description = description;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
		this.employe = employe;
	}
	
	public Conge(String description, Date dateDebut, Date dateFin) {
		super();
		this.description = description;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
	}
	
	public Conge(Long id,String description, Date dateDebut, Date dateFin) {
		super();
		this.id = id;
		this.description = description;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public Date getDateRupture() {
		return dateRupture;
	}

	public void setDateRupture(Date dateRupture) {
		this.dateRupture = dateRupture;
	}

	public EtatConge getEtatconge() {
		return etatconge;
	}

	public void setEtatconge(EtatConge etatconge) {
		this.etatconge = etatconge;
	}

	public Employe getEmploye() {
		return employe;
	}

	public void setEmploye(Employe employe) {
		this.employe = employe;
	}

	@Override
	public String toString() {
		return "Conge [id=" + id + ", description=" + description + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin
				+ ", dateRupture=" + dateRupture + ", etatconge=" + etatconge + "]";
	}
	public Conge(Employe employe, Date dateDebut, Date dateFin, String description, String etat) {
	    this.employe = employe;
	    this.dateDebut = dateDebut;
	    this.dateFin = dateFin;
	    this.description = description;
	    this.etatconge = EtatConge.valueOf(etat);
	}
	public long getDuration() {
        // Assuming dateDebut and dateFin are of type java.util.Date
        // Convert java.util.Date to java.time.LocalDate before calculation
        LocalDate start = new java.sql.Date(dateDebut.getTime()).toLocalDate();
        LocalDate end = new java.sql.Date(dateFin.getTime()).toLocalDate();
        return ChronoUnit.DAYS.between(start, end) + 1;  // Include both start and end days
    }

}
