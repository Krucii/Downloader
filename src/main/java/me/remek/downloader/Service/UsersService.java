package me.remek.downloader.Service;

import me.remek.downloader.Model.Users;
import me.remek.downloader.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService implements UserDetailsService {

    @Autowired
    private UsersRepository userRepository;

    public List<Users> findAll() {
        return userRepository.findAll();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public Optional<Users> getUser(String username) {
        return userRepository.findByUsername(username);
    }

    private Users getUserById(Long id) {
        return userRepository.getUsersById(id);
    }

    public Users getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        return getUser(name).orElse(null);
    }

    public void save(Users newUser) {
        userRepository.save(newUser);
    }

    public void delete(Long id) {
        Users u = getUserById(id);
        userRepository.delete(u);
    }
}
