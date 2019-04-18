import org.riversun.xternal.simpleslackapi.SlackUser;

public class MockSlackUser implements SlackUser {
    @Override
    public String getId() {
        return "0001";
    }

    @Override
    public String getUserName() {
        return "Slack一郎";
    }

    @Override
    public String getRealName() {
        return null;
    }

    @Override
    public String getUserMail() {
        return null;
    }

    @Override
    public String getUserSkype() {
        return null;
    }

    @Override
    public String getUserPhone() {
        return null;
    }

    @Override
    public String getUserTitle() {
        return null;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public boolean isOwner() {
        return false;
    }

    @Override
    public boolean isPrimaryOwner() {
        return false;
    }

    @Override
    public boolean isRestricted() {
        return false;
    }

    @Override
    public boolean isUltraRestricted() {
        return false;
    }

    @Override
    public boolean isBot() {
        return false;
    }

    @Override
    public String getTimeZone() {
        return null;
    }

    @Override
    public String getTimeZoneLabel() {
        return null;
    }

    @Override
    public Integer getTimeZoneOffset() {
        return null;
    }

    @Override
    public SlackPresence getPresence() {
        return null;
    }
}
