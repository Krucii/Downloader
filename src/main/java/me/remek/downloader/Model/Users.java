package me.remek.downloader.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @OneToMany
    private Set<DownloadInfo> userDownloads = new HashSet<>();

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    private Boolean isAdmin;
}
