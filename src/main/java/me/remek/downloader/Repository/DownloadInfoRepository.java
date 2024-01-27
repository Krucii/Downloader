package me.remek.downloader.Repository;

import me.remek.downloader.Model.DownloadInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DownloadInfoRepository extends JpaRepository<DownloadInfo, Long> {
}
