package entity.slack;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Channels {
    private String id;
    private String name;
    @JsonProperty("is_channel")
    private Boolean isChannel;
    private Long created;
    @JsonProperty("is_archived")
    private Boolean isArchived;
    @JsonProperty("is_general")
    private Boolean isGeneral;
    private Integer unlinked;
    private String creator;
    @JsonProperty("name_normalized")
    private String nameNormalized;
    @JsonProperty("is_shared")
    private Boolean isShared;
    @JsonProperty("is_org_shared")
    private Boolean isOrgShared;
    @JsonProperty("is_member")
    private Boolean isMember;
    @JsonProperty("is_private")
    private Boolean isPrivate;
    @JsonProperty("is_mpim")
    private Boolean isMpim;
    private List<String> members;
    private Topic topic;
    private Purpose purpose;
    @JsonProperty("previous_names")
    private List<String> previousNames;
    @JsonProperty("num_members")
    private Integer numMembers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getChannel() {
        return isChannel;
    }

    public void setChannel(Boolean channel) {
        isChannel = channel;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public Boolean getGeneral() {
        return isGeneral;
    }

    public void setGeneral(Boolean general) {
        isGeneral = general;
    }

    public Integer getUnlinked() {
        return unlinked;
    }

    public void setUnlinked(Integer unlinked) {
        this.unlinked = unlinked;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getNameNormalized() {
        return nameNormalized;
    }

    public void setNameNormalized(String nameNormalized) {
        this.nameNormalized = nameNormalized;
    }

    public Boolean getShared() {
        return isShared;
    }

    public void setShared(Boolean shared) {
        isShared = shared;
    }

    public Boolean getOrgShared() {
        return isOrgShared;
    }

    public void setOrgShared(Boolean orgShared) {
        isOrgShared = orgShared;
    }

    public Boolean getMember() {
        return isMember;
    }

    public void setMember(Boolean member) {
        isMember = member;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Boolean getMpim() {
        return isMpim;
    }

    public void setMpim(Boolean mpim) {
        isMpim = mpim;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public List<String> getPreviousNames() {
        return previousNames;
    }

    public void setPreviousNames(List<String> previousNames) {
        this.previousNames = previousNames;
    }

    public Integer getNumMembers() {
        return numMembers;
    }

    public void setNumMembers(Integer numMembers) {
        this.numMembers = numMembers;
    }
}
