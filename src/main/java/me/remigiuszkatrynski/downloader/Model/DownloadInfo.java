package me.remigiuszkatrynski.downloader.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "download_info")
@Data
public class DownloadInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "downloaded_file_path")
    private String downloadedFilePath;

    @Column(name = "resume_offset")
    private long resumeOffset;

    @Column(name = "is_downloading")
    private boolean isDownloading;

    public DownloadInfo() {
    }

    public DownloadInfo(String fileUrl, String downloadedFilePath, long resumeOffset, boolean isDownloading) {
        this.fileUrl = fileUrl;
        this.downloadedFilePath = downloadedFilePath;
        this.resumeOffset = resumeOffset;
        this.isDownloading = isDownloading;
    }
}

