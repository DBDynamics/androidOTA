package cn.wch.otalib.entry;

import androidx.annotation.NonNull;


public class CurrentImageInfo {
    private ImageType type=ImageType.UNKNOWN;
    private String version;
    private int offset;

    private ChipType chipType;

    //10/27
    private int blockSize=0;

    public CurrentImageInfo() {
    }

    public CurrentImageInfo(ImageType type, String version, int offset) {
        this.type = type;
        this.version = version;
        this.offset = offset;
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public ChipType getChipType() {
        return chipType;
    }

    public void setChipType(ChipType chipType) {
        this.chipType = chipType;
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrentImage: type-->"+type.toString()+";version-->"+version+";offset-->"+offset
                +";BlockSize-->"+blockSize+";ChipType-->"+chipType.toString();
    }
}
