package sec.project.controller;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.config.CustomUserDetailsService;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {
    
    CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService();

    @Autowired
    private SignupRepository signupRepository;
    
    /*salasanaa ei enkryptata, authorization pielessä (ei käytä authia), voi tallettaa tietokantaan haitallista koodia/ei tarkisteta
    pääsee sivulle jonne ei pitäisi päästä, injection*/
    

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        if (customUserDetailsService.checkInit() == null) {
            customUserDetailsService.init();
        }
        return "form";
    }
    
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminForm() {
        if (customUserDetailsService.checkInit() == null) {
            customUserDetailsService.init();
        }
        return "admin";
    }
    
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String userForm() {
        if (customUserDetailsService.checkInit() == null) {
            customUserDetailsService.init();
        }
        return "user";
    }
    
    @RequestMapping(value = "/attendants", method = RequestMethod.GET)
    public String attendantsView(Model model) {
        model.addAttribute("attendants", signupRepository.findAll());
        return "attendants";
    }
    
    @RequestMapping(value = "/attendant/{name}", method = RequestMethod.GET)
    public String attendantView(Model model, @PathVariable String name) {
        List<Signup> list = signupRepository.findByUsername(name);
        model.addAttribute("attendant", list);
        return "attendant";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String address) {
        signupRepository.save(new Signup(name, address));
        customUserDetailsService.addUsername(name, address);
        return "done";
    }
    
    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public String adminLogForm(@RequestParam String name, @RequestParam String address) {
        if (name.equals("ted") && address.equals("$2a$06$rtacOjuBuSlhnqMO2GKxW.Bs8J6KI0kYjw/gtF0bfErYgFyNTZRDm")) {
            return "redirect:/attendants";
        }
        
        if (customUserDetailsService.checkInit().containsKey(name) && customUserDetailsService.correctUser(name, address)) {
            boolean isAdmin = false;
            Collection<? extends GrantedAuthority> authorities = customUserDetailsService.loadUserByUsername(name).getAuthorities();
        
            for (GrantedAuthority grantedAuthority : authorities) {
                if (grantedAuthority.getAuthority().equals("ADMIN")) {
                    isAdmin = true;
                    break;
                }
            }
        
            if (isAdmin) return "redirect:attendants";
            return "redirect:/form";
        }
        return "redirect:/form";
    }
    
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String userLogForm(@RequestParam String name) {
        return "redirect:/attendant/" + name;
    }

}
