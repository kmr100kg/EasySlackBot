package entity.slack;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileOption {

    private File file;
    private List<String> channels;
    private String title;
    private String fileName;
    private String comment;

    public FileOption() {
        this.channels = Collections.singletonList("");
        this.title = "";
        this.fileName = "";
        this.comment = "";
    }

    public FileOption(String file, String channels) {
        this.file = new File(file);
        this.channels = Arrays.asList(channels.split(","));
        this.title = "";
        this.fileName = "";
        this.comment = "";
    }

    public FileOption(String file, String channels, String fileName) {
        this.file = new File(file);
        this.channels = Arrays.asList(channels.split(","));
        this.fileName = fileName;
        this.title = "";
        this.comment = "";
    }

    public FileOption(String file, String channels, String title, String fileName, String comment) {
        this.file = new File(file);
        this.channels = Arrays.asList(channels.split(","));
        this.title = title;
        this.fileName = fileName;
        this.comment = comment;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
