package cn.wch.otalib.utils;


import cn.wch.blelib.utils.LogUtil;
import cn.wch.otalib.entry.ChipType;
import cn.wch.otalib.entry.CurrentImageInfo;
import cn.wch.otalib.entry.ImageType;

public class ParseUtil {
    public static CurrentImageInfo parseImageFromResponse(byte[] response){
        if(response==null || response.length!=20){
            return null;
        }
        CurrentImageInfo imageInfo=new CurrentImageInfo();
        if(response[0]==(byte) 0x01){
            imageInfo.setType(ImageType.A);
        }else if(response[0]==(byte)0x02){
            imageInfo.setType(ImageType.B);
        }else {
            imageInfo.setType(ImageType.UNKNOWN);
        }
        //imageInfo.setVersion(String.format(Locale.US,"%02X",response[7]));
        imageInfo.setOffset(FormatUtil.bytesToIntLittleEndian(response,1));
        //10/27
        imageInfo.setBlockSize((response[6] & 0xff)*256+(response[5] & 0xff));
        //chip type
        int b1 = response[7] & 0xff;
        int b2 = response[8] & 0xff;
        LogUtil.d("response is:"+cn.wch.blelib.utils.FormatUtil.bytesToHexString(response));
        if(b1==0x83 && b2==0x00){
            imageInfo.setChipType(ChipType.CH583);
        }else if(b1==0x08 && b2==0x02) {
            imageInfo.setChipType(ChipType.CH32V208);
        }else if(b1==0x08 && b2==0xf2) {
            imageInfo.setChipType(ChipType.CH32F208);
        }else if(b1==0x79 && b2==0x00) {
            imageInfo.setChipType(ChipType.CH579);
        }else if(b1==0x73 && b2==0x00) {
            imageInfo.setChipType(ChipType.CH573);
        }else if(b1==0x92 && b2==0x00) {
            imageInfo.setChipType(ChipType.CH592);
        }else {
            imageInfo.setChipType(ChipType.UNKNOWN);
        }
        return imageInfo;
    }

    public static boolean parseEraseResponse(byte[] response){
        return response!=null && response.length!=0 && response[0]==0x00;
    }

    public static boolean parseVerifyResponse(byte[] response){
        return response!=null && response.length!=0 && response[0]==0x00;
    }
}
