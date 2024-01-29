package me.remek.downloader.Controller;

import me.remek.downloader.Model.Users;
import me.remek.downloader.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsersService usersService;

    @GetMapping()
    public String listUsers(Model model) {

        if (usersService.getLoggedUser().getIsAdmin()) {
            model.addAttribute("users", usersService.findAll());
            model.addAttribute("newUser", new Users());
            return "admin";
        }
        else {
            return "home";
        }
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute Users newUser) {
        if (usersService.getLoggedUser().getIsAdmin()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
            newUser.setPassword(encoder.encode(newUser.getPassword()));
            usersService.save(newUser);
            return "redirect:/admin";
        }
        else {
            return "home";
        }
    }

    @GetMapping("/remove/{id}")
    public String removeUser(@PathVariable Long id) {
        if (usersService.getLoggedUser().getIsAdmin()) {
            usersService.delete(id);
            return "redirect:/admin";
        }
        else {
            return "home";
        }
    }

}
