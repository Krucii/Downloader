package me.remek.downloader.Repository;

import me.remek.downloader.Model.DownloadInfo;
import me.remek.downloader.Model.Stats;
import me.remek.downloader.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query("select o from Stats o where o.owner=:user")
    Stats findAll(@Param("user") Users user);
}
