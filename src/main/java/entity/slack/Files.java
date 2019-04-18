package entity.slack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Files {

    private String[] channels;
    @JsonProperty("comments_count")
    private Integer commentsCount;
    private Long created;
    @JsonProperty("display_as_bot")
    private String displayAsBot;
    private Boolean editable;
    @JsonProperty("external_type")
    private String externalType;
    @JsonProperty("filetype")
    private String fileType;
    private String[] groups;
    private String id;
    @JsonProperty("image_exif_rotation")
    private Integer imageExifRotation;
    private String[] ims;
    @JsonProperty("is_external")
    private Boolean isExternal;
    @JsonProperty("is_public")
    private Boolean isPublic;
    @JsonProperty("mimetype")
    private String mimeType;
    private String mode;
    private String name;
    @JsonProperty("original_h")
    private Integer originalH;
    @JsonProperty("original_w")
    private Integer originalW;
    private String permalink;
    @JsonProperty("permalink_public")
    private String permalinkPublic;
    @JsonProperty("pretty_type")
    private String prettyType;
    @JsonProperty("public_url_shared")
    private Boolean publicUrlShared;
    private Long size;
    @JsonProperty("thumb_1024")
    private String thumb1024;
    @JsonProperty("thumb_1024_h")
    private Integer thumb1024H;
    @JsonProperty("thumb_1024_w")
    private Integer thumb1024W;
    @JsonProperty("thumb_160")
    private String thumb160;
    @JsonProperty("thumb_360")
    private String thumb360;
    @JsonProperty("thumb_360_h")
    private Integer thumb360H;
    @JsonProperty("thumb_360_w")
    private Integer thumb360W;
    @JsonProperty("thumb_480")
    private String thumb480;
    @JsonProperty("thumb_480_h")
    private Integer thumb480H;
    @JsonProperty("thumb_480_w")
    private Integer thumb480W;
    @JsonProperty("thumb_64")
    private String thumb64;
    @JsonProperty("thumb_720")
    private String thumb720;
    @JsonProperty("thumb_720_h")
    private Integer thumb720H;
    @JsonProperty("thumb_720_w")
    private Integer thumb720W;
    @JsonProperty("thumb_80")
    private String thumb80;
    @JsonProperty("thumb_800")
    private String thumb800;
    @JsonProperty("thumb_800_h")
    private Integer thumb800H;
    @JsonProperty("thumb_800_w")
    private Integer thumb800W;
    @JsonProperty("thumb_960")
    private String thumb960;
    @JsonProperty("thumb_960_h")
    private Integer thumb960H;
    @JsonProperty("thumb_960_w")
    private Integer thumb960W;
    private Long timestamp;
    private String title;
    @JsonProperty("url_private")
    private String urlPrivate;
    @JsonProperty("url_private_download")
    private String urlPrivateDownload;
    private String user;
    @JsonProperty("username")
    private String userName;

    public String[] getChannels() {
        return channels;
    }

    public void setChannels(String[] channels) {
        this.channels = channels;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getDisplayAsBot() {
        return displayAsBot;
    }

    public void setDisplayAsBot(String displayAsBot) {
        this.displayAsBot = displayAsBot;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public String getExternalType() {
        return externalType;
    }

    public void setExternalType(String externalType) {
        this.externalType = externalType;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getImageExifRotation() {
        return imageExifRotation;
    }

    public void setImageExifRotation(Integer imageExifRotation) {
        this.imageExifRotation = imageExifRotation;
    }

    public String[] getIms() {
        return ims;
    }

    public void setIms(String[] ims) {
        this.ims = ims;
    }

    public Boolean getExternal() {
        return isExternal;
    }

    public void setExternal(Boolean external) {
        isExternal = external;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOriginalH() {
        return originalH;
    }

    public void setOriginalH(Integer originalH) {
        this.originalH = originalH;
    }

    public Integer getOriginalW() {
        return originalW;
    }

    public void setOriginalW(Integer originalW) {
        this.originalW = originalW;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getPermalinkPublic() {
        return permalinkPublic;
    }

    public void setPermalinkPublic(String permalinkPublic) {
        this.permalinkPublic = permalinkPublic;
    }

    public String getPrettyType() {
        return prettyType;
    }

    public void setPrettyType(String prettyType) {
        this.prettyType = prettyType;
    }

    public Boolean getPublicUrlShared() {
        return publicUrlShared;
    }

    public void setPublicUrlShared(Boolean publicUrlShared) {
        this.publicUrlShared = publicUrlShared;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getThumb1024() {
        return thumb1024;
    }

    public void setThumb1024(String thumb1024) {
        this.thumb1024 = thumb1024;
    }

    public Integer getThumb1024H() {
        return thumb1024H;
    }

    public void setThumb1024H(Integer thumb1024H) {
        this.thumb1024H = thumb1024H;
    }

    public Integer getThumb1024W() {
        return thumb1024W;
    }

    public void setThumb1024W(Integer thumb1024W) {
        this.thumb1024W = thumb1024W;
    }

    public String getThumb160() {
        return thumb160;
    }

    public void setThumb160(String thumb160) {
        this.thumb160 = thumb160;
    }

    public String getThumb360() {
        return thumb360;
    }

    public void setThumb360(String thumb360) {
        this.thumb360 = thumb360;
    }

    public Integer getThumb360H() {
        return thumb360H;
    }

    public void setThumb360H(Integer thumb360H) {
        this.thumb360H = thumb360H;
    }

    public Integer getThumb360W() {
        return thumb360W;
    }

    public void setThumb360W(Integer thumb360W) {
        this.thumb360W = thumb360W;
    }

    public String getThumb480() {
        return thumb480;
    }

    public void setThumb480(String thumb480) {
        this.thumb480 = thumb480;
    }

    public Integer getThumb480H() {
        return thumb480H;
    }

    public void setThumb480H(Integer thumb480H) {
        this.thumb480H = thumb480H;
    }

    public Integer getThumb480W() {
        return thumb480W;
    }

    public void setThumb480W(Integer thumb480W) {
        this.thumb480W = thumb480W;
    }

    public String getThumb64() {
        return thumb64;
    }

    public void setThumb64(String thumb64) {
        this.thumb64 = thumb64;
    }

    public String getThumb720() {
        return thumb720;
    }

    public void setThumb720(String thumb720) {
        this.thumb720 = thumb720;
    }

    public Integer getThumb720H() {
        return thumb720H;
    }

    public void setThumb720H(Integer thumb720H) {
        this.thumb720H = thumb720H;
    }

    public Integer getThumb720W() {
        return thumb720W;
    }

    public void setThumb720W(Integer thumb720W) {
        this.thumb720W = thumb720W;
    }

    public String getThumb80() {
        return thumb80;
    }

    public void setThumb80(String thumb80) {
        this.thumb80 = thumb80;
    }

    public String getThumb800() {
        return thumb800;
    }

    public void setThumb800(String thumb800) {
        this.thumb800 = thumb800;
    }

    public Integer getThumb800H() {
        return thumb800H;
    }

    public void setThumb800H(Integer thumb800H) {
        this.thumb800H = thumb800H;
    }

    public Integer getThumb800W() {
        return thumb800W;
    }

    public void setThumb800W(Integer thumb800W) {
        this.thumb800W = thumb800W;
    }

    public String getThumb960() {
        return thumb960;
    }

    public void setThumb960(String thumb960) {
        this.thumb960 = thumb960;
    }

    public Integer getThumb960H() {
        return thumb960H;
    }

    public void setThumb960H(Integer thumb960H) {
        this.thumb960H = thumb960H;
    }

    public Integer getThumb960W() {
        return thumb960W;
    }

    public void setThumb960W(Integer thumb960W) {
        this.thumb960W = thumb960W;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlPrivate() {
        return urlPrivate;
    }

    public void setUrlPrivate(String urlPrivate) {
        this.urlPrivate = urlPrivate;
    }

    public String getUrlPrivateDownload() {
        return urlPrivateDownload;
    }

    public void setUrlPrivateDownload(String urlPrivateDownload) {
        this.urlPrivateDownload = urlPrivateDownload;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
