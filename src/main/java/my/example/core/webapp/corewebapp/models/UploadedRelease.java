package my.example.core.webapp.corewebapp.models;

import java.util.Date;

public class UploadedRelease {

    private Integer id;
    private String version;
    private String gitTag;
    private Date uploadTime;
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGitTag() {
        return gitTag;
    }

    public void setGitTag(String gitTag) {
        this.gitTag = gitTag;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
