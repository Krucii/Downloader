package me.remigiuszkatrynski.downloader.Repository;

import me.remigiuszkatrynski.downloader.Model.DownloadInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DownloadInfoRepository extends JpaRepository<DownloadInfo, Long> {
}
