package cn.wch.otalib.callback;

public  interface IProgress {
     void onEraseStart();
     void onEraseFinish();
     void onProgramStart();
     void onProgramProgress(int current,int total);
     void onProgramFinish();
     void onVerifyStart();
     void onVerifyProgress(int current,int total);
     void onVerifyFinish();
     void onEnd();
     void onCancel();
     void onError(String message);
     void onInformation(String message);
}
