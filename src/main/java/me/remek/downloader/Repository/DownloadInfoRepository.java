package me.remek.downloader.Repository;

import me.remek.downloader.Model.DownloadInfo;
import me.remek.downloader.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DownloadInfoRepository extends JpaRepository<DownloadInfo, Long> {
    @Query("select o from DownloadInfo o where o.owner=:user")
    List<DownloadInfo> findAll(@Param("user") Users user);
}
