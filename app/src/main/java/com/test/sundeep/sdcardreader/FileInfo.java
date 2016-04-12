package com.test.sundeep.sdcardreader;

/**
 * Created by SaideepReddy on 4/12/2016.
 */
public class FileInfo {
    String extension;
    String name;
    int countOfExt;
    int size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getCountOfExt() {
        return countOfExt;
    }

    public void setCountOfExt(int countOfExt) {
        this.countOfExt = countOfExt;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
