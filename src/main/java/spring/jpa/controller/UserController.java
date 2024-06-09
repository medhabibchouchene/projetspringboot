package spring.jpa.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import spring.jpa.model.Administrateur;
import spring.jpa.model.Conge;
import spring.jpa.model.Employe;
import spring.jpa.model.EtatConge;
import spring.jpa.model.User;
import spring.jpa.repository.CongeRepository;
import spring.jpa.repository.EmployeRepository;
import spring.jpa.repository.UserRepository;

@Controller
@SessionAttributes("user")
@RequestMapping("/conge")
public class UserController {

    @Autowired
    private UserRepository userRepos;

    @Autowired
    private CongeRepository congeRepos;

    @Autowired
    private EmployeRepository employeRepos;

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        List<Conge> conges = congeRepos.findAll();
        model.addAttribute("conges", conges);
        return "admin";
    }
  
    @PostMapping("/validateConge/{id}")
    public String validateConge(@PathVariable Long id, Model model) {
        Conge conge = congeRepos.findById(id).orElse(null);
        if (conge != null) {
            conge.setEtatconge(EtatConge.Valide);
            congeRepos.save(conge);
        }
        List<Conge> conges = congeRepos.findAllByIdNot(conge.getId()); // Assurez-vous d'implémenter cette méthode dans votre repository
        model.addAttribute("conges", conges);
        return "admin";  // Assurez-vous que la page 'admin' peut gérer l'affichage sans l'élément mis à jour
    }

    @PostMapping("/refuseConge/{id}")
    public String refuseConge(@PathVariable Long id, Model model) {
        Conge conge = congeRepos.findById(id).orElse(null);
        if (conge != null) {
            conge.setEtatconge(EtatConge.Refuse);
            congeRepos.save(conge);
        }
        List<Conge> conges = congeRepos.findAllByIdNot(conge.getId()); // Assurez-vous d'implémenter cette méthode dans votre repository
        model.addAttribute("conges", conges);
        return "admin";  // Assurez-vous que la page 'admin' peut gérer l'affichage sans l'élément mis à jour
    }


    @GetMapping("/allusers")
    public String allUsers(Model model) {
        List<User> users = userRepos.findAll();
        model.addAttribute("users", users);
        return "allUserList";
    }
    
    
  


    @GetMapping("/userlist")
    public String userList() {
        return "userList";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        User foundUser = userRepos.findByLoginAndPassword(user.getLogin(), user.getPassword());
        if (foundUser != null) {
            if (foundUser instanceof Employe) {
                Collection<Conge> conges = congeRepos.findByEmploye((Employe) foundUser);
                model.addAttribute("id", foundUser.getId());
                model.addAttribute("conges", conges);
                model.addAttribute("user", foundUser);
                return "redirect:welcome";
            } else if (foundUser instanceof Administrateur) {
                model.addAttribute("user", foundUser);
                return "redirect:admin";
            }
        } else {
            model.addAttribute("error", true);
            return "login";
        }
        return "login";
    }

    @GetMapping("/congelist")
    public String empCongeList(@Valid Employe emp, Model model) {
        List<Conge> conges = congeRepos.findByEmploye(emp);
        model.addAttribute("conges", conges);
        return "userList";
    }

    @GetMapping("/statistic")
    public String statistic(@RequestParam(required = false) EtatConge etatConge, Model model) {
        List<Conge> conges;
        if (etatConge != null) {
            conges = congeRepos.findByEtatconge(etatConge);
        } else {
            conges = congeRepos.findAll();
        }
        
        Map<EtatConge, Long> congeCountByEtat = new HashMap<>();
        for (Conge conge : conges) {
            EtatConge etat = conge.getEtatconge();
            congeCountByEtat.put(etat, congeCountByEtat.getOrDefault(etat, 0L) + 1);
        }
        model.addAttribute("congeCountByEtat", congeCountByEtat);
        return "historique";
    }


    @GetMapping("/employeId/{employeId}")
    public String getCongesByEmploye(@PathVariable("employeId") Long employeId, Model model) {
        Employe employe = employeRepos.findById(employeId).orElse(null);
        if (employe == null) {
            model.addAttribute("error", "Employé non trouvé");
            return "errorPage"; // Assurez-vous d'avoir une page d'erreur appropriée
        }
        List<Conge> conges = congeRepos.findByEmploye(employe);
        model.addAttribute("conges", conges);
        model.addAttribute("employe", employe);
        model.addAttribute("id", employeId);
        return "userList";
    }

    @GetMapping("/formconge")
    public String formConge(Model model, HttpSession session) {
        // Récupérer l'ID de l'utilisateur à partir de la session
        Long userId = (Long) session.getAttribute("userId");
        
        // Ajouter l'ID de l'utilisateur au modèle
        model.addAttribute("userId", userId);
        
        // Ajouter un nouvel objet Conge au modèle
        model.addAttribute("conge", new Conge());
        
        // Retourner le nom de la vue
        return "formConge";
    }


    @PostMapping("/save")
    public String saveConge(@ModelAttribute("conge") Conge conge, 
                            @RequestParam("startDate") String startDate,
                            @RequestParam("endDate") String endDate,
                            @RequestParam("employeId") Long employeId) {
        // Assurez-vous que le congé soumis n'est pas nul
        if (conge != null) {
            // Convertir les dates de type String en objet Date
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date parsedStartDate = formatter.parse(startDate);
                Date parsedEndDate = formatter.parse(endDate);
                conge.setDateDebut(parsedStartDate);
                conge.setDateFin(parsedEndDate);
            } catch (ParseException e) {
                // Gérer les erreurs de formatage des dates
                e.printStackTrace();
            }
            
            // Récupérer l'employé à partir de l'ID
            Optional<Employe> employeOptional = employeRepos.findById(employeId);
            if (employeOptional.isPresent()) {
                Employe employe = employeOptional.get();
                conge.setEmploye(employe);
            } else {
                // Si l'employé n'est pas trouvé, afficher un message d'erreur et rediriger vers une autre page
                return "redirect:formconge"; // Ou toute autre page appropriée pour afficher le formulaire de nouveau
            }
            
            // Enregistrer le congé
            congeRepos.save(conge);

            // Redirection vers une autre page après la sauvegarde
            return "redirect:welcome";
        } else {
            // Si le congé soumis est nul, affichez un message d'erreur et redirigez l'utilisateur vers une autre page
            return "redirect:formconge"; // Ou toute autre page appropriée pour afficher le formulaire de nouveau
        }
    }

    @GetMapping("/logout")
    public String logout(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/login"; // Redirige vers la page de connexion
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<Conge> congeOptional = congeRepos.findById(id);
        if (congeOptional.isPresent()) {
            Conge conge = congeOptional.get();
            model.addAttribute("conge", conge);
            return "editConge";
        } else {
            model.addAttribute("error", "Congé non trouvé");
            return "errorPage";
        }
    }

    @PostMapping("/update")
    public String updateConge(@ModelAttribute("conge") Conge conge,
                              @RequestParam("startDate") String startDate,
                              @RequestParam("endDate") String endDate) {
        if (conge != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date parsedStartDate = formatter.parse(startDate);
                Date parsedEndDate = formatter.parse(endDate);
                conge.setDateDebut(parsedStartDate);
                conge.setDateFin(parsedEndDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            congeRepos.save(conge);
            return "redirect:/welcome";
        } else {
            return "redirect:/edit/" + conge.getId(); // Ou toute autre page appropriée pour afficher le formulaire de nouveau
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                         @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                         @RequestParam(value = "etatConge", required = false) EtatConge etatConge,
                         Model model) {
        List<Conge> conges;

        if (startDate != null && endDate != null && etatConge != null) {
            conges = congeRepos.findByDateDebutBetweenAndEtatconge(startDate, endDate, etatConge);
        } else if (startDate != null && endDate != null) {
            conges = congeRepos.findByDateDebutBetween(startDate, endDate);
        } else if (etatConge != null) {
            conges = congeRepos.findByEtatconge(etatConge);
        } else {
            conges = congeRepos.findAll();
        }

        model.addAttribute("conges", conges);
        return "userList";
    }

}
