package me.remek.downloader.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Users owner;

    private Double downloadedGigabytes = 0D;
    private Integer downloadsCompleted = 0;

    public Stats(Users owner) {
        this.owner = owner;
    }
}
